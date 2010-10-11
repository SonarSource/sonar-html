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

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.sonar.api.rules.Rule;
import org.sonar.plugins.web.AbstractWebPluginTester;
import org.sonar.plugins.web.rules.markup.MarkupRuleRepository;


/**
 * @author Matthijs Galesloot
 */
public class MarkupRulesRepositoryTest extends AbstractWebPluginTester {


  @Test
  public void initializeMarkupRulesRepository() {
    MarkupRuleRepository repository = new MarkupRuleRepository();

    List<Rule> rules = repository.createRules();

    for (Rule rule : rules) {
      assertNotNull(rule.getRulesCategory());
    }
    assertTrue(repository.createRules().size() > 400);
  }
}
