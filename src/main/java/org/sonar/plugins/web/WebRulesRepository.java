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

package org.sonar.plugins.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.rules.ConfigurationExportable;
import org.sonar.api.rules.ConfigurationImportable;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleParam;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RulesCategory;
import org.sonar.api.rules.RulesRepository;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.rules.xml.Property;
import org.sonar.plugins.web.rules.xml.RuleDefinition;
import org.sonar.plugins.web.rules.xml.RulesUtils;
import org.sonar.plugins.web.rules.xml.Ruleset;

/**
 * @author Matthijs Galesloot
 */
public class WebRulesRepository implements RulesRepository<Web>, ConfigurationExportable, ConfigurationImportable {

  private Web web;
  public static String RULE_FILE = "rules.xml";

  public WebRulesRepository(Web web) {
    this.web = web;
  }

  public Web getLanguage() {
    return web;
  }

  public final List<Rule> getInitialReferential() {
    return parseReferential(RULE_FILE);
  }

  public List<Rule> parseReferential(String path) {
    Ruleset ruleset = RulesUtils.buildRuleSetFromXml(WebRulesRepository.getConfigurationFromFile(path));
    List<Rule> rulesRepository = new ArrayList<Rule>();
    for (RuleDefinition ruleDefinition : ruleset.getRules()) {
      rulesRepository.add(createRepositoryRule(ruleDefinition));
    }
    return rulesRepository;
  }

  public final List<RulesProfile> getProvidedProfiles() {
    List<RulesProfile> profiles = new ArrayList<RulesProfile>();
    profiles.add(buildProfile("Default Web Profile", RULE_FILE));
    return profiles;
  }

  public final RulesProfile buildProfile(String name, String path) {
    WebUtils.LOG.debug("Building profile " + name);

    RulesProfile profile = new RulesProfile(name, web.getKey());
    List<ActiveRule> activeRules = importConfiguration(WebRulesRepository.getConfigurationFromFile(path), getInitialReferential());
    profile.setActiveRules(activeRules);
    return profile;
  }

  public List<ActiveRule> importConfiguration(String configuration, List<Rule> rulesRepository) {

    List<ActiveRule> activeRules = new ArrayList<ActiveRule>();

    Ruleset ruleset = RulesUtils.buildRuleSetFromXml(configuration);
    if (ruleset != null) {

      for (RuleDefinition fRule : ruleset.getRules()) {
        ActiveRule activeRule = createActiveRule(fRule, rulesRepository);
        if (activeRule != null) {
          activeRules.add(activeRule);
        }
      }
    }

    return activeRules;
  }

  public String exportConfiguration(RulesProfile activeProfile) {

    Ruleset tree = buildRulesetFromActiveProfile(activeProfile.getActiveRulesByPlugin(WebPlugin.KEY));
    return RulesUtils.buildXmlFromRuleset(tree);
  }

  private Rule createRepositoryRule(RuleDefinition ruleDefinition) {
    RulesCategory category = RulesUtils.matchRuleCategory(ruleDefinition.getCategory());
    RulePriority priority = RulePriority.valueOf(ruleDefinition.getPriority());
    Rule rule = new Rule(WebPlugin.KEY, ruleDefinition.getName(), ruleDefinition.getMessage(), category, priority);
    rule.setDescription(ruleDefinition.getDescription());

    // build params
    List<RuleParam> ruleParams = new ArrayList<RuleParam>();
    if (ruleDefinition.getProperties() != null) {
      for (Property property : ruleDefinition.getProperties()) {
        ruleParams.add(new RuleParam(rule, property.getName(), property.getName(), "s"));
      }
    }
    rule.setParams(ruleParams);
    return rule;
  }

  private ActiveRule createActiveRule(RuleDefinition fRule, List<Rule> rulesRepository) {
    String ruleName = fRule.getName();
    RulePriority fRulePriority = RulePriority.valueOf(fRule.getPriority());

    for (Rule rule : rulesRepository) {
      if (rule.getKey().equals(ruleName)) {
        RulePriority priority = fRulePriority != null ? fRulePriority : rule.getPriority();
        ActiveRule activeRule = new ActiveRule(null, rule, priority);
        activeRule.setActiveRuleParams(buildActiveRuleParams(fRule, rule, activeRule));
        return activeRule;
      }
    }
    return null;
  }

  protected List<ActiveRuleParam> buildActiveRuleParams(RuleDefinition ruleDefinition, Rule repositoryRule, ActiveRule activeRule) {
    List<ActiveRuleParam> activeRuleParams = new ArrayList<ActiveRuleParam>();
    if (ruleDefinition.getProperties() != null) {
      for (Property property : ruleDefinition.getProperties()) {
        if (repositoryRule.getParams() != null) {
          for (RuleParam ruleParam : repositoryRule.getParams()) {
            if (ruleParam.getKey().equals(property.getName())) {
              activeRuleParams.add(new ActiveRuleParam(activeRule, ruleParam, property.getValue()));
            }
          }
        }
      }
    }
    return activeRuleParams;
  }

  protected Ruleset buildRulesetFromActiveProfile(List<ActiveRule> activeRules) {
    Ruleset ruleset = new Ruleset();
    for (ActiveRule activeRule : activeRules) {
      if (activeRule.getRule().getPluginName().equals(WebPlugin.KEY)) {
        String key = activeRule.getRule().getKey();
        String priority = activeRule.getPriority().name();
        RuleDefinition ruleDefinition = new RuleDefinition(key, priority);
        List<Property> properties = new ArrayList<Property>();
        for (ActiveRuleParam activeRuleParam : activeRule.getActiveRuleParams()) {
          properties.add(new Property(activeRuleParam.getRuleParam().getKey(), activeRuleParam.getValue()));
        }
        ruleDefinition.setProperties(properties);
        ruleDefinition.setMessage(activeRule.getRule().getName());
        ruleset.addRule(ruleDefinition);
      }
    }
    return ruleset;
  }

  public static String getConfigurationFromFile(String path) {
    InputStream inputStream = WebRulesRepository.class.getResourceAsStream(path);
    String configuration = null;
    try {
      configuration = IOUtils.toString(inputStream, "UTF-8");
    } catch (IOException e) {
      throw new SonarException("Configuration file not found for the profile : " + configuration, e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
    return configuration;
  }

}