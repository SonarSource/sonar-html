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

import javax.annotation.CheckForNull;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

abstract class AbstractWebviewCheck extends AbstractPageCheck {

  protected final boolean isWebview(TagNode node) {
    return node.equalsElementName("webview");
  }

  @CheckForNull
  protected static Attribute getLiteralAttribute(TagNode node, String name) {
    for (Attribute attribute : node.getAttributes()) {
      if (name.equalsIgnoreCase(attribute.getName())) {
        return attribute;
      }
    }
    return null;
  }
}
