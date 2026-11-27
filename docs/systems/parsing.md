---
title: Parsing HTML templates
description: Distinguish what the HTML lexer recognizes from constructs that tests or rules must handle explicitly.
generated:
  by: sonar-tirion
---

# Parsing HTML templates

- `sonar-html-plugin/src/main/java/org/sonar/plugins/html/lex/PageLexer.java` lexes markup, not embedded C#. Razor such as `@Html.TextBoxFor(...)` in a text position stays a `TextNode`, not an `ExpressionNode`; handle recognized forms in text when a rule needs them. JSP constructs with `<% ... %>` have explicit delimiters and an expression tokenizer, so JSP expressions are `ExpressionNode`s.
- `sonar-html-plugin/src/main/java/org/sonar/plugins/html/checks/coding/UnclosedTagCheck.java` skips `.cshtml`: Razor can split the document into branches that don't look balanced to a markup-only check. Preserve this guard unless there is a test-backed way to avoid those false positives.
- `sonar-html-plugin/src/main/java/org/sonar/plugins/html/lex/VueLexer.java` gives HTML rules the first `<template>` body, including nested templates, not the wrapper or top-level script and style blocks. Those blocks are outside the markup being checked.
- `sonar-html-plugin/src/main/java/org/sonar/plugins/html/core/ErbFileFilter.java` requires evidence of HTML because ERB also produces non-HTML files. It accepts recognized double extensions and otherwise sniffs the file prefix for markup after removing ERB blocks.

For file selection, check `FILE_EXTENSIONS_DEF_VALUE`, `JSP_FILE_EXTENSIONS_DEF_VALUE` and `OTHER_FILE_SUFFIXES` in `sonar-html-plugin/src/main/java/org/sonar/plugins/html/api/HtmlConstants.java`; the sensor can analyze some extensions outside the `web` and `jsp` language defaults.

When changing these boundaries, cover the relevant template syntax rather than testing HTML alone; see [Rule tests](rule-tests.md).
