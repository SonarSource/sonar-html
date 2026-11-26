"""Review or accept the SARIF reports from a completed scanner run."""

import argparse
import difflib
import shutil
import sys
from pathlib import Path


RULING = Path(__file__).resolve().parents[1] / "its/ruling"
EXPECTED = RULING / "src/test/resources/expected/project"
ACTUAL = RULING / "target/actual/project"


def reports(directory):
    files = {}
    for path in sorted(directory.glob("*.sarif")):
        if path.is_symlink() or not path.is_file():
            raise ValueError(f"Not a regular SARIF file: {path}")
        files[path.name] = path
    return files


def show_diff(old, new):
    for name in sorted(old.keys() | new.keys()):
        before = old[name].read_text(encoding="utf-8").splitlines() if name in old else []
        after = new[name].read_text(encoding="utf-8").splitlines() if name in new else []
        for line in difflib.unified_diff(before, after, f"expected/{name}", f"actual/{name}", lineterm=""):
            print(line)


def sync_results(operation, expected, actual, differences):
    new = reports(actual)
    if not new or not differences.is_file():
        raise ValueError("No completed ruling result; run mise run ruling:check first.")
    old = reports(expected)
    if operation == "update":
        expected.mkdir(parents=True, exist_ok=True)
        for name, source in new.items():
            shutil.copyfile(source, expected / name)
        for name in old.keys() - new.keys():
            old[name].unlink()
        print("Updated ruling expectations. Review git diff and run mise run ruling:check.")
    else:
        show_diff(old, new)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("operation", choices=("diff", "update"))
    args = parser.parse_args()
    try:
        sync_results(args.operation, EXPECTED, ACTUAL, RULING / "target/differences")
    except (OSError, ValueError) as error:
        print(error, file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
