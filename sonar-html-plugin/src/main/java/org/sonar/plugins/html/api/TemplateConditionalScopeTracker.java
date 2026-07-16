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

import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

/**
 * Tracks mutually exclusive template branches across several templating syntaxes
 * without requiring a dedicated parser for each of them.
 */
public final class TemplateConditionalScopeTracker {

  private static final Set<String> JSTL_CONDITIONAL_TAGS = Set.of(
    "c:if", "c:when", "c:otherwise", "c:choose"
  );

  private static final Set<String> VUE_CONDITIONAL_ATTRS = Set.of(
    "v-if", "v-else-if", "v-else", "v-for"
  );

  private static final Set<String> ANGULAR_CONDITIONAL_ATTRS = Set.of(
    "*ngIf", "*ngFor", "*ngSwitchCase", "*ngSwitchDefault"
  );

  private static final Pattern RAZOR_BLOCK_START_PATTERN = Pattern.compile("@(if|switch)\\s*\\(", Pattern.CASE_INSENSITIVE);
  private static final Pattern RAZOR_BRANCH_START_PATTERN = Pattern.compile("@(case|default)\\s*[({]", Pattern.CASE_INSENSITIVE);
  private static final Pattern PHP_DIRECTIVE_CONDITIONAL_START_PATTERN = Pattern.compile("(if|foreach|for)\\b", Pattern.CASE_INSENSITIVE);
  private static final Pattern TWIG_CONDITIONAL_START_PATTERN = Pattern.compile("\\{%[-\\s]*(if|for)\\b", Pattern.CASE_INSENSITIVE);
  private static final Pattern TWIG_CONDITIONAL_END_PATTERN = Pattern.compile("\\{%[-\\s]*(endif|endfor)\\b", Pattern.CASE_INSENSITIVE);
  private static final Pattern PHP_DIRECTIVE_CONDITIONAL_END_PATTERN = Pattern.compile("(endif|endforeach|endfor)\\b", Pattern.CASE_INSENSITIVE);

  private int textConditionalDepth;
  private int braceBasedTextConditionalDepth;
  private int pendingConditionalBranchOpenings;
  private int nestedTextBlockDepth;
  private int tagConditionalDepth;

  public void reset() {
    textConditionalDepth = 0;
    braceBasedTextConditionalDepth = 0;
    pendingConditionalBranchOpenings = 0;
    nestedTextBlockDepth = 0;
    tagConditionalDepth = 0;
  }

  public void visitText(TextNode textNode) {
    scanFragment(textNode.getCode(), false);
  }

  public void visitDirective(DirectiveNode directiveNode) {
    scanFragment(unwrapDirective(directiveNode.getCode()), true);
  }

  public void startElement(TagNode node) {
    if (isJstlConditionalTag(node)) {
      tagConditionalDepth++;
    }
  }

  public void endElement(TagNode node) {
    if (isJstlConditionalTag(node) && tagConditionalDepth > 0) {
      tagConditionalDepth--;
    }
  }

  public boolean isInConditional(TagNode node) {
    return hasOpenConditionalScope() || hasConditionalAttribute(node);
  }

  private boolean hasOpenConditionalScope() {
    return textConditionalDepth > 0 || tagConditionalDepth > 0;
  }

  /**
   * Scans a text or directive fragment and keeps the open conditional scopes in sync
   * with structural braces, explicit template endings, and branch continuations.
   *
   * @param text the fragment to scan
   * @param directive whether the fragment comes from a directive node
   */
  private void scanFragment(String text, boolean directive) {
    FragmentScanState state = new FragmentScanState();
    while (state.index < text.length()) {
      if (consumeProtectedCharacter(text, state)
        || consumeCommentOrStringStart(text, directive, state)
        || consumeConditionalToken(text, directive, state)
        || consumeStructuralToken(text, state)) {
        continue;
      }
      state.index++;
    }

    if (textConditionalDepth == 0) {
      clearBraceTracking();
    }
  }

