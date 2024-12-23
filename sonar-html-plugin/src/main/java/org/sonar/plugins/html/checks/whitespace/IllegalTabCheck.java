/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.whitespace;

import java.io.IOException;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;

@Rule(key = "IllegalTabCheck")
public class IllegalTabCheck extends AbstractPageCheck {

  @Override
  public void startDocument(List<Node> nodes) {
    String content;
    try {
      content = getHtmlSourceCode().inputFile().contents();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    String[] lines = content.split("\\r?\\n");
    for (int i = 0; i < lines.length; i++) {
      if (lines[i].contains("\t")) {
        createViolation(i + 1, "Replace all tab characters in this file by sequences of white-spaces.");
        break;
      }
    }
  }
}
