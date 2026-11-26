"""Report readiness for the local mise tasks without running builds or printing credentials."""

import base64
import json
import os
from pathlib import Path
from queue import Empty, Queue
import re
import shutil
import subprocess
import sys
from threading import Thread
from time import monotonic
from urllib.error import HTTPError
from urllib.parse import urlsplit
from urllib.request import HTTPRedirectHandler, Request, build_opener
from xml.etree import ElementTree


ROOT = Path(__file__).resolve().parent.parent
SUBMODULE = "its/sources/project"
ACTUAL_SARIF = "its/ruling/target/actual/project"
LICENSE_API = "https://api.github.com/repos/SonarSource/licenses/contents/"
ENTERPRISE_METADATA = ("sonarsource/com/sonarsource/sonarqube/"
                       "sonarqube-enterprise-lw/maven-metadata.xml")
HTTP_TIMEOUT = 3.0
COMMAND_TIMEOUT = 4.0
DOCTOR_BUDGET = 4.3
ANSI_STYLE = re.compile(r"\x1b\[[0-9;]*m")
GIB = 1024 ** 3
REPOX_FIX = "configure the Repox mirror and its matching server in ~/.m2/settings.xml"
ARTIFACTORY_FIX = "export the Repox ARTIFACTORY_URL and a read-only ARTIFACTORY_ACCESS_TOKEN from Vault"
LICENSE_FIX = ("set a token with read access to SonarSource/licenses; replace or remove github.token in "
               "~/.sonar/orchestrator/orchestrator.properties before relying on GITHUB_TOKEN")


def blocked(reason, fix):
    return f"CANNOT RUN: {reason} — {fix}"


def command(*args, merge_stderr=False):
    executable = shutil.which(args[0])
    if executable is None:
        raise FileNotFoundError
    return subprocess.run(
        [executable, *args[1:]], cwd=ROOT, stdin=subprocess.DEVNULL, stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT if merge_stderr else subprocess.DEVNULL,
        encoding="utf-8", errors="replace", timeout=COMMAND_TIMEOUT, check=False,
    )


def check_java():
    result = command("java", "-version", merge_stderr=True)
    if result.returncode or not re.search(r'(?m)^(?:openjdk|java) version "21(?=[.\-+"])', result.stdout):
        return blocked("Java is not version 21", "run mise install and use mise's Java 21")
    return None


def check_maven():
    result = command("mvn", "--version", merge_stderr=True)
    version_info = ANSI_STYLE.sub("", result.stdout)
    if result.returncode or not re.search(r"(?m)^Apache Maven 3\.9\.", version_info):
        return blocked("Maven is not version 3.9", "run mise install and use mise's Maven 3.9")
    if not re.search(r"(?m)^Java version: 21(?=[.,\-+\s])", version_info):
        return blocked("Maven is not using Java 21", "set JAVA_HOME to the Java 21 installed by mise")
    return None


