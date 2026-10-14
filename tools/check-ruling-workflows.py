#!/usr/bin/env python3
"""Check the ruling handoff and mutation contract; requires PyYAML."""

import json
import os
from pathlib import Path
import subprocess
import tempfile
import xml.etree.ElementTree as ET

import yaml


ROOT = Path(__file__).resolve().parents[1]


def document(name):
    # BaseLoader retains GitHub's `on` key instead of treating it as YAML 1.1 true.
    return yaml.load((ROOT / name).read_text(), Loader=yaml.BaseLoader)


def step(job, identifier):
    return next(item for item in job["steps"] if item.get("id") == identifier)


build = document(".github/workflows/build.yml")
report = document(".github/workflows/ruling-diff-comment.yml")
cleanup = document(".github/workflows/ruling-fix-cleanup.yml")
jobs = build["jobs"]
producer, updater = jobs["ruling"], jobs["ruling-update"]

# Coverage must come from the tested checkout and reach analysis before scanning.
bot_tests = jobs["bot-tests"]
assert bot_tests["permissions"] == {"contents": "read"}
assert bot_tests["steps"][0]["with"]["persist-credentials"] == "false"
assert any(s.get("run") == "npm run coverage" for s in bot_tests["steps"])
coverage_upload = next(s for s in bot_tests["steps"] if s.get("name") == "Save ruling bot coverage")
analysis = jobs["analysis"]
assert "bot-tests" in analysis["needs"]
coverage_download = next(s for s in analysis["steps"] if s.get("name") == "Download ruling bot coverage")
assert coverage_upload["with"]["name"] == coverage_download["with"]["name"]
assert coverage_upload["with"]["if-no-files-found"] == "error"
report_path = coverage_download["with"]["path"] + "/lcov.info"
assert coverage_upload["with"]["path"] == report_path
properties = ET.parse(ROOT / "pom.xml").getroot().find("{*}properties")
lcov_path = properties.find("{*}sonar.javascript.lcov.reportPaths").text
assert lcov_path == "${maven.multiModuleProjectDirectory}/" + report_path
assert properties.find("{*}sonar.coverage.exclusions").text == ".github/actions/ruling_bot/tests/**"

assert producer["permissions"]["contents"] == "read"
checkout = producer["steps"][0]["with"]
assert checkout["ref"] == "${{ github.sha }}"
assert checkout["fetch-depth"] == "2"
assert checkout["persist-credentials"] == "false"
capture = step(producer, "tested_merge")
assert "HEAD^1" in capture["run"] and "HEAD^2" in capture["run"]
assert "head.sha" in capture["env"]["TESTED_PR_HEAD_SHA"]

for name in ("qa", "ruling"):
    job = jobs[name]
    assert job["env"]["BUILD_NUMBER"] == "${{ needs.build.outputs.build-number }}"
    download = next(s for s in job["steps"] if s.get("name") == "Download the tested build")
    assert download["with"]["name"] == "build-output-${{ needs.build.outputs.build-number }}"
    assert any(s.get("name") == "Install the tested analyzer" for s in job["steps"])

# Classifier JARs share the same prefix. Exercise the real install step with both
# a sources JAR and the packaged analyzer, then with the analyzer missing.
install = next(s["run"] for s in producer["steps"] if s.get("name") == "Install the tested analyzer")
with tempfile.TemporaryDirectory(prefix="html-ruling-plugin-") as tmp:
    root = Path(tmp)
    target = root / "sonar-html-plugin/target"
    target.mkdir(parents=True)
    (target / "sonar-html-plugin-3.33.0.9748-sources.jar").touch()
    plugin = target / "sonar-html-plugin-3.33.0.9748.jar"
    plugin.touch()
    commands = root / "commands"
    commands.mkdir()
    mvn = commands / "mvn"
    mvn.write_text('#!/bin/bash\nprintf "%s\\n" "$*" >> "$INSTALL_LOG"\n')
    mvn.chmod(0o755)
    log = root / "install.log"
    env = dict(os.environ, PATH=str(commands) + os.pathsep + os.environ["PATH"],
               PROJECT_VERSION="3.33.0.9748", INSTALL_LOG=str(log))
    subprocess.run(["bash", "-e", "-c", install], cwd=root, env=env, check=True)
    assert "-Dfile=sonar-html-plugin/target/sonar-html-plugin-3.33.0.9748.jar" in log.read_text()
    assert "sources.jar" not in log.read_text()
    plugin.unlink()
    before = log.read_text()
    assert subprocess.run(["bash", "-e", "-c", install], cwd=root, env=env).returncode != 0
    assert log.read_text() == before

