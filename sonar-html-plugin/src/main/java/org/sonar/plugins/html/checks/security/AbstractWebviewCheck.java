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
package org.sonar.plugins.html.checks.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.CheckForNull;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

abstract class AbstractWebviewCheck extends AbstractPageCheck {

  /**
   * Tells whether a tag is an Electron webview element.
   *
   * @param node the tag to inspect
   * @return {@code true} when the tag is a webview
   */
  protected final boolean isWebview(TagNode node) {
    return node.equalsElementName("webview");
  }

  /**
   * Raises an issue on the triggering attribute, falling back to the attribute line when needed.
   *
   * @param node the tag containing the attribute
   * @param attribute the triggering attribute
   * @param message the issue message
   */
  protected final void createViolationOnAttribute(TagNode node, Attribute attribute, String message) {
    AttributeLocation location = findAttributeLocation(node, attribute);
    if (location == null) {
      createViolation(attribute.getLine(), message);
      return;
    }
    createViolation(location.startLine, location.startColumn, location.endLine, location.endColumn, message);
  }

  /**
   * Finds the precise location of an attribute name within a tag.
   *
   * @param node the tag containing the attribute
   * @param attribute the attribute to locate
   * @return the resolved attribute location, or {@code null} when it cannot be found
   */
  @CheckForNull
  private static AttributeLocation findAttributeLocation(TagNode node, Attribute attribute) {
    Pattern pattern = Pattern.compile("(?i)(^|\\s)(" + Pattern.quote(attribute.getName()) + ")(?=[\\s=/>])");
    Matcher matcher = pattern.matcher(node.getCode());
    while (matcher.find()) {
      AttributeLocation location = toAttributeLocation(node, matcher.start(2), matcher.end(2));
      if (location.startLine == attribute.getLine()) {
        return location;
      }
    }
    return null;
  }

  /**
   * Converts offsets within a tag into source coordinates.
   *
   * @param node the tag that owns the code snippet
   * @param startOffset the start offset of the attribute name
   * @param endOffset the end offset of the attribute name
   * @return the resolved source coordinates
   */
  private static AttributeLocation toAttributeLocation(TagNode node, int startOffset, int endOffset) {
    SourcePosition start = toSourcePosition(node, startOffset);
    SourcePosition end = toSourcePosition(node, endOffset);
    return new AttributeLocation(start.line, start.column, end.line, end.column);
  }

  /**
   * Resolves a character offset within a tag code snippet to a source position.
   *
   * @param node the tag that owns the code snippet
   * @param offset the offset to resolve
   * @return the resolved line and column
   */
  private static SourcePosition toSourcePosition(TagNode node, int offset) {
    int line = node.getStartLinePosition();
    int column = node.getStartColumnPosition();
    for (int i = 0; i < offset; i++) {
      char character = node.getCode().charAt(i);
      if (character == '\n') {
        line++;
        column = 0;
      } else if (character != '\r') {
        column++;
      }
    }
    return new SourcePosition(line, column);
  }

  private static final class AttributeLocation {
    private final int startLine;
    private final int startColumn;
    private final int endLine;
    private final int endColumn;

    private AttributeLocation(int startLine, int startColumn, int endLine, int endColumn) {
      this.startLine = startLine;
      this.startColumn = startColumn;
      this.endLine = endLine;
      this.endColumn = endColumn;
    }
  }

  private static final class SourcePosition {
    private final int line;
    private final int column;

    private SourcePosition(int line, int column) {
      this.line = line;
      this.column = column;
    }
  }
}
