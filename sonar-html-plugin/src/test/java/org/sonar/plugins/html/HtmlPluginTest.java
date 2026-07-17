/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html;

import com.sonarsource.scanner.engine.sensor.test.fixtures.TestSonarRuntime;
import org.junit.jupiter.api.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.utils.Version;
import org.sonar.plugins.html.core.AnalysisWarningsWrapper;
import org.sonar.plugins.html.core.Html;
import org.sonar.plugins.html.core.HtmlSensor;
import org.sonar.plugins.html.core.Jsp;
import org.sonar.plugins.html.rules.HtmlRulesDefinition;
import org.sonar.plugins.html.rules.JspQualityProfile;
import org.sonar.plugins.html.rules.SonarWayProfile;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlPluginTest {

  @Test
  void webPluginTester() {
    Plugin.Context context = new Plugin.Context(TestSonarRuntime.forSonarQube(Version.create(7, 9), SonarQubeSide.SERVER, SonarEdition.COMMUNITY));

    new HtmlPlugin().define(context);
    assertThat(context.getExtensions())
      .contains(
        Html.class,
        Jsp.class,
        HtmlRulesDefinition.class,
        SonarWayProfile.class,
        JspQualityProfile.class,
        AnalysisWarningsWrapper.class,
        HtmlSensor.class)
      .hasSize(9);
  }
}
