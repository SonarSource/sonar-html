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

package org.sonar.plugins.web.core;

import org.sonar.plugins.web.core.WebCodeColorizerFormat;

import org.junit.Test;
import org.sonar.colorizer.CodeColorizer;

import java.io.StringReader;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class WebCodeColorizerFormatTest {

  WebCodeColorizerFormat webColorizer = new WebCodeColorizerFormat();
  CodeColorizer codeColorizer = new CodeColorizer(webColorizer.getTokenizers());

  @Test
  public void testHighlightTag() {
    assertThat(highlight("<table nospace>"), containsString("<span class=\"k\">&lt;table</span> nospace<span class=\"k\">&gt;</span>"));
    assertThat(highlight("</tr>"), containsString("<span class=\"k\">&lt;/tr&gt;</span>"));
  }

  private String highlight(String webSourceCode) {
    return codeColorizer.toHtml(new StringReader(webSourceCode));
  }

}
