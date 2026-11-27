"""Guard the local equivalent of the Linux/Windows builds that use the shared Maven action."""

import shlex
import tomllib
import unittest
from pathlib import Path
from xml.etree import ElementTree

import yaml

ROOT = Path(__file__).resolve().parents[1]
MAVEN_NS = {"m": "http://maven.apache.org/POM/4.0.0"}
SHARED_MAVEN = "SonarSource/ci-github-actions/build-maven@v2"


class MavenTaskParityTest(unittest.TestCase):
    def test_shared_builds_and_mise_run_the_same_plugin_unit_test_gate(self):
        tasks = tomllib.loads((ROOT / "mise.toml").read_text(encoding="utf-8"))["tasks"]
        workflow = yaml.safe_load((ROOT / ".github/workflows/build.yml").read_text(encoding="utf-8"))
        pom = ElementTree.parse(ROOT / "pom.xml")
        modules = [module.text for module in pom.findall("m:modules/m:module", MAVEN_NS)]
        request = shlex.split(tasks["test:plugin"]["run"])
        # Optional positional selector is absent for parity with the full CI test suite.
        self.assertEqual(request, ["mvn", "-B", "-T1C", "-pl", "sonar-html-plugin",
                                   "${usage_test:+-Dtest=$usage_test}", "test"])
        self.assertEqual(modules, [request[request.index("-pl") + 1]])
        self.assertEqual(shlex.split(tasks["build:plugin"]["run"]), ["mvn", "-B", "-T1C", "clean", "install"])
        for job in ("build", "build-windows"):
            with self.subTest(job=job):
                [step] = [step for step in workflow["jobs"][job]["steps"] if step.get("uses") == SHARED_MAVEN]
                options = step["with"]
                # build-maven v2 runs deploy, install or verify depending on the ref.
                # All reach validate, test and package. CI adds coverage on Linux and
                # owns the deploy, scanner and platform setup; don't replay these locally.
                self.assertFalse(options.get("skip-build", False))
                self.assertIn(options.get("working-directory", "."), ("", "."))
                extra_args = shlex.split(options["maven-args"])
                if job == "build":
                    extra_args.remove("-Pcoverage")
                self.assertEqual(extra_args, [request[2]])
                self.assertFalse(step.get("continue-on-error", False))


if __name__ == "__main__":
    unittest.main()
