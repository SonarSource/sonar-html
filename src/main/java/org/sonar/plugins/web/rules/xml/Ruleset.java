/*
 * Copyright (C) 2010
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

package org.sonar.plugins.web.rules.xml;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("ruleset")
public class Ruleset {

  private String description;

  @XStreamImplicit
  private List<RuleDefinition> ruleDefinitions = new ArrayList<RuleDefinition>();

  @XStreamOmitField
  @XStreamAlias(value = "exclude-pattern")
  private String excludePattern;

  @XStreamOmitField
  @XStreamAlias(value = "include-pattern")
  private String includePattern;

  public Ruleset() {
  }

  public Ruleset(String description) {
    this.description = description;
  }

  public List<RuleDefinition> getRules() {
    return ruleDefinitions;
  }

  public void setRules(List<RuleDefinition> ruleDefinitions) {
    this.ruleDefinitions = ruleDefinitions;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void addRule(RuleDefinition ruleDefinition) {
    ruleDefinitions.add(ruleDefinition);
  }
}
