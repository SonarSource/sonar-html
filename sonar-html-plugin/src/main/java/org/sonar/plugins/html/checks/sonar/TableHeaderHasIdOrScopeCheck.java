/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.sonar;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.HtmlTable;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "TableHeaderHasIdOrScopeCheck")
public class TableHeaderHasIdOrScopeCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Add either an 'id' or a 'scope' attribute to this <th> tag.";

  private final Deque<HtmlTable> tables = new ArrayDeque<>();

  @Override
  public void startElement(TagNode node) {
    if (isTableTag(node)) {
      tables.push(new HtmlTable());
    } else if (!tables.isEmpty()) {
      HtmlTable currentTable = tables.peek();
      if (isTheadTag(node)) {
        currentTable.enterSection(HtmlTable.Section.HEAD);
      } else if (isTbodyTag(node)) {
        currentTable.enterSection(HtmlTable.Section.BODY);
      } else if (isTfootTag(node)) {
        currentTable.enterSection(HtmlTable.Section.FOOT);
      } else if (isTrTag(node)) {
        currentTable.addRow(filterCells(node));
      }
    } else if (isTrTag(node)) {
      // Rows defined outside any <table> (e.g. in JSP fragments) cannot be classified,
      // so they are treated as belonging to "not simple tables".
      raiseIssueOnTableHeadersWithoutScopeOrId(node.getChildren());
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (!tables.isEmpty() && (isTheadTag(node) || isTbodyTag(node) || isTfootTag(node))) {
      tables.peek().exitSection();
    }
    if (isTableTag(node) && !tables.isEmpty()) {
      HtmlTable current = tables.pop();
      raiseIssueOnTableHeadersWithoutScopeOrId(headersNeedingIdOrScope(current));
    }
  }

  @Override
  public void endDocument() {
    tables.clear();
  }

  /**
   * Returns the {@code <th>} elements that are neither in the first row nor in
   * the first column of the rendered grid. When this set is empty, every header
   * is exempt and the table is "simple".
   */
  private static Set<TagNode> headersNeedingIdOrScope(HtmlTable table) {
    Set<TagNode> firstRow = table.firstRow();
    Set<TagNode> firstColumn = table.firstColumn();
    Set<TagNode> nonExempt = new LinkedHashSet<>();
    for (TagNode header : table.allHeaders()) {
      if (!firstRow.contains(header) && !firstColumn.contains(header)) {
        nonExempt.add(header);
      }
    }
    return nonExempt;
  }

  private static List<TagNode> filterCells(TagNode trNode) {
    List<TagNode> cells = new ArrayList<>();
    for (TagNode child : trNode.getChildren()) {
      if (HtmlTable.isTh(child) || HtmlTable.isTd(child)) {
        cells.add(child);
      }
    }
    return cells;
  }

  private static boolean isTrTag(TagNode node) {
    return "tr".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isTableTag(TagNode node) {
    return "table".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isTheadTag(TagNode node) {
    return "thead".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isTbodyTag(TagNode node) {
    return "tbody".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isTfootTag(TagNode node) {
    return "tfoot".equalsIgnoreCase(node.getNodeName());
  }

  private void raiseIssueOnTableHeadersWithoutScopeOrId(Collection<TagNode> cells) {
    cells.stream()
      .filter(TableHeaderHasIdOrScopeCheck::isHeaderTableWithoutScopeOrId)
      .forEach(th -> createViolation(th, MESSAGE));
  }

  private static boolean isHeaderTableWithoutScopeOrId(TagNode child) {
    return HtmlTable.isTh(child) && !child.hasProperty("id") && !child.hasProperty("scope");
  }
}
