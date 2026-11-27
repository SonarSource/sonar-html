"""Targeted tests for the local readiness report; no live credentials or network needed."""

from contextlib import redirect_stderr, redirect_stdout
from io import StringIO
from pathlib import Path
from threading import Event
from time import monotonic
from types import SimpleNamespace
import unittest
from unittest.mock import Mock, mock_open, patch
from xml.etree import ElementTree

from tools import doctor


class DoctorTest(unittest.TestCase):
    @patch.object(doctor, "command")
    def test_versions_include_the_jdk_maven_actually_uses(self, command):
        command.return_value = SimpleNamespace(returncode=0, stdout='openjdk version "21.0.11"\n')
        self.assertIsNone(doctor.check_java())
        command.assert_called_with("java", "-version", merge_stderr=True)
        command.return_value.stdout = 'openjdk version "210.0.1"'
        self.assertIn("Java is not version 21", doctor.check_java())
        command.return_value.stdout = "\x1b[1mApache Maven 3.9.16\x1b[m\nJava version: 21.0.11, vendor: OpenJDK\n"
        self.assertIsNone(doctor.check_maven())
        command.return_value.stdout = "Apache Maven 3.9.16\nJava version: 17.0.2, vendor: OpenJDK\n"
        self.assertIn("Maven is not using Java 21", doctor.check_maven())
        command.return_value.stdout = "Apache Maven 3.10.1\nJava version: 21.0.11, vendor: OpenJDK\n"
        self.assertIn("Maven is not version 3.9", doctor.check_maven())

    def repox_settings(self, credential, mirror_id="download"):
        root = ElementTree.fromstring(f"""<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
            <mirrors><mirror><id>{mirror_id}</id><mirrorOf>*</mirrorOf>
            <url>https://repox.example.org/artifactory/sonarsource/</url></mirror></mirrors>
            <servers><server><id>download</id><username>u</username>
            <password>{credential}</password></server></servers></settings>""")
        return patch.object(ElementTree, "parse", return_value=SimpleNamespace(getroot=lambda: root))

    @patch.object(doctor, "http_status", return_value=401)
    def test_repox_matches_server_and_probes_with_interpolated_credentials(self, request):
        with self.repox_settings("${env.TEST_REPOX_CREDENTIAL}"), patch.dict(doctor.os.environ, TEST_REPOX_CREDENTIAL="p"):
            status = doctor.check_repox()
        self.assertIn("rejected the Maven server credentials", status)
        request.assert_called_once_with(
            "https://repox.example.org/artifactory/sonarsource/org/sonarsource/parent/parent/maven-metadata.xml",
            {"Authorization": "Basic dTpw"},
        )
        request.reset_mock()
        with self.repox_settings("p", "other"):
            self.assertIn("matching Maven server", doctor.check_repox())
        request.assert_not_called()

    @patch.object(doctor, "http_status", return_value=401)
    @patch.object(Path, "is_file", return_value=True)
    def test_encrypted_repox_password_is_not_sent(self, is_file, request):
        with self.repox_settings("{opaque}"):
            self.assertIsNone(doctor.check_repox())
            self.assertEqual({}, request.call_args.args[1])
            request.return_value = None
            self.assertIn("could not be verified", doctor.check_repox())
            is_file.return_value = False
            self.assertIn("settings-security.xml", doctor.check_repox())

    @patch.object(doctor, "http_status", return_value=401)
    def test_expired_orchestrator_property_takes_precedence_with_one_request(self, request):
        properties = "# github.token=comment\ngithub.token=old\ngithub\\.token : last-\\\n  value\n"
        with patch.object(Path, "open", mock_open(read_data=properties)), patch.dict(doctor.os.environ, GITHUB_TOKEN="env"):
            status = doctor.check_license()
        request.assert_called_once_with(doctor.LICENSE_API, {
            "Authorization": "Bearer last-value", "Accept": "application/vnd.github+json",
        })
        self.assertIn("invalid or expired", status)
        self.assertIn("replace or remove github.token", status)
        self.assertNotIn("last-value", status)

    @patch.object(doctor, "http_status", return_value=200)
    def test_license_falls_back_only_when_property_is_absent(self, request):
        with patch.dict(doctor.os.environ, GITHUB_TOKEN="env"):
            with patch.object(Path, "open", side_effect=FileNotFoundError):
                self.assertIsNone(doctor.check_license())
            self.assertEqual("Bearer env", request.call_args.args[1]["Authorization"])
            request.reset_mock()
            with patch.object(Path, "open", mock_open(read_data="github.token=\n")):
                self.assertIn("effective GitHub licensing token is missing", doctor.check_license())
            request.assert_not_called()

    @patch.object(doctor, "http_status", return_value=403)
    def test_artifactory_checks_enterprise_access_and_requires_https(self, request):
        with patch.dict(doctor.os.environ, {"ARTIFACTORY_URL": "https://repox.example.org/artifactory/",
                                           "ARTIFACTORY_ACCESS_TOKEN": "sample"}):
            self.assertIn("did not grant access", doctor.check_artifactory())
        request.assert_called_once_with(
            f"https://repox.example.org/artifactory/{doctor.ENTERPRISE_METADATA}", {"Authorization": "Bearer sample"},
        )
        request.reset_mock()
        with patch.dict(doctor.os.environ, {"ARTIFACTORY_URL": "http://repox.example.org/artifactory/",
                                           "ARTIFACTORY_ACCESS_TOKEN": "sample"}):
            self.assertIn("missing or invalid", doctor.check_artifactory())
        request.assert_not_called()

    @patch.object(doctor, "command")
    def test_sonar_requires_verified_connected_status(self, command):
        command.return_value = SimpleNamespace(returncode=0, stdout='{"status":"connected","details":"private"}')
        self.assertIsNone(doctor.check_sonar())
        command.assert_called_once_with("sonar", "auth", "status", "--format", "json")
        for returncode, stdout in ((1, '{"status":"connected"}'), (0, '{"status":"disconnected"}'), (0, 'private')):
            with self.subTest(returncode=returncode, stdout=stdout):
                command.return_value = SimpleNamespace(returncode=returncode, stdout=stdout)
                status = doctor.check_sonar()
                self.assertIn("run sonar auth login", status)
                self.assertNotIn("private", status)

    @patch.object(doctor, "command")
    def test_ruling_submodule_must_match_gitlink(self, command):
        command.return_value = SimpleNamespace(returncode=0, stdout=" deadbeef its/sources/project\n")
        self.assertIsNone(doctor.check_submodule())
        for flag in "-+U":
            command.return_value.stdout = flag + "deadbeef its/sources/project\n"
            self.assertIn("pinned revision", doctor.check_submodule())

    @patch.object(Path, "glob", autospec=True, return_value=[])
    @patch.object(Path, "is_file", return_value=False)
    def test_ruling_reports_must_exist_in_a_completed_run(self, is_file, glob):
        self.assertIn("no SARIF reports", doctor.check_reports())
        glob.assert_called_once_with(doctor.ROOT / doctor.ACTUAL_SARIF, "*.sarif")
        report = Mock(is_file=lambda: True)
        report.is_symlink.return_value = True
        glob.return_value = [report]
        self.assertIn("regular files", doctor.check_reports())
        report.is_symlink.return_value = False
        self.assertIn("ruling run has not completed", doctor.check_reports())
        is_file.return_value = True
        self.assertIsNone(doctor.check_reports())

    def healthy_checks(self):
        return {**dict.fromkeys(doctor.PROBES), "disk space": 10 * doctor.GIB}

    def test_optional_credentials_do_not_gate_build_ide_or_local_sarif_tasks(self):
        checks = self.healthy_checks()
        checks.update({"GitHub licensing": "license issue", "Artifactory": "Artifactory issue",
                       "ruling submodule": "submodule issue", "origin/master": "ref issue", "Sonar CLI login": "auth issue"})
        statuses = dict(doctor.task_statuses(checks, windows=False))
        for task in ("check", "compile:plugin", "test:plugin", "its:sonarlint", "ruling:diff", "ruling:update", "doctor"):
            with self.subTest(task=task):
                self.assertEqual("OK", statuses[task])
        for task in ("its:sonarqube", "its", "ruling:check", "ci"):
            self.assertEqual("license issue", statuses[task])
        self.assertEqual("OK", statuses["test:plugin:changed"])
        self.assertEqual("ref issue", statuses["analyze:changed"])
        self.assertIn("Windows runner", statuses["build-windows"])
        self.assertEqual("OK", dict(doctor.task_statuses(checks, windows=True))["build-windows"])

    def test_disk_thresholds_are_task_specific(self):
        checks = {**self.healthy_checks(), "disk space": 3 * doctor.GIB}
        statuses = dict(doctor.task_statuses(checks, windows=True))
        self.assertEqual("OK", statuses["build:plugin"])
        self.assertIn("less than 5 GiB", statuses["ruling:check"])
        self.assertEqual("OK", statuses["ruling:diff"])

    def test_hanging_workers_share_one_budget_and_errors_are_redacted(self):
        release = Event()

        def hang():
            release.wait(2)

        def fail():
            raise OSError("sensitive error text")

        probes = {"hang1": (hang, "retry"), "hang2": (hang, "retry"), "error": (fail, "retry"),
                  "ok": (lambda: None, "retry")}
        started = monotonic()
        try:
            statuses = doctor.collect_probes(probes, 0.1)
        finally:
            release.set()
        self.assertLess(monotonic() - started, 1)
        self.assertIn("timed out", statuses["hang1"])
        self.assertIn("timed out", statuses["hang2"])
        self.assertIn("could not run", statuses["error"])
        self.assertNotIn("sensitive error text", statuses["error"])
        self.assertIsNone(statuses["ok"])

    def test_main_prints_one_line_per_task(self):
        stdout, stderr = StringIO(), StringIO()
        with patch.object(doctor, "collect_probes", return_value=self.healthy_checks()), \
                patch.object(doctor.sys, "platform", "linux"), redirect_stdout(stdout), redirect_stderr(stderr):
            doctor.main()
        lines = stdout.getvalue().splitlines()
        self.assertEqual(list(doctor.TASKS), [line.rsplit(": OK", 1)[0].split(": CANNOT RUN:", 1)[0] for line in lines])
        self.assertEqual("", stderr.getvalue())
        for line in lines:
            self.assertRegex(line, r": (OK|CANNOT RUN: .+ — .+)$")
