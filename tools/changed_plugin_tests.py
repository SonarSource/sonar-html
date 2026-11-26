"""Run the plugin unit tests related to local changes since origin/master."""

import os
from pathlib import Path, PurePosixPath
import re
import shutil
import subprocess
import sys
from typing import Optional


PLUGIN = PurePosixPath("sonar-html-plugin")
MAIN_JAVA = PLUGIN / "src/main/java"
TEST_JAVA = PLUGIN / "src/test/java"
TEST_RESOURCES = PLUGIN / "src/test/resources"
PACKAGE = PurePosixPath("org/sonar/plugins/html")
CHECKS = MAIN_JAVA / PACKAGE / "checks"
RULE_METADATA = PLUGIN / "src/main/resources/org/sonar/l10n/web/rules/Web"
MAVEN_TEST = ["mvn", "-B", "-T1C", "-pl", str(PLUGIN), "test"]
IDENTIFIER = re.compile(r"[A-Za-z_][A-Za-z_0-9]*")
RULE_FILE = re.compile(r"[A-Za-z0-9_]+\.(?:html|json)")


def git(root: Path, *args: str) -> bytes:
    return subprocess.run(["git", "--no-optional-locks", *args], cwd=root, check=True, capture_output=True).stdout


def changed_paths(root: Path) -> set[PurePosixPath]:
    base = git(root, "merge-base", "refs/remotes/origin/master", "HEAD").strip().decode("ascii")
    diff = ("diff", "--name-only", "-z", "--no-renames", "--no-ext-diff", "--ignore-submodules=none")
    # Compare each layer separately so that an unstaged reversal of a staged change
    # does not hide the staged change. Disabling rename detection retains both paths.
    outputs = (
        git(root, *diff, base, "HEAD", "--"),
        git(root, *diff, "--cached", "HEAD", "--"),
        git(root, *diff, "--"),
        git(root, "ls-files", "--others", "--exclude-standard", "-z"),
    )
    return {PurePosixPath(os.fsdecode(path)) for output in outputs for path in output.split(b"\0") if path}


def test_class(source: PurePosixPath) -> Optional[str]:
    """Translate a source path relative to TEST_JAVA to a literal Surefire filter."""
    if source.name.endswith("Test.java"):
        identifiers = source.with_suffix("").parts
        if all(IDENTIFIER.fullmatch(identifier) for identifier in identifiers):
            return ".".join(identifiers)
    return None


def existing_tests(root: Path, sources: list[PurePosixPath]) -> Optional[set[str]]:
    names = set()
    for source in sources:
        name = test_class(source)
        if name is None or not (root / TEST_JAVA / source).is_file():
            return None
        names.add(name)
    return names


def fixture_tests(root: Path, fixtures: set[PurePosixPath]) -> Optional[set[str]]:
    if not fixtures:
        return set()
    # Match explicit references instead of guessing from the basename: some check
    # fixtures are shared with analyzer tests, and some filenames have no rule name.
    literals = {
        fixture: tuple(f'"{path}"'.encode("utf-8", "surrogateescape") for path in (
            fixture.as_posix(), f"/{fixture}", f"src/test/resources/{fixture}"
        ))
        for fixture in fixtures
    }
    names = set()
    matched = set()
    test_directory = root / TEST_JAVA
    for source in test_directory.rglob("*Test.java"):
        name = test_class(PurePosixPath(source.relative_to(test_directory).as_posix()))
        if name is None or not source.is_file():
            continue
        contents = source.read_bytes()
        for fixture, references in literals.items():
            if any(reference in contents for reference in references):
                matched.add(fixture)
                names.add(name)
    return names if matched == fixtures else None


def tests_for_input(root: Path, path: PurePosixPath) -> Optional[set[str]]:
    if TEST_JAVA in path.parents:
        return existing_tests(root, [path.relative_to(TEST_JAVA)])
    if CHECKS in path.parents and path.name.endswith("Check.java"):
        source = path.relative_to(MAIN_JAVA).with_name(f"{path.stem}Test.java")
        tests = existing_tests(root, [source])
        return tests if tests and (root / path).is_file() else None
    if path.parent == RULE_METADATA and RULE_FILE.fullmatch(path.name):
        # These tests load the packaged metadata; individual check tests do not.
        rules = PACKAGE / "rules"
        sources = [rules / "CheckClassesTest.java", rules / "HtmlRulesDefinitionTest.java"]
        if path.name == "Sonar_way_profile.json":
            sources.append(rules / "SonarWayProfileTest.java")
        return existing_tests(root, sources)
    return None


def select_tests(root: Path, paths: set[PurePosixPath]) -> Optional[set[str]]:
    """Return literal test filters, an empty set to skip, or None for the full suite."""
    selected = set()
    fixtures = set()
    for path in paths:
        if path.parts[0] == ".mvn" or path == PurePosixPath("pom.xml"):
            return None
        if path.parts[0] != str(PLUGIN):
            continue
        if TEST_RESOURCES in path.parents:
            fixtures.add(path.relative_to(TEST_RESOURCES))
            continue
        tests = tests_for_input(root, path)
        if tests is None:
            return None
        selected.update(tests)
    tests = fixture_tests(root, fixtures)
    return None if tests is None else selected | tests


def main(root: Path) -> int:
    try:
        tests = select_tests(root, changed_paths(root))
    except subprocess.CalledProcessError as error:
        detail = os.fsdecode(error.stderr).strip()
        print(f"Cannot determine changed paths against origin/master: {detail or error}", file=sys.stderr)
        tests = None
    except OSError as error:
        print(f"Cannot inspect changed plugin inputs: {error}", file=sys.stderr)
        tests = None

    if tests is not None and not tests:
        print("No plugin changes; skipping plugin tests.", flush=True)
        return 0

    command = MAVEN_TEST.copy()
    if os.name == "nt":
        # Native Python invoked from Git Bash also needs to find Maven's mvn.cmd.
        command[0] = shutil.which("mvn") or command[0]
    test_filter = f"-Dtest={','.join(sorted(tests))}" if tests else ""
    if test_filter and not (os.name == "nt" and len(test_filter) > 7000):
        print(f"Running plugin tests: {test_filter}", flush=True)
        command.append(test_filter)
    else:
        print("Running the full plugin unit test suite.", flush=True)
    try:
        return subprocess.run(command, cwd=root, check=False).returncode
    except OSError as error:
        print(f"Cannot start Maven: {error}", file=sys.stderr)
        return 127


if __name__ == "__main__":
    sys.exit(main(Path(__file__).resolve().parent.parent))
