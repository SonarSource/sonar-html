---
title: Integration and ruling tests
description: Reproduce the credentialed QA jobs and review changes to ruling expectations.
generated:
  by: sonar-tirion
sources:
  - resource: https://xtranet-sonarsource.atlassian.net/wiki/spaces/CodeQualit/pages/5767266367
---

# Integration and ruling tests

`its/pom.xml` defines the `qa` profile. The QA suites in `its/plugin/` and `its/sonarlint-tests/` cover SonarQube Server and SonarQube for IDE respectively; `its/ruling/` compares analyzer issues on real sources against checked-in results.

## Run locally

1. Build and install the current plugin and use the [root commands](../../AGENTS.md#commands). The QA profile resolves a plugin from Maven and copies it into the analyzer's build output; a stale installed SNAPSHOT can test older code.
2. Configure Maven for Repox read access. `.github/workflows/build.yml` gets `ARTIFACTORY_URL`, `ARTIFACTORY_ACCESS_TOKEN` and the read-only GitHub license token (`GITHUB_TOKEN`) from Vault. Get authorized credentials before running Enterprise QA; do not print them or copy them into the checkout.
3. Orchestrator loads `~/.sonar/orchestrator/orchestrator.properties` before the environment for the license token. If the license fetch fails, check for a stale `github.token` there before assuming `GITHUB_TOKEN` is invalid.
4. Before ruling, initialize the `its/sources/project/` submodule (`SonarCommunity/web-test-sources`) at the revision pinned by this repository. Report missing access rather than analyzing substitute sources.

## Review ruling differences

`its/ruling/src/test/resources/expected/project/` holds the expected SARIF files. When ruling fails, run `./tools/ruling-debug-script.sh` from the root to compare them with the generated SARIF under the ruling module's `target` directory. Review changes in issue locations and messages; update the expectations only for intended analyzer changes. The `ruling-update-and-notify` action in the build workflow can comment on the PR or propose refreshed expectations.

## Shared ruling reference

[Unify Ruling Workflows Across Code Quality Repositories](https://xtranet-sonarsource.atlassian.net/wiki/spaces/CodeQualit/pages/5767266367) (Confluence, CodeQualit space, page `5767266367`, access required) explains the shared SARIF and ruling update workflow. Use this repository's CI and ruling test as the authority for the local paths and jobs.
