---
title: Verification before merge
description: Check the analyzer and documentation locally, interpret PR checks and identify the release gates.
generated:
  by: sonar-tirion
---

# Verification before merge

## Local checks

- Run the relevant unit tests from [Commands](../../AGENTS.md#commands), and build and verify the plugin before changing what is packaged. `sonar-html-plugin/pom.xml` minimizes the shaded JAR to avoid shipping unused dependency classes. Review dependency changes for both size and required classes, especially classes loaded indirectly that minimization may drop.
- For dependency changes run `mvn -pl sonar-html-plugin -Plicense-regeneration clean verify` and inspect the generated license files before committing them.
- For documentation changes run `python3 tools/check_docs.py` with the dependencies listed in `.github/workflows/documentation.yml`, and use that workflow's Markdown lint command.
- When files changed, follow the tool-managed Vortex block in [AGENTS.md](../../AGENTS.md) for local DEEP analysis. That check is not a substitute for CI or the quality gate.

## Pull request checks

`.github/workflows/build.yml` is the source of truth for the job conditions. Before merge, verify the applicable results for:

- `build` and `build-windows`: build and unit tests on Linux and Windows.
- `qa` and `ruling`: [credentialed integration and ruling suites](integration-tests.md). `sonar-html-plugin/src/main/java/org/sonar/plugins/html/core/HtmlSensor.java` uses product-specific paths to avoid relying on server-only APIs or outputs in the IDE (for example, it does not emit token output there). Run the IDE suite when touching that boundary.
- `NEXT analysis`: analysis on Next for `org.sonarsource.html:html`; check the quality gate as well as the job result.
- `Documentation`: structural checks and Markdown lint. Review repository-specific constraints using `.gitar/rules/` where Gitar PR review is enabled.
- For performance-sensitive changes, ask a repository member to trigger the `SonarSource/peachee-js` performance workflow with `/pvf`; see `.github/workflows/pvf-comment.yml`.

## Nightly and release checks

Scheduled builds add QA against SonarQube DEV, Cloud EU and US shadow analyses (`SonarSource_sonar-html`), and IRIS comparison with Next.

`promote` requires a non-cancelled workflow, successful Linux and Windows builds, QA and ruling, and a successful or skipped NEXT analysis. The `releasability` job then reports status on the applicable branches. Review these conditions in the workflow rather than assuming a successful local build is enough to release.
