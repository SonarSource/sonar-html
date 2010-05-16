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

package org.sonar.plugins.web.rules;

import org.sonar.api.rules.Rule;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.AbstractNodeVisitor;

/**
 * @author Matthijs Galesloot
 */
public abstract class AbstractPageCheck extends AbstractNodeVisitor {

  private String ruleKey;

  protected void createViolation(int linePosition) {
    createViolation(linePosition, null);
  }

  protected void createViolation(int linePosition, String message) {
    Rule rule = WebRulesRepository.getRule(getRuleKey());
    Violation violation = new Violation(rule);
    violation.setMessage(message == null ? rule.getDescription() : message);
    violation.setLineId(linePosition);
    getWebSourceCode().addViolation(violation);
  }

  protected void createViolation(Node node) {
    createViolation(node.getStartLinePosition());
  }

  public String getRuleKey() {
    return ruleKey == null ? getClass().getSimpleName() : ruleKey;
  }

  public void setRuleKey(String ruleKey) {
    this.ruleKey = ruleKey;
  }
}
