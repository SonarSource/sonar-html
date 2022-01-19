/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "TableHeaderHasIdOrScopeCheck")
public class TableHeaderHasIdOrScopeCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Add either an 'id' or a 'scope' attribute to this <th> tag.";

  private Deque<TableElement> tables = new ArrayDeque<>();

  @Override
  public void startElement(TagNode node) {
    if (isTableTag(node)) {
      tables.push(new TableElement());
    }
    if (isTrTag(node)) {
      if (!tables.isEmpty()) {
        // This should be the standard case: we find a <tr> tag, so we expect that we are visiting a table, which should be stacked already.
        tables.peek().rows.add(node);
      } else {
        // Sometimes rows are defined in separate files. In this case we treat them as part of "not simple tables".
        for (TagNode child : node.getChildren()) {
          if (isHeaderTableWithoutScopeOrId(child)) {
            createViolation(child, MESSAGE);
          }
        }
      }
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (!tables.isEmpty() && isTableTag(node)) {
      TableElement currentTable = tables.peek();
      List<TagNode> rows = currentTable.rows;

      currentTable.isSimple = isSimpleTable(rows);

      for (TagNode rowElement : rows) {
        for (TagNode colElement : rowElement.getChildren()) {
          if (!currentTable.isSimple && isHeaderTableWithoutScopeOrId(colElement)) {
            createViolation(colElement, MESSAGE);
          }
        }
      }
      tables.pop();
    }
  }

  /**
   * We consider as simple tables, tables which have all headers only in the first row or in the first column.
   * If both first row and first column are all (or partially) composed by headers are not considered simple tables.
   **/
  private static boolean isSimpleTable(List<TagNode> tableRows) {
    boolean firstRowAllHeaders = isFirstRowAllHeaders(tableRows);
    boolean firstColumnAllHeaders = isFirstColumnAllHeaders(tableRows);
    boolean otherCellsDifferentFromFirstRowContainHeaders = otherCellsDifferentFromFirstRowContainHeaders(tableRows);
    boolean otherCellsDifferentFromFirstColumnContainHeaders = otherCellsDifferentFromFirstColumnContainHeaders(tableRows);

    return (firstRowAllHeaders && !otherCellsDifferentFromFirstRowContainHeaders) || (firstColumnAllHeaders && !otherCellsDifferentFromFirstColumnContainHeaders);
  }

  private static boolean isFirstRowAllHeaders(List<TagNode> tableRows) {
    if (tableRows.isEmpty()) {
      return false;
    }
    List<TagNode> firstRow = tableRows.get(0).getChildren();
    return firstRow.stream().allMatch(node -> "th".equalsIgnoreCase(node.getNodeName()));
  }

  private static boolean isFirstColumnAllHeaders(List<TagNode> tableRows) {
    return tableRows.stream().allMatch(tr ->
    {
      List<TagNode> row = tr.getChildren();
      return !row.isEmpty() && "th".equalsIgnoreCase(row.get(0).getNodeName());
    });
  }

  private static boolean otherCellsDifferentFromFirstRowContainHeaders(List<TagNode> tableRows) {
    return tableRows.stream()
      .anyMatch(tr -> tr != tableRows.get(0) && tr.getChildren().stream()
        .anyMatch(trChild -> "th".equalsIgnoreCase(trChild.getNodeName())));
  }

  private static boolean otherCellsDifferentFromFirstColumnContainHeaders(List<TagNode> tableRows) {
    return tableRows.stream().anyMatch(tr -> tr.getChildren().stream()
      .anyMatch(trChild -> !trChild.equals(tr.getChildren().get(0)) && "th".equalsIgnoreCase(trChild.getNodeName())));
  }

  private static boolean isThTag(TagNode node) {
    return "th".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isTrTag(TagNode node) {
    return "tr".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isTableTag(TagNode node) {
    return "table".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isHeaderTableWithoutScopeOrId(TagNode child) {
    return isThTag(child) && !child.hasProperty("ID") && !child.hasProperty("SCOPE");
  }

  private static class TableElement {
    List<TagNode> rows = new ArrayList<>();
    boolean isSimple = false;
  }
}
