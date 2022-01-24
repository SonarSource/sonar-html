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
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
      visitTrNode(node);
    }
  }

  private void visitTrNode(TagNode node) {
    List<TagNode> row = node.getChildren();
    if (!tables.isEmpty() && !row.isEmpty()) {

      TableElement currentTable = tables.peek();

      if (currentTable.firstRow.isEmpty()) {
        currentTable.firstRow = row;
      } else {
        currentTable.headers.addAll(row.subList(1, row.size()).stream().filter(TableHeaderHasIdOrScopeCheck::isThTag).collect(Collectors.toList()));
      }
      currentTable.firstCol.add(row.get(0));

    } else {
      // Sometimes rows are defined in separate files. In this case we treat them as part of "not simple tables".
      raiseIssueOnTableHeadersWithoutScopeOrId(row);
    }
  }

  private void raiseIssueOnTableHeadersWithoutScopeOrId(Collection<TagNode> row) {
    row.stream().filter(TableHeaderHasIdOrScopeCheck::isHeaderTableWithoutScopeOrId).forEach(th -> createViolation(th, MESSAGE));
  }

  @Override
  public void endElement(TagNode node) {
    if (isTableTag(node) && !tables.isEmpty()) {
      TableElement currentTable = tables.peek();

      if (!currentTable.isSimpleTable()) {
        currentTable.headers.addAll(currentTable.firstRow);
        currentTable.headers.addAll(currentTable.firstCol);
        raiseIssueOnTableHeadersWithoutScopeOrId(currentTable.headers);
      }
      tables.pop();
    }
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
    Set<TagNode> headers = new HashSet<>();
    List<TagNode> firstRow = new ArrayList<>();
    List<TagNode> firstCol = new ArrayList<>();


    /**
     * We consider as simple tables, tables which have all headers only in the first row or in the first column.
     * If both first row and first column are all (or partially) composed by headers are not considered simple tables.
     **/
    boolean isSimpleTable() {

      return firstRow.isEmpty() || (headers.isEmpty() &&
        ((firstRow.stream().allMatch(TableHeaderHasIdOrScopeCheck::isThTag) && firstCol.subList(1, firstCol.size()).stream().noneMatch(TableHeaderHasIdOrScopeCheck::isThTag)) ||
          (firstCol.stream().allMatch(TableHeaderHasIdOrScopeCheck::isThTag) && firstRow.subList(1, firstRow.size()).stream().noneMatch(TableHeaderHasIdOrScopeCheck::isThTag))
        ));

    }
  }
}