  /**
   * Consumes the next character when the scan is already inside a comment or quoted string.
   *
   * @param text the fragment being scanned
   * @param state the mutable scan state
   * @return {@code true} when the current position was consumed
   */
  private static boolean consumeProtectedCharacter(String text, FragmentScanState state) {
    if (state.inLineComment) {
      if (isLineBreak(text.charAt(state.index))) {
        state.inLineComment = false;
      }
      state.index++;
      return true;
    }
    if (state.inBlockComment) {
      if (startsWith(text, state.index, "*/")) {
        state.inBlockComment = false;
        state.index += 2;
      } else {
        state.index++;
      }
      return true;
    }
    if (state.quoteDelimiter != '\0') {
      char current = text.charAt(state.index);
      if (state.escaped) {
        state.escaped = false;
      } else if (current == '\\') {
        state.escaped = true;
      } else if (current == state.quoteDelimiter) {
        state.quoteDelimiter = '\0';
      }
      state.index++;
      return true;
    }
    return false;
  }

  /**
   * Starts scanning a comment or quoted string at the current position when one begins here.
   *
   * @param text the fragment being scanned
   * @param directive whether the fragment comes from a directive node
   * @param state the mutable scan state
   * @return {@code true} when a comment or string opener was consumed
   */
  private static boolean consumeCommentOrStringStart(String text, boolean directive, FragmentScanState state) {
    if (!directive && startsWith(text, state.index, "@*")) {
      state.index = skipDelimitedBlock(text, state.index + 2, "*@");
      return true;
    }
    if (startsWith(text, state.index, "/*")) {
      state.inBlockComment = true;
      state.index += 2;
      return true;
    }
    if (startsWith(text, state.index, "//")) {
      state.inLineComment = true;
      state.index += 2;
      return true;
    }
    if (text.charAt(state.index) == '#') {
      state.inLineComment = true;
      state.index++;
      return true;
    }
    if (text.charAt(state.index) == '\'' || text.charAt(state.index) == '"') {
      state.quoteDelimiter = text.charAt(state.index);
      state.escaped = false;
      state.index++;
      return true;
    }
    return false;
  }

  /**
   * Consumes a conditional start or end token at the current position when one begins here.
   *
   * @param text the fragment being scanned
   * @param directive whether the fragment comes from a directive node
   * @param state the mutable scan state
   * @return {@code true} when a conditional token was consumed
   */
  private boolean consumeConditionalToken(String text, boolean directive, FragmentScanState state) {
    return directive
      ? consumePhpDirectiveConditional(text, state)
      : consumeTemplateConditional(text, state);
  }

  /**
   * Consumes a PHP directive conditional token at the current position when one begins here.
   *
   * @param text the fragment being scanned
   * @param state the mutable scan state
   * @return {@code true} when a PHP directive conditional token was consumed
   */
  private boolean consumePhpDirectiveConditional(String text, FragmentScanState state) {
    boolean startsAtWordBoundary = state.index == 0
      || (!Character.isLetterOrDigit(text.charAt(state.index - 1)) && text.charAt(state.index - 1) != '_');

    int conditionalEndLength = startsAtWordBoundary
      ? matchedPrefixLength(PHP_DIRECTIVE_CONDITIONAL_END_PATTERN, text, state.index)
      : 0;
    if (conditionalEndLength > 0) {
      applyTextConditionalClosings(1);
      state.index += conditionalEndLength;
      return true;
    }

    int conditionalStartLength = startsAtWordBoundary
      ? matchedPrefixLength(PHP_DIRECTIVE_CONDITIONAL_START_PATTERN, text, state.index)
      : 0;
    if (conditionalStartLength > 0) {
      textConditionalDepth++;
      if (isBraceDelimitedPhpConditional(text, state.index + conditionalStartLength)) {
        openBraceBasedConditional();
      }
      state.index += conditionalStartLength;
      return true;
    }
    return false;
  }