class NoRedirect(HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def http_status(url, headers):
    # Credentials are sent only to the original HTTPS endpoint, never via redirects or command arguments.
    request = Request(url, headers=headers)
    try:
        with build_opener(NoRedirect()).open(request, timeout=HTTP_TIMEOUT) as response:
            return response.status
    except HTTPError as error:
        error.close()
        return error.code
    except (OSError, ValueError):
        return None


def https_host(url):
    try:
        parsed = urlsplit(url)
        if (parsed.scheme == "https" and parsed.hostname and parsed.username is None
                and not parsed.query and not parsed.fragment and not any(c.isspace() for c in url)):
            return parsed.hostname
    except ValueError:
        pass
    return None


def maven_value(node, tag):
    value = node.findtext(f"{{*}}{tag}", "").strip()
    return re.sub(r"\$\{env\.([A-Za-z_]\w*)\}", lambda m: os.environ.get(m[1], ""), value, flags=re.ASCII)


def repox_mirror(settings):
    servers = {maven_value(node, "id"): node for node in settings.findall("./{*}servers/{*}server")}
    for mirror in settings.findall("./{*}mirrors/{*}mirror"):
        url = maven_value(mirror, "url").rstrip("/")
        host = https_host(url)
        server_id = maven_value(mirror, "id")
        if (host and (host.startswith("repox.") or host == https_host(os.environ.get("ARTIFACTORY_URL", "")))
                and server_id and server_id in servers and maven_value(mirror, "mirrorOf")):
            return url, servers[server_id]
    return "", None


def check_repox():
    m2 = Path.home() / ".m2"
    try:
        settings = ElementTree.parse(m2 / "settings.xml").getroot()
    except (OSError, ElementTree.ParseError):
        return blocked("~/.m2/settings.xml is missing or unreadable", REPOX_FIX)
    url, server = repox_mirror(settings)
    if server is None:
        return blocked("no HTTPS Repox mirror with a matching Maven server", REPOX_FIX)

    credential_fields = ("username", "password")
    credentials = [maven_value(server, field) for field in credential_fields]
    user, password = credentials
    if not user or not password or any("${" in value for value in (user, password)):
        return blocked("the matching Maven server has no usable credentials", REPOX_FIX)
    encrypted = bool(re.search(r"(?<!\\)\{[^{}]+\}", password))
    headers = {}
    if encrypted:
        if not (m2 / "settings-security.xml").is_file():
            return blocked("the Repox password is encrypted but ~/.m2/settings-security.xml is missing",
                           "configure Maven's settings security file")
        # Leave Maven's encrypted password to Maven; check only reachability in this case.
    else:
        basic_auth = base64.b64encode(f"{user}:{password}".encode()).decode("ascii")
        headers["Authorization"] = f"Basic {basic_auth}"

    status = http_status(f"{url}/org/sonarsource/parent/parent/maven-metadata.xml", headers)
    if status == 200 or (encrypted and status in (401, 403)):
        return None
    if status in (401, 403):
        return blocked("Repox rejected the Maven server credentials", "refresh the Repox credentials in ~/.m2/settings.xml")
    return blocked("the Maven Repox mirror could not be verified", "check network/VPN access and the Repox mirror configuration")


def bearer_headers(token):
    return {"Authorization": f"Bearer {token}"}


def check_artifactory():
    url = os.environ.get("ARTIFACTORY_URL", "").rstrip("/")
    token = os.environ.get("ARTIFACTORY_ACCESS_TOKEN", "").strip()
    if not https_host(url) or not token:
        return blocked("HTTPS ARTIFACTORY_URL or ARTIFACTORY_ACCESS_TOKEN is missing or invalid", ARTIFACTORY_FIX)
    status = http_status(f"{url}/{ENTERPRISE_METADATA}", bearer_headers(token))
    if status == 200:
        return None
    if status in (401, 403, 404):
        return blocked("Artifactory did not grant access to SonarQube Enterprise", ARTIFACTORY_FIX)
    return blocked("Artifactory access could not be verified", "check network/VPN access and ARTIFACTORY_URL")


PROPERTY_KEY = re.compile(r"(?:\\.|[^\\:= \t\f])+")
PROPERTY_ESCAPE = re.compile(r"\\(?:u([0-9a-fA-F]{4})|(.))")


def unescape_property(value):
    def replace(match):
        if match[1]:
            return chr(int(match[1], 16))
        return {"t": "\t", "r": "\r", "n": "\n", "f": "\f"}.get(match[2], match[2])

    return PROPERTY_ESCAPE.sub(replace, value)


def github_property(path):
    # Orchestrator uses Java Properties: Latin-1, continued lines, and last assignment wins.
    token = None
    with path.open(encoding="latin-1") as file:
        lines = iter(file)
        for line in lines:
            line = line.rstrip("\r\n").lstrip(" \t\f")
            if not line or line.startswith(("#", "!")):
                continue
            while (len(line) - len(line.rstrip("\\"))) % 2:
                line = line[:-1] + next(lines, "").rstrip("\r\n").lstrip(" \t\f")
            match = PROPERTY_KEY.match(line)
            if match and unescape_property(match[0]) == "github.token":
                value = line[match.end():].lstrip(" \t\f")
                if value.startswith((":", "=")):
                    value = value[1:].lstrip(" \t\f")
                token = unescape_property(value)
    return token


def check_license():
    path = Path.home() / ".sonar/orchestrator/orchestrator.properties"
    try:
        token = github_property(path)
    except FileNotFoundError:
        token = None
    except OSError:
        return blocked("Orchestrator properties could not be read", LICENSE_FIX)
    if token is None:
        token = os.environ.get("GITHUB_TOKEN", "")
    if not token.strip():
        return blocked("the effective GitHub licensing token is missing", LICENSE_FIX)
    headers = {**bearer_headers(token), "Accept": "application/vnd.github+json"}
    status = http_status(LICENSE_API, headers)
    if status == 200:
        return None
    if status == 401:
        return blocked("GitHub rejected the effective licensing token (invalid or expired)", LICENSE_FIX)
    if status in (403, 404):
        return blocked("GitHub denied access to SonarSource/licenses", LICENSE_FIX)
    return blocked("GitHub licensing access could not be verified", "check connectivity to api.github.com and retry")


def free_disk():
    return min(shutil.disk_usage(path).free for path in (ROOT, Path.home()))


def check_submodule():
    result = command("git", "submodule", "status", "--", SUBMODULE)
    # A leading space means the checkout matches the gitlink; '-', '+' and 'U' do not.
    if result.returncode or not result.stdout.startswith(" "):
        return blocked("the ruling submodule is not initialized at the pinned revision",
                       f"run git submodule update --init -- {SUBMODULE}")
    return None


def check_reports():
    reports = list((ROOT / ACTUAL_SARIF).glob("*.sarif"))
    fix = "run mise run ruling:check to generate reports"
    if not reports:
        return blocked(f"no SARIF reports in {ACTUAL_SARIF}", fix)
    if any(path.is_symlink() or not path.is_file() for path in reports):
        return blocked(f"not all SARIF reports in {ACTUAL_SARIF} are regular files", fix)
    if not (ROOT / "its/ruling/target/differences").is_file():
        return blocked("the ruling run has not completed", fix)
    return None


def check_master():
    if command("git", "rev-parse", "--verify", "--quiet", "refs/remotes/origin/master^{commit}").returncode:
        return blocked("origin/master is unavailable", "run git fetch origin master:refs/remotes/origin/master")
    return None


def check_sonar():
    result = command("sonar", "auth", "status", "--format", "json")
    try:
        status = json.loads(result.stdout)
    except ValueError:
        status = None
    if result.returncode or not isinstance(status, dict) or status.get("status") != "connected":
        return blocked("Sonar CLI login could not be verified", "run sonar auth login for SonarQube Cloud")
    return None


DISK_PROBE = "disk space"
SARIF_PROBE = "SARIF reports"

PROBES = {
    "java": (check_java, "run mise install and use mise's Java 21"),
    "maven": (check_maven, "run mise install and use mise's Maven 3.9"),
    "Repox": (check_repox, REPOX_FIX),
    "Artifactory": (check_artifactory, ARTIFACTORY_FIX),
    "GitHub licensing": (check_license, LICENSE_FIX),
    DISK_PROBE: (free_disk, "ensure the checkout and home directory are accessible"),
    "ruling submodule": (check_submodule, f"ensure Git is available and run git submodule update --init -- {SUBMODULE}"),
    SARIF_PROBE: (check_reports, "run mise run ruling:check to generate reports"),
    "origin/master": (check_master, "ensure Git is available and fetch origin/master"),
    "Sonar CLI login": (check_sonar, "install the Sonar CLI and run sonar auth login for SonarQube Cloud"),
}


def run_probe(results, name, probe, fix):
    try:
        result = probe()
    except subprocess.TimeoutExpired:
        result = blocked(f"the {name} check timed out", fix)
    except Exception:
        # File contents, CLI output and transport exceptions may include credentials.
        result = blocked(f"the {name} check could not run", fix)
    results.put((name, result))


def collect_probes(probes, budget):
    # Daemon workers keep stalled DNS, commands or filesystems from extending the overall deadline.
    deadline = monotonic() + budget
    results = Queue()
    for name, (probe, fix) in probes.items():
        Thread(target=run_probe, args=(results, name, probe, fix), daemon=True).start()
    statuses = {name: blocked(f"the {name} check timed out", fix) for name, (_, fix) in probes.items()}
    for _ in probes:
        try:
            name, result = results.get(timeout=max(0, deadline - monotonic()))
        except Empty:
            break
        statuses[name] = result
    return statuses


BUILD = ("java", "maven", "Repox", "build disk")
ENTERPRISE = (*BUILD, "enterprise disk", "GitHub licensing", "Artifactory")
RULING = (*ENTERPRISE, "ruling submodule")
ANALYSIS = ("origin/master", "Sonar CLI login")
TASKS = {
    "test:plugin": BUILD,
    "test:plugin:changed": BUILD,  # Falls back to the full suite without a merge base.
    "compile:plugin": BUILD,
    "lint:license-headers": BUILD,
    "format:license-headers": BUILD,
    "build:plugin": BUILD,
    "its:sonarqube": ENTERPRISE,
    "its:sonarlint": BUILD,
    "its": ENTERPRISE,
    "ruling:check": RULING,
    "ruling:diff": (SARIF_PROBE,),
    "ruling:update": (SARIF_PROBE,),
    "analyze:changed": ANALYSIS,
    "check": BUILD,
    "ci": (*RULING, *ANALYSIS),
    "doctor": (),
    "build-windows": ("Windows", *BUILD),
}


def disk_status(free, minimum):
    if isinstance(free, str):
        return free
    if free < minimum * GIB:
        return blocked(f"less than {minimum} GiB free for build files",
                       f"free at least {minimum} GiB on the checkout and home volumes")
    return None


def task_statuses(checks, windows):
    checks = {
        **checks,
        "build disk": disk_status(checks[DISK_PROBE], 2),
        "enterprise disk": disk_status(checks[DISK_PROBE], 5),
        "Windows": None if windows else blocked("Windows is required", "run the CI-only build-windows job on a Windows runner"),
    }
    for task, requirements in TASKS.items():
        yield task, next((checks[key] for key in requirements if checks[key]), "OK")


def main():
    windows = sys.platform.startswith(("win", "msys", "cygwin"))
    for task, status in task_statuses(collect_probes(PROBES, DOCTOR_BUDGET), windows):
        print(f"{task}: {status}")


if __name__ == "__main__":
    sys.stdout.reconfigure(encoding="utf-8")
    main()
