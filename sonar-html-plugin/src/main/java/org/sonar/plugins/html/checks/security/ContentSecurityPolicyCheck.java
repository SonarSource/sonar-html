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

import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S7039")
public class ContentSecurityPolicyCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Make sure allowing %s in this Content Security Policy directive is safe here.";

  // Negative lookarounds on [\w-] keep these from matching inside compound tokens like 'wasm-unsafe-eval'.
  private static final Pattern UNSAFE_INLINE = Pattern.compile("(?<![\\w-])unsafe-inline(?![\\w-])", Pattern.CASE_INSENSITIVE);
  private static final Pattern UNSAFE_HASHES = Pattern.compile("(?<![\\w-])unsafe-hashes(?![\\w-])", Pattern.CASE_INSENSITIVE);
  private static final Pattern UNSAFE_EVAL = Pattern.compile("(?<![\\w-])unsafe-eval(?![\\w-])", Pattern.CASE_INSENSITIVE);

  @Override
  public void startElement(TagNode node) {
    if (!node.equalsElementName("meta")) {
      return;
    }
    String httpEquiv = node.getAttribute("http-equiv");
    if (httpEquiv == null || !"Content-Security-Policy".equalsIgnoreCase(httpEquiv)) {
      return;
    }
    String content = node.getAttribute("content");
    if (content == null || Helpers.isDynamicValue(content, getHtmlSourceCode())) {
      return;
    }
    reportUnsafeTokens(node, content);
  }

  /**
   * Emits one violation per insecure CSP source expression found in {@code content}.
   * @param node the meta tag carrying the CSP
   * @param content the value of the meta tag's content attribute
   */
  private void reportUnsafeTokens(TagNode node, String content) {
    if (content.contains("*")) {
      createViolation(node, String.format(MESSAGE, "wildcards"));
    }
    if (UNSAFE_INLINE.matcher(content).find()) {
      createViolation(node, String.format(MESSAGE, "'unsafe-inline'"));
    }
    if (UNSAFE_HASHES.matcher(content).find()) {
      createViolation(node, String.format(MESSAGE, "'unsafe-hashes'"));
    }
    if (UNSAFE_EVAL.matcher(content).find()) {
      createViolation(node, String.format(MESSAGE, "'unsafe-eval'"));
    }
  }

}
