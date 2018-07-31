/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.rules;

import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;

import static org.fest.assertions.Assertions.assertThat;

public class WebRulesDefinitionTest {

  @Test
  public void test() {
    WebRulesDefinition rulesDefinition = new WebRulesDefinition();
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);
    RulesDefinition.Repository repository = context.repository("Web");

    assertThat(repository.name()).isEqualTo("SonarAnalyzer");
    assertThat(repository.language()).isEqualTo("web");
    assertThat(repository.rules()).hasSize(Iterables.size(CheckClasses.getCheckClasses()));

    RulesDefinition.Rule alertUseRule = repository.rule("IllegalAttributeCheck");
    assertThat(alertUseRule).isNotNull();
    assertThat(alertUseRule.name()).isEqualTo("Track uses of disallowed attributes");

    Set<String> templateRules = repository.rules().stream()
      .filter(RulesDefinition.Rule::template)
      .map(RulesDefinition.Rule::key)
      .collect(Collectors.toSet());
    assertThat(templateRules).hasSize(6);
    assertThat(templateRules).containsOnly("IllegalAttributeCheck",
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