assert "steps.ruling.outcome" in producer["outputs"]["ruling-outcome"]
assert "steps.save_ruling.outcome" in producer["outputs"]["ruling-results-saved"]
assert "GITHUB_RUN_ATTEMPT" in step(producer, "ruling_artifact")["run"]
assert step(producer, "save_ruling")["with"]["name"] == "${{ steps.ruling_artifact.outputs.name }}"
assert step(producer, "save_ruling")["with"]["path"] == "its/ruling/target/actual/"
# A missing comparison must not publish an empty snapshot. Completed comparisons
# with no issue files are valid and still retain an uploadable marker.
with tempfile.TemporaryDirectory(prefix="html-ruling-snapshot-") as tmp:
    prepare = step(producer, "prepare_ruling")["run"]
    assert subprocess.run(["bash", "-e", "-c", prepare], cwd=tmp).returncode != 0
    target = Path(tmp) / "its/ruling/target"
    target.mkdir(parents=True)
    (target / "differences").touch()
    subprocess.run(["bash", "-e", "-c", prepare], cwd=tmp, check=True)
    assert (target / "actual/ruling-results-complete.txt").is_file()
download = next(s for s in updater["steps"] if s.get("name") == "Download generated ruling results")
assert download["with"]["name"] == "${{ needs.ruling.outputs.results-artifact-name }}"
assert download["with"]["path"] == "its/ruling/target/actual/"
assert "ruling-results-saved" in download["if"]
assert "ruling-outcome" in updater["if"] and "ruling-results-saved" in updater["if"]
assert "!cancelled()" in updater["if"]
assert updater["steps"][0]["with"]["ref"] == "${{ github.sha }}"
assert "submodules" not in updater["steps"][0]["with"]

assert "ruling-update" in jobs["promote"]["needs"]
assert "needs.ruling-update.result" in jobs["promote"]["if"]
assert "analysis" in jobs["promote"]["needs"]
assert "bot-tests" in jobs["promote"]["needs"]
assert "needs.bot-tests.result == 'success'" in jobs["promote"]["if"]
assert "ruling-update" in jobs["notify-build-failure"]["needs"]
assert "bot-tests" in jobs["notify-build-failure"]["needs"]
assert jobs["releasability"]["needs"] == ["promote"]

report_job = report["jobs"]["ruling-diff-comment"]
assert updater["concurrency"]["cancel-in-progress"] == "false"
assert report_job["concurrency"]["cancel-in-progress"] == "false"
assert report_job["concurrency"]["queue"] == "max"
assert report["concurrency"]["cancel-in-progress"] == "false"
assert "inputs.head-sha || github.sha" in report["concurrency"]["group"]
for job in (updater, report_job, cleanup["jobs"]["cleanup"]):
    assert "ruling-bot-pr-" in job["concurrency"]["group"]
    assert job["concurrency"]["queue"] == "max"
    runtime = next(s for s in job["steps"] if s.get("uses", "").startswith("actions/setup-node@"))
    assert runtime["with"]["node-version"] == "22"
assert "head.repo.fork == false" in report_job["if"]
assert "dependabot" in report_job["if"]
assert cleanup["on"]["pull_request_target"]["types"] == ["closed"]
assert "default_branch" in cleanup["jobs"]["cleanup"]["steps"][0]["with"]["ref"]

# The generated dispatch schema must match the real receiving workflow.
definitions = json.loads(subprocess.check_output(
    ["node", "--input-type=module", "-e",
     "import {inputDefinitions} from './.github/actions/ruling_bot/config.mjs';"
     "import {provenanceDefinitions} from './.github/actions/ruling_bot/provenance.mjs';"
     "console.log(JSON.stringify({...inputDefinitions,...provenanceDefinitions}));"],
    cwd=ROOT, text=True,
))
inputs = report["on"]["workflow_dispatch"]["inputs"]
assert inputs.keys() == definitions.keys()
assert len(inputs) <= 25
for name, definition in definitions.items():
    required = bool(definition.get("required")) if "type" in definition else False
    assert inputs[name]["required"] == str(required).lower()
report_action = document(".github/actions/ruling_bot/report/action.yml")
assert all(name in report_action["inputs"] for name in inputs)
assert "${{ inputs.run-id }}" == report_action["runs"]["steps"][1]["with"]["run-id"]
assert any(s.get("name") == "Record ruling report dispatch" for s in updater["steps"])

print("Ruling workflow contract passed")
