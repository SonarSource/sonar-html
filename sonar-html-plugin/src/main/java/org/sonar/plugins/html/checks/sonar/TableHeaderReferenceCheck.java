/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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

import static java.lang.String.format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5260")
public class TableHeaderReferenceCheck extends AbstractPageCheck {

  private static final Table.Cell NIL = new Table.Cell(null);

  private static final Pattern DYNAMIC_HEADERS = Pattern.compile("[{}$()\\[\\]]");
  private static final String HEADERS = "HEADERS";

  private Deque<TableBuilder> stack = new LinkedList<>();

  @FunctionalInterface
  private interface TriFunction<A, B, C> {

    void apply(A a, B b, C c);
  }

  private static class Table {

    private final List<List<Cell>> rows;

    private static class Cell {
      
      private final TagNode node;

      Cell(TagNode node) {
        this.node = node;
      }

      TagNode node() {
        return node;
      }

      List<String> headers() {
        if (node.hasProperty(HEADERS) && !DYNAMIC_HEADERS.matcher(node.getPropertyValue(HEADERS)).find()) {
          return Arrays.stream(node.getPropertyValue(HEADERS).split("\\s+")).filter(header -> !header.isEmpty()).toList();
        } else {
          return Collections.emptyList();
        }
      }
    }

    private static class Header extends Cell {
      
      private String id;

      Header(TagNode node) {
        super(node);
        id = node.getPropertyValue("ID");
      }

      String id() {
        return id;
      }
    }

    Table(List<List<Cell>> rows) {
      this.rows = Collections.unmodifiableList(rows);
    }

    List<List<Cell>> rows() {
      return rows;
    }

    int numberOfCells() {
      int max = 0;
      for (int i = 0; i < rows.size(); ++i) {
        max = Integer.max(max, rows.get(i).size());
      }
      return max;
    }

    int numberOfRows() {
      return rows.size();
    }

    void forEachCell(TriFunction<Table.Cell, Integer, Integer> action) {
      for (int row = 0; row < rows.size(); ++row) {
        for (int column = 0; column < rows.get(row).size(); ++column) {
          Table.Cell cell = rows.get(row).get(column);
          if (cell != NIL) {
            action.apply(cell, row, column);
          }
        }
      }
    }

    List<Set<String>> findHorizontalHeaders() {
      List<Set<String>> headers = new ArrayList<>();
      for (int i = 0; i < numberOfCells(); ++i) {
        headers.add(new HashSet<>());
      }
      forEachCell((cell, row, column) -> {
        if (cell instanceof Table.Header header) {
          headers.get(column).add(header.id());
        }
      });
      return headers;
    }

    List<Set<String>> findVerticalHeaders() {
      List<Set<String>> headers = new ArrayList<>();
      for (int i = 0; i < numberOfRows(); ++i) {
        headers.add(new HashSet<>());
      }
      forEachCell((cell, row, column) -> {
        if (cell instanceof Table.Header header) {
          headers.get(row).add(header.id());
        }
      });
      return headers;
    }

    Map<TagNode, List<String>> findReferenceableHeadersPerCellNode() {
      List<Set<String>> horizontalHeaders = findHorizontalHeaders();
      List<Set<String>> verticalHeaders = findVerticalHeaders();
      Map<TagNode, List<String>> referenceable = new HashMap<>();
      forEachCell((cell, row, column) -> {
        if (!cell.headers().isEmpty()) {
          List<String> headers = new ArrayList<>();
          headers.addAll(horizontalHeaders.get(column));
          headers.addAll(verticalHeaders.get(row));
          referenceable.merge(cell.node(), headers, (acc, val) -> { acc.addAll(val); return acc; });
        }
      });
      return referenceable;
    }
  }

  private static class TableBuilder {

    private ArrayList<RowBuilder> rows = new ArrayList<>();
    private RowBuilder currentRow = null;

    private static class RowBuilder {

      private List<Table.Cell> cells = new ArrayList<>();

      int indexOfVacantCell() {
        for (int i = 0; i < cells.size(); ++i) {
          if (cells.get(i) == NIL) {
            return i;
          }
        }
        return -1;
      }

      List<Table.Cell> build() {
        return Collections.unmodifiableList(cells);
      }

      void set(int cellIndex, Table.Cell cell) {
        cells.set(cellIndex, cell);
      }

