/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2021 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.rules;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmlRulesDefinitionTest {

  @Test
  public void test() {
    HtmlRulesDefinition rulesDefinition = new HtmlRulesDefinition();
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);
    RulesDefinition.Repository repository = context.repository("Web");

    assertThat(repository.name()).isEqualTo("SonarAnalyzer");
    assertThat(repository.language()).isEqualTo("web");
    assertThat(repository.rules()).hasSize(CheckClasses.getCheckClasses().size());

    RulesDefinition.Rule alertUseRule = repository.rule("IllegalAttributeCheck");
    assertThat(alertUseRule).isNotNull();
    assertThat(alertUseRule.name()).isEqualTo("Track uses of disallowed attributes");

    RulesDefinition.Rule hotspotsRule = repository.rule("S5725");
    assertThat(hotspotsRule).isNotNull();
    assertThat(hotspotsRule.type()).isEqualTo(RuleType.SECURITY_HOTSPOT);

    Set<String> templateRules = repository.rules().stream()
      .filter(RulesDefinition.Rule::template)
      .map(RulesDefinition.Rule::key)
      .collect(Collectors.toSet());
    assertThat(templateRules)
      .hasSize(6)
      .containsExactlyInAnyOrder(
        "IllegalAttributeCheck",
        "LibraryDependencyCheck",
        "ChildElementIllegalCheck",
        "ChildElementRequiredCheck",
        "ParentElementIllegalCheck",
        "ParentElementRequiredCheck");

    for (RulesDefinition.Rule rule : repository.rules()) {
      for (RulesDefinition.Param param : rule.params()) {
        assertThat(param.description()).as("description for " + param.key()).isNotEmpty();
      }
    }

    List<RulesDefinition.Rule> activated = repository.rules().stream().filter(RulesDefinition.Rule::activatedByDefault).collect(Collectors.toList());
    assertThat(activated).isNotEmpty();
    assertThat(activated.size()).isLessThan(CheckClasses.getCheckClasses().size());
  }
}
