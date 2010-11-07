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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.platform.ServerFileSystem;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.rules.AnnotationRuleParser;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.api.utils.SonarException;
import org.sonar.check.Cardinality;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.jsp.JspCheckClasses;
import org.sonar.plugins.web.checks.xhtml.XhtmlCheckClasses;
import org.sonar.plugins.web.language.Web;
/**
 * @author Matthijs Galesloot
 */
public final class WebRulesRepository extends RuleRepository {

  private static final Logger LOG = LoggerFactory.getLogger(WebRulesRepository.class);

  public static final String REPOSITORY_NAME = "Web";
  public static final String REPOSITORY_KEY = "Web";

  // for user extensions
  private final ServerFileSystem fileSystem;

  public WebRulesRepository(ServerFileSystem fileSystem) {
    super(REPOSITORY_KEY, Web.KEY);
    setName(REPOSITORY_NAME);
    this.fileSystem = fileSystem;
  }

  @Override
  public List<Rule> createRules() {
    AnnotationRuleParser annotationRuleParser = new AnnotationRuleParser();
    List<Rule> rules = annotationRuleParser.parse(REPOSITORY_KEY, getCheckClasses());
    for (Rule rule : rules) {
      rule.setCardinality(Cardinality.MULTIPLE);
    }
    return rules;
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
    LOG.info("Loading checks for profile " + profile.getName());

    List<AbstractPageCheck> checks = new ArrayList<AbstractPageCheck>();

    for (ActiveRule activeRule : profile.getActiveRules()) {
      if (REPOSITORY_KEY.equals(activeRule.getRepositoryKey())) {
        Class<AbstractPageCheck> checkClass = getCheckClass(activeRule);
        if (checkClass != null) {
          checks.add(createCheck(checkClass, activeRule));
        }
      }
    }

    return checks;
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
          if (!StringUtils.isEmpty(param.getValue())) {
            LOG.debug("Rule param " + param.getKey() + " = " + param.getValue());
            BeanUtils.setProperty(check, param.getRuleParam().getKey(), param.getValue());
          }
        }
      }

      return check;
    } catch (IllegalAccessException e) {
      throw new SonarException(e);
    } catch (InvocationTargetException e) {
      throw new SonarException(e);
    } catch (InstantiationException e) {
      throw new SonarException(e);
    }
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

  private static Class<AbstractPageCheck> getCheckClass(ActiveRule activeRule) {
    for (Class<AbstractPageCheck> checkClass : getCheckClasses()) {

      org.sonar.check.Rule ruleAnnotation = AnnotationUtils.getClassAnnotation(checkClass, org.sonar.check.Rule.class);
      if (ruleAnnotation.key().equals(activeRule.getConfigKey())) {
        return checkClass;
      }
    }
    LOG.error("Could not find check class for config key " + activeRule.getConfigKey());
    return null;
  }
}