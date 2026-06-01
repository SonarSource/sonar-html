/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.lex;

import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sonar.plugins.html.lex.PhpEmbeddedHtmlExtractor.StringLiteral;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.NodeType;
import org.sonar.plugins.html.node.TagNode;

import static org.assertj.core.api.Assertions.assertThat;

class PhpEmbeddedHtmlExtractorTest {

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  private static DirectiveNode directive(String nodeName, String code, int startLine, int startCol) {
    DirectiveNode node = new DirectiveNode();
    node.setNodeName(nodeName);
    node.setCode(code);
    node.setStartLinePosition(startLine);
    node.setStartColumnPosition(startCol);
    return node;
  }

  private static DirectiveNode phpDirective(String body) {
    return directive("?php", "<?php " + body + " ?>", 1, 0);
  }

  // ---------------------------------------------------------------------------
  // isPhpDirective
  // ---------------------------------------------------------------------------

  @Test
  void isPhpDirectiveAcceptsPhpNames() {
    assertThat(PhpEmbeddedHtmlExtractor.isPhpDirective(directive("?php", "<?php ?>", 1, 0))).isTrue();
    assertThat(PhpEmbeddedHtmlExtractor.isPhpDirective(directive("?PHP", "<?PHP ?>", 1, 0))).isTrue();
    assertThat(PhpEmbeddedHtmlExtractor.isPhpDirective(directive("?=", "<?= 1 ?>", 1, 0))).isTrue();
    // PHP 8 named args, attributes – any "?php..." variant
    assertThat(PhpEmbeddedHtmlExtractor.isPhpDirective(directive("?phpecho", "<?phpecho ?>", 1, 0))).isTrue();
  }

  @Test
  void isPhpDirectiveRejectsNonPhp() {
    assertThat(PhpEmbeddedHtmlExtractor.isPhpDirective(directive("?xml", "<?xml ?>", 1, 0))).isFalse();
    assertThat(PhpEmbeddedHtmlExtractor.isPhpDirective(directive(null, "<?xml ?>", 1, 0))).isFalse();
    assertThat(PhpEmbeddedHtmlExtractor.isPhpDirective(directive("", "<??>", 1, 0))).isFalse();
    assertThat(PhpEmbeddedHtmlExtractor.isPhpDirective(directive("%@", "<%@ page %>", 1, 0))).isFalse();
  }

  // ---------------------------------------------------------------------------
  // extractLiterals – double-quoted strings
  // ---------------------------------------------------------------------------

  @Test
  void doubleQuotedLiteralDecodesEscapes() {
    DirectiveNode node = phpDirective("echo \"<div role=\\\"toolbar\\\">\"");
    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(1);
    assertThat(literals.get(0).value()).isEqualTo("<div role=\"toolbar\">");
  }

  @Test
  void doubleQuotedLiteralDecodesCommonEscapes() {
    DirectiveNode node = phpDirective("$x = \"line1\\nline2\\ttab\";");
    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(1);
    assertThat(literals.get(0).value()).isEqualTo("line1\nline2\ttab");
  }

  // ---------------------------------------------------------------------------
  // extractLiterals – single-quoted strings
  // ---------------------------------------------------------------------------

  @Test
  void singleQuotedLiteralOnlyUnescapesBackslashAndQuote() {
    // In single-quoted strings only \\ and \' are escapes; \n is literal
    DirectiveNode node = phpDirective("echo '<div role=\\'toolbar\\'>';");
    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(1);
    assertThat(literals.get(0).value()).isEqualTo("<div role='toolbar'>");
  }

  @Test
  void singleQuotedLiteralKeepsBackslashNLiteral() {
    DirectiveNode node = phpDirective("'a\\nb'");
    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(1);
    // \n inside single-quoted string is NOT a newline escape
    assertThat(literals.get(0).value()).isEqualTo("a\\nb");
  }

  // ---------------------------------------------------------------------------
  // extractLiterals – concatenation
  // ---------------------------------------------------------------------------

  @Test
  void concatenationEmitsTwoLiteralsAtDistinctColumns() {
    // Two separate string literals joined with .
    DirectiveNode node = phpDirective("\"<a \" . \"href='x'>\"");
    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(2);
    assertThat(literals.get(0).value()).isEqualTo("<a ");
    assertThat(literals.get(1).value()).isEqualTo("href='x'>");
    assertThat(literals.get(1).columnOffset()).isGreaterThan(literals.get(0).columnOffset());
  }

