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
  private static final Pattern TWIG_CONDITIONAL_START_PATTERN = Pattern.compile("\\{%[-\\s]*(if|for)\\b", Pattern.CASE_INSENSITIVE);
  private static final Pattern PHP_CONDITIONAL_START_PATTERN = Pattern.compile("<\\?(?:php)?\\s*(if|foreach|for)\\b", Pattern.CASE_INSENSITIVE);

  private static final Pattern TWIG_CONDITIONAL_END_PATTERN = Pattern.compile("\\{%[-\\s]*(endif|endfor)\\b", Pattern.CASE_INSENSITIVE);
  private static final Pattern PHP_CONDITIONAL_END_PATTERN = Pattern.compile("<\\?(?:php)?\\s*(endif|endforeach|endfor)\\b", Pattern.CASE_INSENSITIVE);

  private int textConditionalDepth;
  private int tagConditionalDepth;

  public void reset() {
    textConditionalDepth = 0;
    tagConditionalDepth = 0;
  }

  public void visitText(TextNode textNode) {
    updateTextConditionalDepth(textNode.getCode(), textNode.getCode());
  }

  public void visitDirective(DirectiveNode directiveNode) {
    String code = directiveNode.getCode();
    updateTextConditionalDepth(code, unwrapDirective(code));
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

  private void updateTextConditionalDepth(String conditionalText, String braceText) {
    int conditionalStarts = incrementTextConditionalDepthForStarts(conditionalText);
    decrementTextConditionalDepthForExplicitEnds(conditionalText);
    applyStructuralClosingBraces(conditionalText, braceText, conditionalStarts);
  }

  private int incrementTextConditionalDepthForStarts(String conditionalText) {
    int starts = countConditionalStarts(conditionalText);
    textConditionalDepth += starts;
    return starts;
  }

  private void decrementTextConditionalDepthForExplicitEnds(String conditionalText) {
    applyTextConditionalClosings(countConditionalEnds(conditionalText));
  }

  private void applyStructuralClosingBraces(String conditionalText, String braceText, int conditionalStarts) {
    applyTextConditionalClosings(countStructuralClosings(conditionalText, braceText, conditionalStarts));
  }

  private int countStructuralClosings(String conditionalText, String braceText, int conditionalStarts) {
    if (!hasOpenTextConditional() || !braceText.contains("}")) {
      return 0;
    }

    String trimmed = braceText.trim();
    boolean continuation = isConditionalContinuation(trimmed);
    int structuralClosings = opensAndClosesInSameFragment(conditionalText, conditionalStarts)
      ? StructuralClosingBraceCounter.count(trimmed)
      : LeadingClosingBraceCounter.count(trimmed);

    if (continuation && structuralClosings > 0) {
      return structuralClosings - 1;
    }
    return structuralClosings;
  }

  private boolean hasOpenTextConditional() {
    return textConditionalDepth > 0;
  }

  private static boolean opensAndClosesInSameFragment(String conditionalText, int conditionalStarts) {
    return conditionalStarts > 0 && !conditionalText.contains("{%");
  }

  private void applyTextConditionalClosings(int closingCount) {
    if (closingCount > 0) {
      textConditionalDepth = Math.max(0, textConditionalDepth - closingCount);
    }
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

  private static int countMatches(Pattern pattern, String text) {
    Matcher matcher = pattern.matcher(text);
    int matches = 0;
    while (matcher.find()) {
      matches++;
    }
    return matches;
  }

  private static int countConditionalStarts(String conditionalText) {
    return countMatches(RAZOR_BLOCK_START_PATTERN, conditionalText)
      + countMatches(RAZOR_BRANCH_START_PATTERN, conditionalText)
      + countMatches(TWIG_CONDITIONAL_START_PATTERN, conditionalText)
      + countMatches(PHP_CONDITIONAL_START_PATTERN, conditionalText);
  }

  private static int countConditionalEnds(String conditionalText) {
    return countMatches(TWIG_CONDITIONAL_END_PATTERN, conditionalText)
      + countMatches(PHP_CONDITIONAL_END_PATTERN, conditionalText);
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

  private static boolean isConditionalContinuation(String text) {
    int index = skipLeadingTrivia(text, 0);
    if (!startsWithSingleClosingBrace(text, index)) {
      return false;
    }

    index = skipLeadingTrivia(text, index + 1);
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

  private static boolean isWordBoundary(String text, int index) {
    return index >= text.length()
      || (!Character.isLetterOrDigit(text.charAt(index)) && text.charAt(index) != '_');
  }

  private static boolean startsWithSingleClosingBrace(String text, int index) {
    return index < text.length()
      && text.charAt(index) == '}'
      && !(index + 1 < text.length() && text.charAt(index + 1) == '}');
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

  private static boolean startsWith(String text, int index, String token) {
    return index + token.length() <= text.length()
      && text.regionMatches(index, token, 0, token.length());
  }

  private static final class LeadingClosingBraceCounter {

    private final String text;
    private int index;

    private LeadingClosingBraceCounter(String text) {
      this.text = text;
    }

    private static int count(String text) {
      return new LeadingClosingBraceCounter(text).count();
    }

    private int count() {
      int closingBraces = 0;
      while (hasMoreCharacters()) {
        skipWhitespaceAndComments();
        if (!hasMoreCharacters() || startsWithDoubleClosingBrace() || currentChar() != '}') {
          return closingBraces;
        }
        closingBraces++;
        index++;
      }
      return closingBraces;
    }

    private void skipWhitespaceAndComments() {
      boolean advanced = true;
      while (advanced && hasMoreCharacters()) {
        advanced = skipWhitespace() || skipLineComment() || skipBlockComment();
      }
    }

    private boolean skipWhitespace() {
      int start = index;
      while (hasMoreCharacters() && Character.isWhitespace(currentChar())) {
        index++;
      }
      return index > start;
    }

    private boolean skipLineComment() {
      if (!startsLineComment()) {
        return false;
      }
      index += currentChar() == '#' ? 1 : 2;
      while (hasMoreCharacters() && !isLineBreak(currentChar())) {
        index++;
      }
      return true;
    }

    private boolean skipBlockComment() {
      if (!startsBlockComment()) {
        return false;
      }
      index += 2;
      while (hasMoreCharacters() && !startsWith("*/")) {
        index++;
      }
      if (startsWith("*/")) {
        index += 2;
      }
      return true;
    }

    private boolean startsLineComment() {
      return currentChar() == '#' || startsWith("//");
    }

    private boolean startsBlockComment() {
      return startsWith("/*");
    }

    private boolean startsWithDoubleClosingBrace() {
      return startsWith("}}");
    }

    private boolean startsWith(String token) {
      return text.regionMatches(index, token, 0, token.length());
    }

    private boolean hasMoreCharacters() {
      return index < text.length();
    }

    private char currentChar() {
      return text.charAt(index);
    }
  }

  private static final class StructuralClosingBraceCounter {

    private final String text;
    private int index;
    private boolean inLineComment;
    private boolean inBlockComment;
    private boolean escaped;
    private char quoteDelimiter;

    private StructuralClosingBraceCounter(String text) {
      this.text = text;
    }

    private static int count(String text) {
      return new StructuralClosingBraceCounter(text).count();
    }

    private int count() {
      int closingBraces = 0;
      while (hasMoreCharacters()) {
        closingBraces += scanCurrentCharacter();
      }
      return closingBraces;
    }

    private int scanCurrentCharacter() {
      if (inLineComment) {
        advanceLineComment();
        return 0;
      }
      if (inBlockComment) {
        advanceBlockComment();
        return 0;
      }
      if (isInsideQuotedString()) {
        advanceQuotedString();
        return 0;
      }
      if (startsBlockComment()) {
        inBlockComment = true;
        index += 2;
        return 0;
      }
      if (startsLineComment()) {
        enterLineComment();
        return 0;
      }
      if (startsQuotedString()) {
        quoteDelimiter = currentChar();
        escaped = false;
        index++;
        return 0;
      }
      if (startsWithDoubleClosingBrace()) {
        index += 2;
        return 0;
      }
      return consumeClosingBrace();
    }

    private void advanceLineComment() {
      if (isLineBreak(currentChar())) {
        inLineComment = false;
      }
      index++;
    }

    private void advanceBlockComment() {
      if (startsWith("*/")) {
        inBlockComment = false;
        index += 2;
      } else {
        index++;
      }
    }

    private void advanceQuotedString() {
      char current = currentChar();
      if (escaped) {
        escaped = false;
      } else if (current == '\\') {
        escaped = true;
      } else if (current == quoteDelimiter) {
        quoteDelimiter = '\0';
      }
      index++;
    }

    private void enterLineComment() {
      inLineComment = true;
      index += currentChar() == '#' ? 1 : 2;
    }

    private boolean isInsideQuotedString() {
      return quoteDelimiter != '\0';
    }

    private boolean startsBlockComment() {
      return startsWith("/*");
    }

    private boolean startsLineComment() {
      return currentChar() == '#' || startsWith("//");
    }

    private boolean startsQuotedString() {
      char current = currentChar();
      return current == '\'' || current == '"';
    }

    private boolean startsWithDoubleClosingBrace() {
      return startsWith("}}");
    }

    private int consumeClosingBrace() {
      int closingBraces = currentChar() == '}' ? 1 : 0;
      index++;
      return closingBraces;
    }

    private boolean startsWith(String token) {
      return text.regionMatches(index, token, 0, token.length());
    }

    private boolean hasMoreCharacters() {
      return index < text.length();
    }

    private char currentChar() {
      return text.charAt(index);
    }
  }

  private static boolean isLineBreak(char character) {
    return character == '\n' || character == '\r';
  }
}
