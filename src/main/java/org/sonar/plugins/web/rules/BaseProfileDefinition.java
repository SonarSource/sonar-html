/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleAnnotationUtils;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;

public abstract class BaseProfileDefinition extends ProfileDefinition {

  private static final String NAME = "Sonar way";

  private final RuleFinder ruleFinder;

  public BaseProfileDefinition(RuleFinder ruleFinder) {
    this.ruleFinder = ruleFinder;
  }

  @Override
  public final RulesProfile createProfile(ValidationMessages validation) {
    RulesProfile profile = RulesProfile.create(NAME, getLanguageKey());
    for (Class ruleClass : CheckClasses.getCheckClasses()) {
      String ruleKey = RuleAnnotationUtils.getRuleKey(ruleClass);
      if (isActive(ruleClass)) {
        Rule rule = ruleFinder.findByKey(getRepositoryKey(), ruleKey);
        profile.activateRule(rule, null);
      }
    }
    return profile;
  }

  protected abstract boolean isActive(Class ruleClass);

  protected abstract String getLanguageKey();

  protected abstract String getRepositoryKey();

}
