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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
      List<TagNode> row = node.getChildren();
      if (!tables.isEmpty() && !row.isEmpty()) {

        TableElement currentTable = tables.peek();

        if (currentTable.firstRow.isEmpty()) {
          currentTable.firstRow = row;
        } else {
          TagNode firstColumnElement = row.get(0);

          for (TagNode tagNode : row) {
            if (tagNode.equals(firstColumnElement)) {
              currentTable.firstCol.add(tagNode);
            } else {
              if (isThTag(tagNode)) {
                currentTable.containsThInNotFirstRowOrColumn = true;
                currentTable.headers.add(tagNode);
              }
            }
          }
        }

      } else {
        // Sometimes rows are defined in separate files. In this case we treat them as part of "not simple tables".
        for (TagNode child : row) {
          if (isHeaderTableWithoutScopeOrId(child)) {
            createViolation(child, MESSAGE);
          }
        }
      }
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isTableTag(node) && !tables.isEmpty()) {
      TableElement currentTable = tables.peek();

      if (!currentTable.isSimpleTable()) {
        currentTable.headers.stream()
          .filter(TableHeaderHasIdOrScopeCheck::isHeaderTableWithoutScopeOrId)
          .forEach(th -> createViolation(th, MESSAGE));
        currentTable.firstRow.stream()
          .filter(TableHeaderHasIdOrScopeCheck::isHeaderTableWithoutScopeOrId)
          .forEach(th -> createViolation(th, MESSAGE));
        currentTable.firstCol.stream()
          .filter(TableHeaderHasIdOrScopeCheck::isHeaderTableWithoutScopeOrId)
          .forEach(th -> createViolation(th, MESSAGE));
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
    boolean containsThInNotFirstRowOrColumn = false;


    /**
     * We consider as simple tables, tables which have all headers only in the first row or in the first column.
     * If both first row and first column are all (or partially) composed by headers are not considered simple tables.
     **/
    boolean isSimpleTable() {

      return !containsThInNotFirstRowOrColumn &&
        ( (firstRow.stream().allMatch(TableHeaderHasIdOrScopeCheck::isThTag) && firstCol.stream().noneMatch(TableHeaderHasIdOrScopeCheck::isThTag)) ||
          (isThTag(firstRow.get(0)) && firstCol.stream().allMatch(TableHeaderHasIdOrScopeCheck::isThTag) && firstRow.subList(1, firstRow.size()).stream().noneMatch(TableHeaderHasIdOrScopeCheck::isThTag))
          );

    }
  }
}
