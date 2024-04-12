/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.html.checks.coding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
