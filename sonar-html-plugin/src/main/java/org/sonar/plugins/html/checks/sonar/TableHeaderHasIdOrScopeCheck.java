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
import java.util.Collections;
import java.util.Deque;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
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
    } else if (!tables.isEmpty()) {
      TableElement currentTable = tables.peek();
      if (isTheadTag(node)) {
        currentTable.enterSection(TableElement.Section.HEAD);
      } else if (isTbodyTag(node)) {
        currentTable.enterSection(TableElement.Section.BODY);
      } else if (isTfootTag(node)) {
        currentTable.enterSection(TableElement.Section.FOOT);
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
      TableElement current = tables.pop();
      raiseIssueOnTableHeadersWithoutScopeOrId(current.headersOutsideFirstRowAndColumn());
    }
  }

  @Override
  public void endDocument() {
    tables.clear();
  }

  private static List<TagNode> filterCells(TagNode trNode) {
    List<TagNode> cells = new ArrayList<>();
    for (TagNode child : trNode.getChildren()) {
      if (isThTag(child) || isTdTag(child)) {
        cells.add(child);
      }
    }
    return cells;
  }

  private static boolean isThTag(TagNode node) {
    return "th".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isTdTag(TagNode node) {
    return "td".equalsIgnoreCase(node.getNodeName());
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
    return isThTag(child) && !child.hasProperty("id") && !child.hasProperty("scope");
  }

  /**
   * Accumulates rows grouped by section so the grid can be replayed in
   * logical order (thead, then tbody/anonymous, then tfoot) regardless of
   * the source order they appear in.
   */
  private static class TableElement {
    enum Section { HEAD, BODY, FOOT }

    private final EnumMap<Section, List<List<TagNode>>> rowsBySection = new EnumMap<>(Section.class);
    private final Deque<Section> sectionStack = new ArrayDeque<>();
    private Section currentSection = Section.BODY;

    TableElement() {
      for (Section section : Section.values()) {
        rowsBySection.put(section, new ArrayList<>());
      }
    }

    void enterSection(Section section) {
      sectionStack.push(currentSection);
      currentSection = section;
    }

    void exitSection() {
      currentSection = sectionStack.isEmpty() ? Section.BODY : sectionStack.pop();
    }

    void addRow(List<TagNode> cells) {
      rowsBySection.get(currentSection).add(cells);
    }

    /**
     * Returns headers that need {@code id} or {@code scope}. A header is exempt
     * when it occupies a position in the first logical row or the first column
     * of the rendered grid (rowspan/colspan-aware). When all headers are
     * exempt the table is "simple" and no issues are raised.
     */
    Set<TagNode> headersOutsideFirstRowAndColumn() {
      List<List<TagNode>> orderedRows = new ArrayList<>();
      orderedRows.addAll(rowsBySection.get(Section.HEAD));
      orderedRows.addAll(rowsBySection.get(Section.BODY));
      orderedRows.addAll(rowsBySection.get(Section.FOOT));

      List<List<TagNode>> grid = buildGrid(orderedRows);
      Set<TagNode> headers = collectHeaders(orderedRows);
      Set<TagNode> firstRow = cellsAtGridRow(grid, 0);
      Set<TagNode> firstColumn = cellsAtGridColumn(grid, 0);

      Set<TagNode> nonExempt = new LinkedHashSet<>();
      for (TagNode header : headers) {
        if (!firstRow.contains(header) && !firstColumn.contains(header)) {
          nonExempt.add(header);
        }
      }
      // Simple table: every header sits in the first row or first column.
      if (nonExempt.isEmpty()) {
        return Collections.emptySet();
      }
      return nonExempt;
    }
  }

  private static Set<TagNode> collectHeaders(List<List<TagNode>> sourceRows) {
    Set<TagNode> headers = new LinkedHashSet<>();
    for (List<TagNode> row : sourceRows) {
      for (TagNode cell : row) {
        if (isThTag(cell)) {
          headers.add(cell);
        }
      }
    }
    return headers;
  }

  /**
   * Expands the source rows into a 2D grid that accounts for {@code rowspan}
   * and {@code colspan}. Spanned cells appear at every grid position they
   * occupy, so callers can ask "what cell is at column 0 of row N" and get
   * the answer the browser would render.
   */
  private static List<List<TagNode>> buildGrid(List<List<TagNode>> sourceRows) {
    List<List<TagNode>> grid = new ArrayList<>();
    for (int r = 0; r < sourceRows.size(); r++) {
      while (grid.size() <= r) {
        grid.add(new ArrayList<>());
      }
      int col = 0;
      for (TagNode cell : sourceRows.get(r)) {
        List<TagNode> gridRow = grid.get(r);
        while (col < gridRow.size() && gridRow.get(col) != null) {
          col++;
        }
        int rowspan = parseSpan(cell.getPropertyValue("ROWSPAN"));
        int colspan = parseSpan(cell.getPropertyValue("COLSPAN"));
        placeCell(grid, cell, r, col, rowspan, colspan);
        col += colspan;
      }
    }
    return grid;
  }

  private static void placeCell(List<List<TagNode>> grid, TagNode cell, int rowStart, int colStart, int rowspan, int colspan) {
    for (int dr = 0; dr < rowspan; dr++) {
      int targetRow = rowStart + dr;
      while (grid.size() <= targetRow) {
        grid.add(new ArrayList<>());
      }
      List<TagNode> gridRow = grid.get(targetRow);
      for (int dc = 0; dc < colspan; dc++) {
        int targetCol = colStart + dc;
        while (gridRow.size() <= targetCol) {
          gridRow.add(null);
        }
        gridRow.set(targetCol, cell);
      }
    }
  }

  private static int parseSpan(@Nullable String value) {
    if (value == null) {
      return 1;
    }
    try {
      int n = Integer.parseInt(value.trim());
      if (n < 1) {
        return 1;
      }
      return Math.min(n, 65534);
    } catch (NumberFormatException e) {
      return 1;
    }
  }

  private static Set<TagNode> cellsAtGridRow(List<List<TagNode>> grid, int row) {
    Set<TagNode> result = new LinkedHashSet<>();
    if (row < grid.size()) {
      for (TagNode cell : grid.get(row)) {
        if (cell != null) {
          result.add(cell);
        }
      }
    }
    return result;
  }

  private static Set<TagNode> cellsAtGridColumn(List<List<TagNode>> grid, int col) {
    Set<TagNode> result = new LinkedHashSet<>();
    for (List<TagNode> row : grid) {
      if (col < row.size() && row.get(col) != null) {
        result.add(row.get(col));
      }
    }
    return result;
  }
}
