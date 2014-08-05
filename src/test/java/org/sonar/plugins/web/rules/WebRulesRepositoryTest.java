/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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
package org.sonar.plugins.web.rules;

import org.junit.Test;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.AnnotationRuleParser;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.web.AbstractWebPluginTester;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class WebRulesRepositoryTest extends AbstractWebPluginTester {

  @Test
  public void createSonarWayProfile() {
    ProfileDefinition profileDefinition = new SonarWayProfile(newRuleFinder());
    ValidationMessages validationMessages = ValidationMessages.create();
    RulesProfile profile = profileDefinition.createProfile(validationMessages);

    assertThat(profile.getActiveRulesByRepository(WebRulesRepository.REPOSITORY_KEY).size(), equalTo(14));
    assertThat(validationMessages.hasErrors(), is(false));
  }

  @Test
  public void initializeWebRulesRepository() {
    WebRulesRepository rulesRepository = new WebRulesRepository(new AnnotationRuleParser());

    assertTrue(rulesRepository.createRules().size() > 20);
  }

}
