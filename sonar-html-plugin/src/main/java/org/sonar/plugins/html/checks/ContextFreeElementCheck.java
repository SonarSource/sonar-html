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
package org.sonar.plugins.html.checks;

import java.io.StringReader;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

/**
 * Base class for checks whose entire logic is self-contained within a single opening tag's
 * attributes — no parent, sibling, or child context required.
 *
 * <p>Subclasses automatically receive {@link #startElement(TagNode)} calls for HTML tags
 * found inside PHP double-quoted string literals (e.g. {@code $html = "<div role=\"x\">"}).
 * PHP interpolations ({@code $var}, {@code {$expr}}, {@code <?= ... ?>}) are replaced with
 * the {@code ${dynamic}} sentinel before dispatch, so {@code Helpers.isDynamicValue} suppresses
 * them exactly as it does for JSP/EL expressions in ordinary HTML.
 */
public abstract class ContextFreeElementCheck extends AbstractPageCheck {

  private static final Pattern EMBEDDED_HTML = Pattern.compile("<\\s*[/a-zA-Z]");
  private static final Pattern PHP_INTERPOLATION = Pattern.compile("\\{\\$[^}]+\\}|<\\?=.*?\\?>");
  private static final Pattern PHP_VAR = Pattern.compile("\\$\\{?[a-zA-Z_]\\w*\\}?");
  private static final String DYNAMIC = "${dynamic}";

  @Override
  public final void directive(DirectiveNode node) {
    if (!isPhpDirective(node)) {
      return;
    }
    for (Attribute attr : node.getAttributes()) {
      String value = attr.getValue();
      if (value == null || !EMBEDDED_HTML.matcher(value).find()) {
        continue;
      }
      String sanitized = replaceInterpolations(value);
      List<Node> reLexed = new PageLexer().parse(new StringReader(sanitized));
      for (Node child : reLexed) {
        if (child instanceof TagNode tag && !tag.isEndElement()) {
          normalizeDynamicAttributes(tag);
          tag.setStartLinePosition(node.getStartLinePosition());
          startElement(tag);
        }
      }
    }
  }

  private static boolean isPhpDirective(DirectiveNode node) {
    String name = node.getNodeName();
    return name != null && name.toLowerCase(Locale.ROOT).startsWith("?php");
  }

  private static String replaceInterpolations(String value) {
    String replacement = Matcher.quoteReplacement(DYNAMIC);
    String s = PHP_INTERPOLATION.matcher(value).replaceAll(replacement);
    return PHP_VAR.matcher(s).replaceAll(replacement);
  }

  private static void normalizeDynamicAttributes(TagNode tag) {
    for (Attribute attr : tag.getAttributes()) {
      String v = attr.getValue();
      if (v != null && v.contains(DYNAMIC)) {
        attr.setValue(DYNAMIC);
      }
    }
  }
}
