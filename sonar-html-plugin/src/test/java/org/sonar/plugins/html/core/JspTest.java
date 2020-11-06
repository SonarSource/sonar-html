/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2020 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.core;

import org.junit.Test;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.plugins.html.api.HtmlConstants;

import static org.fest.assertions.Assertions.assertThat;

public class JspTest {

  @Test
  public void testDefaultFileSuffixes() {
    MapSettings settings = new MapSettings();
    settings.setProperty(HtmlConstants.JSP_FILE_EXTENSIONS_PROP_KEY, HtmlConstants.JSP_FILE_EXTENSIONS_DEF_VALUE);
    Jsp jsp = new Jsp(settings.asConfig());
    assertThat(jsp.getFileSuffixes()).containsOnly(".jsp", ".jspf", ".jspx");
  }

  @Test
  public void testCustomFileSuffixes() {
    MapSettings settings = new MapSettings();
    settings.setProperty(HtmlConstants.JSP_FILE_EXTENSIONS_PROP_KEY, "foo, bar ,   toto");
    Jsp jsp = new Jsp(settings.asConfig());
    assertThat(jsp.getFileSuffixes()).containsOnly("foo", "bar", "toto");
  }

}
