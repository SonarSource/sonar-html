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

package org.sonar.plugins.web;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.checks.NoSonarFilter;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.rules.DefaultWebProfile;
import org.sonar.plugins.web.rules.WebRulesRepository;

/**
 * @author Matthijs Galesloot
 */
public class WebSensorTest extends AbstractWebPluginTester {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractWebPluginTester.class);

  @Test
  public void testSensor() throws Exception {
    ProfileDefinition profileDefinition = new DefaultWebProfile(new WebRuleFinder());
    RulesProfile profile = profileDefinition.createProfile(ValidationMessages.create());
    NoSonarFilter noSonarFilter = new NoSonarFilter();
    WebSensor sensor = new WebSensor(new Web(), profile, noSonarFilter);
    assertNotNull(sensor.toString());

    final Project project = loadProjectFromPom();

    assertTrue(sensor.shouldExecuteOnProject(project));

    MockSensorContext sensorContext = new MockSensorContext();
    sensor.analyse(project, sensorContext);

    assertTrue("Should have found 1 violation", sensorContext.getViolations().size() > 0);
  }

  private class WebRuleFinder implements RuleFinder {

    private final WebRulesRepository repository;
    private final List<Rule> rules;

    public WebRuleFinder() {
      repository = new WebRulesRepository(newServerFileSystem());
      rules = repository.createRules();
    }

    public Rule findByKey(String repositoryKey, String key) {
      for (Rule rule : rules) {
        if (rule.getKey().equals(key)) {
          return rule;
        }
      }
      return null;
    }

    public Collection<Rule> findAll(RuleQuery query) {
      // TODO Auto-generated method stub
      return null;
    }

    public Rule find(RuleQuery query) {
      // TODO Auto-generated method stub
      return null;
    }
  }
}
