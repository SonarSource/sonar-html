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
  void doubleQuotedLiteralDecodesEscapesAsNonNewlinePlaceholders() {
    // \n / \r / \t in the source must NOT decode to real newlines/CRs in the
    // sanitised buffer, otherwise the re-lex line counter advances past the
    // literal's true source line and tokens land on the wrong file line.
    DirectiveNode node = phpDirective("$x = \"line1\\nline2\\ttab\";");
    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(1);
    assertThat(literals.get(0).value()).isEqualTo("line1 line2 tab");
    assertThat(literals.get(0).value()).doesNotContain("\n");
    assertThat(literals.get(0).value()).doesNotContain("\t");
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
    // Body starts on line 5 (directive line 3 + 1 for the "<?php\n" preamble + 1 for the "<<<EOT\n" header)
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

  // ---------------------------------------------------------------------------
  // Dynamic gap between concatenated literals
  // ---------------------------------------------------------------------------

  @Test
  void expandInsertsDynamicGapBetweenConcatenatedLiterals() {
    // `echo "<a href='x'>" . $label . "</a>";` — the runtime text from the
    // intervening PHP expression must be represented as a non-blank text node
    // between the open and close tags so content-sensitive checks don't see
    // an empty anchor.
    List<Node> nodes = new PageLexer().parse(new StringReader(
      "<?php echo \"<a href='x'>\" . $label . \"</a>\"; ?>"));
    boolean hasDynamicGap = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TEXT)
      .anyMatch(n -> n.getCode().contains("${dynamic}"));
    assertThat(hasDynamicGap).isTrue();
  }

  @Test
  void expandDoesNotCloseOpenTagBetweenConcatenatedLiterals() {
    // The directive-level balance must NOT fabricate a `</a>` between the
    // opening fragment and the gap text, otherwise the real `</a>` from the
    // second literal becomes orphan and the first `<a>` is left empty.
    List<Node> nodes = new PageLexer().parse(new StringReader(
      "<?php echo \"<a href='x'>\" . $label . \"</a>\"; ?>"));

    List<Node> tagsAndText = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TAG || n.getNodeType() == NodeType.TEXT)
      .filter(n -> {
        if (n.getNodeType() == NodeType.TEXT) {
          return n.getCode().contains("${dynamic}");
        }
        TagNode t = (TagNode) n;
        return "a".equalsIgnoreCase(t.getNodeName());
      })
      .toList();

    assertThat(tagsAndText).hasSize(3);
    assertThat(((TagNode) tagsAndText.get(0)).isEndElement()).isFalse();
    assertThat(tagsAndText.get(1).getNodeType()).isEqualTo(NodeType.TEXT);
    assertThat(((TagNode) tagsAndText.get(2)).isEndElement()).isTrue();
  }

  // ---------------------------------------------------------------------------
  // Void elements never get a synthetic close tag
  // ---------------------------------------------------------------------------

  @Test
  void voidElementInLiteralProducesNoSyntheticEndTag() {
    // `<br>` is a void HTML element. The balance step must not push it onto
    // the open stack and must therefore not append `</br>`.
    List<Node> nodes = new PageLexer().parse(new StringReader("<?php echo \"<br>\"; ?>"));
    List<TagNode> brTags = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TAG)
      .map(n -> (TagNode) n)
      .filter(t -> "br".equalsIgnoreCase(t.getNodeName()))
      .toList();
    assertThat(brTags).hasSize(1);
    assertThat(brTags.get(0).isEndElement()).isFalse();
  }

  @Test
  void voidElementsListIsRecognizedByBalancer() {
    // A literal with several void elements must produce exactly those tags
    // and nothing else — no synthetic closes for any of them.
    List<Node> nodes = new PageLexer().parse(new StringReader(
      "<?php echo \"<img><input><hr>\"; ?>"));
    long syntheticCloses = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TAG)
      .map(n -> (TagNode) n)
      .filter(TagNode::isEndElement)
      .count();
    assertThat(syntheticCloses).isZero();
  }

  @Test
  void expandDoesNotInsertGapForLoneLiteral() {
    List<Node> nodes = new PageLexer().parse(new StringReader("<?php echo \"<div></div>\"; ?>"));
    boolean hasDynamicGap = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TEXT)
      .anyMatch(n -> n.getCode().contains("${dynamic}"));
    assertThat(hasDynamicGap).isFalse();
  }

  // ---------------------------------------------------------------------------
  // Line stability across escape sequences (H4)
  // ---------------------------------------------------------------------------

  @Test
  void escapeNewlineDoesNotAdvanceTagSourceLine() {
    // The \n escape must NOT shift the embedded <div> off the literal's line.
    List<Node> nodes = new PageLexer().parse(new StringReader(
      "<?php echo \"\\n<div></div>\"; ?>"));
    TagNode div = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TAG)
      .map(n -> (TagNode) n)
      .filter(t -> "div".equalsIgnoreCase(t.getNodeName()))
      .filter(t -> !t.isEndElement())
      .findFirst().orElse(null);
    assertThat(div).isNotNull();
    assertThat(div.getStartLinePosition()).isEqualTo(1);
  }

  // ---------------------------------------------------------------------------
  // Single-quoted literals are not sanitized (M2)
  // ---------------------------------------------------------------------------

  @Test
  void singleQuotedLiteralIsNotSanitized() {
    DirectiveNode node = phpDirective("echo '<div id=\"$myId\"></div>';");
    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).hasSize(1);
    assertThat(literals.get(0).interpolated()).isFalse();
    assertThat(literals.get(0).value()).contains("$myId");
  }

  @Test
  void singleQuotedDollarSignSurvivesPipeline() {
    // The $myId in a single-quoted literal must NOT be rewritten to ${dynamic}
    // because PHP single-quoted strings do not interpolate variables.
    List<Node> nodes = new PageLexer().parse(new StringReader(
      "<?php echo '<div id=\"$myId\"></div>'; ?>"));
    TagNode div = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TAG)
      .map(n -> (TagNode) n)
      .filter(t -> "div".equalsIgnoreCase(t.getNodeName()))
      .filter(t -> !t.isEndElement())
      .findFirst().orElse(null);
    assertThat(div).isNotNull();
    assertThat(div.getAttribute("id")).isEqualTo("$myId");
  }

  // ---------------------------------------------------------------------------
  // Column rebasing through source-column map (M3)
  // ---------------------------------------------------------------------------

  @Test
  void columnsAfterEscapeAccountForExtraSourceChar() {
    // `<?php echo "\"<div>"; ?>` — the leading \" escape spans 2 source chars
    // but decodes to 1 in the sanitised buffer. The `<` of `<div>` lives at
    // source col 14 (right after the 2-char `\"` whose `\` is at col 12); a
    // naive {@code baseCol + localCol} would put it at col 13.
    List<Node> nodes = new PageLexer().parse(new StringReader("<?php echo \"\\\"<div>\"; ?>"));
    TagNode div = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TAG)
      .map(n -> (TagNode) n)
      .filter(t -> "div".equalsIgnoreCase(t.getNodeName()))
      .filter(t -> !t.isEndElement())
      .findFirst().orElse(null);
    assertThat(div).isNotNull();
    assertThat(div.getStartColumnPosition()).isEqualTo(14);
  }

  // ---------------------------------------------------------------------------
  // Orphan open tag is balanced (M4)
  // ---------------------------------------------------------------------------

  @Test
  void openOnlyLiteralGetsBalancedByForeignEndTag() {
    // `echo "<span>"` would otherwise splice an orphan <span> open into the
    // flat list and turn every following real-file tag into its child.
    String source = "<?php echo \"<span>\"; ?>\n<p>real-file</p>";
    List<Node> nodes = new PageLexer().parse(new StringReader(source));
    TagNode p = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TAG)
      .map(n -> (TagNode) n)
      .filter(t -> "p".equalsIgnoreCase(t.getNodeName()))
      .filter(t -> !t.isEndElement())
      .findFirst().orElse(null);
    assertThat(p).isNotNull();
    assertThat(p.getParent()).isNull();
  }

  @Test
  void closeOnlyLiteralDoesNotAddSyntheticOpen() {
    String source = "<?php echo \"</span>\"; ?>";
    List<Node> nodes = new PageLexer().parse(new StringReader(source));
    long spanOpenCount = nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TAG)
      .map(n -> (TagNode) n)
      .filter(t -> "span".equalsIgnoreCase(t.getNodeName()))
      .filter(t -> !t.isEndElement())
      .count();
    assertThat(spanOpenCount).isZero();
  }

  // ---------------------------------------------------------------------------
  // Heredoc EOF without terminator is rejected (L5)
  // ---------------------------------------------------------------------------

  @Test
  void heredocWithoutTerminatorYieldsNoLiteral() {
    // The terminator label is misspelled; the body must not be returned because
    // the parser is now scanning to EOF and would otherwise swallow whatever
    // follows as embedded HTML.
    String code = "<?php\n$x = <<<EOT\n<div></div>\nEOX;\n?>";
    DirectiveNode node = new DirectiveNode();
    node.setNodeName("?php");
    node.setCode(code);
    node.setStartLinePosition(1);
    node.setStartColumnPosition(0);

    List<StringLiteral> literals = PhpEmbeddedHtmlExtractor.extractLiterals(node);
    assertThat(literals).isEmpty();
  }

  // ---------------------------------------------------------------------------
  // Multi-line literals with deeply nested HTML
  // ---------------------------------------------------------------------------

  @Test
  void doubleQuotedMultiLineLiteralReportsNestedTagsOnRightLines() {
    // Five levels of nesting spread across distinct source lines. The opening
    // `"` sits on line 1, the first real newline puts <div> on line 2, and so
    // on. The implementation tracks real source newlines inside the literal so
    // each rebased tag must land on its source line.
    String source =
        "<?php echo \"\n"          // line 1
      + "<div>\n"                  // line 2
      + "  <section>\n"            // line 3
      + "    <article>\n"          // line 4
      + "      <p>\n"              // line 5
      + "        <span>text</span>\n" // line 6
      + "      </p>\n"             // line 7
      + "    </article>\n"         // line 8
      + "  </section>\n"           // line 9
      + "</div>\n"                 // line 10
      + "\"; ?>";

    List<Node> nodes = new PageLexer().parse(new StringReader(source));

    assertThat(openTagLine(nodes, "div")).isEqualTo(2);
    assertThat(openTagLine(nodes, "section")).isEqualTo(3);
    assertThat(openTagLine(nodes, "article")).isEqualTo(4);
    assertThat(openTagLine(nodes, "p")).isEqualTo(5);
    assertThat(openTagLine(nodes, "span")).isEqualTo(6);
  }

  @Test
  void doubleQuotedMultiLineLiteralPreservesParentChainAcrossNestedTags() {
    String source =
        "<?php echo \"\n"
      + "<div>\n"
      + "  <section>\n"
      + "    <article>\n"
      + "      <p>\n"
      + "        <span>text</span>\n"
      + "      </p>\n"
      + "    </article>\n"
      + "  </section>\n"
      + "</div>\n"
      + "\"; ?>";

    List<Node> nodes = new PageLexer().parse(new StringReader(source));

    TagNode span = openTag(nodes, "span");
    assertThat(span).isNotNull();
    assertThat(span.getParent()).isNotNull();
    assertThat(span.getParent().getNodeName()).isEqualToIgnoringCase("p");
    assertThat(span.getParent().getParent().getNodeName()).isEqualToIgnoringCase("article");
    assertThat(span.getParent().getParent().getParent().getNodeName()).isEqualToIgnoringCase("section");
    assertThat(span.getParent().getParent().getParent().getParent().getNodeName()).isEqualToIgnoringCase("div");
    assertThat(span.getParent().getParent().getParent().getParent().getParent()).isNull();
  }

  @Test
  void heredocMultiLineBodyReportsNestedTagsOnRightLines() {
    // The directive sits at line 1, `<<<EOT\n` ends line 2, so the body opens
    // at line 3. Five levels of nesting beneath <div> span lines 3 to 7.
    String code =
        "<?php\n"                      // line 1
      + "$x = <<<EOT\n"                // line 2
      + "<div>\n"                      // line 3
      + "  <section>\n"                // line 4
      + "    <article>\n"              // line 5
      + "      <p>\n"                  // line 6
      + "        <span>text</span>\n"  // line 7
      + "      </p>\n"                 // line 8
      + "    </article>\n"             // line 9
      + "  </section>\n"               // line 10
      + "</div>\n"                     // line 11
      + "EOT;\n"
      + "?>";

    List<Node> nodes = new PageLexer().parse(new StringReader(code));

    assertThat(openTagLine(nodes, "div")).isEqualTo(3);
    assertThat(openTagLine(nodes, "section")).isEqualTo(4);
    assertThat(openTagLine(nodes, "article")).isEqualTo(5);
    assertThat(openTagLine(nodes, "p")).isEqualTo(6);
    assertThat(openTagLine(nodes, "span")).isEqualTo(7);
  }

  @Test
  void heredocMultiLineBodyPreservesParentChainAcrossNestedTags() {
    String code =
        "<?php\n"
      + "$x = <<<EOT\n"
      + "<div>\n"
      + "  <section>\n"
      + "    <article>\n"
      + "      <p>\n"
      + "        <span>text</span>\n"
      + "      </p>\n"
      + "    </article>\n"
      + "  </section>\n"
      + "</div>\n"
      + "EOT;\n"
      + "?>";

    List<Node> nodes = new PageLexer().parse(new StringReader(code));

    TagNode span = openTag(nodes, "span");
    assertThat(span).isNotNull();
    assertThat(span.getParent().getNodeName()).isEqualToIgnoringCase("p");
    assertThat(span.getParent().getParent().getNodeName()).isEqualToIgnoringCase("article");
    assertThat(span.getParent().getParent().getParent().getNodeName()).isEqualToIgnoringCase("section");
    assertThat(span.getParent().getParent().getParent().getParent().getNodeName()).isEqualToIgnoringCase("div");
  }

  private static TagNode openTag(List<Node> nodes, String name) {
    return nodes.stream()
      .filter(n -> n.getNodeType() == NodeType.TAG)
      .map(n -> (TagNode) n)
      .filter(t -> name.equalsIgnoreCase(t.getNodeName()))
      .filter(t -> !t.isEndElement())
      .findFirst().orElse(null);
  }

  private static int openTagLine(List<Node> nodes, String name) {
    TagNode tag = openTag(nodes, name);
    assertThat(tag).as("open <%s> tag", name).isNotNull();
    return tag.getStartLinePosition();
  }
}
