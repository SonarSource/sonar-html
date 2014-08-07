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

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.web.AbstractWebPluginTester;
import org.sonar.plugins.web.api.WebConstants;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SonarWayProfileTest extends AbstractWebPluginTester {

  @Test
  public void test() {
    ValidationMessages validationMessages = ValidationMessages.create();
    RulesProfile profile = new SonarWayProfile(newRuleFinder()).createProfile(validationMessages);

    Assertions.assertThat(profile.getName()).isEqualTo("Sonar way");
    Assertions.assertThat(profile.getLanguage()).isEqualTo(WebConstants.LANGUAGE_KEY);
    Assertions.assertThat(profile.getActiveRules()).onProperty("repositoryKey").containsOnly(WebRulesRepository.REPOSITORY_KEY);
    Assertions.assertThat(profile.getActiveRules().size()).isEqualTo(15);
    assertThat(validationMessages.hasErrors(), is(false));
  }

}
