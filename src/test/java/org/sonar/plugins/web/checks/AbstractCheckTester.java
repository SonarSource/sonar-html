/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.checks;

import static junit.framework.Assert.assertNotNull;

import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.resources.File;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.rules.AnnotationRuleParser;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.web.AbstractWebPluginTester;
import org.sonar.plugins.web.analyzers.PageCountLines;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.rules.DefaultWebProfile;
import org.sonar.plugins.web.visitor.PageScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

public abstract class AbstractCheckTester extends AbstractWebPluginTester {

  private Rule getRule(String ruleKey, Class<? extends AbstractPageCheck> checkClass) {

    AnnotationRuleParser parser = new AnnotationRuleParser();
    List<Rule> rules = parser.parse("Web", Arrays.asList(new Class[] { checkClass }));
    for (Rule rule : rules) {
      if (rule.getKey().equals(ruleKey)) {
        return rule;
      }
    }
    return null;
  }

  public WebSourceCode parseAndCheck(Reader reader, Class<? extends AbstractPageCheck> checkClass, String... params) {

    return parseAndCheck(reader, null, null, checkClass, params);
  }

  public WebSourceCode parseAndCheck(Reader reader, java.io.File file,
      String code, Class<? extends AbstractPageCheck> checkClass, String... params) {

    AbstractPageCheck check = instantiateCheck(checkClass, params);

    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);
    WebSourceCode webSourceCode = new WebSourceCode(new File("test"));

    PageScanner pageScanner = new PageScanner();
    pageScanner.addVisitor(new PageCountLines());
    pageScanner.addVisitor(check);
    pageScanner.scan(nodeList, webSourceCode);
    return webSourceCode;
  }

  protected AbstractPageCheck instantiateCheck(Class<? extends AbstractPageCheck> checkClass, String... params) {
    try {
      AbstractPageCheck check = checkClass.newInstance();

      Rule rule = getRule(checkClass.getSimpleName(), checkClass);
      assertNotNull("Could not find rule", rule);
      check.setRule(rule);
      configureDefaultParams(check, rule);

      for (int i = 0; i < params.length / 2; i++) {
        BeanUtils.setProperty(check, params[i * 2], params[i * 2 + 1]);
        assertNotNull(BeanUtils.getProperty(check, params[i * 2]));
      }
      return check;
    } catch (IllegalAccessException e) {
      throw new SonarException(e);
    } catch (InstantiationException e) {
      throw new SonarException(e);
    } catch (InvocationTargetException e) {
      throw new SonarException(e);
    } catch (NoSuchMethodException e) {
      throw new SonarException(e);
    }
  }

  private static final class WebRuleFinder implements RuleFinder {

    private final Rule rule;

    public WebRuleFinder(Rule rule) {
      this.rule = rule;
    }

    public Rule findByKey(String repositoryKey, String key) {
      if (rule.getKey().equals(key)) {
        return rule;
      } else {
        return null;
      }
    }

    public Rule find(RuleQuery query) {
      return rule;
    }

    public Collection<Rule> findAll(RuleQuery query) {
      return new ArrayList<Rule>();
    }
  }

  private void configureDefaultParams(AbstractPageCheck check, Rule rule) {
    WebRuleFinder ruleFinder = new WebRuleFinder(rule);
    ProfileDefinition profileDefinition = new DefaultWebProfile(new XMLProfileParser(ruleFinder));
    ValidationMessages validationMessages = ValidationMessages.create();
    RulesProfile rulesProfile = profileDefinition.createProfile(validationMessages);

    ActiveRule activeRule = rulesProfile.getActiveRule(rule);

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
