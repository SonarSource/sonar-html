/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.rules.AnnotationRuleParser;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.web.AbstractWebPluginTester;
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

    try {
      AbstractPageCheck check = checkClass.newInstance();

      Rule rule = getRule(checkClass.getSimpleName(), checkClass);
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

  private void configureParams(AbstractPageCheck check, Rule rule) {
    WebRuleFinder finder = new WebRuleFinder(rule);
    ProfileDefinition profileDefinition = new DefaultWebProfile(finder);
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
