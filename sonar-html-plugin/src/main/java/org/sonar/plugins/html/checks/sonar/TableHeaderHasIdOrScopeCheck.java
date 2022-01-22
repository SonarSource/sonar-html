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
      if (!tables.isEmpty()) {
        TableElement currentTable = tables.peek();
        currentTable.numberOfRows++;
        List<TagNode> rowElements = node.getChildren();

        if (!rowElements.isEmpty()) {
          if (!currentTable.firstRowWasVisited) {
            currentTable.firstRowWasVisited = true;
            if (isThTag(rowElements.get(0))) {
              currentTable.firstCellIsTh = true;
              currentTable.visitedThs.add(rowElements.get(0));
              currentTable.numberOfCols++;
            }
            for (int i = 1; i < rowElements.size(); i++) {
              currentTable.numberOfCols++;
              TagNode child = rowElements.get(i);
              if (isThTag(child)) {
                currentTable.firstRowContainsTh = true;
                currentTable.visitedThs.add(rowElements.get(i));
              } else {
                currentTable.firstRowContainsNotTh = true;
              }
            }
          } else {
            TagNode el0 = rowElements.get(0);
            if (isThTag(el0)) {
              currentTable.firstColContainsTh = true;
              currentTable.visitedThs.add(el0);
            } else {
              currentTable.firstColContainsNotTh = true;
            }
            for (int i = 1; i < rowElements.size(); i++) {
              TagNode child = rowElements.get(i);
              if (isThTag(child)) {
                currentTable.containsThInNotFirstRowOrColumn = true;
                currentTable.visitedThs.add(child);
              }
            }
          }

        }

//
//        if (!rowElements.isEmpty()) {
//          if (currentTable.firstRow.isEmpty()) {
//            // if it is the first row store it in the table
//            currentTable.firstRow.addAll(rowElements);
//          } else {
//            // if it is another row, check the tags and if there are ths add them to the visited ones.
//            for (TagNode child : rowElements.subList(1, rowElements.size())) {
//              if (isThTag(child)) {
//                currentTable.visitedThs.add(child);
//              }
//            }
//          }
//          // in any case store the elements found in the first column
//          currentTable.firstCol.add(rowElements.get(0));
//        }
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
    if (isTableTag(node) && !tables.isEmpty()) {
      TableElement currentTable = tables.peek();
//
//      boolean containsThInNotFirstRowOrColumn = currentTable.containsThInNotFirstRowOrColumn;
//      boolean firstCellIsTh = currentTable.firstCellIsTh;
//      boolean firstRowContainsTh = currentTable.firstRowContainsTh;
//      boolean firstRowContainsNotTh = currentTable.firstRowContainsNotTh;
//      boolean firstColContainsTh = currentTable.firstColContainsTh;
//      boolean firstColContainsNotTh = currentTable.firstColContainsNotTh;
//
//
//      boolean isFirstRowAllTh = !firstRowContainsNotTh || currentTable.numberOfCols == 1;
//      boolean isFirstColAllTh = !firstColContainsNotTh  || currentTable.numberOfRows == 1;
//
//
//      if ( !(firstCellIsTh && !containsThInNotFirstRowOrColumn && (
//        (isFirstRowAllTh && !firstColContainsTh) || (isFirstColAllTh && !firstRowContainsTh))))

      if (!currentTable.isSimpleTable())
      {
        for (TagNode visitedTh : currentTable.visitedThs) {
          if (isHeaderTableWithoutScopeOrId(visitedTh)) {
            createViolation(visitedTh, MESSAGE);
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
//  private static boolean isSimpleTable(TableElement currentTable) {
//    return !currentTable.firstCol.isEmpty() && !currentTable.firstRow.isEmpty() && currentTable.visitedThs.isEmpty() &&
//      (
//        currentTable.firstRow.stream().allMatch(el -> isThTag(el)) && currentTable.firstCol.subList(1, currentTable.firstCol.size()).stream().noneMatch(el -> isThTag(el)) ||
//          currentTable.firstCol.stream().allMatch(el -> isThTag(el)) && currentTable.firstRow.subList(1, currentTable.firstRow.size()).stream().noneMatch(el -> isThTag(el))
//      );
//  }
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
    Set<TagNode> visitedThs = new HashSet<>();
    boolean firstRowContainsNotTh = false;
    boolean firstColContainsNotTh = false;
    boolean firstCellIsTh = false;
    boolean containsThInNotFirstRowOrColumn = false;

    int numberOfRows = 0;
    int numberOfCols = 0;

    boolean firstRowWasVisited = false;
    boolean firstRowContainsTh = false;
    boolean firstColContainsTh = false;

    boolean isSimpleTable() {

      boolean isFirstRowAllTh = !firstRowContainsNotTh;
      boolean isFirstColAllTh = !firstColContainsNotTh;

      return ( (firstCellIsTh && !containsThInNotFirstRowOrColumn && (
        (isFirstRowAllTh && !firstColContainsTh) || (isFirstColAllTh && !firstRowContainsTh))));

    }
  }
}
