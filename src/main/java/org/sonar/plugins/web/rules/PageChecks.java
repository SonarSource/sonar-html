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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.plugins.web.WebUtils;

/**
 * @author Matthijs Galesloot
 */
public final class PageChecks {

  private static List<AbstractPageCheck> checks;

  /**
   * Instantiate checks as defined in the RulesProfile.
   * 
   * @param profile
   */
  public static List<AbstractPageCheck> getChecks(RulesProfile profile) {
    if (checks == null) {
      checks = new ArrayList<AbstractPageCheck>();

      for (ActiveRule activeRule : profile.getActiveRules()) {
        Class<AbstractPageCheck> checkClass = WebRulesRepository.getCheckClass(activeRule);
        if (checkClass == null) {
          continue;
        }

        try {
          Constructor<AbstractPageCheck> constructor = checkClass.getConstructor();
          AbstractPageCheck checker = constructor.newInstance();
          checker.setRuleKey(activeRule.getRuleKey());
          if (activeRule.getActiveRuleParams() != null) {
            for (ActiveRuleParam param : activeRule.getActiveRuleParams()) {
              Object value = PropertyUtils.getProperty(checker, param.getRuleParam().getKey());
              if (value instanceof Integer) {
                value = Integer.parseInt(param.getValue());
              } else {
                value = param.getValue();
              }
              PropertyUtils.setProperty(checker, param.getRuleParam().getKey(), value);
            }
          }
          checks.add(checker);

          // debug
          if (WebUtils.LOG.isDebugEnabled()) {
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
            WebUtils.LOG.debug(sb.toString());
          }
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
    return checks;
  }

  private PageChecks() {
  }
}
