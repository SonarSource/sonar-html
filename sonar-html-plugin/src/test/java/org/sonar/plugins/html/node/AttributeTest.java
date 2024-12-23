/*
 * SonarQube HTML
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.node;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
