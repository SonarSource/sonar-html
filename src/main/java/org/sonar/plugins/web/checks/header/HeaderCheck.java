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

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Rule(
  key = "HeaderCheck",
  priority = Priority.MAJOR)
@WebRule(activeByDefault = false)
public class HeaderCheck extends AbstractPageCheck {

  private static final String DEFAULT_FORMAT = "^.*Copyright.*$";

  @RuleProperty(
    key = "expression",
    defaultValue = DEFAULT_FORMAT)
  public String format = DEFAULT_FORMAT;

  private boolean hasHeader;
  private Matcher matcher;
  private boolean visiting;

  @Override
  public void startDocument(List<Node> nodes) {
    hasHeader = false;
    visiting = true;
  }

  @Override
  public void comment(CommentNode node) {
    if (visiting) {
      if (matchHeader(node.getCode())) {
        hasHeader = true;
      } else {
        createViolation(node.getStartLinePosition(), "Change this header comment to match the regular expression: " + format);
      }
    }

    visiting = false;
  }

  private boolean matchHeader(String header) {
    if (matcher == null) {
      Pattern pattern = Pattern.compile(format, Pattern.MULTILINE);
      matcher = pattern.matcher(header);
    } else {
      matcher.reset(header);
    }

    return matcher.find();
  }

  @Override
  public void startElement(TagNode node) {
    if (visiting) {
      if (!hasHeader) {
        createViolation(node.getStartLinePosition(), "Insert a header comment before this tag.");
      }
      visiting = false;
    }
  }

}
