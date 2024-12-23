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

/**
 * Defines a node.
 *

 */
public abstract class Node {

  private String code;
  private int endColumnPosition;
  private int endLinePosition;
  private final NodeType nodeType;
  private int startColumnPosition;
  private int startLinePosition;

  protected Node(NodeType nodeType) {
    this.nodeType = nodeType;
  }

  public String getCode() {
    return code == null ? "" : code;
  }

  public int getEndColumnPosition() {
    return endColumnPosition;
  }

  public int getEndLinePosition() {
    return endLinePosition;
  }

  public NodeType getNodeType() {
    return nodeType;
  }

  public int getStartColumnPosition() {
    return startColumnPosition;
  }

  public int getStartLinePosition() {
    return startLinePosition;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setEndColumnPosition(int endColumnPosition) {
    this.endColumnPosition = endColumnPosition;
  }

  public void setEndLinePosition(int endLinePosition) {
    this.endLinePosition = endLinePosition;
  }

  public void setStartColumnPosition(int startColumnPosition) {
    this.startColumnPosition = startColumnPosition;
  }

  public void setStartLinePosition(int startLinePosition) {
    this.startLinePosition = startLinePosition;
  }

  @Override
  public String toString() {
    return getCode();
  }

}
