/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2019 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.sonar;

import java.util.Deque;
import java.util.LinkedList;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5256")
public class TableWithoutHeaderCheck extends AbstractPageCheck {

  private static class TableEntry {

    int line;
    boolean ignored;
    boolean hasHeader;

    public TableEntry(int line, boolean ignored) {
      this.line = line;
      this.ignored = ignored;
      this.hasHeader = false;
    }
  }

  private Deque<TableEntry> tables = new LinkedList<>();

  @Override
  public void init() {
    tables.clear();
  }

  @Override
  public void startElement(TagNode node) {
    if (isTable(node)) {
      boolean ignored = isParentIgnored() || isLayout(node) || isHidden(node);
      tables.addFirst(new TableEntry(node.getStartLinePosition(), ignored));
    } else if (isTableHeader(node) && !tables.isEmpty()) {
      tables.peekFirst().hasHeader = true;
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isTable(node) && !tables.isEmpty()) {
      TableEntry table = tables.removeFirst();
      if (!table.hasHeader && !table.ignored) {
        createViolation(table.line, "Add \"<th>\" headers to this \"<table>\".");
      }
    }
  }

  private static boolean isTable(TagNode node) {
    return "TABLE".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isLayout(TagNode node) {
    String role = node.getAttribute("role");
    return role != null && ("PRESENTATION".equalsIgnoreCase(role) || "NONE".equalsIgnoreCase(role));
  }

  private static boolean isTableHeader(TagNode node) {
    return "TH".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isHidden(TagNode node) {
    String ariaHidden = node.getAttribute("aria-hidden");
    return ariaHidden != null && "TRUE".equalsIgnoreCase(ariaHidden);
  }

  private boolean isParentIgnored() {
    return !tables.isEmpty() && tables.peekFirst().ignored;
  }
}
