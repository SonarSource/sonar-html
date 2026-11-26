import io
from contextlib import redirect_stdout
from pathlib import Path
from tempfile import TemporaryDirectory
import unittest

from ruling_results import sync_results


class RulingResultsTest(unittest.TestCase):
    def setUp(self):
        workspace = TemporaryDirectory()
        self.addCleanup(workspace.cleanup)
        root = Path(workspace.name)
        self.expected = root / "expected"
        self.actual = root / "actual"
        self.differences = root / "differences"
        self.setup_reports(self.expected, {"changed.sarif": "old", "deleted.sarif": "gone", "README": "keep"})
        self.setup_reports(self.actual, {"changed.sarif": "new", "added.sarif": "added", "other.txt": "ignore"})

    def setup_reports(self, directory, files):
        directory.mkdir()
        for name, content in files.items():
            (directory / name).write_text(content, encoding="utf-8")

    def run_task(self, operation):
        with redirect_stdout(io.StringIO()) as output:
            sync_results(operation, self.expected, self.actual, self.differences)
        return output.getvalue()

    def test_diff_shows_added_changed_and_removed_reports_without_writing(self):
        self.differences.touch()
        output = self.run_task("diff")
        for line in ("+added", "-old", "+new", "-gone"):
            self.assertIn(line, output)
        self.assertEqual((self.expected / "changed.sarif").read_text(encoding="utf-8"), "old")

    def test_update_syncs_only_sarif_reports(self):
        self.differences.touch()
        self.run_task("update")
        self.assertEqual(sorted(p.name for p in self.expected.iterdir()), ["README", "added.sarif", "changed.sarif"])
        self.assertEqual((self.expected / "changed.sarif").read_text(encoding="utf-8"), "new")

    def test_incomplete_results_are_rejected(self):
        with self.assertRaisesRegex(ValueError, "No completed ruling result"):
            self.run_task("update")
        self.assertTrue((self.expected / "deleted.sarif").is_file())
        self.differences.touch()
        for report in self.actual.glob("*.sarif"):
            report.unlink()
        with self.assertRaisesRegex(ValueError, "No completed ruling result"):
            self.run_task("update")
