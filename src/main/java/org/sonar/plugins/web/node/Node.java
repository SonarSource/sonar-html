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

import org.apache.commons.lang.StringUtils;

/**
 * @author Matthijs Galesloot
 */
public abstract class Node {

  private String code;
  private int endColumnPosition;
  private int endLinePosition;
  private final NodeType nodeType;
  private int startColumnPosition;
  private int startLinePosition;

  public Node(NodeType nodeType) {
    this.nodeType = nodeType;
  }

  public String getCode() {
    return code;
  }

  public int getEndColumnPosition() {
    return endColumnPosition;
  }

  public int getEndLinePosition() {
    return endLinePosition;
  }

  public int getLinesOfCode() {
    return StringUtils.countMatches(code, "\n");
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
    return code;
  }

}