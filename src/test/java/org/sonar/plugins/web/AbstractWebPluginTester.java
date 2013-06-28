/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot and SonarSource
 * dev@sonar.codehaus.org
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
package org.sonar.plugins.web;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.rules.AnnotationRuleParser;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.web.rules.WebRulesRepository;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Matthijs Galesloot
 *
 */
public class AbstractWebPluginTester {

  private static class WebRuleFinder implements RuleFinder {

    private final WebRulesRepository repository;
    private final List<Rule> rules;

    public WebRuleFinder() {
      repository = new WebRulesRepository(new AnnotationRuleParser());
      rules = repository.createRules();
    }

    @Override
    public Rule find(RuleQuery query) {
      return null;
    }

    @Override
    public Collection<Rule> findAll(RuleQuery query) {
      return null;
    }

    @Override
    public Rule findByKey(String repositoryKey, String key) {
      for (Rule rule : rules) {
        if (rule.getKey().equals(key)) {
          return rule;
        }
      }
      return null;
    }

    @Override
    public Rule findById(int ruleId) {
      return null;
    }
  }

  /**
   * create standard rules profile
   */
  protected RulesProfile createStandardRulesProfile() {
    ProfileDefinition profileDefinition = new TestProfileWithAllRules(new XMLProfileParser(newRuleFinder()));
    ValidationMessages messages = ValidationMessages.create();
    RulesProfile profile = profileDefinition.createProfile(messages);
    assertEquals(0, messages.getErrors().size());
    assertEquals(0, messages.getWarnings().size());
    assertEquals(0, messages.getInfos().size());
    return profile;
  }

  protected RuleFinder newRuleFinder() {
    return new WebRuleFinder();
  }
}
