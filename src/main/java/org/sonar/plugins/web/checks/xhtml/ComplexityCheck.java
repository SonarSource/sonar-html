/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.web.checks.xhtml;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.Utils;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * Checks cyclomatic complexity against a specified limit. The complexity is measured by counting decision tags (such as if and forEach) and
 * boolean operators in expressions ("&amp;&amp;" and "||"), plus one for the body of the document. It is a measure of the minimum number of
 * possible paths to render the page.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "ComplexityCheck", name = "Complexity", description = "Complexity", priority = Priority.MINOR,
    isoCategory = IsoCategory.Maintainability)
public final class ComplexityCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_COMPLEXITY = 10;

  private int complexity;

  @RuleProperty(key = "max", description = "Maximum allowed complexity")
  private int max = DEFAULT_MAX_COMPLEXITY;

  @RuleProperty(key = "operators", description = "Operators")
  private String[] operators = new String[] { "&&", "||", "and", "or" };

  @RuleProperty(key = "tags", description = "Decision Tags")
  private String[] tags = new String[] { "catch", "choose", "if", "forEach", "forTokens", "when" };

  @Override
  public void endDocument() {
    super.endDocument();

    if (complexity > max) {
      String msg = String.format("%s is %d (max allowed is %d)", getRule().getDescription(), complexity, max);
      createViolation(0, msg);
    }

    getWebSourceCode().addMeasure(CoreMetrics.COMPLEXITY, complexity);
  }

  public int getMax() {
    return max;
  }

  public String getOperators() {
    return StringUtils.join(operators, ",");
  }

  public String getTags() {
    return StringUtils.join(tags, ",");
  }

  public void setMax(int max) {
    this.max = max;
  }

  public void setOperators(String value) {
    this.operators = Utils.trimSplitCommaSeparatedList(value);
  }

  public void setTags(String value) {
    this.tags = Utils.trimSplitCommaSeparatedList(value);
  }

  @Override
  public void startDocument(WebSourceCode webSourceCode) {
    super.startDocument(webSourceCode);
    complexity = 1;
  }

  @Override
  public void startElement(TagNode node) {

    // count jstl tags
    if (ArrayUtils.contains(tags, node.getLocalName()) || ArrayUtils.contains(tags, node.getNodeName())) {
      complexity++;
    } else {
      // count complexity in expressions
      for (Attribute a : node.getAttributes()) {
        if (Utils.isUnifiedExpression(a.getValue())) {
          String[] tokens = StringUtils.split(a.getValue(), " \t\n");

          for (String token : tokens) {
            if (ArrayUtils.contains(operators, token)) {
              complexity++;
            }
          }
        }
      }
    }
  }
}