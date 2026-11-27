"""Positive and negative tests using miniature repositories inside this worktree."""

from contextlib import redirect_stderr
from datetime import date
from io import StringIO
from pathlib import Path
import tempfile
from textwrap import dedent
import unittest
from unittest.mock import patch

import check_docs


QA = "mise run its"
RULING = "mise run ruling:check"
CANONICAL = ".agents/skills/regenerate-rule-metadata/SKILL.md"
CLAUDE_SKILL = ".claude/skills/regenerate-rule-metadata/SKILL.md"
TEST_TARGET = "sonar-html-plugin/src/test/java/LabelHasAssociatedControlCheckTest.java"
VORTEX_BEGIN = "<!-- sonar:begin:sonarqube-agentic-analysis-protocol -->"
VORTEX_END = "<!-- sonar:end:sonarqube-agentic-analysis-protocol -->"


class CheckDocsTest(unittest.TestCase):
    def setUp(self):
        scratch = tempfile.TemporaryDirectory(prefix=".test-check-docs-", dir=Path(__file__).resolve().parent)
        self.addCleanup(scratch.cleanup)
        self.root = Path(scratch.name)
        self.put("AGENTS.md", f"""\
            # Scope

            Analyzer docs.

            ## Commands

            Run `mise run test:plugin 'LabelHasAssociatedControlCheckTest#razor'` first.
            Configure credentials before running `its` and `ruling:check`.

            ## Constraints

            Keep public API stable. Upstream: `SonarSource/rspec`.

            ## Documentation

            Read [the docs](docs/) or [the skill]({CANONICAL}).

            {VORTEX_BEGIN}
            # Protocol

            Managed examples such as `src/foo.ts` are not repo references.
            {VORTEX_END}
            """)
        self.put("CLAUDE.md", "@AGENTS.md\n")
        self.put("mise.toml", '''[tools]
java = "21.0"
maven = "3.9"
[tasks."test:plugin"]
run = "mvn test"
[tasks.its]
run = "mvn verify"
[tasks."ruling:check"]
run = "mvn verify"
''')
        self.put(".github/workflows/build.yml", f"""\
            jobs:
              build:
                steps:
                  - uses: jdx/mise-action@v5
                  - uses: SonarSource/ci-github-actions/build-maven@v2
              qa:
                steps:
                  - run: {QA}
              qa-dev:
                steps:
                  - run: {QA} --runtime DEV
              ruling:
                steps:
                  - run: {RULING}
            """)
        self.doc("docs/index.md", "`docs/documentation-conventions.md` and [systems][sys].\n\n[sys]: systems/index.md",
                 'okf_version: "0.2"\n')
        self.doc("docs/documentation-conventions.md", "# Conventions", dedent("""\
            baseline:
              resource: https://example.com/repository-baseline
              checked: '2026-01-01'
              template: template not yet published
            sources:
              - resource: AP-1529
            """))
        self.doc("docs/systems/index.md", "Read [the guide](how%20to.md#local-checks-1).")
        self.doc("docs/systems/how to.md", """\
            # Local checks

            `src/Main.java` and [docs](../index.md). File extensions such as `.cshtml` are not paths.

            # Local checks

            ```md
            [Example](missing.md) and `src/generated.java`.
            ```

            <!-- `src/another-example.java` -->
            """)
        self.put("src/Main.java", "class Main {}\n")
        self.put(TEST_TARGET, "class LabelHasAssociatedControlCheckTest { @Test void razor() {} }\n")
        self.put(CANONICAL, "# Rule metadata\n\nRegenerate it from upstream.\n")
        for adapter in (CLAUDE_SKILL, CLAUDE_SKILL.replace(".claude/", ".cursor/")):
            self.put(adapter, f"# Rule metadata\n\n[Canonical skill](../../../{CANONICAL}).\n")

    def put(self, path, content):
        target = self.root / path
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(dedent(content), encoding="utf-8")

    def doc(self, path, body, metadata=""):
        fields = "title: Guide\ndescription: Explain the analyzer.\ngenerated:\n  by: sonar-tirion\n"
        self.put(path, f"---\n{fields}{metadata}---\n\n{dedent(body)}\n")

    def modify(self, path, old, new):
        original = (self.root / path).read_text(encoding="utf-8")
        self.assertIn(old, original)
        self.put(path, original.replace(old, new))

    def fixture_paths(self):
        return {path.relative_to(self.root) for path in self.root.rglob("*") if path.is_file()}

    def check(self):
        return check_docs.run_checks(self.root, self.fixture_paths(), today=date(2026, 11, 26))

    def assert_error(self, expected):
        self.assertIn(expected, "\n".join(self.check().errors))

    def test_valid_tree_accepts_relative_links_root_paths_and_task_references(self):
        report = self.check()
        self.assertEqual([], report.errors)
        self.assertEqual([], report.warnings)

    def test_valid_tree_accepts_an_exact_nested_import(self):
        self.modify("AGENTS.md", "(docs/)", "(docs/) and `component/AGENTS.md`")
        self.put("component/AGENTS.md", "# Component\n\nInstructions for the component.\n")
        (self.root / "component/CLAUDE.md").write_bytes(b"@AGENTS.md\r\n")
        self.assertEqual([], self.check().errors)

    def test_markdown_link_is_file_relative_and_requires_a_valid_fragment(self):
        self.modify("docs/systems/how to.md", "../index.md", "docs/index.md")
        self.modify("docs/systems/index.md", "#local-checks-1", "#not-a-heading")
        errors = "\n".join(self.check().errors)
        self.assertIn("`docs/index.md` does not resolve relative to docs/systems", errors)
        self.assertIn("has no matching anchor in docs/systems/how to.md", errors)

    def test_backticked_path_is_root_relative_and_must_exist(self):
        self.modify("docs/systems/how to.md", "src/Main.java", "src/Missing.java")
        self.assert_error("repo-relative path `src/Missing.java` does not exist")

    def test_orphaned_doc_and_missing_directory_index(self):
        self.doc("docs/orphans/one.md", "# Orphan")
        errors = "\n".join(self.check().errors)
        self.assertIn("docs/orphans/index.md: required Markdown file is missing", errors)
        self.assertIn("docs/orphans/one.md: not reachable", errors)

    def test_frontmatter_checks_yaml_types_and_baseline(self):
        self.modify("docs/index.md", '"0.2"', "0.2")
        self.modify("docs/systems/index.md", "by: sonar-tirion", "by: [sonar-tirion, tirion]")
        self.modify("docs/documentation-conventions.md", "checked: '2026-01-01'", "checked: yesterday")
        self.modify("docs/documentation-conventions.md", "- resource: AP-1529", "- issue: AP-1529")
        self.put("docs/systems/how to.md", "---\ntitle: [\n---\n# Broken\n")
        self.put("docs/no-frontmatter.md", "# No frontmatter\n")
        errors = "\n".join(self.check().errors)
        self.assertIn('okf_version: "0.2" (string)', errors)
        self.assertIn("generated.by must be a single non-empty string", errors)
        self.assertIn("baseline.checked must be a YYYY-MM-DD date", errors)
        self.assertIn("sources must be a non-empty list", errors)
        self.assertIn("how to.md: invalid YAML frontmatter", errors)
        self.assertIn("no-frontmatter.md: expected a YAML frontmatter mapping", errors)

    def test_expiry_warns_but_bad_date_fails(self):
        self.modify("docs/systems/index.md", "title: Guide", "title: Guide\nstale_after: 2026-01-01")
        report = self.check()
        self.assertEqual([], report.errors)
        self.assertEqual(["docs/systems/index.md: stale_after 2026-01-01 has passed; review this document"], report.warnings)
        self.modify("docs/systems/index.md", "2026-01-01", "a long time ago")
        self.assert_error("stale_after must be a YYYY-MM-DD date")

    def test_workflow_flag_drift_and_missing_mise_tool(self):
        self.modify(".github/workflows/build.yml", "--runtime DEV", "--runtime LATEST_RELEASE")
        self.modify("mise.toml", 'maven = "3.9"\n', "")
        errors = "\n".join(self.check().errors)
        self.assertIn("mise.toml: [tools] must declare a maven version", errors)
        self.assertIn("expected `mise run its --runtime DEV` in the qa-dev job", errors)

    def test_unit_test_method_must_exist(self):
        self.modify(TEST_TARGET, "void razor()", "void razorSyntax()")
        self.assert_error("(LabelHasAssociatedControlCheckTest#razor) no longer exists")

    def test_commands_require_public_tasks_and_no_raw_maven(self):
        self.modify("AGENTS.md", "`its`", "`mvn verify`")
        self.modify("AGENTS.md", "`ruling:check`", "`mise run ruling:typo`")
        errors = "\n".join(self.check().errors)
        self.assertIn("Commands is missing mise task `its`", errors)
        self.assertIn("Commands must use mise tasks instead of raw Maven commands", errors)
        self.assertIn("Commands refers to an unknown task `ruling:typo`", errors)

    def test_required_sections_must_be_ordered(self):
        self.modify("AGENTS.md", "## Commands", "## Build")
        self.assert_error("start with Scope, Commands, Constraints and Documentation")

    def test_exact_import_requires_sibling_and_no_other_content(self):
        self.put("CLAUDE.md", "@AGENTS.md\n\nAdditional instructions\n")
        self.put(".claude/CLAUDE.md", "@AGENTS.md\n")
        self.put("nested/AGENTS.md", "# Nested instructions\n")
        errors = "\n".join(self.check().errors)
        self.assertIn("\nCLAUDE.md: must contain only @AGENTS.md", "\n" + errors)
        self.assertIn("nested/CLAUDE.md: must contain only @AGENTS.md", errors)
        self.assertIn(".claude/CLAUDE.md: unexpected CLAUDE.md", errors)
        self.assertIn("nested/AGENTS.md: not reachable", errors)

    def test_limits_count_managed_blocks_and_utf8_bytes(self):
        self.modify("AGENTS.md", VORTEX_END, ("é" * 4100 + "\n") + "managed\n" * 151 + VORTEX_END)
        self.put("nested/AGENTS.md", "# Nested\n\n" + "é" * 2049 + "\n")
        self.put("nested/CLAUDE.md", "@AGENTS.md\n")
        self.doc("docs/systems/how to.md", "# Local checks-1\n\n" + "line\n" * 300)
        errors = "\n".join(self.check().errors)
        self.assertIn("\nAGENTS.md: exceeds the 8 KiB limit", "\n" + errors)
        self.assertIn("\nAGENTS.md: exceeds the 150-line limit", "\n" + errors)
        self.assertIn("nested/AGENTS.md: exceeds the 4 KiB limit", errors)
        self.assertIn("how to.md: exceeds the 300-line limit", errors)

    def test_adapter_requires_matching_canonical_stub(self):
        wrong = ".agents/skills/some-other-skill/SKILL.md"
        self.put(wrong, "# Other skill\n")
        self.modify(CLAUDE_SKILL, CANONICAL, wrong)
        self.modify(CLAUDE_SKILL.replace(".claude/", ".cursor/"), CANONICAL, CANONICAL + "#rule-metadata")
        errors = "\n".join(self.check().errors)
        self.assertEqual(2, errors.count("must be a Markdown stub containing one link"))

    def test_cli_returns_nonzero_for_invalid_documentation(self):
        self.modify("docs/index.md", '"0.2"', '"0.1"')
        with (
            patch.object(check_docs, "__file__", self.root / "tools/check_docs.py"),
            patch.object(check_docs, "git_paths", return_value=self.fixture_paths()),
            redirect_stderr(StringIO()) as stderr,
        ):
            self.assertEqual(1, check_docs.main())
        self.assertIn("error: docs/index.md: frontmatter requires okf_version", stderr.getvalue())
