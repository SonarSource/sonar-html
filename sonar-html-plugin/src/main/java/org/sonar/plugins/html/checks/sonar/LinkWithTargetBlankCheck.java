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

  @Override
  public void startElement(TagNode node) {
    if (isAnchorWithTargetBlank(node) && isInsecureUrl(node) && missingRelAttribute(node)) {
      createViolation(node, "Make sure not using rel=\"noopener\" is safe here.");
    }
  }

  private static boolean missingRelAttribute(TagNode node) {
    return !relAttributeValues(node).contains(NOOPENER);
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

  private static boolean isInsecureUrl(TagNode node) {
    String href = node.getPropertyValue("HREF");
    if (href == null) {
      return false;
    }
    boolean external = isExternalUrl(href);
    boolean dynamic = isDynamic(href);
    return (external && !dynamic) || (!external && dynamic && !isRelativelUrl(href));
  }

  private static boolean isDynamic(String href) {
    return DYNAMIC_URL.matcher(href).find();
  }

  private static boolean isExternalUrl(String href) {
    return href.startsWith("http://") || href.startsWith("https://");
  }

  private static boolean isRelativelUrl(String href) {
    return href.startsWith("/") || href.startsWith(".");
  }
}
