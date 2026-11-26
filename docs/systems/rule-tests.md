---
title: Rule tests
description: Choose a fixture and test helper that exercise the rule's actual parser and verify its issues.
generated:
  by: sonar-tirion
---

# Rule tests

- Put fixtures under `sonar-html-plugin/src/test/resources/checks/` in the directory for the check. Cover both the issue and a nearby compliant case; use the real template suffix if the behavior depends on it.
- Use `sonar-html-plugin/src/test/java/org/sonar/plugins/html/checks/TestHelper.java` to scan with the check under test. Use `sonar-html-plugin/src/test/java/org/sonar/plugins/html/checks/CheckMessagesVerifierRule.java` to assert reported locations and messages, ending with `noMore()` so extra issues fail the test.
- `sonar-html-plugin/src/test/java/org/sonar/plugins/html/checks/accessibility/LabelHasAssociatedControlCheckTest.java` shows the use of an actual `.cshtml` fixture to exercise Razor text. Check [template boundaries](parsing.md) before assuming a fixture creates a particular node type.

See [Commands](../../AGENTS.md#commands) for running a class or a single method from the root.
