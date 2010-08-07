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

package org.sonar.plugins.web.checks;

import static junit.framework.Assert.assertNotNull;

import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.rules.Rule;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.web.WebRulesRepository;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.PageScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

public abstract class AbstractCheckTester {

  public WebSourceCode parseAndCheck(Reader reader, Class<? extends AbstractPageCheck> checkClass, String... params) {

    try {
      AbstractPageCheck check = checkClass.newInstance();

      Rule rule = WebRulesRepository.getRule(checkClass.getSimpleName());
      assertNotNull("Could not find rule", rule);
      check.setRule(rule);
      configureParams(check, rule);

      for (int i = 0; i < params.length / 2; i++) {
        BeanUtils.setProperty(check, params[i * 2], params[i * 2 + 1]);
      }

      PageLexer lexer = new PageLexer();
      List<Node> nodeList = lexer.parse(reader);
      WebSourceCode webSourceCode = new WebSourceCode(null);

      PageScanner pageScanner = new PageScanner();
      pageScanner.addVisitor(check);
      pageScanner.scan(nodeList, webSourceCode);
      return webSourceCode;
    } catch (IllegalAccessException e) {
      throw new SonarException(e);
    } catch (InstantiationException e) {
      throw new SonarException(e);
    } catch (InvocationTargetException e) {
      throw new SonarException(e);
    }
  }

  private void configureParams(AbstractPageCheck check, Rule rule) {
    RulesProfile profile = new WebRulesRepository(new Web()).getProvidedProfiles().get(0);
    ActiveRule activeRule = profile.getActiveRule(rule);

    assertNotNull("Could not find activeRule", activeRule);

    try {
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
    } catch (IllegalAccessException e) {
      throw new SonarException(e);
    } catch (InvocationTargetException e) {
      throw new SonarException(e);
    } catch (NoSuchMethodException e) {
      throw new SonarException(e);
    }
  }
}
