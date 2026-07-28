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
package org.sonar.plugins.html.checks.comments;

import java.io.StringReader;
import java.util.List;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.node.CommentNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "AvoidCommentedOutCodeCheck")
public class AvoidCommentedOutCodeCheck extends AbstractPageCheck {

  private static final Pattern COPYRIGHT_CASE_INSENSITIVE = Pattern.compile("copyright", Pattern.CASE_INSENSITIVE);
  private static final List<String> IGNORED_COMMENT_ANNOTATIONS = List.of("@thymesVar", "@elvariable");

  @Override
  public void comment(CommentNode node) {
    if (node.isHtml()) {
      String comment = node.getCode();

      if (!isIgnored(node, comment) && containsCommentedOutHtml(stripCommentDelimiters(node))) {
        createViolation(node.getStartLinePosition(), "Remove this commented out code.");
      }
    }
  }

  /**
   * Detects whether a stripped HTML comment body contains real markup structure.
   * @param commentBody the comment body without its delimiters
   * @return {@code true} when the body contains commented-out HTML rather than prose
   */
  private static boolean containsCommentedOutHtml(String commentBody) {
    String trimmedCommentBody = commentBody.strip();
    if (trimmedCommentBody.isEmpty()) {
      return false;
    }
    List<Node> nodes = new PageLexer().parse(new StringReader(trimmedCommentBody));
    String[] lines = trimmedCommentBody.split("\\R", -1);
    return nodes.stream()
      .filter(TagNode.class::isInstance)
      .map(TagNode.class::cast)
      .anyMatch(tag -> isCommentedOutStructure(tag, lines));
  }

  /**
   * Checks whether a parsed tag is strong evidence of commented-out HTML.
   * @param tag the parsed tag node
   * @param lines the stripped comment body split into lines
   * @return {@code true} when the tag represents real markup instead of an inline mention
   */
  private static boolean isCommentedOutStructure(TagNode tag, String[] lines) {
    return tag.isEndElement()
      || tag.hasEnd()
      || (!tag.getAttributes().isEmpty() && isStandaloneOpeningTag(tag, lines));
  }

  /**
   * Checks whether an opening tag occupies its own line block inside the comment.
   * @param tag the parsed opening tag
   * @param lines the stripped comment body split into lines
   * @return {@code true} when the opening tag is standalone instead of embedded in prose
   */
  private static boolean isStandaloneOpeningTag(TagNode tag, String[] lines) {
    String leadingText = lines[tag.getStartLinePosition() - 1].substring(0, tag.getStartColumnPosition());
    String trailingText = lines[tag.getEndLinePosition() - 1].substring(tag.getEndColumnPosition());
    return leadingText.isBlank() && trailingText.isBlank();
  }

  private static boolean isIgnored(CommentNode node, String comment) {
    return COPYRIGHT_CASE_INSENSITIVE.matcher(comment).find()
      // Conditional comments
      || comment.startsWith("<!--[if")
      // Server Side Includes
      || node.isServerSideInclude()
      // Annotated comments
      || IGNORED_COMMENT_ANNOTATIONS.stream().anyMatch(comment::contains);
  }

  /**
   * Removes the start and end delimiters from an HTML comment node.
   * @param node the comment node to normalize
   * @return the raw comment body without its delimiters
   */
  private static String stripCommentDelimiters(CommentNode node) {
    String code = node.getCode();
    return code.substring(node.getStartDelimiter().length(), code.length() - node.getEndDelimiter().length());
  }

}
