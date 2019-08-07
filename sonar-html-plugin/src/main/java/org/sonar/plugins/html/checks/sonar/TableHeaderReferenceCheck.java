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
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5260")
public class TableHeaderReferenceCheck extends AbstractPageCheck {

  private static final Pattern DYNAMIC_HEADERS = Pattern.compile("[{}$()\\[\\]]");
  private static final String HEADERS = "HEADERS";
  private Deque<TableBuilder> stack = new LinkedList<>();

  private static class Table {

    private final List<List<Column>> rows;

    private static class Column {
      
      private final TagNode node;

      public Column(TagNode node) {
        this.node = node;
      }

      public TagNode node() {
        return node;
      }
    }

    private static class Header extends Column {
      
      private String id;

      public Header(TagNode node) {
        super(node);
        id = node.getPropertyValue("ID");
      }

      public String id() {
        return id;
      }
    }

    public Table(List<List<Column>> rows) {
      this.rows = Collections.unmodifiableList(rows);
    }

    public List<List<Column>> rows() {
      return rows;
    }

    public int numberOfColumns() {
      int max = 0;
      for (int i = 0; i < rows.size(); ++i) {
        max = Integer.max(max, rows.get(i).size());
      }
      return max;
    }

    public int numberOfRows() {
      return rows.size();
    }
  }

  private static class TableBuilder {

    private ArrayList<RowBuilder> rows = new ArrayList<>();
    private RowBuilder currentRow = null;

    private static class RowBuilder {

      private List<Table.Column> columns = new ArrayList<>();

      public int indexOfVacantColumn() {
        for (int i = 0; i < columns.size(); ++i) {
          if (columns.get(i) == null) {
            return i;
          }
        }
        return -1;
      }

      public List<Table.Column> build() {
        return Collections.unmodifiableList(columns);
      }

      public void set(int columnIndex, Table.Column column) {
        columns.set(columnIndex, column);
      }

      public int size() {
        return columns.size();
      }

      public void add(@Nullable Table.Column column) {
        columns.add(column);
      }
    }

    public void newRow() {
      rows.add(new RowBuilder());
      currentRow = rows.get(rows.indexOf(currentRow) + 1);
    }

    public void newColumn(Table.Column column) {
      if (!rows.isEmpty()) {
        TagNode node = column.node();
        if (hasColSpan(node) && hasRowSpan(node)) {
          newColumnWithRowColSpan(column);
        } else if (hasColSpan(node)) {
          newColumnWithColSpan(column);
        } else if (hasRowSpan(node)) {
          newColumnWithRowSpan(column);
        } else {
          newColumnWithoutSpan(column);
        }
      }
    }

    private void newColumnWithoutSpan(Table.Column column) {
      int columnIndex = currentRow.indexOfVacantColumn();
      if (columnIndex > -1) {
        currentRow.set(columnIndex, column);
      } else {
        currentRow.add(column);
      }
    }

    private void newColumnWithColSpan(Table.Column column) {
      int colspan = getColSpan(column.node());
      while (colspan > 0) {
        int columnIndex = currentRow.indexOfVacantColumn();
        if (columnIndex > -1) {
          currentRow.set(columnIndex, column);
        } else {
          currentRow.add(column);
        }
        colspan--;
      }
    }

    private void newColumnWithRowSpan(Table.Column column) {
      int rowspan = getRowSpan(column.node());
      int rowStart = rows.indexOf(currentRow);
      int rowEnd = rowStart + rowspan;
      int columnIndex = rows.get(rowStart).indexOfVacantColumn();
      if (columnIndex == -1) {
        columnIndex = rows.get(rowStart).size();
      }
      for (int row = rowStart; row < rowEnd; ++row) {
        if (row == rows.size()) {
          rows.add(new RowBuilder());
        }
        if (columnIndex < rows.get(row).size()) {
          rows.get(row).set(columnIndex, column);
        } else {
          for (int i = rows.get(row).size(); i < columnIndex; ++i) {
            rows.get(row).add(null);
          }
          rows.get(row).add(column);
        }
      }
    }

    private void newColumnWithRowColSpan(Table.Column column) {
      int rowspan = getRowSpan(column.node());
      int rowStart = rows.indexOf(currentRow);
      int rowEnd = rowStart + rowspan;
      int colspan = getColSpan(column.node());
      int columnStart = rows.get(rowStart).indexOfVacantColumn();
      if (columnStart == -1) {
        columnStart = rows.get(rowStart).size();
      }
      int columnEnd = columnStart + colspan;
      for (int row = rowStart; row < rowEnd; ++row) {
        if (row == rows.size()) {
          rows.add(new RowBuilder());
        }
        for (int col = columnStart; col < columnEnd; ++col) {
          if (col < rows.get(row).size()) {
            rows.get(row).set(col, column);
          } else {
            for (int i = rows.get(row).size(); i < col; ++i) {
              rows.get(row).add(null);
            }
            rows.get(row).add(column);
          }
        }
      }
    }

