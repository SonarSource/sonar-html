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

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.web.api.WebConstants;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

public final class WebRulesDefinition implements RulesDefinition {

  public static final String REPOSITORY_KEY = WebConstants.LANGUAGE_NAME;
  public static final String REPOSITORY_NAME = "SonarAnalyzer";

  private static final Set<String> TEMPLATE_RULE_KEYS = Collections.unmodifiableSet(Stream.of(
    "IllegalAttributeCheck",
    "LibraryDependencyCheck",
    "ChildElementIllegalCheck",
    "ChildElementRequiredCheck",
    "ParentElementIllegalCheck",
    "ParentElementRequiredCheck").collect(Collectors.toSet()));

  public static final String RESOURCE_BASE_PATH = "org/sonar/l10n/web/rules/Web";

  @Override
  public void define(Context context) {
    NewRepository repository = context
      .createRepository(REPOSITORY_KEY, WebConstants.LANGUAGE_KEY)
      .setName(REPOSITORY_NAME);

    RuleMetadataLoader ruleMetadataLoader = new RuleMetadataLoader(RESOURCE_BASE_PATH, SonarWayProfile.JSON_PROFILE_PATH);

    ruleMetadataLoader.addRulesByAnnotatedClass(repository, CheckClasses.getCheckClasses());

    for (NewRule rule : repository.rules()) {
      if (TEMPLATE_RULE_KEYS.contains(rule.key())) {
        rule.setTemplate(true);
      }
    }

    repository.done();
  }
}
