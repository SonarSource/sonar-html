"""Focused tests for the changed plugin test selector (no Maven required)."""

from contextlib import redirect_stderr, redirect_stdout
from io import StringIO
import os
from pathlib import Path, PurePosixPath
import shutil
import subprocess
import tempfile
import unittest
from unittest.mock import patch

import changed_plugin_tests as selector


PKG = PurePosixPath("org/sonar/plugins/html")
CHECK_DIR = PKG / "checks/accessibility"
CHECK_SOURCE = selector.MAIN_JAVA / CHECK_DIR / "ExampleCheck.java"
CHECK_TEST = selector.TEST_JAVA / CHECK_DIR / "ExampleCheckTest.java"
CHECK_FILTER = "org.sonar.plugins.html.checks.accessibility.ExampleCheckTest"
RULES_FILTER = "org.sonar.plugins.html.rules."
FULL_COMMAND = ["mvn", "-B", "-T1C", "-pl", "sonar-html-plugin", "test"]
UPSTREAM = "refs/remotes/origin/master"
TRACKED_PLUGIN_FILE = selector.PLUGIN / ".gitignore"


class ScratchTestCase(unittest.TestCase):
    def setUp(self):
        scratch = tempfile.TemporaryDirectory()
        self.addCleanup(scratch.cleanup)
        self.root = Path(scratch.name)

    def put(self, path, content="example\n"):
        file = self.root / path
        file.parent.mkdir(parents=True, exist_ok=True)
        file.write_text(content, encoding="utf-8")


class SelectionTest(ScratchTestCase):
    def test_check_uses_existing_test_in_the_same_package(self):
        self.put(CHECK_SOURCE)
        self.put(selector.TEST_JAVA / PKG / "checks/sonar/ExampleCheckTest.java")
        self.assertIsNone(selector.select_tests(self.root, {CHECK_SOURCE}))

        self.put(CHECK_TEST)
        self.assertEqual({CHECK_FILTER}, selector.select_tests(self.root, {CHECK_SOURCE, CHECK_TEST}))

        (self.root / CHECK_SOURCE).unlink()
        self.assertIsNone(selector.select_tests(self.root, {CHECK_SOURCE}))

    def test_changed_test_must_still_exist(self):
        path = selector.TEST_JAVA / PKG / "lex/PageLexerTest.java"
        self.put(path)
        self.assertEqual({"org.sonar.plugins.html.lex.PageLexerTest"}, selector.select_tests(self.root, {path}))

        (self.root / path).unlink()
        self.assertIsNone(selector.select_tests(self.root, {path}))

    def test_fixtures_select_all_tests_referencing_the_exact_paths(self):
        fixture = "checks/ExampleCheck/detail with spaces.html"
        variant = "checks/ExampleCheckVariant.cshtml"
        self.put(CHECK_TEST, f'new File("src/test/resources/{fixture}"); new File("src/test/resources/{variant}");')
        self.put(selector.TEST_JAVA / PKG / "analyzers/PageCountLinesTest.java", f'readFile("{fixture}");')
        self.put(selector.TEST_JAVA / PKG / "checks/sonar/ExampleCheckTest.java", f'readFile("{fixture}.other");')
        self.assertEqual(
            {CHECK_FILTER, "org.sonar.plugins.html.analyzers.PageCountLinesTest"},
            selector.select_tests(self.root, {selector.TEST_RESOURCES / fixture, selector.TEST_RESOURCES / variant}),
        )
        self.assertIsNone(selector.select_tests(self.root, {selector.TEST_RESOURCES / "checks/ExampleCheck.html"}))

    def test_rule_metadata_and_profile_select_tests_that_load_them(self):
        for name in ("CheckClassesTest", "HtmlRulesDefinitionTest", "SonarWayProfileTest"):
            self.put(selector.TEST_JAVA / PKG / "rules" / f"{name}.java")
        rule_tests = {RULES_FILTER + name for name in ("CheckClassesTest", "HtmlRulesDefinitionTest")}
        self.assertEqual(rule_tests, selector.select_tests(self.root, {
            selector.RULE_METADATA / "S6853.html", selector.RULE_METADATA / "ImgWithoutAltCheck.json",
        }))
        self.assertEqual(rule_tests | {RULES_FILTER + "SonarWayProfileTest"}, selector.select_tests(self.root, {
            selector.RULE_METADATA / "Sonar_way_profile.json",
        }))

    def test_any_unmapped_plugin_input_overrides_a_targeted_selection(self):
        self.put(CHECK_TEST)
        for path in (
            PurePosixPath("pom.xml"), PurePosixPath(".mvn/maven.config"), selector.PLUGIN / "pom.xml",
            selector.MAIN_JAVA / PKG / "lex/PageLexer.java",
            selector.TEST_JAVA / PKG / "checks/TestHelper.java",
            selector.TEST_JAVA / PKG / "weird/Some,OtherTest.java",
            selector.TEST_JAVA / PKG / "not.a.package/ExampleTest.java",
        ):
            with self.subTest(path=path):
                self.put(path)
                self.assertIsNone(selector.select_tests(self.root, {CHECK_TEST, path}))

    def test_non_plugin_paths_are_ignored(self):
        self.assertEqual(set(), selector.select_tests(self.root, {
            PurePosixPath("docs/index.md"), PurePosixPath("its/plugin/pom.xml"),
            PurePosixPath("sonar-html-plugin-other/pom.xml"),
        }))


