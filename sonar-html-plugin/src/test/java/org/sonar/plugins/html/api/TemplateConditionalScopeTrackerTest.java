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
package org.sonar.plugins.html.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

class TemplateConditionalScopeTrackerTest {

  @Test
  void tracks_php_conditionals_without_leaking_on_literal_braces() {
    List<Node> nodes = parse("""
      <?php if (random_int(0, 1)) { ?>
        <div id="first">Shown</div>
      <?php } /* } */ else { ?>
        <div id="second">Fallback</div>
      <?php } ?>
      <?php if (random_int(0, 1)) { echo "}"; } ?>
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 2)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 7)).isFalse();
  }

  @Test
  void tracks_angular_brace_based_conditionals() {
    List<Node> nodes = parse("""
      @if (gridOptions) {
        <div id="conditional">Table</div>
      } @else {
        <div id="conditional">Fallback</div>
      }
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 2)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 6)).isFalse();
  }

  /**
   * Keeps Razor if/else branches conditional when nested C# blocks close before the else branch.
   */
  @Test
  void tracks_razor_conditionals_across_nested_csharp_blocks() {
    List<Node> nodes = parse("""
      @if (Model.HasData && Model.HasValidRows)
      {
        using (Html.BeginForm("ConfirmImportAllValidTrainingRecords", "TrainingImport", null, FormMethod.Post, new { onsubmit = "renderOverlay(this.action); return false;" }))
        {
          <button id="submit-valid-training-records" type="submit">Import</button>
        }
      }
      else
      {
        <button id="submit-valid-training-records" type="submit" disabled="disabled">Import</button>
      }
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "button", 5)).isTrue();
    assertThat(isConditionalAtLine(nodes, "button", 10)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 12)).isFalse();
  }

  @Test
  void tracks_jstl_conditional_tags() {
    List<Node> nodes = parse("""
      <c:if test="${cond}">
        <div id="conditional">Inside</div>
      </c:if>
      <div id="footer">Outside</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 2)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 4)).isFalse();
  }

  @Test
  void treats_conditional_attributes_as_conditional_scopes() {
    List<Node> nodes = parse("""
      <div v-if="flag" id="vue">Inside</div>
      <div *ngIf="flag" id="angular">Inside</div>
      <div id="plain">Outside</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 1)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 2)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 3)).isFalse();
  }

  private static List<Node> parse(String content) {
    return new PageLexer().parse(new StringReader(content));
  }

  private static boolean isConditionalAtLine(List<Node> nodes, String tagName, int startLine) {
    return isConditional(nodes, findTag(nodes, tagName, startLine));
  }

  private static boolean isConditional(List<Node> nodes, TagNode target) {
    TemplateConditionalScopeTracker tracker = new TemplateConditionalScopeTracker();
    for (Node node : nodes) {
      if (node instanceof TextNode textNode) {
        tracker.visitText(textNode);
      } else if (node instanceof DirectiveNode directiveNode) {
        tracker.visitDirective(directiveNode);
      } else if (node instanceof TagNode tagNode) {
        if (tagNode.isEndElement()) {
          tracker.endElement(tagNode);
        } else {
          tracker.startElement(tagNode);
          if (tagNode == target) {
            return tracker.isInConditional(tagNode);
          }
        }
      }
    }
    throw new IllegalArgumentException("Target tag was not encountered during scan");
  }

  private static TagNode findTag(List<Node> nodes, String tagName, int startLine) {
    return nodes.stream()
      .filter(TagNode.class::isInstance)
      .map(TagNode.class::cast)
      .filter(tag -> !tag.isEndElement())
      .filter(tag -> tagName.equalsIgnoreCase(tag.getNodeName()))
      .filter(tag -> tag.getStartLinePosition() == startLine)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("No <" + tagName + "> tag at line " + startLine));
  }
}
