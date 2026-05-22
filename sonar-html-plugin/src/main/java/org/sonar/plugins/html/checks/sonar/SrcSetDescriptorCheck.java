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
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;


@Rule(key = "S8697")
public class SrcSetDescriptorCheck extends AbstractPageCheck {
  // Per the HTML srcset spec:
  //   - a width descriptor is a "valid non-negative integer" (one or more digits) with value > 0,
  //     followed by 'w'. Leading zeros are allowed, e.g. "01w" is the same as "1w".
  //   - a density descriptor is a "valid floating-point number" with value > 0, followed by 'x'.
  //     The grammar accepts an optional [eE][+-]?digits exponent and requires at least one digit
  //     after a '.' when one is written (so "1." is invalid, "1.0" and ".5" are valid).
  // The mantissa alternatives are split so we can require at least one non-zero digit; otherwise
  // the value is zero regardless of the exponent.
  private static final Pattern VALID_DESCRIPTOR = Pattern.compile(
      "0*[1-9]\\d*w"
      + "|(?:\\d*[1-9]\\d*(?:\\.\\d+)?|0+\\.\\d*[1-9]\\d*|\\.\\d*[1-9]\\d*)(?:[eE][+-]?\\d+)?x"
  );

  @Override
  public void startElement(TagNode node) {
    if (!isTargetedNode(node)) {
      return;
    }

    String srcSetValue = node.getAttribute("srcset");
    if (srcSetValue == null || Helpers.isDynamicValue(srcSetValue, getHtmlSourceCode())) {
      return;
    }

    List<Candidate> candidates = SrcSetParser.parse(srcSetValue);
    if (candidates.size() == 1
        && candidates.get(0).descriptors.isEmpty()
        && isIdiomaticSingleSource(node)) {
      return;
    }

    Candidate invalid = findInvalidCandidate(candidates);
    if (invalid != null) {
      createViolation(node,
          "Element \"" + node.getNodeName()
          + "\" has no valid and explicit descriptor for srcset candidate \"" + invalid.url + "\".");
    }
  }

  private Candidate findInvalidCandidate(List<Candidate> candidates) {
    for (Candidate candidate : candidates) {
      if (Helpers.isDynamicValue(candidate.url, getHtmlSourceCode())) {
        continue;
      }
      if (!isValidCandidate(candidate)) {
        return candidate;
      }
    }
    return null;
  }

  private static boolean isValidCandidate(Candidate candidate) {
    return candidate.descriptors.size() == 1
        && VALID_DESCRIPTOR.matcher(candidate.descriptors.get(0)).matches();
  }

  private static boolean isTargetedNode(TagNode node) {
    return (isImageNode(node) || isSourceNode(node)) && node.hasAttribute("srcset");
  }

  private static boolean isImageNode(TagNode node) {
    return "img".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isSourceNode(TagNode node) {
    return "source".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isIdiomaticSingleSource(TagNode node) {
    return isSourceNode(node) && (node.hasAttribute("media") || node.hasAttribute("type"));
  }

  /**
   * Parses a srcset attribute value into a list of candidates following the HTML spec.
   */
  private static final class SrcSetParser {
    private final String input;
    private int position;

    private SrcSetParser(String input) {
      this.input = input;
    }

    static List<Candidate> parse(String input) {
      SrcSetParser parser = new SrcSetParser(input);
      List<Candidate> candidates = new ArrayList<>();
      while (!parser.atEnd()) {
        parser.skipSeparators();
        if (parser.atEnd()) {
          break;
        }
        candidates.add(parser.parseCandidate());
      }
      return candidates;
    }

    private Candidate parseCandidate() {
      String rawUrl = consumeNonWhitespace();
      String url = stripTrailingCommas(rawUrl);
      // Per spec, a URL ending in commas signals that the candidate has no descriptors.
      List<String> descriptors = rawUrl.equals(url) ? consumeDescriptors() : Collections.emptyList();
      return new Candidate(url, descriptors);
    }

    private List<String> consumeDescriptors() {
      List<String> descriptors = new ArrayList<>();
      skipWhitespace();
      while (!atEnd() && peek() != ',') {
        String token = consumeUntilWhitespaceOrComma();
        if (!token.isEmpty()) {
          descriptors.add(token);
        }
        skipWhitespace();
      }
      return descriptors;
    }

    private void skipSeparators() {
      while (!atEnd() && (isAsciiWhitespace(peek()) || peek() == ',')) {
        position++;
      }
    }

    private void skipWhitespace() {
      while (!atEnd() && isAsciiWhitespace(peek())) {
        position++;
      }
    }

    private String consumeNonWhitespace() {
      int start = position;
      while (!atEnd() && !isAsciiWhitespace(peek())) {
        position++;
      }
      return input.substring(start, position);
    }

    private String consumeUntilWhitespaceOrComma() {
      int start = position;
      while (!atEnd() && !isAsciiWhitespace(peek()) && peek() != ',') {
        position++;
      }
      return input.substring(start, position);
    }

    private boolean atEnd() {
      return position >= input.length();
    }

    private char peek() {
      return input.charAt(position);
    }

    private static String stripTrailingCommas(String value) {
      int end = value.length();
      while (end > 0 && value.charAt(end - 1) == ',') {
        end--;
      }
      return value.substring(0, end);
    }

    private static boolean isAsciiWhitespace(char c) {
      return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f';
    }
  }

  private static final class Candidate {
    final String url;
    final List<String> descriptors;

    Candidate(String url, List<String> descriptors) {
      this.url = url;
      this.descriptors = descriptors;
    }
  }
}
