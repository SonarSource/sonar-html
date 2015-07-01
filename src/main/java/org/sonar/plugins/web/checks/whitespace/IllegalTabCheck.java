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
package org.sonar.plugins.web.checks.whitespace;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.sonar.api.utils.SonarException;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.CharsetAwareVisitor;

import com.google.common.io.Files;

@Rule(
  key = "IllegalTabCheck",
  priority = Priority.MAJOR,
  name = "Tabulation characters should not be used")
@WebRule(activeByDefault = false)
@RuleTags({
  RuleTags.CONVENTION,
  RuleTags.PSR2
})
public class IllegalTabCheck extends AbstractPageCheck implements CharsetAwareVisitor {

  private Charset charset;

  @Override
  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  @Override
  public void startDocument(List<Node> nodes) {
    List<String> lines;
    try {
      lines = Files.readLines(getWebSourceCode().inputFile().file(), charset);
    } catch (IOException e) {
      throw new SonarException(e);
    }
    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).contains("\t")) {
        createViolation(i + 1, "Replace all tab characters in this file by sequences of white-spaces.");
        break;
      }
    }
  }
}
