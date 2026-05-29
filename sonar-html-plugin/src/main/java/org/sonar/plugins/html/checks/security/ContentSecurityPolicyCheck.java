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

import java.util.HashSet;
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

  // A Razor expression starts at an unescaped '@' that begins an identifier, a parenthesised expression,
  // a code block, or a comment. The lookbehind excludes '@' preceded by an identifier char so that emails
  // (security@example.com) and version specifiers (pkg@1.0.0) are not mistaken for expressions. Kept local
  // because Helpers.RAZOR_EXPRESSION is intentionally broader for short-token attributes (lang, role, id).
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

  private boolean isDynamicCspValue(String value) {
    HtmlSourceCode code = getHtmlSourceCode();
    return Helpers.containsServerSideMarker(value)
      || (Helpers.isRazorFile(code) && RAZOR_EXPRESSION_IN_CONTENT.matcher(value).find());
  }

  private void reportUnsafeTokens(TagNode node, String content) {
    Set<String> reported = new HashSet<>();
    for (String directive : content.split(";")) {
      inspectDirective(node, directive.trim(), reported);
    }
  }

  private void inspectDirective(TagNode node, String directive, Set<String> reported) {
    if (directive.isEmpty()) {
      return;
    }
    String[] tokens = directive.split("\\s+");
    if (!isScannableDirective(tokens[0])) {
      return;
    }
    for (int i = 1; i < tokens.length; i++) {
      String label = classifyToken(tokens[i]);
      if (label != null && reported.add(label)) {
        createViolation(node, String.format(MESSAGE, label));
      }
    }
  }

  private static boolean isScannableDirective(String name) {
    String lower = name.toLowerCase(Locale.ROOT);
    return !META_IGNORED_DIRECTIVES.contains(lower) && SOURCE_LIST_DIRECTIVES.contains(lower);
  }

  private static String classifyToken(String token) {
    if (token.indexOf('*') >= 0) {
      return "wildcards";
    }
    return switch (stripSingleQuotes(token).toLowerCase(Locale.ROOT)) {
      case "unsafe-inline" -> "'unsafe-inline'";
      case "unsafe-hashes" -> "'unsafe-hashes'";
      case "unsafe-eval" -> "'unsafe-eval'";
      default -> null;
    };
  }

  private static String stripSingleQuotes(String token) {
    if (token.length() >= 2 && token.charAt(0) == '\'' && token.charAt(token.length() - 1) == '\'') {
      return token.substring(1, token.length() - 1);
    }
    return token;
  }

}
