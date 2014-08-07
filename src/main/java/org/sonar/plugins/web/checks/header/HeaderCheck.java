/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
 * dev@sonar.codehaus.org
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
package org.sonar.plugins.web.checks.header;

import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.sonar.api.utils.SonarException;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.CharsetAwareVisitor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@Rule(
  key = "HeaderCheck",
  priority = Priority.BLOCKER)
@WebRule(activeByDefault = false)
@RuleTags({
  RuleTags.CONVENTION
})
public class HeaderCheck extends AbstractPageCheck implements CharsetAwareVisitor {

  private static final String DEFAULT_HEADER_FORMAT = "";
  private static final String MESSAGE = "Add or update the header of this file.";

  @RuleProperty(
    key = "headerFormat",
    type = "TEXT",
    defaultValue = DEFAULT_HEADER_FORMAT)
  public String headerFormat = DEFAULT_HEADER_FORMAT;

  private String[] expectedLines;
  private Charset charset;

  @Override
  public void init() {
    expectedLines = headerFormat.split("(?:\r)?\n|\r");
  }

  @Override
  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  static class HeaderLinesProcessor implements LineProcessor<Boolean> {

    private boolean result = false;
    private int lineNumber = 0;
    private String[] expectedLines;

    public HeaderLinesProcessor(String[] expectedLines) {
      this.expectedLines = expectedLines;
    }

    @Override
    public boolean processLine(String line) throws IOException {
      lineNumber++;
      if (lineNumber == 1) {
        result = true;
      }
      if (lineNumber > expectedLines.length) {
        // we are done checking, stop processor
      } else if (line.equals(expectedLines[lineNumber - 1])) {
        return true;
      } else {
        result = false;
      }
      return false;
    }

    @Override
    public Boolean getResult() {
      return result && lineNumber >= expectedLines.length;
    }

  }

  @Override
  public void startDocument(List<Node> nodes) {
    LineProcessor<Boolean> processor = new HeaderLinesProcessor(expectedLines);
    try {
      Files.readLines(getWebSourceCode().getFile(), charset, processor);
    } catch (IOException e) {
      throw new SonarException(e);
    }
    if (!processor.getResult()) {
      createViolation(0, MESSAGE);
    }
  }

}
