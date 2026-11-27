# Scope

This is the SonarQube analyzer for HTML, JSP and HTML-based templates, shipped in SonarQube Server, Cloud and for IDE.
The root instructions cover `sonar-html-plugin/` and `its/`; there are no nested instructions. Rule specs, ruling sources and shared CI actions live upstream.
Analyzer changes can affect all three products and the HTML performance validation in `SonarSource/peachee-js`.

## Commands

Run from the repository root. Use Bash (Git Bash on Windows) and Python 3.9+ for the local tasks. Run `mise install`, then `mise tasks` to list commands and `mise run doctor` to check prerequisites.

- Fast loop: `mise run compile:plugin` or `mise run test:plugin:changed`, then `mise run lint:license-headers`, then `mise run analyze:changed` (Vortex, not NEXT upload).
- Run all unit tests with `mise run test:plugin`, or pass a class or method, e.g. `mise run test:plugin 'LabelHasAssociatedControlCheckTest#razor'`.
- Fix headers with `mise run format:license-headers`. Run `mise run check` for a clean, tested build; it runs `build:plugin`, which installs the artifact for QA and ruling.
- See [Integration tests](docs/systems/integration-tests.md) before running `its`, `its:sonarqube [--runtime <version>]`, `its:sonarlint`, `ruling:check`, `ruling:diff`, or `ruling:update`.
- With all prerequisites met, `mise run ci` runs the local build, QA, ruling, and Vortex gates without publishing.

## Constraints

- This repository, its agent files and its docs are public. There is no mirror or mirror exclusion rule. Keep secrets, customer data and non-public plans out of the repository.
- Never hand-edit the [generated rule resources](docs/systems/rule-metadata.md) under `sonar-html-plugin/src/main/resources/org/sonar/l10n/web/rules/Web/`. Change the RSPEC, then use the regeneration skill below.
- Pass the exact `@Rule(key = ...)` value to `rule-api`, including legacy names such as `ImgWithoutAltCheck`. With filename preservation, passing the S-key instead can create duplicate resources.
- Rule keys, rule repository key `Web`, language keys `web` and `jsp`, and plugin key `web` are public API. Do not change them without a migration that preserves existing profiles, installations and issues.
- The plugin JAR must stay within the min/max checked by `enforce-plugin-size` in `sonar-html-plugin/pom.xml`. Check the packaged JAR when changing dependencies or shading.
- When changing dependencies, regenerate `sonar-html-plugin/src/main/resources/licenses/THIRD_PARTY_LICENSES/` with the `license-regeneration` profile; see [Build verification](docs/systems/verification.md#local-checks).
- Use the SSALv1 source header from `LICENSE.txt`; the build checks source headers in Maven's `validate` phase.
- Preserve SonarQube for IDE behavior in `HtmlSensor`. Tests must work on Linux and Windows; do not assume path separators or line endings.
- QA and ruling require credentials. Without them, run the unit tests, and report QA/ruling as not run. For intentional ruling changes, review the [expected SARIF and debug tool](docs/systems/integration-tests.md#review-ruling-differences).
- The block in `README.md` between the `sonar-marketing` markers belongs to Marketing. Consult the Product Marketing Manager before changing it.
- Use `SONARHTML-<n> <summary>` as the PR title. A title without a Jira key causes `.github/workflows/PullRequestCreated.yml` to create a ticket.

## Documentation

- [Documentation index](docs/index.md) — find the relevant system or decision before starting work.
- [Documentation conventions](docs/documentation-conventions.md) — read before writing or moving docs.
- [Parsing](docs/systems/parsing.md) — read before changing the lexer, file selection or template handling.
- [Rule metadata](docs/systems/rule-metadata.md) and the [regeneration skill](.agents/skills/regenerate-rule-metadata/SKILL.md) — read before adding a rule or changing its metadata or default profile.
- [Rule tests](docs/systems/rule-tests.md) — read before changing a rule or its fixtures.
- [Integration tests](docs/systems/integration-tests.md) — read before running QA or updating ruling expectations.

## Escalation

The owner is `@SonarSource/code-quality-dynamic-languages-squad`, as recorded in `.github/CODEOWNERS`.
If required Vault or Repox access or the ruling submodule is unavailable, stop the affected check and report the blocker to the owner; do not substitute a mock for that check.
If the SonarQube CLI is not logged in to Next, stop the Vortex check and report the blocker.

## Verification before merge

Read [Verification](docs/systems/verification.md) before reporting check results, reviewing a PR or assessing release readiness.

## External context

- [SonarSource/rspec](https://github.com/SonarSource/rspec) is the source of rule specs. With repository access, look under `rules/<S-key>/html` for the HTML variant and in its parent folder for shared metadata.
- For **Unify Ruling Workflows Across Code Quality Repositories** and its Confluence retrieval path, see [Integration tests](docs/systems/integration-tests.md#shared-ruling-reference).

For the Vortex commands below, pass `--project org.sonarsource.html:html` to target the Next project.

<!-- sonar:begin:sonarqube-agentic-analysis-protocol -->
# Vortex analysis protocol

These instructions were installed by `sonar integrate` at the user's request. Vortex analysis checks the final change set for issues before you reply.

At the end of a turn that changed file contents in the workspace (create, edit, patch, or format), run Vortex analysis before your final reply or handoff. Analyze all files changed in the turn in one command.

Pass each modified path with `--file`, relative to the directory you run the command from (for example, `src/foo.ts`). Use `--depth DEEP` even for one file so the analysis includes cross-file findings.

```bash
sonar analyze agentic --depth DEEP --file <path/to/file1> --file <path/to/file2>
```

If you cannot reliably list every modified path, analyze the git change set with DEEP depth:

```bash
sonar analyze agentic --depth DEEP
```

Per-edit hooks use faster STANDARD analysis. The end-of-turn check uses DEEP depth, including when only one file changed.

In a repository with several Sonar projects (a monorepo), the directory you run the command from decides what is analyzed. From the monorepo root, one command analyzes every changed file against the project that owns it. From inside one project, only that project is analyzed: a `--file` path outside the current directory is rejected, and changes in other projects are listed as not analyzed. Run the command from a directory that contains every file changed this turn, usually the monorepo root. Files owned by no project are skipped in the change-set report and rejected as `--file` paths; leave them out rather than retrying.

After the analysis:

1. If no project is configured, stop analysis attempts for this session. A routine skip needs no mention in the final reply unless the user asks about Vortex analysis. If the user asks for Vortex analysis or configures a project, run the analysis again and report any error.
2. If analysis finds issues on lines you changed this turn, fix them and re-run the same scope. Continue until no touched-line findings remain. Leave findings on untouched lines to their existing owners unless the user asks you to address them.
3. If analysis is skipped for another reason (for example, no SonarQube Cloud connection), state the reason once in your reply and continue without retrying this session.
4. Include any remaining Vortex findings verbatim in your reply.
<!-- sonar:end:sonarqube-agentic-analysis-protocol -->

Do not fix expected Vortex findings in `sonar-html-plugin/src/test/resources/checks/**`, `its/sources/**`, or `its/ruling/src/test/resources/expected/**`.
