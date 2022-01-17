/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.sonar;

import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "FieldsetWithoutLegendCheck")
public class FieldsetWithoutLegendCheck extends AbstractPageCheck {

  private boolean foundLegend;
  private TagNode fieldset;

  @Override
  public void startDocument(List<Node> nodes) {
    fieldset = null;
  }

  @Override
  public void startElement(TagNode node) {
    if (isFieldSet(node)) {
      foundLegend = false;
      fieldset = node;
    } else if (isLegend(node)) {
      foundLegend = true;
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isFieldSet(node)) {
      if (!foundLegend && fieldset != null) {
        createViolation(fieldset, "Add a <legend> tag to this fieldset.");
      }

      foundLegend = false;
      fieldset = null;
    }
  }

  private static boolean isFieldSet(TagNode node) {
    return "FIELDSET".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isLegend(TagNode node) {
    return "LEGEND".equalsIgnoreCase(node.getNodeName());
  }

}
