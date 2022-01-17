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
package org.sonar.plugins.html.checks.security;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5725")
public class ResourceIntegrityCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (node.equalsElementName("script")) {
      Attribute src = node.getProperty("src");
      if (src != null && isExternal(src.getValue()) && !node.hasProperty("integrity")) {
        createViolation(node, "Make sure not using resource integrity feature is safe here.");
      }
    }
  }

  private static boolean isExternal(String srcValue) {
    return srcValue.startsWith("http") || srcValue.startsWith("//");
  }

}