    public Table build() {
      return new Table(rows.stream().map(RowBuilder::build).collect(Collectors.toList()));
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
      } else if (isTableColumn(node)) {
        stack.peek().newColumn(new Table.Column(node));
      } else if (isTableHeader(node)) {
        stack.peek().newColumn(new Table.Header(node));
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
    Map<TagNode, List<String>> referenceableHeaders = findReferenceableHeadersPerColumnNode(table);
    for (int row = 0; row < table.rows().size(); ++row) {
      for (int col = 0; col < table.rows().get(row).size(); ++col) {
        Table.Column column = table.rows().get(row).get(col);
        if (column != null) {
          TagNode node = column.node();
          if (hasHeaders(node)) {
            List<String> actual = getHeaders(column.node());
            List<String> expected = referenceableHeaders.getOrDefault(node, new ArrayList<>());
            for (String header : actual) {
              if (!expected.contains(header)) {
                if (isExistingHeader(table, header)) {
                  createViolation(node.getStartLinePosition(),
                    format("id \"%s\" in \"headers\" reference the header of another column/row.", header));
                } else {
                  createViolation(node.getStartLinePosition(),
                    format("id \"%s\" in \"headers\" does not reference any <th> header.", header));
                }
                break;
              }
            }
          }
        }
      }
    }
  }

  private static List<Set<String>> findHorizontalHeaders(Table table) {
    List<Set<String>> headers = new ArrayList<>();
    for (int i = 0; i < table.numberOfColumns(); ++i) {
      headers.add(new HashSet<>());
    }
    for (int row = 0; row < table.rows().size(); ++row) {
      for (int col = 0; col < table.rows().get(row).size(); ++col) {
        Table.Column column = table.rows().get(row).get(col);
        if (column instanceof Table.Header) {
          Table.Header header = (Table.Header) column;
          headers.get(col).add(header.id());
        }
      }
    }
    return headers;
  }

  private static List<Set<String>> findVerticalHeaders(Table table) {
    List<Set<String>> headers = new ArrayList<>();
    for (int i = 0; i < table.numberOfRows(); ++i) {
      headers.add(new HashSet<>());
    }
    for (int row = 0; row < table.rows().size(); ++row) {
      for (int col = 0; col < table.rows().get(row).size(); ++col) {
        Table.Column column = table.rows().get(row).get(col);
        if (column instanceof Table.Header) {
          Table.Header header = (Table.Header) column;
          headers.get(row).add(header.id());
        }
      }
    }
    return headers;
  }

  private static Map<TagNode, List<String>> findReferenceableHeadersPerColumnNode(Table table) {
    List<Set<String>> horizontalHeaders = findHorizontalHeaders(table);
    List<Set<String>> verticalHeaders = findVerticalHeaders(table);
    Map<TagNode, List<String>> referenceable = new HashMap<>();
    for (int row = 0; row < table.rows().size(); ++row) {
      for (int col = 0; col < table.rows().get(row).size(); ++col) {
        Table.Column column = table.rows().get(row).get(col);
        if (column != null && hasHeaders(column.node())) {
          List<String> headers = new ArrayList<>();
          headers.addAll(horizontalHeaders.get(col));
          headers.addAll(verticalHeaders.get(row));
          referenceable.merge(column.node(), headers, (acc, val) -> { acc.addAll(val); return acc; });
        }
      }
    }
    return referenceable;
  }

  private static boolean isExistingHeader(Table table, String headerName) {
    return table.rows().stream().flatMap(List::stream).filter(column -> column instanceof Table.Header)
        .map(column -> (Table.Header) column).anyMatch(header -> headerName.equalsIgnoreCase(header.id()));
  }

  private static boolean isTable(TagNode node) {
    return "TABLE".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isTableRow(TagNode node) {
    return "TR".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isTableColumn(TagNode node) {
    return "TD".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isTableHeader(TagNode node) {
    return "TH".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasHeaders(TagNode node) {
    return node.hasProperty(HEADERS) && !isDynamicHeaders(node);
  }

  private static boolean isDynamicHeaders(TagNode node) {
    return DYNAMIC_HEADERS.matcher(node.getPropertyValue(HEADERS)).find();
  }

  private static List<String> getHeaders(TagNode node) {
    return Arrays.asList(node.getPropertyValue(HEADERS).split("\\s+")).stream()
        .filter(header -> !header.isEmpty()).collect(Collectors.toList());
  }

  private static boolean hasRowSpan(TagNode node) {
    return node.hasProperty("ROWSPAN");
  }

  private static boolean hasColSpan(TagNode node) {
    return node.hasProperty("COLSPAN");
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
