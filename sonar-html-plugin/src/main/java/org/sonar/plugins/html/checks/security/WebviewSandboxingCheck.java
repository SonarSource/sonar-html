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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S7071")
public class WebviewSandboxingCheck extends AbstractWebviewCheck {

  private static final String MESSAGE = "Change this code to enable sandboxing.";
  private static final Set<String> NODE_INTEGRATION_ENABLED_VALUES = Set.of("1", "true", "yes");
  private static final Set<String> SANDBOX_DISABLED_VALUES = Set.of("false", "0", "no", "off");

  @Override
  public void startElement(TagNode node) {
    if (!isWebview(node)) {
      return;
    }
    checkNodeIntegration(node);
    checkWebPreferences(node);
  }

  private void checkNodeIntegration(TagNode node) {
    Attribute nodeIntegration = getLiteralAttribute(node, "nodeintegration");
    if (nodeIntegration == null) {
      return;
    }
    createViolationOnAttribute(nodeIntegration, MESSAGE);
  }

  private void checkWebPreferences(TagNode node) {
    Attribute webPreferences = getLiteralAttribute(node, "webpreferences");
    if (webPreferences == null) {
      return;
    }
    String value = webPreferences.getValue();
    if (Helpers.isDynamicValue(value, getHtmlSourceCode())) {
      return;
    }
    Map<String, String> prefs = parseWebPreferences(value);
    String sandbox = prefs.get("sandbox");
    if (sandbox != null && SANDBOX_DISABLED_VALUES.contains(sandbox)) {
      createViolationOnAttribute(webPreferences, MESSAGE);
      return;
    }
    String nodeIntegration = prefs.get("nodeIntegration");
    if (nodeIntegration != null && NODE_INTEGRATION_ENABLED_VALUES.contains(nodeIntegration)) {
      createViolationOnAttribute(webPreferences, MESSAGE);
    }
  }

  private static Map<String, String> parseWebPreferences(String value) {
    Map<String, String> entries = new HashMap<>();
    for (String entry : value.split(",")) {
      int eq = entry.indexOf('=');
      if (eq == 0) {
        continue;
      }
      String key;
      String val;
      if (eq < 0) {
        key = entry.trim();
        val = "true";
      } else {
        key = entry.substring(0, eq).trim();
        val = entry.substring(eq + 1).trim();
      }
      if (key.isEmpty()) {
        continue;
      }
      entries.putIfAbsent(key, val);
    }
    return entries;
  }
}
