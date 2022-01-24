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
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "TableHeaderHasIdOrScopeCheck")
public class TableHeaderHasIdOrScopeCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Add either an 'id' or a 'scope' attribute to this <th> tag.";

  private final Deque<TableElement> tables = new ArrayDeque<>();

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
      for (TagNode element : row) {
        if (isThTag(element)) {
          currentTable.headers.add(element);
        }
      }
      currentTable.firstCol.add(row.get(0));

      if (currentTable.firstRow.isEmpty()) {
        currentTable.firstRow = row;
      }

    } else {
      // Sometimes rows are defined in separate files, and at this point we don't see them as part of a current table.
      // In this case we treat them as belonging to "not simple tables".
      raiseIssueOnTableHeadersWithoutScopeOrId(row);
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isTableTag(node) && !tables.isEmpty()) {
      TableElement currentTable = tables.pop();
      if (!currentTable.isSimpleTable()) {
        raiseIssueOnTableHeadersWithoutScopeOrId(currentTable.headers);
      }
    }
  }

  @Override
  public void endDocument() {
    tables.clear();
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

  private void raiseIssueOnTableHeadersWithoutScopeOrId(Collection<TagNode> row) {
    row.stream()
      .filter(TableHeaderHasIdOrScopeCheck::isHeaderTableWithoutScopeOrId)
      .forEach(th -> createViolation(th, MESSAGE));
  }

  private static boolean isHeaderTableWithoutScopeOrId(TagNode child) {
    return isThTag(child) && !child.hasProperty("id") && !child.hasProperty("scope");
  }

  private static class TableElement {
    List<TagNode> headers = new ArrayList<>();
    List<TagNode> firstRow = new ArrayList<>();
    List<TagNode> firstCol = new ArrayList<>();

    /**
     * Simple tables are considered as such when the headers are either all in the first row, or all in the first column. The two conditions must not apply together.
     **/
    boolean isSimpleTable() {
      return headers.equals(firstRow) || headers.equals(firstCol);
    }
  }
}
