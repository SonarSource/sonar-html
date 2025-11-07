/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
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

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

public final class HtmlRulesDefinition implements RulesDefinition {

  public static final String REPOSITORY_KEY = "Web";
  public static final String REPOSITORY_NAME = "SonarAnalyzer";

  private static final Set<String> TEMPLATE_RULE_KEYS = Collections.unmodifiableSet(Stream.of(
    "IllegalAttributeCheck",
    "LibraryDependencyCheck",
    "ChildElementIllegalCheck",
    "ChildElementRequiredCheck",
    "ParentElementIllegalCheck",
    "ParentElementRequiredCheck").collect(Collectors.toSet()));

  public static final String RESOURCE_BASE_PATH = "org/sonar/l10n/web/rules/Web";

  private final SonarRuntime sonarRuntime;

  public HtmlRulesDefinition(SonarRuntime sonarRuntime) {
    this.sonarRuntime = sonarRuntime;
  }

  @Override
  public void define(Context context) {
    NewRepository repository = context
      .createRepository(REPOSITORY_KEY, HtmlConstants.LANGUAGE_KEY)
      .setName(REPOSITORY_NAME);

    RuleMetadataLoader ruleMetadataLoader = new RuleMetadataLoader(RESOURCE_BASE_PATH, SonarWayProfile.JSON_PROFILE_PATH, sonarRuntime);

    ruleMetadataLoader.addRulesByAnnotatedClass(repository, CheckClasses.getCheckClasses());

    for (NewRule rule : repository.rules()) {
      if (TEMPLATE_RULE_KEYS.contains(rule.key())) {
        rule.setTemplate(true);
      }
    }

    repository.done();
  }
}