  // ---------------------------------------------------------------------------
  // extractLiterals – short-echo directive
  // ---------------------------------------------------------------------------

  @Test
  void shortEchoDirectiveBodyIsScannedForLiterals() {
    // <?= can contain string literals too
    DirectiveNode node = directive("?=", "<?= \"<div role='toolbar'></div>\" ?>", 1, 0);
    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(1);
    assertThat(literals.get(0).value()).isEqualTo("<div role='toolbar'></div>");
  }

  // ---------------------------------------------------------------------------
  // extractLiterals – multiple literals in one directive
  // ---------------------------------------------------------------------------

  @Test
  void multipleLiteralsFromOneDirective() {
    DirectiveNode node = phpDirective("echo \"<div></div>\"; print \"<span></span>\";");
    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(2);
    assertThat(literals.get(0).value()).isEqualTo("<div></div>");
    assertThat(literals.get(1).value()).isEqualTo("<span></span>");
  }

  // ---------------------------------------------------------------------------
  // extractLiterals – interpolation sanitisation
  // ---------------------------------------------------------------------------

  @Test
  void phpVariablesAreSanitizedInDoubleQuotedStrings() {
    DirectiveNode node = phpDirective("\"$var<div>\"");
    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(1);
    String sanitized = PhpEmbeddedHtmlExtractor.sanitizeInterpolations(literals.get(0).value());
    assertThat(sanitized).contains("${dynamic}");
    assertThat(sanitized).doesNotContain("$var");
  }

  @Test
  void phpCurlyInterpolationIsSanitized() {
    DirectiveNode node = phpDirective("\"<div>{$expr}</div>\"");
    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(1);
    String sanitized = PhpEmbeddedHtmlExtractor.sanitizeInterpolations(literals.get(0).value());
    assertThat(sanitized).isEqualTo("<div>${dynamic}</div>");
  }

  // ---------------------------------------------------------------------------
  // extractLiterals – multi-line literal positions
  // ---------------------------------------------------------------------------

  @Test
  void multiLineLiteralPositionsAreAccurate() {
    // Directive starts at line 5, column 0
    // Code is: <?php echo "\n<div role='toolbar'>" ?>
    // The literal content starts at line 5, after the opening "
    DirectiveNode node = new DirectiveNode();
    node.setNodeName("?php");
    node.setCode("<?php echo \"\\n<div>\"; ?>");
    node.setStartLinePosition(5);
    node.setStartColumnPosition(0);

    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(1);
    // The literal content itself starts on line 5 (same line as directive)
    // because we record the position of the first content char after the opening quote
    assertThat(literals.get(0).lineOffset()).isEqualTo(5);
  }

  // ---------------------------------------------------------------------------
  // extractLiterals – heredoc
  // ---------------------------------------------------------------------------

  @Test
  void heredocBodyIsEmittedAtHeredocBodyLine() {
    // Directive at line 3; heredoc body starts on line 4
    String code = "<?php\n$x = <<<EOT\n<div role=\"toolbar\"></div>\nEOT;\n?>";
    DirectiveNode node = new DirectiveNode();
    node.setNodeName("?php");
    node.setCode(code);
    node.setStartLinePosition(3);
    node.setStartColumnPosition(0);

    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(1);
    assertThat(literals.get(0).value()).contains("<div role=\"toolbar\">");
    // Body starts on line 4 (directive line 3 + 1 for the <<<EOT line)
    assertThat(literals.get(0).lineOffset()).isEqualTo(5);
  }

  @Test
  void nowdocDoesNotSanitizeInterpolations() {
    String code = "<?php\n$x = <<<'EOT'\n<div>{$nope}</div>\nEOT;\n?>";
    DirectiveNode node = new DirectiveNode();
    node.setNodeName("?php");
    node.setCode(code);
    node.setStartLinePosition(1);
    node.setStartColumnPosition(0);

    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(1);
    // Nowdoc: no interpolation, {$nope} is literal text
    assertThat(literals.get(0).value()).contains("{$nope}");
    assertThat(literals.get(0).value()).doesNotContain("${dynamic}");
  }

