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

import org.sonar.sslr.channel.CodeReader;
import org.sonar.sslr.channel.EndMatcher;

/*
 * Detects the end `>` of an HTML element, on top of the basic `<` ... `>` nesting count from
 * the generic EndTokenMatcher. The generic matcher gets fooled by `<` or `>` characters that
 * appear inside attribute values or inside JSP/ERB scriptlets; this one tracks three extra
 * states to avoid that.
 *
 * Double-quoted attribute value.
 *   Opens on `"`, closes on the next `"`. Same as the generic matcher.
 *
 * Single-quoted attribute value.
 *   Opens on `'` only when the previous non-whitespace char is `=`, so a stray apostrophe in
 *   text or in code (like `Ask'Once` or `l'evenement`) doesn't open a scope. Closes only when
 *   the next char is an attribute terminator: whitespace, `>`, `/`, `=`, `"`, or EOF. The
 *   terminator gate is what keeps EL string literals like `${'foo'}` from closing the
 *   surrounding attribute — the inner `'` is followed by `}`, which isn't a terminator.
 *
 * JSP/ERB scriptlet.
 *   A `<%` opens an opaque sub-scope — both at the top level and inside an attribute value,
 *   so a scriptlet embedded in an attribute (`<input value='<%= ... %>' />`) is also handled.
 *   Quote tracking is suspended and `<` / `>` inside the scriptlet are ignored for nesting.
 *   The scope closes on the matching `%>`. This is what stops Ruby/Java char literals like
 *   `'x'` inside `<%= ... %>` from being mistaken for HTML attribute quotes.
 */
class HtmlElementEndMatcher implements EndMatcher {

  private final CodeReader codeReader;
  private int activeQuote;
  private int previousChar;
  private int previousNonWhitespace;
  private int nesting;
  private int jspNesting;

  HtmlElementEndMatcher(CodeReader codeReader) {
    this.codeReader = codeReader;
  }

  @Override
  public boolean match(int endFlag) {
    boolean enteringJsp = previousChar == '<' && endFlag == '%' && jspNesting == 0;
    boolean exitingJsp = previousChar == '%' && endFlag == '>' && jspNesting > 0;

    boolean result = false;
    if (enteringJsp) {
      jspNesting++;
      // The preceding '<' only incremented nesting when we were outside any quote scope; undo
      // it in that case so the <%...%> doesn't leave a residue.
      if (activeQuote == 0) {
        nesting--;
      }
    } else if (jspNesting > 0) {
      if (exitingJsp) {
        jspNesting--;
      }
    } else {
      updateQuoteState(endFlag);
      if (activeQuote == 0) {
        if (endFlag == '<') {
          nesting++;
        } else if (endFlag == '>') {
          nesting--;
          result = nesting < 0;
        }
      }
    }

    previousChar = endFlag;
    if (!Character.isWhitespace(endFlag)) {
      previousNonWhitespace = endFlag;
    }
    return result;
  }

  private void updateQuoteState(int endFlag) {
    if (endFlag == '"') {
      if (activeQuote == 0) {
        activeQuote = endFlag;
      } else if (activeQuote == endFlag) {
        activeQuote = 0;
      }
    } else if (endFlag == '\'') {
      if (activeQuote == 0 && previousNonWhitespace == '=') {
        activeQuote = endFlag;
      } else if (activeQuote == endFlag && isAttributeTerminator(charAfterPeek())) {
        activeQuote = 0;
      }
    }
  }

  private char charAfterPeek() {
    char[] lookahead = codeReader.peek(2);
    return lookahead.length >= 2 ? lookahead[1] : '\0';
  }

  private static boolean isAttributeTerminator(char c) {
    return c == '\0' || c == '>' || c == '/' || c == '=' || c == '"' || Character.isWhitespace(c);
  }
}
