/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.checks.dependencies;

import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker to find dynamic includes.
 *
 * e.g. <jsp:include page="header.jsp">
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "DynamicJspIncludeCheck", name = "Dynamic Jsp Include", description = "Dynamic Jsp Include is not allowed",
    priority = Priority.CRITICAL, isoCategory = IsoCategory.Portability)
public class DynamicJspIncludeCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if ("jsp:include".equals(node.getNodeName())) {
      createViolation(node);
    }
  }
}