      int size() {
        return cells.size();
      }

      void add(Table.Cell cell) {
        cells.add(cell);
      }
    }

    void newRow() {
      int indexOfCurrentRow = rows.indexOf(currentRow);
      if (indexOfCurrentRow == rows.size() - 1) {
        currentRow = new RowBuilder();
        rows.add(currentRow);
      } else {
        currentRow = rows.get(rows.indexOf(currentRow) + 1);
      }
    }

    void newCell(Table.Cell cell) {
      if (rows.isEmpty()) {
        return;
      }
      int rowspan = getRowSpan(cell.node());
      int rowStart = rows.indexOf(currentRow);
      int rowEnd = rowStart + rowspan;
      int colspan = getColSpan(cell.node());
      int cellStart = rows.get(rowStart).indexOfVacantCell();
      if (cellStart == -1) {
        cellStart = rows.get(rowStart).size();
      }
      int cellEnd = cellStart + colspan;
      for (int row = rowStart; row < rowEnd; ++row) {
        if (row == rows.size()) {
          rows.add(new RowBuilder());
        }
        for (int col = cellStart; col < cellEnd; ++col) {
          if (col < rows.get(row).size()) {
            rows.get(row).set(col, cell);
          } else {
            for (int i = rows.get(row).size(); i < col; ++i) {
              rows.get(row).add(NIL);
            }
            rows.get(row).add(cell);
          }
        }
      }
    }

    Table build() {
      return new Table(rows.stream().map(RowBuilder::build).toList());
    }

    private static int getRowSpan(TagNode node) {
      String rowspan = node.getPropertyValue("ROWSPAN");
      try {
        return Integer.parseInt(rowspan);
      } catch (NumberFormatException ex) {
        return 1;
      }
    }
  
    private static int getColSpan(TagNode node) {
      String rowspan = node.getPropertyValue("COLSPAN");
      try {
        return Integer.parseInt(rowspan);
      } catch (NumberFormatException ex) {
        return 1;
      }
    }
  }

  @Override
  public void startDocument(List<Node> nodes) {
    stack.clear();
  }

  @Override
  public void startElement(TagNode node) {
    if (isTable(node)) {
      stack.push(new TableBuilder());
    } else if (!stack.isEmpty()) {
      if (isTableRow(node)) {
        stack.peek().newRow();
      } else if (isTableData(node)) {
        stack.peek().newCell(new Table.Cell(node));
      } else if (isTableHeader(node)) {
        stack.peek().newCell(new Table.Header(node));
      }
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isTable(node) && !stack.isEmpty()) {
      raiseViolationOnInvalidReference(stack.pop().build());
    }
  }

  private void raiseViolationOnInvalidReference(Table table) {
    Map<TagNode, List<String>> referenceableHeaders = table.findReferenceableHeadersPerCellNode();
    Map<TagNode, List<String>> raisedFor = new HashMap<>();
    table.forEachCell((cell, row, column) -> {
      TagNode node = cell.node();
      List<String> actual = cell.headers();
      List<String> expected = referenceableHeaders.getOrDefault(node, Collections.emptyList());
      for (String header : actual) {
        if (!expected.contains(header) && !raisedFor.getOrDefault(node, Collections.emptyList()).contains(header)) {
          if (isExistingHeader(table, header)) {
            createViolation(node,
              format("id \"%s\" in \"headers\" reference the header of another column/row.", header));
          } else {
            createViolation(node,
              format("id \"%s\" in \"headers\" does not reference any <th> header.", header));
          }
          raisedFor.merge(node, Arrays.asList(header), (acc, val) -> { acc.addAll(val); return acc; });
          break;
        }
      }
    });
  }

  private static boolean isExistingHeader(Table table, String headerName) {
    return table.rows().stream().flatMap(List::stream).filter(cell -> cell instanceof Table.Header)
        .map(cell -> (Table.Header) cell).anyMatch(header -> headerName.equalsIgnoreCase(header.id()));
  }

  private static boolean isTable(TagNode node) {
    return node.equalsElementName("TABLE");
  }

  private static boolean isTableRow(TagNode node) {
    return node.equalsElementName("TR");
  }

  private static boolean isTableData(TagNode node) {
    return node.equalsElementName("TD");
  }

  private static boolean isTableHeader(TagNode node) {
    return node.equalsElementName("TH");
  }
}
