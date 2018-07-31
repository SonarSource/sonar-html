/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.rules;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.BuiltInQualityProfile;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.Context;
import org.sonar.api.utils.Version;
import org.sonar.plugins.web.api.WebConstants;

public class SonarWayProfileTest {

  @Test
  public void test() {
    SonarRuntime sonarRuntime = SonarRuntimeImpl.forSonarQube(Version.create(7, 3), SonarQubeSide.SERVER);
    SonarWayProfile definition = new SonarWayProfile(sonarRuntime);
    Context context = new Context();
    definition.define(context);
    BuiltInQualityProfile profile = context.profile("web", "Sonar way");
    Assertions.assertThat(profile.name()).isEqualTo("Sonar way");
    Assertions.assertThat(profile.language()).isEqualTo(WebConstants.LANGUAGE_KEY);
    Assertions.assertThat(profile.rules().size()).isGreaterThan(10);
  }

}
