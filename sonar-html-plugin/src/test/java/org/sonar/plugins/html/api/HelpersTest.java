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
package org.sonar.plugins.html.api;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

class HelpersTest {

  private static HtmlSourceCode sourceCode(String filename) {
    return new HtmlSourceCode(
      new TestInputFileBuilder("key", filename)
        .setLanguage(HtmlConstants.LANGUAGE_KEY)
        .setType(InputFile.Type.MAIN)
        .setCharset(StandardCharsets.UTF_8)
        .build()
    );
  }

  @Test
  void contains_dynamic_value_detects_each_marker() {
    HtmlSourceCode code = sourceCode("file.html");
    assertThat(Helpers.containsDynamicValue("Welcome ${name}", code)).isTrue();
    assertThat(Helpers.containsDynamicValue("intro {{ greeting }} outro", code)).isTrue();
    assertThat(Helpers.containsDynamicValue("intro {% if x %} outro", code)).isTrue();
    assertThat(Helpers.containsDynamicValue("hello <?= $user ?> bye", code)).isTrue();
    assertThat(Helpers.containsDynamicValue("hello <?php echo $u; ?> bye", code)).isTrue();
    assertThat(Helpers.containsDynamicValue("hello <% scriptlet %> bye", code)).isTrue();
    assertThat(Helpers.containsDynamicValue("hello <%= expr %> bye", code)).isTrue();
  }

  @Test
  void contains_dynamic_value_returns_false_when_no_marker_present() {
    HtmlSourceCode code = sourceCode("file.html");
    assertThat(Helpers.containsDynamicValue("just a static string", code)).isFalse();
    assertThat(Helpers.containsDynamicValue("", code)).isFalse();
    assertThat(Helpers.containsDynamicValue("uses $ and { but not together", code)).isFalse();
  }

  @Test
  void contains_dynamic_value_detects_razor_only_for_cshtml() {
    String razor = "Hello @user.Name";
    assertThat(Helpers.containsDynamicValue(razor, sourceCode("file.cshtml"))).isTrue();
    assertThat(Helpers.containsDynamicValue(razor, sourceCode("file.html"))).isFalse();
  }

  @Test
  void contains_dynamic_value_ignores_escaped_razor_at() {
    HtmlSourceCode code = sourceCode("file.cshtml");
    assertThat(Helpers.containsDynamicValue("user@@example.com", code)).isFalse();
  }
}
