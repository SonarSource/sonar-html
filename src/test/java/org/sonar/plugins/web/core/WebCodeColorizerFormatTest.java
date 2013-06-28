/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot and SonarSource
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

import org.junit.Ignore;
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
    assertThat(highlight("</tr>"), containsString("<span class=\"k\">&lt;/tr&gt;</span>"));
    assertThat(highlight("<h3>"), containsString("<span class=\"k\">&lt;h3&gt;</span>"));
  }

  @Test
  public void testHighlightTagWithNamespace() {
    assertThat(highlight("<namespace:table >"), containsString("<span class=\"k\">&lt;namespace:table</span> <span class=\"k\">&gt;</span>"));
  }

  @Test
  public void testHighlightTagWithNoValueProperty() {
    assertThat(highlight("<table nospace>"), containsString("<span class=\"k\">&lt;table</span> nospace<span class=\"k\">&gt;</span>"));
  }

  @Test
  public void testHighlightTagWithProperties() {
    assertThat(highlight("<table size=\"45px\">"), containsString("<span class=\"k\">&lt;table</span> size=<span class=\"s\">\"45px\"</span><span class=\"k\">&gt;</span>"));
    assertThat(highlight("<table size='45px'>"), containsString("<span class=\"k\">&lt;table</span> size=<span class=\"s\">'45px'</span><span class=\"k\">&gt;</span>"));
  }

  @Test
  public void testHighlightJspDirective() {
    assertThat(highlight("<%@ taglib uri=\"/struts-tags\" %> Foo"),
        containsString("<span class=\"a\">&lt;%@ taglib uri=\"/struts-tags\" %</span><span class=\"k\">&gt;</span> Foo"));
  }

  @Test
  @Ignore("Needs SONARPLUGINS-1885 to be fixed")
  public void testHighlightJspExpressions() {
    assertThat(highlight("<% System.out.println('foo') %> Foo"), containsString("<span class=\"a\">&lt;% System.out.println('foo') %</span><span class=\"k\">&gt;</span> Foo"));
  }

  @Test
  public void testHighlightComments() {
    assertThat(highlight("<!-- hello world!! --> Foo"), containsString("<span class=\"j\">&lt;!-- hello world!! --</span><span class=\"k\">&gt;</span> Foo"));
    assertThat(highlight("<%-- hello world!! --%> Foo"), containsString("<span class=\"j\">&lt;%-- hello world!! --%</span><span class=\"k\">&gt;</span> Foo"));
  }

  @Test
  public void testHighlightCommentsAndOtherTag() {
    assertThat(
        highlight("<%-- hello world!! --%><table size='45px'>"),
        containsString("<span class=\"j\">&lt;%-- hello world!! --%</span><span class=\"k\">&gt;</span><span class=\"k\">&lt;table</span> size=<span class=\"s\">'45px'</span><span class=\"k\">&gt;</span>"));
  }

  @Test
  public void testHighlightDoctype() {
    assertThat(highlight("<!DOCTYPE foo bar >"), containsString("<span class=\"j\">&lt;!DOCTYPE foo bar &gt;</span>"));
  }

  private String highlight(String webSourceCode) {
    return codeColorizer.toHtml(new StringReader(webSourceCode));
  }

}
