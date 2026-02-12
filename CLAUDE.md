# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SonarQube HTML Plugin - A static code analyzer for HTML, JSP, and related template languages (CSHTML, Vue, PHP, etc.). The plugin provides rules for code quality, accessibility (WCAG/ARIA compliance), and security.

## Build Commands

```bash
# Build and run all unit tests
mvn clean install

# Run a single test class
mvn test -Dtest=LabelHasAssociatedControlCheckTest -pl sonar-html-plugin

# Run a specific test method
mvn test -Dtest=LabelHasAssociatedControlCheckTest#razor -pl sonar-html-plugin

# Integration tests (requires submodule init first)
git submodule init && git submodule update
mvn verify -Pits

# Plugin integration tests only
cd its/plugin && mvn verify

# Ruling tests only (compares analysis results against expected issues)
cd its/ruling && mvn verify
```

## Architecture

### Lexer/Parser Pipeline
Files are parsed by `PageLexer` which uses a chain of tokenizers to produce a list of `Node` objects:
- `CommentTokenizer` - HTML (`<!-- -->`) and JSP (`<%-- --%>`) comments
- `DirectiveTokenizer` - XML (`<? ?>`) and JSP (`<%@ %>`) directives
- `ExpressionTokenizer` - JSP expressions (`<% %>`)
- `NormalElementTokenizer` - HTML/XML tags
- `TextTokenizer` - Everything else (including Razor `@...` syntax, which is NOT parsed as expressions)

Node types: `TagNode`, `TextNode`, `CommentNode`, `DirectiveNode`, `ExpressionNode`

### Rule Implementation
Rules extend `AbstractPageCheck` and implement visitor methods:
- `startElement(TagNode)` / `endElement(TagNode)` - Tag open/close
- `characters(TextNode)` - Text content
- `comment(CommentNode)`, `directive(DirectiveNode)`, `expression(ExpressionNode)`
- `startDocument(List<Node>)` / `endDocument()` - Document boundaries

Rules are annotated with `@Rule(key = "S1234")` and discovered via reflection in `CheckClasses`.

### Rule Metadata
Each rule has two resource files in `src/main/resources/org/sonar/l10n/web/rules/Web/`:
- `{RuleKey}.json` - Rule metadata (title, severity, tags, type)
- `{RuleKey}.html` - Rule description (why, how to fix, examples)

### Test Pattern
Tests use `TestHelper.scan()` with `CheckMessagesVerifierRule`:
```java
@Test
void testName() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/MyCheck/test.html"),
        new MyCheck());
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(5).withMessage("Expected message")
        .noMore();
}
```

Test files go in `src/test/resources/checks/{CheckName}/`.

## Key Limitations

- **Razor syntax** (`@Html.TextBoxFor`, `@if`, etc.) is parsed as plain `TextNode`, not as `ExpressionNode`. Rules needing Razor support must detect patterns in text content.
- **JSP expressions** (`<% %>`) ARE parsed as `ExpressionNode`.
- **Vue files** use `VueLexer` which extracts the `<template>` section.

## Supported File Extensions

HTML: `.html`, `.htm`, `.xhtml`, `.cshtml`, `.vbhtml`, `.aspx`, `.ascx`, `.rhtml`, `.erb`, `.shtm`, `.shtml`, `.cmp`, `.twig`
JSP: `.jsp`, `.jspf`, `.jspx`

## Syncing RSPEC Definitions

Rule metadata and descriptions are managed in the [RSPEC repository](https://github.com/SonarSource/rspec). To sync updates:

1. Download the rule-api JAR from [sonar-rule-api](https://github.com/SonarSource/sonar-rule-api?tab=readme-ov-file#usage)
2. From the repository root, run:
```bash
java -jar ../rule-api-2.18.0.5734.jar update
```

## Pull Requests

When creating PRs, add `quality-web-squad` as a reviewer (requires org prefix):
```bash
gh pr edit <PR_NUMBER> --add-reviewer SonarSource/quality-web-squad
```
