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

package org.sonar.plugins.web;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
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
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.jsp.JspCheckClasses;
import org.sonar.plugins.web.checks.xhtml.XhtmlCheckClasses;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.rules.ProfileXmlParser;

/**
 * @author Matthijs Galesloot
 */
public final class WebRulesRepository implements RulesRepository<Web>, ConfigurationExportable, ConfigurationImportable {

  private static final Logger LOG = LoggerFactory.getLogger(WebRulesRepository.class);

  private static final String RULE_FILE = "/rules.xml";

  private static List<Rule> rulesRepository = new ArrayList<Rule>();

  static {
    for (Class<AbstractPageCheck> checkClass : getCheckClasses()) {
      rulesRepository.add(createRepositoryRule(checkClass));
    }
  }

  private static AbstractPageCheck createCheck(Class<AbstractPageCheck> checkClass, ActiveRule activeRule) {
    if (LOG.isDebugEnabled()) {
      debugActiveRuleConfiguration(checkClass, activeRule);
    }

    try {
      AbstractPageCheck check = checkClass.newInstance();
      check.setRule(activeRule.getRule());
      if (activeRule.getActiveRuleParams() != null) {
        for (ActiveRuleParam param : activeRule.getActiveRuleParams()) {
          Object value = PropertyUtils.getProperty(check, param.getRuleParam().getKey());
          if (value instanceof Integer) {
            value = Integer.parseInt(param.getValue());
          } else {
            value = param.getValue();
          }
          PropertyUtils.setProperty(check, param.getRuleParam().getKey(), value);
        }
      }

      return check;
    } catch (IllegalAccessException e) {
      throw new SonarException(e);
    } catch (InvocationTargetException e) {
      throw new SonarException(e);
    } catch (NoSuchMethodException e) {
      throw new SonarException(e);
    } catch (InstantiationException e) {
      throw new SonarException(e);
    }
  }

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

  private static void debugActiveRuleConfiguration(Class<AbstractPageCheck> checkClass, ActiveRule activeRule) {
    StringBuilder sb = new StringBuilder();
    if (activeRule.getActiveRuleParams() != null) {
      for (ActiveRuleParam param : activeRule.getActiveRuleParams()) {
        if (sb.length() > 0) {
          sb.append(',');
        }
        sb.append(param.getRuleParam().getKey());
        sb.append('=');
        sb.append(param.getValue());
      }
      sb.append(')');
      sb.insert(0, " (");
    }
    sb.insert(0, checkClass.getSimpleName());
    sb.insert(0, "Created checker ");
    LOG.debug(sb.toString());
  }

  public static Class<AbstractPageCheck> getCheckClass(ActiveRule activeRule) {
    for (Class<AbstractPageCheck> checkClass : getCheckClasses()) {
      Check check = AnnotationIntrospector.getCheckAnnotation(checkClass);
      if (check.key().equals(activeRule.getConfigKey())) {
        return checkClass;
      }
    }
    LOG.error("Could not find check class for config key " + activeRule.getConfigKey());
    return null;
  }

  private static List<Class> getCheckClasses() {
    List<Class> classes = new ArrayList<Class>();
    classes.addAll(JspCheckClasses.getCheckClasses());
    classes.addAll(XhtmlCheckClasses.getCheckClasses());
    return classes;
  }

  /**
   * Instantiate checks as defined in the RulesProfile.
   * 
   * @param profile
   */
  public static List<AbstractPageCheck> createChecks(RulesProfile profile) {
    LOG.info("Loading web rules for profile " + profile.getName());

    List<AbstractPageCheck> checks = new ArrayList<AbstractPageCheck>();

    for (ActiveRule activeRule : profile.getActiveRules()) {
      Class<AbstractPageCheck> checkClass = getCheckClass(activeRule);
      if (checkClass == null) {
        continue; //TODO raise warning
      }

      checks.add(createCheck(checkClass, activeRule));
    }

    return checks;
  }

  public static Rule getRule(String ruleKey) {
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
    LOG.error("Unexpected category name " + category);
    return Iso9126RulesCategories.MAINTAINABILITY;
  }

  private final Web web;

  public WebRulesRepository(Web web) {
    this.web = web;
  }

  public String exportConfiguration(RulesProfile activeProfile) {

    return new ProfileXmlParser().exportConfiguration(activeProfile);
  }

  private String getConfigurationFromFile(String path) {
    InputStream inputStream = WebRulesRepository.class.getResourceAsStream(path);
    try {
      return IOUtils.toString(inputStream, "UTF-8");
    } catch (IOException e) {
      throw new SonarException("Configuration file not found for the profile : " + path, e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  public final List<Rule> getInitialReferential() {
    return parseReferential(RULE_FILE);
  }

  public Web getLanguage() {
    return web;
  }

  /**
   * Convert the built-in rules configurations to RulesProfiles.
   */
  public final List<RulesProfile> getProvidedProfiles() {
    List<RulesProfile> profiles = new ArrayList<RulesProfile>();
    StandardProfileXmlParser parser = new StandardProfileXmlParser(getInitialReferential());

    RulesProfile profile = parser.importConfiguration(getConfigurationFromFile(RULE_FILE));
    profile.setLanguage(web.getKey());
    LOG.debug("Building profile " + profile.getName());

    profiles.add(profile);

    return profiles;
  }

  /**
   * Converts the built-in rules configuration to ActiveRules.
   */
  public List<ActiveRule> importConfiguration(String configuration, List<Rule> rulesRepository) {

    LOG.debug("importConfiguration");

    StandardProfileXmlParser parser = new StandardProfileXmlParser(rulesRepository);
    RulesProfile profile = parser.importConfiguration(configuration);
    return profile.getActiveRules();
  }

  /**
   * Gets the built-in rules.
   */
  public List<Rule> parseReferential(String path) {

    return rulesRepository;
  }
}