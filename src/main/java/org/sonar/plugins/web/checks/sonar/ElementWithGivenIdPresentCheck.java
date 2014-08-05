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
package org.sonar.plugins.web.checks.sonar;

import org.sonar.check.Cardinality;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

import java.util.List;

@Rule(
  key = "S1436",
  priority = Priority.MAJOR,
  cardinality = Cardinality.MULTIPLE)
@WebRule(activeByDefault = false)
public class ElementWithGivenIdPresentCheck extends AbstractPageCheck {

  private static final String DEFAULT_ID = "";

  @RuleProperty(
    key = "id",
    defaultValue = DEFAULT_ID)
  public String id = DEFAULT_ID;

  private boolean foundId;

  @Override
  public void startDocument(List<Node> nodes) {
    foundId = false;
  }

  @Override
  public void startElement(TagNode node) {
    if (id.equals(node.getAttribute("id"))) {
      foundId = true;
    }
  }

  @Override
  public void endDocument() {
    if (!id.isEmpty() && !foundId) {
      createViolation(0, "The ID \"" + id + "\" is missing from this page and should be added.");
    }
  }

}
