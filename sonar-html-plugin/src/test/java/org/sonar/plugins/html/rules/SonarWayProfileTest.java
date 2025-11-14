/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.rules;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.BuiltInQualityProfile;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.Context;
import org.sonar.plugins.html.api.HtmlConstants;

class SonarWayProfileTest {

  @Test
  void test() {
    SonarWayProfile definition = new SonarWayProfile();
    Context context = new Context();
    definition.define(context);
    BuiltInQualityProfile profile = context.profile("web", "Sonar way");
    Assertions.assertThat(profile.name()).isEqualTo("Sonar way");
    Assertions.assertThat(profile.language()).isEqualTo(HtmlConstants.LANGUAGE_KEY);
    Assertions.assertThat(profile.rules()).hasSizeGreaterThan(10);
  }

}
