/*
 * SonarWeb :: SonarQube Plugin
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
package org.sonar.plugins.web.node;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class AttributeTest {

  @Test
  public void one_arg_constructor() {
    assertThat(new Attribute("foo").getName()).isEqualTo("foo");
    assertThat(new Attribute("foo").getValue()).isEmpty();
  }

  @Test
  public void two_args_constructor() {
    assertThat(new Attribute("foo", "bar").getName()).isEqualTo("foo");
    assertThat(new Attribute("foo", "bar").getValue()).isEqualTo("bar");
  }

  @Test
  public void name() {
    Attribute attribute = new Attribute("");
    attribute.setName("test");
    assertThat(attribute.getName()).isEqualTo("test");
  }

  @Test
  public void quote_char() {
    Attribute attribute = new Attribute("");

    attribute.setQuoteChar('a');
    assertThat(attribute.isSingleQuoted()).isFalse();
    assertThat(attribute.isDoubleQuoted()).isFalse();

    attribute.setQuoteChar('\'');
    assertThat(attribute.isSingleQuoted()).isTrue();
    assertThat(attribute.isDoubleQuoted()).isFalse();

    attribute.setQuoteChar('"');
    assertThat(attribute.isSingleQuoted()).isFalse();
    assertThat(attribute.isDoubleQuoted()).isTrue();
  }

  @Test
  public void value() {
    Attribute attribute = new Attribute("");
    attribute.setValue("test");
    assertThat(attribute.getValue()).isEqualTo("test");
  }

  @Test
  public void line() {
    Attribute attribute = new Attribute("");
    attribute.setLine(42);
    assertThat(attribute.getLine()).isEqualTo(42);
  }

}
