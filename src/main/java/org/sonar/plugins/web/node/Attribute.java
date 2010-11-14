/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.web.node;

public class Attribute {

  private String name;
  private char quoteChar;
  private String value;

  public Attribute(String name) {
    this.name = name;
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
}
