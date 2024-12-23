/*
 * SonarQube HTML Plugin :: Sonar Plugin
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

/**
 * Defines an attribute of a node.
 */
public class Attribute {

  private String name;
  private char quoteChar;
  private String value;
  private int line;

  public Attribute(String name) {
    this(name, "");
  }

  public Attribute(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public boolean isDoubleQuoted() {
    return quoteChar == '\"';
  }

  public boolean isSingleQuoted() {
    return quoteChar == '\'';
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setQuoteChar(char quoteChar) {
    this.quoteChar = quoteChar;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public int getLine() {
    return line;
  }

  public void setLine(int line) {
    this.line = line;
  }

}
