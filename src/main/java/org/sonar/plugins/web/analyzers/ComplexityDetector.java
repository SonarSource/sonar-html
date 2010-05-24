/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.analyzers;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.visitor.AbstractNodeVisitor;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 * 
 */
public final class ComplexityDetector extends AbstractNodeVisitor {

  private int complexity;

  @Override
  public void startDocument(WebSourceCode webSourceCode) {
    super.startDocument(webSourceCode);
    complexity = 1;
  }

  @Override
  public void startElement(TagNode node) {

    String unprefixedNodeName = node.getLocalName();
    if ("if".equals(unprefixedNodeName) || "choose".equals(unprefixedNodeName)) {
      complexity++;
    } else {
      // count complexity in Expression Language
      for (Attribute a : node.getAttributes()) {
        if (a.getValue() != null && a.getValue().startsWith("#{")) {
          complexity += StringUtils.countMatches(a.getValue(), "&&");
          complexity += StringUtils.countMatches(a.getValue(), "||");
        }
      }
    }
  }

  @Override
  public void endDocument() {
    super.endDocument();

    getWebSourceCode().addMeasure(CoreMetrics.COMPLEXITY, complexity);
  }
}