  /**
   * Consumes a Twig, Razor, or template-delimited token at the current position when one begins here.
   *
   * @param text the fragment being scanned
   * @param state the mutable scan state
   * @return {@code true} when a template conditional token was consumed
   */
  private boolean consumeTemplateConditional(String text, FragmentScanState state) {
    int twigConditionalEndLength = matchedPrefixLength(TWIG_CONDITIONAL_END_PATTERN, text, state.index);
    if (twigConditionalEndLength > 0) {
      applyTextConditionalClosings(1);
      state.index = skipDelimitedBlock(text, state.index + twigConditionalEndLength, "%}");
      return true;
    }

    int twigConditionalStartLength = matchedPrefixLength(TWIG_CONDITIONAL_START_PATTERN, text, state.index);
    if (twigConditionalStartLength > 0) {
      textConditionalDepth++;
      state.index = skipDelimitedBlock(text, state.index + twigConditionalStartLength, "%}");
      return true;
    }

    int conditionalStartLength = matchedPrefixLength(RAZOR_BLOCK_START_PATTERN, text, state.index);
    if (conditionalStartLength == 0) {
      conditionalStartLength = matchedPrefixLength(RAZOR_BRANCH_START_PATTERN, text, state.index);
    }
    if (conditionalStartLength > 0) {
      textConditionalDepth++;
      openBraceBasedConditional();
      state.index += conditionalStartLength;
      return true;
    }

    int skippedTemplateBlock = skipTemplateBlock(text, state.index);
    if (skippedTemplateBlock > state.index) {
      state.index = skippedTemplateBlock;
      return true;
    }
    return false;
  }

  /**
   * Consumes structural delimiters and brace bookkeeping at the current position when needed.
   *
   * @param text the fragment being scanned
   * @param state the mutable scan state
   * @return {@code true} when a structural token was consumed
   */
  private boolean consumeStructuralToken(String text, FragmentScanState state) {
    if (startsWith(text, state.index, "}}") || startsWith(text, state.index, "%}") || startsWith(text, state.index, "#}")) {
      state.index += 2;
      return true;
    }
    if (text.charAt(state.index) == '{') {
      if (pendingConditionalBranchOpenings > 0) {
        pendingConditionalBranchOpenings--;
      } else if (braceBasedTextConditionalDepth > 0) {
        nestedTextBlockDepth++;
      }
      state.index++;
      return true;
    }
    if (text.charAt(state.index) == '}') {
      if (nestedTextBlockDepth > 0) {
        nestedTextBlockDepth--;
      } else if (braceBasedTextConditionalDepth > 0) {
        if (isConditionalContinuation(text, state.index)) {
          pendingConditionalBranchOpenings++;
        } else {
          braceBasedTextConditionalDepth--;
          applyTextConditionalClosings(1);
          if (braceBasedTextConditionalDepth == 0) {
            clearBraceTracking();
          }
        }
      }
      state.index++;
      return true;
    }
    return false;
  }

  /**
   * Marks a brace-delimited conditional as open and waits for its branch opening brace.
   */
  private void openBraceBasedConditional() {
    braceBasedTextConditionalDepth++;
    pendingConditionalBranchOpenings++;
  }

  private void applyTextConditionalClosings(int closingCount) {
    if (closingCount > 0) {
      textConditionalDepth = Math.max(0, textConditionalDepth - closingCount);
      if (textConditionalDepth == 0) {
        clearBraceTracking();
      }
    }
  }

  /**
   * Clears the brace-related state once no brace-delimited conditional remains open.
   */
  private void clearBraceTracking() {
    braceBasedTextConditionalDepth = 0;
    pendingConditionalBranchOpenings = 0;
    nestedTextBlockDepth = 0;
  }

