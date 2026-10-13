# HTML CI

## Ruling

Build, QA and ruling use the same commit: GitHub's synthetic merge for a PR.
QA and ruling install the packaged analyzer from that Build's artifact, using
its shared build number. They run against SonarQube DEV.

On a ruling mismatch, CI saves a complete `actual_html-<attempt>` snapshot before
creating an expectation-only fix PR against the original branch. Review and merge
that fix into your branch; the bot does not push to the author's branch. The original
ruling failure remains visible until a subsequent run passes. Successful runs close
obsolete owned fixes and refresh the report, including when rerun without a new push.

The report compares expectations from the exact merge's first parent with results
applied to that merge. It includes additions and deletions and preserves HTML source
links/snippets. See the [ruling bot contract](../.github/actions/ruling_bot/README.md)
for freshness, ownership, transfer-conflict handling and lifecycle guarantees.

For a local report, run `BASE_BRANCH=<tested-base-sha> tools/ruling-report.sh`.
This uses the same renderer and configuration as the independent reporter.

The updater, reporter and closed-PR cleanup share a mutation queue. Fork and
Dependabot PRs skip these jobs. Cleanup starts after its workflow reaches master.
Promotion and protected-branch notifications include updater failures.

### Retry a report

Use `gh run rerun <report-run-id> --repo SonarSource/sonar-html` to preserve the
original inputs. Reporting retries do not run ruling again. To dispatch independently,
use the original Build run ID, its current attempt, the exact tested merge and first
parent, the original branch and fix PR URL. Preserve the artifact attempt that actually
produced the snapshot: an updater-only retry can advance the Build attempt while
continuing to use `actual_html-1`.

<!-- BEGIN GENERATED RETRY COMMAND -->

```sh
gh workflow run ruling-diff-comment.yml --ref '<original-branch>' \
  -f 'pr-number=<original-pr-number>' \
  -f 'base-sha=<tested-merge-first-parent-sha>' \
  -f 'head-sha=<tested-merge-sha>' \
  -f 'run-id=<build-run-id>' \
  -f 'run-attempt=<current-build-run-attempt>' \
  -f ruling-failed=true \
  -f is-pull-request=true \
  -f 'fix-pr-url=<fix-pr-url>' \
  -f 'target-ref=<original-branch>' \
  -f 'results-artifact-name=actual_html-<producing-ruling-job-attempt>'
```

<!-- END GENERATED RETRY COMMAND -->

Use `ruling-failed=false` and an empty fix URL for a passing result. A branch run uses
its tested branch commit as both head and base and reports on its fix PR.
Expired artifacts or unavailable tested commits require a new ruling run.
Branches must contain the current reporter workflow and bot before dispatching it.

### Validate bot changes

```sh
node --test .github/actions/ruling_bot/tests/*.test.mjs
node .github/actions/ruling_bot/generate-config.mjs --check
python3 tools/check-ruling-workflows.py
git diff --check
```

The Node suite uses temporary Git repositories and mocked GitHub responses.
The workflow check requires PyYAML. These checks do not publish fixes or comments.
