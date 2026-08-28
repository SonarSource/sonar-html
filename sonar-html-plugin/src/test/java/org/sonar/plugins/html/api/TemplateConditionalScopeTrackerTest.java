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
  void tracks_php_else_if_branches_without_leaking_past_the_chain() {
    List<Node> nodes = parse("""
      <?php if (random_int(0, 2) == 0) { ?>
        <div id="choice">First</div>
      <?php } else if (random_int(0, 1)) { ?>
        <div id="choice">Second</div>
      <?php } ?>
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 2)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 6)).isFalse();
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
  void tracks_razor_conditionals_when_condition_contains_braces() {
    List<Node> nodes = parse("""
      @if (Model.Items.Any(item => new { Value = item }.Value != null))
      {
        <div id="choice">First</div>
      }
      else
      {
        <div id="choice">Second</div>
      }
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 3)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 7)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 9)).isFalse();
  }

  @Test
  void tracks_plain_csharp_conditionals_inside_razor_code_blocks() {
    List<Node> nodes = parse("""
      @{
        if (Model.ShowPrimary)
        {
          <div id="choice">First</div>
        }
        else if (Model.ShowSecondary)
        {
          <div id="choice">Second</div>
        }
        else
        {
          <div id="choice">Fallback</div>
        }
      }
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 8)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 12)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 15)).isFalse();
  }

  @Test
  void tracks_plain_csharp_conditionals_after_void_elements() {
    List<Node> nodes = parse("""
      @{
        <input id="filter" type="text">
        if (Model.ShowPrimary) {
          <div id="choice">First</div>
        } else {
          <div id="choice">Second</div>
        }
      }
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 6)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 9)).isFalse();
  }

  @Test
  void tracks_plain_csharp_conditionals_after_omitted_end_tags() {
    List<Node> nodes = parse("""
      @{
        <ul>
          <li>First
          <li>Second
        </ul>
        if (Model.ShowPrimary) {
          <div id="choice">First</div>
        } else {
          <div id="choice">Second</div>
        }
      }
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 7)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 9)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 12)).isFalse();
  }

  @Test
  void does_not_parse_csharp_after_orphan_closing_tags_in_markup() {
    List<Node> nodes = parse("""
      <section>
        @{
          <article>
          </div>
          if (rendered) {
            <div id="duplicate">First</div>
            <div id="duplicate">Second</div>
          }
          </article>
        }
      </section>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 6)).isFalse();
    assertThat(isConditionalAtLine(nodes, "div", 7)).isFalse();
  }

  @Test
  void follows_lexer_hierarchy_when_malformed_markup_contains_custom_elements() {
    List<Node> nodes = parse("""
      @{
        <div>
          <my-widget>
        </div>
        if (rendered) {
          <span id="duplicate">First</span>
          <span id="duplicate">Second</span>
        }
      }
      """);

    assertThat(isConditionalAtLine(nodes, "span", 6)).isFalse();
    assertThat(isConditionalAtLine(nodes, "span", 7)).isFalse();
  }

  @Test
  void tracks_plain_csharp_conditionals_after_unmatched_comment_tags() {
    List<Node> nodes = parse("""
      <section>
        @{
          // <div id="not-rendered">
          </div>
          if (Model.ShowPrimary) {
            <div id="choice">First</div>
          } else {
            <div id="choice">Second</div>
          }
        }
      </section>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 6)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 8)).isTrue();
  }

  @Test
  void ignores_unbalanced_braces_in_rendered_razor_markup() {
    List<Node> nodes = parse("""
      @{
        if (Model.ShowPrimary) {
          <code>if (x) {</code>
          <div id="choice">First</div>
        }
      }
      Ordinary "quoted text
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 8)).isFalse();
    assertThat(scan(nodes).isInNonRenderedRazorContent()).isFalse();
  }

  @Test
  void ignores_balanced_braces_in_rendered_razor_markup() {
    List<Node> nodes = parse("""
      @{
        if (Model.ShowPrimary) {
          <script>function value() { return 1; }</script>
          <div id="choice">First</div>
        } else {
          <div id="choice">Second</div>
        }
      }
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 6)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 9)).isFalse();
  }

  @Test
  void ignores_unmatched_closing_braces_in_rendered_razor_markup() {
    List<Node> nodes = parse("""
      @{
        if (Model.ShowPrimary) {
          <code>}</code>
          <div id="choice">First</div>
        } else {
          <div id="choice">Second</div>
        }
      }
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 6)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 9)).isFalse();
  }

  @Test
  void tracks_nested_razor_conditionals_in_rendered_markup() {
    List<Node> nodes = parse("""
      @{
        <section>
          @if (Model.ShowPrimary) {
            <div id="choice">First</div>
          } else {
            <div id="choice">Second</div>
          }
        </section>
      }
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 6)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 10)).isFalse();
  }

  @Test
  void tracks_plain_csharp_conditionals_after_interpolated_verbatim_strings() {
    List<Node> nodes = parse("""
      @{
        var message = @$"He said ""hi"" }";
        if (Model.ShowPrimary) {
          <div id="choice">First</div>
        } else {
          <div id="choice">Second</div>
        }
      }
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 6)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 9)).isFalse();
  }

  @Test
  void tracks_plain_csharp_conditionals_after_generic_type_arguments() {
    List<Node> nodes = parse("""
      @{
        var items = new List<string>();
        if (Model.ShowPrimary) {
          <div id="choice">First</div>
        } else {
          <div id="choice">Second</div>
        }
      }
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 6)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 9)).isFalse();
  }

  @Test
  void tracks_plain_csharp_conditionals_after_raw_strings() {
    List<Node> nodes = parse(
      "@{\n"
        + "  var markup = \"\"\"a \" <div id=\"not-rendered\">Text</div>\"\"\";\n"
        + "  if (Model.ShowPrimary) {\n"
        + "    <div id=\"choice\">First</div>\n"
        + "  } else {\n"
        + "    <div id=\"choice\">Second</div>\n"
        + "  }\n"
        + "}\n"
        + "<div id=\"footer\">Footer</div>\n");

    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 6)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 9)).isFalse();
    assertThat(scan(nodes).isInNonRenderedRazorContent()).isFalse();
  }

  @Test
  void treats_explicit_razor_text_as_rendered_content() {
    List<Node> nodes = parse("""
      @{
        @:if (rendered) {
        <div id="duplicate">First</div>
        <div id="duplicate">Second</div>
      }
      """);

    assertThat(isConditionalAtLine(nodes, "div", 3)).isFalse();
    assertThat(isConditionalAtLine(nodes, "div", 4)).isFalse();
  }

  @Test
  void does_not_start_explicit_razor_text_inside_csharp_line_comments() {
    List<Node> nodes = parse("""
      @{
        // Use @: for explicit Razor text.
        if (Model.ShowPrimary) {
          <div id="choice">First</div>
        } else {
          <div id="choice">Second</div>
        }
      }
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 6)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 9)).isFalse();
  }

  @Test
  void does_not_start_explicit_razor_text_inside_csharp_strings() {
    List<Node> nodes = parse("""
      @{
        var marker = "a@:b";
        if (Model.ShowPrimary) {
          <div id="choice">First</div>
        } else {
          <div id="choice">Second</div>
        }
      }
      <div id="footer">Footer</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 4)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 6)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 9)).isFalse();
  }

  @Test
  void closes_csharp_conditionals_when_branch_markup_is_left_open() {
    List<Node> nodes = parse("""
      @{
        if (Model.ShowPrimary) {
          <div id="choice">First }
      }
      <div id="duplicate">First</div>
      <div id="duplicate">Second</div>
      """);

    assertThat(isConditionalAtLine(nodes, "div", 3)).isTrue();
    assertThat(isConditionalAtLine(nodes, "div", 5)).isFalse();
    assertThat(isConditionalAtLine(nodes, "div", 6)).isFalse();
  }

  @Test
  void requires_razor_code_tracking_to_be_enabled_explicitly() {
    List<Node> nodes = parse("""
      @{
        if (Model.ShowPrimary) {
          <div id="choice">First</div>
        }
      }
      """);
    TemplateConditionalScopeTracker tracker = new TemplateConditionalScopeTracker();
    tracker.reset();

    assertThat(isConditional(nodes, findTag(nodes, "div", 3), tracker)).isFalse();
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
    tracker.reset(true);
    return isConditional(nodes, target, tracker);
  }

  private static boolean isConditional(List<Node> nodes, TagNode target, TemplateConditionalScopeTracker tracker) {
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
          if (tagNode.hasEnd()) {
            tracker.endElement(tagNode);
          }
        }
      }
    }
    throw new IllegalArgumentException("Target tag was not encountered during scan");
  }

  private static TemplateConditionalScopeTracker scan(List<Node> nodes) {
    TemplateConditionalScopeTracker tracker = new TemplateConditionalScopeTracker();
    tracker.reset(true);
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
          if (tagNode.hasEnd()) {
            tracker.endElement(tagNode);
          }
        }
      }
    }
    return tracker;
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