@unittest.skipUnless(shutil.which("git"), "Git is required for repository tests")
class GitChangesTest(ScratchTestCase):
    def setUp(self):
        super().setUp()
        settings = patch.dict(os.environ, {"GIT_CONFIG_NOSYSTEM": "1", "GIT_CONFIG_GLOBAL": os.devnull})
        settings.start()
        self.addCleanup(settings.stop)
        self.git("init", "--quiet", "--template=")
        self.put(".gitignore", "target/\n")
        self.put(TRACKED_PLUGIN_FILE, "# Original\n")
        self.put(CHECK_SOURCE)
        self.put(CHECK_TEST)
        self.git("add", ".")
        self.commit("base")
        base = self.git("rev-parse", "HEAD")

        # Create a real upstream-only change, then put HEAD back at the base.
        self.put(selector.MAIN_JAVA / PKG / "lex/PageLexer.java")
        self.git("add", ".")
        self.commit("upstream")
        self.git("update-ref", UPSTREAM, "HEAD")
        self.git("checkout", "--quiet", "--detach", base)

    def git(self, *args):
        return selector.git(self.root, *args).decode("ascii").strip()

    def commit(self, message):
        self.git("-c", "user.name=Test", "-c", "user.email=test@example.invalid", "commit", "--quiet", "-m", message)

    def test_upstream_only_plugin_change_does_not_trigger_maven(self):
        self.put("docs/local notes.md")
        paths = selector.changed_paths(self.root)
        self.assertEqual({PurePosixPath("docs/local notes.md")}, paths)
        self.assertEqual(set(), selector.select_tests(self.root, paths))

    def test_committed_staged_unstaged_and_untracked_changes_are_combined(self):
        self.put(CHECK_SOURCE, "committed\n")
        self.git("add", "--", str(CHECK_SOURCE))
        self.commit("local")
        self.put(CHECK_TEST, "staged\n")
        self.git("add", "--", str(CHECK_TEST))
        self.put(CHECK_TEST)  # Unstaged reversal must not hide the index change.
        self.put(TRACKED_PLUGIN_FILE, "# Unstaged\n")
        untracked = selector.TEST_RESOURCES / "test with space.html"
        self.put(untracked)
        self.put(selector.PLUGIN / "target/ignored.txt")
        self.assertEqual({CHECK_SOURCE, CHECK_TEST, TRACKED_PLUGIN_FILE, untracked}, selector.changed_paths(self.root))

    def test_rename_out_of_plugin_retains_old_path(self):
        renamed = PurePosixPath("ExampleCheckTest.java")
        self.git("mv", str(CHECK_TEST), str(renamed))
        paths = selector.changed_paths(self.root)
        self.assertEqual({CHECK_TEST, renamed}, paths)
        self.assertIsNone(selector.select_tests(self.root, paths))


class InvocationTest(ScratchTestCase):
    def test_nul_paths_preserve_tabs_newlines_and_spaces(self):
        unusual = "sonar-html-plugin/src/test/resources/first\tsecond\nthird.html"
        unicode_path = "docs/détail with space.html"
        paths = b"\0".join((os.fsencode(unusual), os.fsencode(unicode_path), b""))
        with patch.object(selector, "git", side_effect=[b"0123\n", b"", b"", b"", paths]):
            self.assertEqual({PurePosixPath(unusual), PurePosixPath(unicode_path)}, selector.changed_paths(self.root))

    def test_targeted_command_sorts_filters_and_returns_maven_status(self):
        other_test = selector.TEST_JAVA / PKG / "lex/PageLexerTest.java"
        self.put(CHECK_TEST)
        self.put(other_test)
        with patch.object(selector, "changed_paths", return_value={other_test, CHECK_TEST}), \
                patch.object(selector.shutil, "which", return_value=None), \
                patch.object(selector.subprocess, "run", return_value=subprocess.CompletedProcess([], 17)) as mvn, \
                redirect_stdout(StringIO()):
            self.assertEqual(17, selector.main(self.root))
        filters = f"-Dtest={CHECK_FILTER},org.sonar.plugins.html.lex.PageLexerTest"
        mvn.assert_called_once_with([*FULL_COMMAND, filters], cwd=self.root, check=False)

    def test_no_plugin_changes_does_not_start_maven(self):
        with patch.object(selector, "changed_paths", return_value={PurePosixPath("docs/index.md")}), \
                patch.object(selector.subprocess, "run") as mvn, redirect_stdout(StringIO()) as output:
            self.assertEqual(0, selector.main(self.root))
        mvn.assert_not_called()
        self.assertIn("skipping", output.getvalue())

    def test_missing_baseline_runs_full_command_without_fetching(self):
        failure = subprocess.CalledProcessError(128, ["git", "merge-base"], stderr=b"invalid reference")
        with patch.object(selector, "git", side_effect=failure) as git, \
                patch.object(selector.shutil, "which", return_value=None), \
                patch.object(selector.subprocess, "run", return_value=subprocess.CompletedProcess([], 2)) as mvn, \
                redirect_stdout(StringIO()), redirect_stderr(StringIO()) as error:
            self.assertEqual(2, selector.main(self.root))
        git.assert_called_once_with(self.root, "merge-base", UPSTREAM, "HEAD")
        mvn.assert_called_once_with(FULL_COMMAND, cwd=self.root, check=False)
        self.assertIn("origin/master", error.getvalue())


if __name__ == "__main__":
    unittest.main()