  private static boolean isJstlConditionalTag(TagNode node) {
    String nodeName = node.getNodeName();
    return nodeName != null && JSTL_CONDITIONAL_TAGS.contains(nodeName.toLowerCase(Locale.ROOT));
  }

  private static boolean hasConditionalAttribute(TagNode node) {
    return hasAnyAttribute(node, VUE_CONDITIONAL_ATTRS)
      || hasAnyAttribute(node, ANGULAR_CONDITIONAL_ATTRS);
  }

  private static boolean hasAnyAttribute(TagNode node, Set<String> attributes) {
    for (String attribute : attributes) {
      if (node.hasAttribute(attribute)) {
        return true;
      }
    }
    return false;
  }

  private static String unwrapDirective(String code) {
    String trimmed = code.trim();
    if (!trimmed.startsWith("<?") || !trimmed.endsWith("?>")) {
      return trimmed;
    }

    String unwrapped = trimmed.substring(2, trimmed.length() - 2).trim();
    return unwrapped.toLowerCase(Locale.ROOT).startsWith("php")
      ? unwrapped.substring(3).trim()
      : unwrapped;
  }

  /**
   * Checks whether a closing brace at the provided index is followed by an else-like continuation.
   *
   * @param text the scanned fragment
   * @param closingBraceIndex the index of the closing brace to inspect
   * @return {@code true} when the brace continues the current conditional chain
   */
  private static boolean isConditionalContinuation(String text, int closingBraceIndex) {
    int index = skipLeadingTrivia(text, closingBraceIndex + 1);
    if (index < text.length() && text.charAt(index) == '@') {
      index++;
    }

    return startsWithKeyword(text, index, "elseif")
      || startsWithElseIf(text, index)
      || startsWithKeyword(text, index, "else");
  }

  private static boolean startsWithElseIf(String text, int index) {
    if (!startsWithKeyword(text, index, "else")) {
      return false;
    }
    int ifIndex = skipWhitespace(text, index + 4);
    return startsWithKeyword(text, ifIndex, "if");
  }

  private static boolean startsWithKeyword(String text, int index, String keyword) {
    return index >= 0
      && index + keyword.length() <= text.length()
      && text.regionMatches(true, index, keyword, 0, keyword.length())
      && isWordBoundary(text, index + keyword.length());
  }

  /**
   * Returns the length of a pattern matched exactly at the provided index.
   *
   * @param pattern the pattern to test
   * @param text the scanned fragment
   * @param index the current scan index
   * @return the matched length, or {@code 0} when no match starts at {@code index}
   */
  private static int matchedPrefixLength(Pattern pattern, String text, int index) {
    Matcher matcher = pattern.matcher(text);
    matcher.region(index, text.length());
    return matcher.lookingAt() ? matcher.end() - index : 0;
  }

  /**
   * Determines whether a PHP conditional uses structural braces rather than alternative syntax.
   *
   * @param text the unwrapped PHP directive body
   * @param index the index immediately after the conditional keyword
   * @return {@code true} when the first structural token after the header is an opening brace
   */
  private static boolean isBraceDelimitedPhpConditional(String text, int index) {
    boolean inLineComment = false;
    boolean inBlockComment = false;
    boolean escaped = false;
    char quoteDelimiter = '\0';

    for (int current = index; current < text.length(); ) {
      if (inLineComment) {
        if (isLineBreak(text.charAt(current))) {
          inLineComment = false;
        }
        current++;
        continue;
      }
      if (inBlockComment) {
        if (startsWith(text, current, "*/")) {
          inBlockComment = false;
          current += 2;
        } else {
          current++;
        }
        continue;
      }
      if (quoteDelimiter != '\0') {
        char character = text.charAt(current);
        if (escaped) {
          escaped = false;
        } else if (character == '\\') {
          escaped = true;
        } else if (character == quoteDelimiter) {
          quoteDelimiter = '\0';
        }
        current++;
        continue;
      }

      if (startsWith(text, current, "/*")) {
        inBlockComment = true;
        current += 2;
        continue;
      }
      if (startsWith(text, current, "//")) {
        inLineComment = true;
        current += 2;
        continue;
      }
      if (text.charAt(current) == '#') {
        inLineComment = true;
        current++;
        continue;
      }
      if (text.charAt(current) == '\'' || text.charAt(current) == '"') {
        quoteDelimiter = text.charAt(current);
        escaped = false;
        current++;
        continue;
      }
      if (text.charAt(current) == '{') {
        return true;
      }
      if (text.charAt(current) == ':' || text.charAt(current) == ';') {
        return false;
      }
      current++;
    }
    return false;
  }

