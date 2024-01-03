/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
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
package org.sonar.plugins.html.rules;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.BuiltInQualityProfile;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.Context;
import org.sonar.plugins.html.api.HtmlConstants;

public class SonarWayProfileTest {

  @Test
  public void test() {
    SonarWayProfile definition = new SonarWayProfile();
    Context context = new Context();
    definition.define(context);
    BuiltInQualityProfile profile = context.profile("web", "Sonar way");
    Assertions.assertThat(profile.name()).isEqualTo("Sonar way");
    Assertions.assertThat(profile.language()).isEqualTo(HtmlConstants.LANGUAGE_KEY);
    Assertions.assertThat(profile.rules().size()).isGreaterThan(10);
  }

}
