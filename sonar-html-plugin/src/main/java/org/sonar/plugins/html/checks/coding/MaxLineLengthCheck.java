/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.plugins.html.checks.coding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "MaxLineLengthCheck")
public class MaxLineLengthCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_LINE_LENGTH = 120;

  @RuleProperty(
    key = "maxLength",
    description = "The maximum authorized line length.",
    defaultValue = "" + DEFAULT_MAX_LINE_LENGTH)
  public int maxLength = DEFAULT_MAX_LINE_LENGTH;

  private final Set<Integer> ignoredLines = new HashSet<>();

  @Override
  public void endDocument() {
    List<String> lines = readLines(getHtmlSourceCode().inputFile());

    for (int i = 0; i < lines.size(); i++) {
      int length = lines.get(i).length();
      if (length > maxLength && !ignoredLines.contains(i + 1)) {
        createViolation(
            i + 1,
            "Split this " + length + " characters long line (which is greater than " + maxLength + " authorized).");
      }
    }
    ignoredLines.clear();
  }

  @Override
  public void startElement(TagNode node) {
    TagNode nodeParent = node.getParent();
    // We do ignore lines that include an SVG path tag. Splitting SVG path descriptions does not necessarily make them more readable. See SONARHTML-147.
    if ("PATH".equalsIgnoreCase(node.getNodeName()) && nodeParent != null && "SVG".equalsIgnoreCase(nodeParent.getNodeName())) {
      for (int i = node.getStartLinePosition(); i <= node.getEndLinePosition(); i++) {
        ignoredLines.add(i);
      }
    }
  }

  private static List<String> readLines(InputFile file) {
    try (BufferedReader br = new BufferedReader(new StringReader(file.contents()))) {
      return br.lines().toList();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read " + file, e);
    }
  }

}
