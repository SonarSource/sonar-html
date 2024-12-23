/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.rules;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.Version;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmlRulesDefinitionTest {

  @Test
  public void test() {
    SonarRuntime sonarRuntime = SonarRuntimeImpl.forSonarQube(Version.create(8, 9), SonarQubeSide.SERVER, SonarEdition.COMMUNITY);
    HtmlRulesDefinition rulesDefinition = new HtmlRulesDefinition(sonarRuntime);
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);
    RulesDefinition.Repository repository = context.repository("Web");

    assertThat(repository.name()).isEqualTo("Sonar");
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
