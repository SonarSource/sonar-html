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

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

@Rule(key = "S7039")
public class ContentSecurityPolicyCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Make sure allowing %s in this Content Security Policy directive is safe here.";

  private static final String[] DYNAMIC_MARKERS = {"<?php", "{{", "{%", "<?=", "${", "#{", "<%"};

  // A Razor expression starts at an unescaped '@' that begins an identifier, a parenthesised expression,
  // a code block, or a comment. The lookbehind excludes '@' preceded by an identifier char so that emails
  // (security@example.com) and version specifiers (pkg@1.0.0) are not mistaken for expressions.
  private static final Pattern RAZOR_EXPRESSION_IN_CONTENT = Pattern.compile("(?<![\\w@])@(?!@)[A-Za-z_({*]");

  // Directives the user-agent ignores when CSP is delivered via <meta>; see W3C CSP3 §"the meta element".
  // Tokens inside them have no security effect and must not raise issues here.
  private static final Set<String> META_IGNORED_DIRECTIVES = Set.of(
    "frame-ancestors",
    "sandbox",
    "report-uri",
    "report-to"
  );

  // Directives whose values are CSP source lists. Outside this set the tokens are not source expressions
  // (report-to takes an endpoint group, trusted-types takes policy names, etc.), so 'unsafe-inline' or
  // '*' appearing there is just data and must not be flagged.
  private static final Set<String> SOURCE_LIST_DIRECTIVES = Set.of(
    "base-uri",
    "child-src",
    "connect-src",
    "default-src",
    "fenced-frame-src",
    "font-src",
    "form-action",
    "frame-src",
    "img-src",
    "manifest-src",
    "media-src",
    "object-src",
    "prefetch-src",
    "script-src",
    "script-src-attr",
    "script-src-elem",
    "style-src",
    "style-src-attr",
    "style-src-elem",
    "worker-src"
  );

  @Override
  public void startElement(TagNode node) {
    if (!node.equalsElementName("meta")) {
      return;
    }
    String httpEquiv = node.getAttribute("http-equiv");
    if (httpEquiv == null || !isCspHeader(httpEquiv)) {
      return;
    }
    String content = node.getAttribute("content");
    if (content == null || isDynamicCspValue(content)) {
      return;
    }
    reportUnsafeTokens(node, content);
  }

  private static boolean isCspHeader(String httpEquiv) {
    return "Content-Security-Policy".equalsIgnoreCase(httpEquiv)
      || "Content-Security-Policy-Report-Only".equalsIgnoreCase(httpEquiv);
  }

  // Cannot reuse Helpers.isDynamicValue: its Razor heuristic treats any '@' as dynamic, which over-skips
  // static CSPs containing literal '@' in mailto: / npm scope / version tokens.
  private boolean isDynamicCspValue(String value) {
    for (String marker : DYNAMIC_MARKERS) {
      if (value.contains(marker)) {
        return true;
      }
    }
    HtmlSourceCode code = getHtmlSourceCode();
    return Helpers.isRazorFile(code) && RAZOR_EXPRESSION_IN_CONTENT.matcher(value).find();
  }

  private void reportUnsafeTokens(TagNode node, String content) {
    boolean wildcardReported = false;
    boolean unsafeInlineReported = false;
    boolean unsafeHashesReported = false;
    boolean unsafeEvalReported = false;
    for (String directive : content.split(";")) {
      String trimmed = directive.trim();
      if (trimmed.isEmpty()) {
        continue;
      }
      String[] tokens = trimmed.split("\\s+");
      String directiveName = tokens[0].toLowerCase(Locale.ROOT);
      if (META_IGNORED_DIRECTIVES.contains(directiveName) || !SOURCE_LIST_DIRECTIVES.contains(directiveName)) {
        continue;
      }
      for (int i = 1; i < tokens.length; i++) {
        String token = tokens[i];
        if (!wildcardReported && containsWildcard(token)) {
          createViolation(node, String.format(MESSAGE, "wildcards"));
          wildcardReported = true;
        }
        String keyword = stripSingleQuotes(token);
        if (!unsafeInlineReported && "unsafe-inline".equalsIgnoreCase(keyword)) {
          createViolation(node, String.format(MESSAGE, "'unsafe-inline'"));
          unsafeInlineReported = true;
        } else if (!unsafeHashesReported && "unsafe-hashes".equalsIgnoreCase(keyword)) {
          createViolation(node, String.format(MESSAGE, "'unsafe-hashes'"));
          unsafeHashesReported = true;
        } else if (!unsafeEvalReported && "unsafe-eval".equalsIgnoreCase(keyword)) {
          createViolation(node, String.format(MESSAGE, "'unsafe-eval'"));
          unsafeEvalReported = true;
        }
      }
    }
  }

  private static boolean containsWildcard(String token) {
    return token.indexOf('*') >= 0;
  }

  private static String stripSingleQuotes(String token) {
    if (token.length() >= 2 && token.charAt(0) == '\'' && token.charAt(token.length() - 1) == '\'') {
      return token.substring(1, token.length() - 1);
    }
    return token;
  }

}
