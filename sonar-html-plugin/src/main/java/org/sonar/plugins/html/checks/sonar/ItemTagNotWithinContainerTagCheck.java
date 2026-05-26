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
package org.sonar.plugins.html.checks.sonar;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "ItemTagNotWithinContainerTagCheck")
public class ItemTagNotWithinContainerTagCheck extends AbstractPageCheck {

  private static final Pattern RAZOR_SECTION_OPEN = Pattern.compile("@section\\s+\\w+\\s*\\{");

  private final List<int[]> razorSectionRanges = new ArrayList<>();

  @Override
  public void startDocument(List<Node> nodes) {
    razorSectionRanges.clear();
    if (!isRazorFile()) {
      return;
    }
    ScanState state = new ScanState();
    for (Node node : nodes) {
      if (node instanceof TextNode textNode) {
        scanForSections(textNode, state);
      }
    }
  }

  private void scanForSections(TextNode textNode, ScanState state) {
    String code = textNode.getCode();
    int baseLine = textNode.getStartLinePosition();
    int pos = 0;
    while (pos < code.length()) {
      pos = (state.depth == 0)
        ? enterSectionIfFound(code, pos, baseLine, state)
        : advanceBraceDepth(code, pos, baseLine, state);
      if (pos < 0) {
        return;
      }
    }
  }

  private static int enterSectionIfFound(String code, int pos, int baseLine, ScanState state) {
    Matcher m = RAZOR_SECTION_OPEN.matcher(code);
    if (!m.find(pos)) {
      return -1;
    }
    state.sectionStartLine = baseLine + countNewlines(code, 0, m.start());
    state.depth = 1;
    return m.end();
  }

  private int advanceBraceDepth(String code, int pos, int baseLine, ScanState state) {
    char c = code.charAt(pos);
    if (c == '{') {
      state.depth++;
    } else if (c == '}') {
      state.depth--;
      if (state.depth == 0) {
        razorSectionRanges.add(new int[] { state.sectionStartLine, baseLine + countNewlines(code, 0, pos) });
        state.sectionStartLine = -1;
      }
    }
    return pos + 1;
  }

  private static final class ScanState {
    int depth;
    int sectionStartLine = -1;
  }

  @Override
  public void startElement(TagNode node) {
    if (Helpers.hasTemplateAncestor(node) || isInsideRazorSection(node)) {
      return;
    }
    if (isLi(node) && !hasLiOrUlOrOlAncestor(node)) {
      createViolation(node, "Surround this <" + node.getNodeName() + "> item tag by a <ul> or <ol> container one.");
    } else if (isDt(node) && !hasDtOrDlAncestor(node)) {
      createViolation(node, "Surround this <" + node.getNodeName() + "> item tag by a <dl> container one.");
    }
  }

  /**
   * Returns true if {@code node} sits inside a Razor {@code @section ... { ... }} block.
   *
   * @param node the tag node whose location is inspected
   * @return true if any pre-computed section range contains the node's start line
   */
  private boolean isInsideRazorSection(TagNode node) {
    int line = node.getStartLinePosition();
    for (int[] range : razorSectionRanges) {
      if (line >= range[0] && line <= range[1]) {
        return true;
      }
    }
    return false;
  }

  private boolean isRazorFile() {
    String filename = getHtmlSourceCode().inputFile().filename();
    return filename.endsWith(".cshtml") || filename.endsWith(".vbhtml");
  }

  private static int countNewlines(String s, int from, int to) {
    int n = 0;
    int limit = Math.min(to, s.length());
    for (int i = from; i < limit; i++) {
      if (s.charAt(i) == '\n') {
        n++;
      }
    }
    return n;
  }

  private static boolean hasLiOrUlOrOlAncestor(TagNode node) {
    return Helpers.hasAncestorMatching(node, p -> isLi(p) || isLiAllowedParent(p));
  }

  private static boolean hasDtOrDlAncestor(TagNode node) {
    return Helpers.hasAncestorMatching(node, p -> isDt(p) || isDl(p));
  }

  private static boolean isLi(TagNode node) {
    return "LI".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isDt(TagNode node) {
    return "DT".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isLiAllowedParent(TagNode node) {
    return isUl(node) || isOl(node) || isMenu(node);
  }

  private static boolean isUl(TagNode node) {
    return "UL".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isOl(TagNode node) {
    return "OL".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isMenu(TagNode node) {
    return "MENU".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isDl(TagNode node) {
    return "DL".equalsIgnoreCase(node.getNodeName());
  }

}
