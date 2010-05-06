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

package org.sonar.plugins.web.rules.checks;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.plugins.web.WebRulesRepository;
import org.sonar.plugins.web.WebUtils;
import org.sonar.plugins.web.rules.xml.Property;
import org.sonar.plugins.web.rules.xml.RuleDefinition;
import org.sonar.plugins.web.rules.xml.RulesUtils;
import org.sonar.plugins.web.rules.xml.Ruleset;

/**
 * @author Matthijs Galesloot
 */
public final class HtmlChecks {

  private HtmlChecks() {
  }

  private static List<HtmlCheck> htmlChecks;

  /**
   * Instantiate checks as defined in the RulesProfile. 
   * 
   * @param profile
   */
  public static List<HtmlCheck> getChecks(RulesProfile profile) {
    if (htmlChecks == null) {
      htmlChecks = new ArrayList<HtmlCheck>();

      Ruleset ruleset = RulesUtils.buildRuleSetFromXml(WebRulesRepository.getConfigurationFromFile(WebRulesRepository.RULE_FILE));

      for (ActiveRule activeRule : profile.getActiveRules()) {
        RuleDefinition ruleDefinition = getBuiltin(ruleset, activeRule);

        if (ruleDefinition == null) {
          continue;
        }

        try {
          Class clazz = Class.forName(ruleDefinition.getClazz());
          Constructor<HtmlCheck> constructor = clazz.getConstructor();
          HtmlCheck checker = constructor.newInstance();
          checker.setRuleKey(ruleDefinition.getName());
          if (activeRule.getActiveRuleParams() != null) {
            for (ActiveRuleParam param : activeRule.getActiveRuleParams()) {
              PropertyUtils.setProperty(checker, param.getRuleParam().getKey(), param.getValue());
            }
          }
          htmlChecks.add(checker);

          // debug
          if (WebUtils.LOG.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            if (ruleDefinition.getProperties() != null) {
              for (Property property : ruleDefinition.getProperties()) {
                if (sb.length() > 0) {
                  sb.append(',');
                }
                sb.append(property.getName());
                sb.append('=');
                sb.append(property.getValue());
              }
              sb.append(')');
              sb.insert(0, " (");
            }
            sb.insert(0, clazz.getSimpleName());
            sb.insert(0, "Created checker ");
            WebUtils.LOG.debug(sb.toString());
          }
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        } catch (InstantiationException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        } catch (SecurityException e) {
          throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return htmlChecks;
  }

  private static RuleDefinition getBuiltin(Ruleset ruleset, ActiveRule activeRule) {
    for (RuleDefinition ruleDefinition : ruleset.getRules()) {
      if (ruleDefinition.getName().equals(activeRule.getRuleKey())) {
        return ruleDefinition;
      }
    }
    WebUtils.LOG.error("Could not find rule " + activeRule.getRuleKey());
    return null;
  }
}
