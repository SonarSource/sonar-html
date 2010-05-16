/*
 * Copyright (C) 2010 Matthijs Galesloot
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.sonar.api.utils.SonarException;
import org.sonar.check.AnnotationIntrospector;
import org.sonar.check.Check;
import org.sonar.plugins.web.WebPlugin;
import org.sonar.plugins.web.WebUtils;
import org.sonar.plugins.web.checks.jsp.JspCheckClasses;
import org.sonar.plugins.web.checks.xml.XmlCheckClasses;
import org.sonar.plugins.web.language.Web;

/**
 * @author Matthijs Galesloot
 */
public final class WebRulesRepository implements RulesRepository<Web>, ConfigurationExportable, ConfigurationImportable {

  private static final String RULE_FILE = "/rules.xml";

  private static List<Rule> rulesRepository;

  private static Rule createRepositoryRule(Class<AbstractPageCheck> checkClass) {

    Check check = AnnotationIntrospector.getCheckAnnotation(checkClass);

    Rule rule = new Rule(WebPlugin.getKEY(), check.key());
    rule.setName(check.title());
    rule.setDescription(check.description());
    rule.setRulesCategory(matchRuleCategory(check.isoCategory().name()));
    rule.setPriority(RulePriority.fromCheckPriority(check.priority()));

    // build params
    List<RuleParam> ruleParams = new ArrayList<RuleParam>();
    for (Field field : AnnotationIntrospector.getPropertyFields(checkClass)) {
      ruleParams.add(new RuleParam(rule, field.getName(), field.getName(), "s"));
    }
    rule.setParams(ruleParams);
    return rule;
  }

  public static Class<AbstractPageCheck> getCheckClass(ActiveRule activeRule) {
    for (Class<AbstractPageCheck> checkClass : getCheckClasses()) {
      Check check = AnnotationIntrospector.getCheckAnnotation(checkClass);
      if (check.key().equals(activeRule.getConfigKey())) {
        return checkClass;
      }
    }
    WebUtils.LOG.error("Could not find check class for config key " + activeRule.getConfigKey());
    return null;
  }

  private static List<Class> getCheckClasses() {
    List<Class> classes = new ArrayList<Class>();
    classes.addAll(Arrays.asList(JspCheckClasses.getCheckClasses()));
    classes.addAll(Arrays.asList(XmlCheckClasses.getCheckClasses()));
    return classes;
  }

  public static Rule getRule(String ruleKey) {
    parseReferential();
    for (Rule rule : rulesRepository) {
      if (rule.getKey().equals(ruleKey)) {
        return rule;
      }
    }
    return null;
  }

  private static RulesCategory matchRuleCategory(String category) {
    for (RulesCategory ruleCategory : Iso9126RulesCategories.ALL) {
      if (ruleCategory.getName().equalsIgnoreCase(category)) {
        return ruleCategory;
      }
    }
    WebUtils.LOG.error("Unexpected category name " + category);
    return Iso9126RulesCategories.MAINTAINABILITY;
  }

  private static void parseReferential() {
    if (rulesRepository == null) {
      rulesRepository = new ArrayList<Rule>();
      for (Class<AbstractPageCheck> checkClass : getCheckClasses()) {
        rulesRepository.add(createRepositoryRule(checkClass));
      }
    }
  }

  private Web web;

  public WebRulesRepository(Web web) {
    this.web = web;
  }

  public String exportConfiguration(RulesProfile activeProfile) {

    WebUtils.LOG.debug("exportConfiguration");
    // TODO
    return null;
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

  public final List<Rule> getInitialReferential() {
    return parseReferential(RULE_FILE);
  }

  public Web getLanguage() {
    return web;
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

  public List<Rule> parseReferential(String path) {

    parseReferential();
    return rulesRepository;
  }
}