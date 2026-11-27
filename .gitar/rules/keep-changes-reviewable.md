---
title: Keep changes reviewable
description: Check that reporting changes serve the PR purpose and have supporting evidence.
type: check
when: PRs that change SonarHTML analysis or issue reporting
actions: Report unrelated reporting changes and request a rationale and focused regression evidence.
---

# Keep changes reviewable

Compare reporting changes with the PR purpose in its title, description, and linked issue.
Inspect issue detection, messages, primary and secondary locations, rule activation,
and expected ruling results. A small diff can change analysis behavior.

For a reporting change outside that purpose, identify the changed behavior and its path.
Request a rationale and focused regression evidence for the intended result and for
preserved behavior outside the requested change. Include affected HTML, JSP, Razor,
Vue, or other supported template syntax where relevant. An updated fixture or ruling
expectation alone does not explain why a behavior change is correct.

Recommend a separate PR for independent behavior changes. Check the purpose of tests,
fixtures, rule metadata, and ruling expectations before treating them as unrelated.
Several files can support one coherent fix. Preserve necessary supporting changes.

If the PR purpose is unclear, ask for clarification before declaring a reporting change
out of scope. Keep comments specific to observable behavior and actionable evidence.
