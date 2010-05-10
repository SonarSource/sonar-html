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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ConfigurationExportable;
import org.sonar.api.rules.ConfigurationImportable;
import org.sonar.api.rules.Iso9126RulesCategories;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleParam;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RulesCategory;
import org.sonar.api.rules.RulesRepository;
import org.sonar.api.rules.StandardProfileXmlParser;
import org.sonar.api.rules.StandardRulesXmlParser;
import org.sonar.api.utils.SonarException;
import org.sonar.check.AnnotationIntrospector;
import org.sonar.check.Check;
import org.sonar.plugins.web.checks.HtmlCheck;
import org.sonar.plugins.web.checks.JspScriptletCheck;
import org.sonar.plugins.web.checks.RegularExpressionCheck;
import org.sonar.plugins.web.checks.UnclosedTagCheck;
import org.sonar.plugins.web.language.Web;

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

  private static Class<HtmlCheck>[] checkClasses = new Class[] { 
      JspScriptletCheck.class,
      RegularExpressionCheck.class, 
      UnclosedTagCheck.class, };

  public static Class<HtmlCheck> getCheckClass(ActiveRule activeRule) {
    for (Class<HtmlCheck> checkClass : checkClasses) {
      Check check = AnnotationIntrospector.getCheckAnnotation(checkClass);
      if (check.key().equals(activeRule.getConfigKey())) {
        return checkClass;
      }
    }
    WebUtils.LOG.error("Could not find checker for config key " + activeRule.getConfigKey());
    return null;
  }

  public List<Rule> parseReferential(String path) {

    List<Rule> rulesRepository = new ArrayList<Rule>();
    for (Class<HtmlCheck> checkClass : checkClasses) {
      rulesRepository.add(createRepositoryRule(checkClass));
    }
    return rulesRepository;
  }

  public final List<RulesProfile> getProvidedProfiles() {
    List<RulesProfile> profiles = new ArrayList<RulesProfile>();
    StandardProfileXmlParser parser = new StandardProfileXmlParser(getInitialReferential());

    RulesProfile profile = parser.importConfiguration(getConfigurationFromFile(RULE_FILE));
    profile.setLanguage(web.getKey());
    WebUtils.LOG.debug("Building profile " + profile.getName());
    
    profiles.add(profile);
    
    return profiles;
  }

  public List<ActiveRule> importConfiguration(String configuration, List<Rule> rulesRepository) {

    WebUtils.LOG.debug("importConfiguration");
    
    StandardProfileXmlParser parser = new StandardProfileXmlParser(rulesRepository);
    RulesProfile profile = parser.importConfiguration(configuration);
    profile.setLanguage(web.getKey());
    return profile.getActiveRules();
  }

  public String exportConfiguration(RulesProfile activeProfile) {

    WebUtils.LOG.debug("exportConfiguration");
    //TODO 
   return null;
  }

  private static RulesCategory matchRuleCategory(String category) {
    for (RulesCategory ruleCategory : Iso9126RulesCategories.ALL) {
      if (ruleCategory.getName().equalsIgnoreCase(category)) {
        return ruleCategory;
      }
    }
    throw new IllegalArgumentException("Unexpected category name " + category);
  }

  private Rule createRepositoryRule(Class<HtmlCheck> checkClass) {
    Check check = AnnotationIntrospector.getCheckAnnotation(checkClass);

    RulesCategory category = Iso9126RulesCategories.EFFICIENCY; 
    // matchRuleCategory(Iso9126RulesCategories.EFFICIENCY.getName()); // TODO
    RulePriority priority =   RulePriority.MAJOR; // fromCheckPriority(check.priority());
    Rule rule = new Rule(WebPlugin.KEY, check.key(), check.description(), category, priority);

    // build params
    List<RuleParam> ruleParams = new ArrayList<RuleParam>();
    for (Field field : AnnotationIntrospector.getPropertyFields(checkClass)) {
      ruleParams.add(new RuleParam(rule, field.getName(), field.getName(), "s"));
    }
    rule.setParams(ruleParams);
    return rule;
  }

  private String getConfigurationFromFile(String path) {
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