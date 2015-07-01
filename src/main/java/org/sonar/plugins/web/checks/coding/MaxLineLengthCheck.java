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
package org.sonar.plugins.web.checks.coding;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.sonar.api.utils.SonarException;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.CharsetAwareVisitor;

import com.google.common.io.Files;

@Rule(
  key = "MaxLineLengthCheck",
  priority = Priority.MAJOR,
  name = "Lines should not be too long")
@WebRule(activeByDefault = false)
@RuleTags({
  RuleTags.CONVENTION
})
public class MaxLineLengthCheck extends AbstractPageCheck implements CharsetAwareVisitor {

  private static final int DEFAULT_MAX_LINE_LENGTH = 120;

  @RuleProperty(
    key = "maxLength",
    description = "The maximum authorized line length",
    defaultValue = "" + DEFAULT_MAX_LINE_LENGTH)
  public int maxLength = DEFAULT_MAX_LINE_LENGTH;

  private Charset charset;

  @Override
  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  @Override
  public void startDocument(List<Node> nodes) {
    List<String> lines = readLines(getWebSourceCode().inputFile().file());

    for (int i = 0; i < lines.size(); i++) {
      int length = lines.get(i).length();
      if (length > maxLength) {
        createViolation(
            i + 1,
            "Split this " + length + " characters long line (which is greater than " + maxLength + " authorized).");
      }
    }
  }

  private List<String> readLines(File file) {
    try {
      return Files.readLines(file, charset);
    } catch (IOException e) {
      throw new SonarException("Unable to read " + file, e);
    }
  }

}