  @Test
  void indentedHeredocStripsClosingIndentFromAllLines() {
    // PHP 7.3+ indented heredoc
    String code = "<?php\n$x = <<<EOT\n    <div>\n    </div>\n    EOT;\n?>";
    DirectiveNode node = new DirectiveNode();
    node.setNodeName("?php");
    node.setCode(code);
    node.setStartLinePosition(1);
    node.setStartColumnPosition(0);

    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(1);
    String body = literals.get(0).value();
    // Each body line should have the 4-char indent stripped
    assertThat(body).contains("<div>");
    assertThat(body).doesNotContain("    <div>");
  }

  // ---------------------------------------------------------------------------
  // sanitizeInterpolations
  // ---------------------------------------------------------------------------

  @Test
  void sanitizeInterpolationsReplacesPatterns() {
    assertThat(PhpEmbeddedHtmlExtractor.sanitizeInterpolations("$var")).isEqualTo("${dynamic}");
    assertThat(PhpEmbeddedHtmlExtractor.sanitizeInterpolations("{$expr}")).isEqualTo("${dynamic}");
    assertThat(PhpEmbeddedHtmlExtractor.sanitizeInterpolations("$obj->prop")).isEqualTo("${dynamic}->prop");
    assertThat(PhpEmbeddedHtmlExtractor.sanitizeInterpolations("no-vars")).isEqualTo("no-vars");
  }

  // ---------------------------------------------------------------------------
  // expand – end-to-end pipeline integration
  // ---------------------------------------------------------------------------

  @Test
  void expandSplicesTagNodesAfterDirective() {
    List<Node> nodes = new PageLexer().parse(new StringReader("<?php echo \"<div role='toolbar'>\"; ?>"));
    // The flat list should contain the <?php directive plus the embedded <div>
    boolean hasDirective = nodes.stream().anyMatch(n -> n.getNodeType() == NodeType.DIRECTIVE);
    boolean hasEmbeddedDiv = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TAG)
      .map(n -> (TagNode) n)
      .anyMatch(t -> "div".equalsIgnoreCase(t.getNodeName()));
    assertThat(hasDirective).isTrue();
    assertThat(hasEmbeddedDiv).isTrue();
  }

  @Test
  void expandPreservesDirectiveNode() {
    List<Node> nodes = new PageLexer().parse(new StringReader("<?php echo \"<span id='x'>\"; ?>"));
    long directiveCount = nodes.stream().filter(n -> n.getNodeType() == NodeType.DIRECTIVE).count();
    assertThat(directiveCount).isEqualTo(1);
  }

  @Test
  void expandPositionsAreFileAbsolute() {
    // Single-line file: directive at line 1, literal starts right after opening quote
    // "<?php echo \"<div>\";"  — the <div> must appear at line 1
    List<Node> nodes = new PageLexer().parse(new StringReader("<?php echo \"<div>\"; ?>"));
    TagNode div = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TAG)
      .map(n -> (TagNode) n)
      .filter(t -> "div".equalsIgnoreCase(t.getNodeName()))
      .findFirst().orElse(null);
    assertThat(div).isNotNull();
    assertThat(div.getStartLinePosition()).isEqualTo(1);
  }

  @Test
  void expandPositionIsRebasedToDirectiveLine() {
    // File: line 1 = <html>, line 2 = <?php directive with embedded <div>
    // The embedded <div> must appear at line 2 in the merged node list
    String source = "<html>\n<?php echo \"<div role='toolbar'>\"; ?>";
    List<Node> nodes = new PageLexer().parse(new StringReader(source));
    TagNode div = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TAG)
      .map(n -> (TagNode) n)
      .filter(t -> "div".equalsIgnoreCase(t.getNodeName()))
      .findFirst().orElse(null);
    assertThat(div).isNotNull();
    assertThat(div.getStartLinePosition()).isEqualTo(2);
  }

  @Test
  void expandNonPhpDirectiveNotExpanded() {
    List<Node> nodes = new PageLexer().parse(new StringReader("<?xml version=\"1.0\" ?><root/>"));
    long tagCount = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TAG)
      .map(n -> (TagNode) n)
      .filter(t -> "root".equalsIgnoreCase(t.getNodeName()))
      .count();
    // Only the <root/> tag, no phantom extraction from the XML declaration
    assertThat(tagCount).isEqualTo(1);
  }
}
