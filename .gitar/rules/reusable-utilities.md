---
title: Reuse existing utilities
description: Prefer existing SonarHTML utilities when reuse preserves behavior.
type: check
when: PRs that add or change SonarHTML implementation code
actions: Report duplicated utility logic and suggest a concrete behavior-preserving replacement.
---

# Reuse existing utilities

Check existing utility contracts before accepting duplicated logic or a new helper.
Inspect `sonar-html-plugin/src/main/java/org/sonar/plugins/html/api/Helpers.java`
for tag, ancestor, and template detection. Inspect
`sonar-html-plugin/src/main/java/org/sonar/plugins/html/api/accessibility/AccessibilityUtils.java`
for accessibility helpers and the affected package's utilities for local behavior.

For example, replace an ancestor loop with `Helpers.hasAncestorMatching(node, predicate)`
only if it starts at the parent and has the same predicate and stopping behavior.
Do not replace `Helpers.isDynamicValue(value, code)` with
`Helpers.containsDynamicValue(value, code)` without checking their different marker semantics.

Name the utility, its path, and a concrete replacement call or import. Explain why reuse
preserves null handling, case sensitivity, supported template syntax, traversal order,
and issue messages and locations. Similar names do not establish equivalent behavior.

Keep package boundaries. For example,
`sonar-html-plugin/src/main/java/org/sonar/plugins/html/checks/comments/CommentUtils.java`
is package-private; do not recommend it from another package or broaden its visibility
only to remove duplication. Keep intentionally different behavior. If equivalence is
uncertain, request focused evidence instead of presenting the replacement as safe.
