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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparisons.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.junit.Test;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.check.Rule;
import org.sonar.plugins.web.AbstractWebPluginTester;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.rules.DefaultWebProfile;
import org.sonar.plugins.web.rules.JSFProfile;
import org.sonar.plugins.web.rules.JSPProfile;
import org.sonar.plugins.web.rules.WebProfileExporter;
import org.sonar.plugins.web.rules.WebProfileImporter;
import org.sonar.plugins.web.rules.WebRulesRepository;

/**
 * @author Matthijs Galesloot
 */
public class WebRulesRepositoryTest extends AbstractWebPluginTester {

  @Test
  public void createDefaultWebProfile() {
    ProfileDefinition profileDefinition = new DefaultWebProfile(newRuleFinder());
    ValidationMessages validationMessages = ValidationMessages.create();
    RulesProfile profile = profileDefinition.createProfile(validationMessages);

    assertThat(profile.getActiveRulesByRepository(WebRulesRepository.REPOSITORY_KEY).size(), greaterThan(1));
    assertThat(validationMessages.hasErrors(), is(false));
  }

  @Test
  public void createJSFProfile() {
    ProfileDefinition profileDefinition = new JSFProfile(newRuleFinder());
    ValidationMessages validationMessages = ValidationMessages.create();
    RulesProfile profile = profileDefinition.createProfile(validationMessages);

    assertThat(profile.getActiveRulesByRepository(WebRulesRepository.REPOSITORY_KEY).size(), greaterThan(1));
    assertThat(validationMessages.hasErrors(), is(false));
  }

  @Test
  public void createJSPProfile() {
    ProfileDefinition profileDefinition = new JSPProfile(newRuleFinder());
    ValidationMessages validationMessages = ValidationMessages.create();
    RulesProfile profile = profileDefinition.createProfile(validationMessages);

    assertThat(profile.getActiveRulesByRepository(WebRulesRepository.REPOSITORY_KEY).size(), greaterThan(1));
    assertThat(validationMessages.hasErrors(), is(false));
  }

  @Test
  public void initializeWebRulesRepository() {
    WebRulesRepository rulesRepository = new WebRulesRepository(newServerFileSystem());

    assertTrue(rulesRepository.createRules().size() > 20);
  }

  @Test
  public void createChecks() {
    ProfileDefinition profileDefinition = new DefaultWebProfile(newRuleFinder());
    ValidationMessages validationMessages = ValidationMessages.create();
    RulesProfile profile = profileDefinition.createProfile(validationMessages);

    List<AbstractPageCheck> checks = WebRulesRepository.createChecks(profile);

    // check annotation
    for (AbstractPageCheck check : checks) {
      Rule rule = check.getClass().getAnnotation(Rule.class);
      assertNotNull(rule.key());
      assertNotNull(rule.name());
      assertNotNull(rule.isoCategory());
    }
    assertTrue(checks.size() > 20);
  }

  @Test
  public void exportImportProfile() throws FileNotFoundException {
    ValidationMessages validationMessages = ValidationMessages.create();

    // import rules
    String path = "org/sonar/plugins/web/rules/web/jsf-rules.xml";
    Reader reader = new InputStreamReader(JSFProfile.class.getClassLoader().getResourceAsStream(path));
    RulesProfile rulesProfile1 = new WebProfileImporter(newRuleFinder()).importProfile(reader, validationMessages);

    // export the rules to xml
    StringWriter writer = new StringWriter();
    new WebProfileExporter().exportProfile(rulesProfile1, writer);
    assertNotNull(writer.getBuffer().toString());

    reader = new StringReader(writer.getBuffer().toString());
    RulesProfile rulesProfile2 = new WebProfileImporter(newRuleFinder()).importProfile(reader, validationMessages);

    assertNotSame(rulesProfile1.getActiveRules(), rulesProfile2.getActiveRules());
    assertEquals(rulesProfile1.getActiveRules().size(), rulesProfile2.getActiveRules().size());
  }
}
