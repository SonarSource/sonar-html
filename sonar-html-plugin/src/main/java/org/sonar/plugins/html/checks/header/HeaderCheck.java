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
package org.sonar.plugins.html.checks.header;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;

@Rule(key = "HeaderCheck")
public class HeaderCheck extends AbstractPageCheck {

  private static final String DEFAULT_HEADER_FORMAT = "";
  private static final String MESSAGE = "Add or update the header of this file.";

  @RuleProperty(
    key = "headerFormat",
    description = "Expected copyright and license header (plain text)",
    defaultValue = DEFAULT_HEADER_FORMAT,
    type = "TEXT")
  public String headerFormat = DEFAULT_HEADER_FORMAT;

  @RuleProperty(
    key = "isRegularExpression",
    description = "Whether the headerFormat is a regular expression",
    defaultValue = "false")
  public boolean isRegularExpression = false;

  private String[] expectedLines;
  private Pattern searchPattern = null;

  @Override
  public void init() {
    if (isRegularExpression) {
      try {
        searchPattern = Pattern.compile(headerFormat, Pattern.DOTALL);
      } catch (RuntimeException e) {
        throw new IllegalArgumentException("[" + getClass().getSimpleName() + "] Unable to compile the regular expression: " + headerFormat, e);
      }
    } else {
      expectedLines = headerFormat.split("(?:\r)?\n|\r");
    }
  }

  @Override
  public void startDocument(List<Node> nodes) {
    String fileContent;
    try {
      fileContent = getHtmlSourceCode().inputFile().contents();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    if (isRegularExpression) {
      checkRegularExpression(fileContent);
    } else {
      HeaderLinesProcessor processor = new HeaderLinesProcessor(expectedLines);
      try (BufferedReader br = new BufferedReader(new StringReader(fileContent))) {
        List<String> lines = br.lines().toList();
        for (String line : lines) {
          if (!processor.processLine(line)) {
            break;
          }
        }
        if (!processor.getResult()) {
          createViolation(0, MESSAGE);
        }
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  private void checkRegularExpression(String fileContent) {
    Matcher matcher = searchPattern.matcher(fileContent);
    if (!matcher.find() || matcher.start() != 0) {
      createViolation(0, MESSAGE);
    }
  }

  private static class HeaderLinesProcessor {

    private boolean result = false;
    private int lineNumber = 0;
    private String[] expectedLines;

    HeaderLinesProcessor(String[] expectedLines) {
      this.expectedLines = expectedLines;
    }

    boolean processLine(String line) {
      lineNumber++;
      if (lineNumber == 1) {
        result = true;
      }
      if (lineNumber > expectedLines.length) {
        // we are done checking, stop processor
        return false;
      } else if (line.equals(expectedLines[lineNumber - 1])) {
        return true;
      } else {
        result = false;
        return false;
      }
    }

    boolean getResult() {
      return result && lineNumber >= expectedLines.length;
    }

  }

}
