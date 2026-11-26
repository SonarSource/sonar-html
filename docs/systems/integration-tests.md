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

1. Configure Maven for Repox read access. `.github/workflows/build.yml` gets `ARTIFACTORY_URL`, `ARTIFACTORY_ACCESS_TOKEN` and the read-only GitHub license token (`GITHUB_TOKEN`) from Vault. Get authorized credentials before running Enterprise QA; do not print them or copy them into the checkout. Check prerequisites with `mise run doctor`.
2. For Enterprise tests, Orchestrator reads `github.token` from `~/.sonar/orchestrator/orchestrator.properties` before `GITHUB_TOKEN`. Replace or remove a stale property even if the environment token is valid.
3. Run `mise run build:plugin` from this checkout before local QA and ruling. The QA profile copies the plugin from Maven into the analyzer's build output; a stale shared SNAPSHOT can test older code. Rebuild if another checkout installed the same SNAPSHOT.
4. Run `mise run its` for both QA suites, `mise run its:sonarqube` for Server, or `mise run its:sonarlint` for IDE. Server defaults to `LATEST_RELEASE`; use `mise run its:sonarqube --runtime DEV` to override it (`its` also accepts `--runtime`).
5. Before `mise run ruling:check`, run `git submodule update --init -- its/sources/project` to check out the pinned `SonarCommunity/web-test-sources` revision. Report missing access rather than analyzing substitute sources.

## Review ruling differences

`its/ruling/src/test/resources/expected/project/` holds the expected SARIF files. After a ruling run produces reports, run `mise run ruling:diff` to compare the generated and expected SARIF. Review the issue locations and messages, then run `mise run ruling:update` only if you intend to accept those results. Inspect the Git diff and rerun `mise run ruling:check` to verify the new expectations. The `ruling-update-and-notify` action in the build workflow can comment on the PR or propose refreshed expectations.

## Shared ruling reference

[Unify Ruling Workflows Across Code Quality Repositories](https://xtranet-sonarsource.atlassian.net/wiki/spaces/CodeQualit/pages/5767266367) (Confluence, CodeQualit space, page `5767266367`, access required) explains the shared SARIF and ruling update workflow. Use this repository's CI and ruling test as the authority for the local paths and jobs.
