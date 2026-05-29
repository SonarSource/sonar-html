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
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S7071")
public class WebviewSandboxingCheck extends AbstractWebviewCheck {

  private static final String MESSAGE = "Change this code to enable sandboxing.";
  private static final Set<String> ENABLED_NODE_INTEGRATION_VALUES = Set.of("", "1", "on", "true", "yes");
  private static final Pattern SANDBOX_DISABLED_PATTERN = Pattern.compile("(?i)(^|[^\\w-])sandbox\\s*=\\s*false(?=$|[^\\w-])");

  /**
   * Raises an issue when a webview disables sandboxing.
   *
   * @param node the tag being visited
   */
  @Override
  public void startElement(TagNode node) {
    if (!isWebview(node)) {
      return;
    }

    checkNodeIntegration(node);
    checkWebPreferences(node);
  }

  /**
   * Raises an issue when node integration is enabled on a webview.
   *
   * @param node the webview to inspect
   */
  private void checkNodeIntegration(TagNode node) {
    Attribute nodeIntegration = node.getProperty("nodeintegration");
    if (nodeIntegration != null && enablesNodeIntegration(nodeIntegration)) {
      createViolationOnAttribute(node, nodeIntegration, MESSAGE);
    }
  }

  /**
   * Raises an issue when webpreferences disables sandboxing.
   *
   * @param node the webview to inspect
   */
  private void checkWebPreferences(TagNode node) {
    Attribute webPreferences = node.getProperty("webpreferences");
    if (webPreferences != null && disablesSandbox(webPreferences)) {
      createViolationOnAttribute(node, webPreferences, MESSAGE);
    }
  }

  /**
   * Tells whether a nodeintegration attribute enables the insecure mode.
   *
   * @param attribute the attribute to inspect
   * @return {@code true} when node integration is enabled
   */
  private static boolean enablesNodeIntegration(Attribute attribute) {
    String normalizedValue = attribute.getValue().trim().toLowerCase(Locale.ROOT);
    return ENABLED_NODE_INTEGRATION_VALUES.contains(normalizedValue);
  }

  /**
   * Tells whether a webpreferences attribute disables sandboxing.
   *
   * @param attribute the attribute to inspect
   * @return {@code true} when sandboxing is disabled
   */
  private static boolean disablesSandbox(Attribute attribute) {
    return SANDBOX_DISABLED_PATTERN.matcher(attribute.getValue()).find();
  }
}
