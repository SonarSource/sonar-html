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

## Verification before merge

Read [Verification](docs/systems/verification.md) before reporting check results, reviewing a PR or assessing release readiness.

## External context

- [SonarSource/rspec](https://github.com/SonarSource/rspec) is the source of rule specs. With repository access, look under `rules/<S-key>/html` for the HTML variant and in its parent folder for shared metadata.
- For **Unify Ruling Workflows Across Code Quality Repositories** and its Confluence retrieval path, see [Integration tests](docs/systems/integration-tests.md#shared-ruling-reference).

<!-- sonar:begin:sonarqube-agentic-analysis-protocol -->
# Vortex analysis protocol

Vortex analysis is the final confirmation layer at the end of every turn in which you wrote to one or more files in the workspace (create, edit, patch, format — any tool call that changed file contents on disk).

**Trigger:** the moment before you send your final reply for the turn.

When the trigger fires, you **MUST** run Vortex analysis on the full set of files you modified this turn — **one command, not a per-file loop**.

**Preferred:** pass every path you modified this turn in a single invocation (repeat `--file` for each path). Use paths **relative to the project root** (e.g. `src/foo.ts`). For end-of-turn analysis, **always** pass `--depth DEEP` — even when you changed only one file — so cross-file findings are included.

```bash
sonar analyze agentic --project org.sonarsource.html:html --depth DEEP --file <path/to/file1> --file <path/to/file2>
```

**Fallback:** when you cannot reliably list every modified path (e.g. you did not track them, or git state is the only source of truth), use the git change set with DEEP analysis:

```bash
sonar analyze agentic --project org.sonarsource.html:html --depth DEEP
```

Per-edit hooks run faster STANDARD analysis. End-of-turn analysis must always use `--depth DEEP` (including a single `--file`). Use `--depth STANDARD` only for optional faster passes — never for the mandatory end-of-turn run.

Non-negotiable rules:

1. Mid-turn edits do **not** need Vortex analysis — only the final on-disk state matters. Skipping Vortex analysis at end-of-turn is a protocol violation.
2. Run Vortex analysis **before** sending your final reply, marking the task done, or handing control back to the user.
3. If Vortex analysis reports issues on lines you touched in this turn, fix them, then re-run Vortex analysis on the same scope (change set or explicit file list). Repeat until clean (or only pre-existing findings on lines you did not touch remain). Pre-existing findings on untouched lines are out of scope — do not "fix" them unless the user asked.
4. If Vortex analysis is skipped (no SonarQube Cloud connection, or no project configured), state the skip reason to the user once and continue — do not retry.
5. Do not suppress, summarize away, or omit Vortex analysis findings from your reply. Surface them verbatim.
<!-- sonar:end:sonarqube-agentic-analysis-protocol -->
