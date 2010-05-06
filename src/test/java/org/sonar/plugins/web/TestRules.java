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

import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.sonar.api.rules.Rule;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.lex.HtmlElement;
import org.sonar.plugins.web.lex.HtmlTokenList;
import org.sonar.plugins.web.lex.Token;
import org.sonar.plugins.web.rules.checks.HtmlCheck;
import org.sonar.plugins.web.rules.checks.HtmlChecks;

/**
 * @author Matthijs Galesloot
 */
public class TestRules {

  @Test
  public void testRegularExpression() {

    HtmlTokenList tokenList = new HtmlTokenList();
    HtmlElement token = new HtmlElement();
    token.setCode("xx  class=\"yyy\"");
    HtmlElement token2 = new HtmlElement();
    token2.setCode("<br>");
    tokenList.getTokens().add(token);
    // tokenList.collect(token2);

    MockSensorContext context = new MockSensorContext();
    WebRulesRepository repository = new WebRulesRepository(Web.INSTANCE);
    
    for (HtmlCheck check : HtmlChecks.getChecks(repository.getProvidedProfiles().get(0))) {
      check.startDocument(context, null);
    }
    for (Token t : tokenList.getTokens()) {
      for (HtmlCheck check : HtmlChecks.getChecks(repository.getProvidedProfiles().get(0))) {
        check.startElement(t);
      }
    }

    assertTrue("Should have found 1 violation", context.getViolations().size() > 0);
  }

  @Test
  public void testWebRulesRepository() {
    WebRulesRepository rulesRepository = new WebRulesRepository(Web.INSTANCE);

    assertTrue(rulesRepository.getInitialReferential().size() > 1);
    for (Rule rule : rulesRepository.getInitialReferential()) {
      WebUtils.LOG.debug(rule.getKey());
    }
  }
}
