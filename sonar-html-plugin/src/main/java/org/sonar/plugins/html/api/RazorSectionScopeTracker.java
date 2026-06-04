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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

/**
 * Tracks top-level Razor {@code @section ... { ... }} scopes so checks can suppress
 * false positives on markup whose rendered container lives outside the current file fragment.
 */
public final class RazorSectionScopeTracker {

  private static final Pattern RAZOR_SECTION_OPEN = Pattern.compile("@section\\s+\\w+\\s*\\{");
  private static final RazorSectionScopeTracker EMPTY = new RazorSectionScopeTracker(List.of());

  private final List<int[]> sectionRanges;

  private RazorSectionScopeTracker(List<int[]> sectionRanges) {
    this.sectionRanges = sectionRanges;
  }

  public static RazorSectionScopeTracker empty() {
    return EMPTY;
  }

  public static RazorSectionScopeTracker create(List<Node> nodes, HtmlSourceCode code) {
    if (!Helpers.isRazorFile(code)) {
      return EMPTY;
    }
    List<int[]> sectionRanges = new ArrayList<>();
    ScanState state = new ScanState();
    for (Node node : nodes) {
      if (node instanceof TextNode textNode) {
        scanForSections(textNode, state, sectionRanges);
      }
    }
    return sectionRanges.isEmpty() ? EMPTY : new RazorSectionScopeTracker(sectionRanges);
  }

  public boolean contains(TagNode node) {
    int line = node.getStartLinePosition();
    for (int[] range : sectionRanges) {
      if (line >= range[0] && line <= range[1]) {
        return true;
      }
    }
    return false;
  }

  private static void scanForSections(TextNode textNode, ScanState state, List<int[]> sectionRanges) {
    // TextNodes inside an HTML element are body content. Any { or } there is
    // literal text (e.g. `<p>}</p>`) and must not affect Razor section depth.
    if (textNode.getParent() != null) {
      return;
    }
    String code = textNode.getCode();
    int baseLine = textNode.getStartLinePosition();
    int pos = 0;
    while (pos < code.length()) {
      pos = (state.depth == 0)
        ? enterSectionIfFound(code, pos, baseLine, state)
        : advanceBraceDepth(code, pos, baseLine, state, sectionRanges);
      if (pos < 0) {
        return;
      }
    }
  }

  private static int enterSectionIfFound(String code, int pos, int baseLine, ScanState state) {
    Matcher matcher = RAZOR_SECTION_OPEN.matcher(code);
    if (!matcher.find(pos)) {
      return -1;
    }
    state.sectionStartLine = baseLine + countNewlines(code, 0, matcher.start());
    state.depth = 1;
    return matcher.end();
  }

  private static int advanceBraceDepth(String code, int pos, int baseLine, ScanState state, List<int[]> sectionRanges) {
    char c = code.charAt(pos);
    if (c == '{') {
      state.depth++;
    } else if (c == '}') {
      state.depth--;
      if (state.depth == 0) {
        sectionRanges.add(new int[] {state.sectionStartLine, baseLine + countNewlines(code, 0, pos)});
        state.sectionStartLine = -1;
      }
    }
    return pos + 1;
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

  private static final class ScanState {
    int depth;
    int sectionStartLine = -1;
  }
}
