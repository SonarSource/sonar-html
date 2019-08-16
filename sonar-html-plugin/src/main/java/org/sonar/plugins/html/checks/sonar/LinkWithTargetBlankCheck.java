/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2019 SonarSource SA and Matthijs Galesloot
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5148")
public class LinkWithTargetBlankCheck extends AbstractPageCheck {

  private static final Pattern DYNAMIC_URL = Pattern.compile("[{}$()\\[\\]]");
  private static final String NOOPENER = "NOOPENER";
  private static final String NOREFERRER = "NOREFERRER";

  @Override
  public void startElement(TagNode node) {
    if (isAnchorWithTargetBlank(node)
      && ((isExternalAndNotDynamicUrl(node) && missingRelAttribute(node)) || incompleteRelAttribute(node))) {
      createViolation(node, "Add rel=\"noopener noreferrer\" to this link to prevent the original page from being modified by the opened link.");
    }
  }

  private static boolean incompleteRelAttribute(TagNode node) {
    List<String> relValue = relAttributeValues(node);
    return (relValue.contains(NOOPENER) && !relValue.contains(NOREFERRER))
      || (!relValue.contains(NOOPENER) && relValue.contains(NOREFERRER));
  }

  private static boolean missingRelAttribute(TagNode node) {
    List<String> relValue = relAttributeValues(node);
    return !relValue.contains(NOOPENER) || !relValue.contains(NOREFERRER);
  }

  private static List<String> relAttributeValues(TagNode node) {
    String rel = node.getPropertyValue("REL");
    if (rel == null) {
      return Collections.emptyList();
    }
    return Arrays.stream(rel.split(" "))
      .map(s -> s.trim().toUpperCase(Locale.ROOT))
      .collect(Collectors.toList());
  }

  private static boolean isAnchorWithTargetBlank(TagNode node) {
    return node.equalsElementName("A") && "_BLANK".equalsIgnoreCase(node.getPropertyValue("TARGET"));
  }

  private static boolean isExternalAndNotDynamicUrl(TagNode node) {
    String href = node.getPropertyValue("HREF");
    return href != null
      && (href.startsWith("http://") || href.startsWith("https://"))
      && !DYNAMIC_URL.matcher(href).find();
  }
}