  private static boolean isWordBoundary(String text, int index) {
    return index >= text.length()
      || (!Character.isLetterOrDigit(text.charAt(index)) && text.charAt(index) != '_');
  }

  private static int skipLeadingTrivia(String text, int index) {
    int current = index;
    boolean advanced = true;
    while (advanced && current < text.length()) {
      int afterWhitespace = skipWhitespace(text, current);
      int afterComment = skipComment(text, afterWhitespace);
      advanced = afterComment > current;
      current = afterComment;
    }
    return current;
  }

  private static int skipWhitespace(String text, int index) {
    int current = index;
    while (current < text.length() && Character.isWhitespace(text.charAt(current))) {
      current++;
    }
    return current;
  }

  private static int skipComment(String text, int index) {
    if (index >= text.length()) {
      return index;
    }
    if (text.charAt(index) == '#') {
      return skipLineComment(text, index + 1);
    }
    if (startsWith(text, index, "//")) {
      return skipLineComment(text, index + 2);
    }
    if (startsWith(text, index, "/*")) {
      return skipBlockComment(text, index + 2);
    }
    return index;
  }

  private static int skipLineComment(String text, int index) {
    int current = index;
    while (current < text.length() && !isLineBreak(text.charAt(current))) {
      current++;
    }
    return current;
  }

  private static int skipBlockComment(String text, int index) {
    int current = index;
    while (current + 1 < text.length() && !startsWith(text, current, "*/")) {
      current++;
    }
    if (current + 1 < text.length()) {
      return current + 2;
    }
    return text.length();
  }

  /**
   * Skips a template block such as {@code {{ ... }}}, {@code {% ... %}}, or {@code {# ... #}}.
   *
   * @param text the scanned fragment
   * @param index the current scan index
   * @return the index after the skipped block, or the original index when no block starts there
   */
  private static int skipTemplateBlock(String text, int index) {
    if (startsWith(text, index, "{{")) {
      return skipDelimitedBlock(text, index + 2, "}}");
    }
    if (startsWith(text, index, "{%")) {
      return skipDelimitedBlock(text, index + 2, "%}");
    }
    if (startsWith(text, index, "{#")) {
      return skipDelimitedBlock(text, index + 2, "#}");
    }
    return index;
  }

  /**
   * Skips characters until the matching closing delimiter is found.
   *
   * @param text the scanned fragment
   * @param startIndex the index immediately after the opening delimiter
   * @param closingDelimiter the delimiter that ends the skipped block
   * @return the index immediately after the closing delimiter, or {@code text.length()} when it is missing
   */
  private static int skipDelimitedBlock(String text, int startIndex, String closingDelimiter) {
    int closingIndex = text.indexOf(closingDelimiter, startIndex);
    if (closingIndex < 0) {
      return text.length();
    }
    return closingIndex + closingDelimiter.length();
  }

  private static boolean startsWith(String text, int index, String token) {
    return index + token.length() <= text.length()
      && text.regionMatches(index, token, 0, token.length());
  }

  private static final class FragmentScanState {

    private int index;
    private boolean inLineComment;
    private boolean inBlockComment;
    private boolean escaped;
    private char quoteDelimiter = '\0';
  }

  private static boolean isLineBreak(char character) {
    return character == '\n' || character == '\r';
  }
}
