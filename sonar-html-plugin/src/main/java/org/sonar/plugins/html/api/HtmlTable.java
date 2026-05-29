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
package org.sonar.plugins.html.api;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.sonar.plugins.html.node.TagNode;

/**
 * Accumulates the rows and cells of a single {@code <table>} as the visitor
 * walks the document, then exposes a rendered-grid view that respects
 * {@code rowspan}, {@code colspan} and section ordering
 * ({@code <thead>} → {@code <tbody>} → {@code <tfoot>}). Two tables that
 * differ only in source ordering of these sections produce the same grid.
 *
 * <p>Typical usage from a {@code DefaultNodeVisitor}:
 * <pre>{@code
 *   onStartElement <table>     → push new HtmlTable
 *   onStartElement <thead/tbody/tfoot> → enterSection(...)
 *   onEndElement   <thead/tbody/tfoot> → exitSection()
 *   onStartElement <tr>        → addRow(cells)
 *   onEndElement   </table>    → consume firstRow(), firstColumn(), allHeaders()
 * }</pre>
 */
public class HtmlTable {

  public enum Section { HEAD, BODY, FOOT }

  private final EnumMap<Section, List<List<TagNode>>> rowsBySection = new EnumMap<>(Section.class);
  private final Deque<Section> sectionStack = new ArrayDeque<>();
  private Section currentSection = Section.BODY;

  public HtmlTable() {
    for (Section section : Section.values()) {
      rowsBySection.put(section, new ArrayList<>());
    }
  }

  public void enterSection(Section section) {
    sectionStack.push(currentSection);
    currentSection = section;
  }

  public void exitSection() {
    currentSection = sectionStack.isEmpty() ? Section.BODY : sectionStack.pop();
  }

  /** Records a row of cells under the current section. */
  public void addRow(List<TagNode> cells) {
    rowsBySection.get(currentSection).add(cells);
  }

  /**
   * Returns rows in logical (rendered) order: HEAD, then BODY/anonymous, then
   * FOOT. The list inside each row preserves the original cell order.
   */
  public List<List<TagNode>> orderedRows() {
    List<List<TagNode>> ordered = new ArrayList<>();
    ordered.addAll(rowsBySection.get(Section.HEAD));
    ordered.addAll(rowsBySection.get(Section.BODY));
    ordered.addAll(rowsBySection.get(Section.FOOT));
    return ordered;
  }

  /** Unique cells occupying row 0 of the rendered grid. */
  public Set<TagNode> firstRow() {
    return cellsAtGridRow(buildGrid(orderedRows()), 0);
  }

  /** Unique cells occupying column 0 of the rendered grid. */
  public Set<TagNode> firstColumn() {
    return cellsAtGridColumn(buildGrid(orderedRows()), 0);
  }

  /** All {@code <th>} cells across every section, in logical order. */
  public Set<TagNode> allHeaders() {
    Set<TagNode> headers = new LinkedHashSet<>();
    for (List<TagNode> row : orderedRows()) {
      for (TagNode cell : row) {
        if (isTh(cell)) {
          headers.add(cell);
        }
      }
    }
    return headers;
  }

  /**
   * Maximum value accepted for an individual {@code rowspan} or {@code colspan}
   * attribute. The HTML spec caps {@code colspan} at 1000; this also caps
   * {@code rowspan} at 1000 to match (the spec allows up to 65534 there, but
   * tables with thousands of rows in a single span are pathological).
   */
  static final int MAX_SPAN = 1000;

  /**
   * Maximum number of grid positions a single table may occupy. Untrusted HTML
   * can otherwise allocate gigabytes with one {@code <th rowspan colspan>}, so
   * once this budget is exceeded the rest of the table is skipped — any cells
   * not yet placed are absent from the grid and therefore neither in the
   * first row nor first column, which means callers raise issues on them (the
   * conservative fallback already used for orphan rows).
   */
  static final int MAX_GRID_CELLS = 1_000_000;

  /**
   * Expands the source rows into a 2D grid that accounts for {@code rowspan}
   * and {@code colspan}. Spanned cells appear at every grid position they
   * occupy, so callers can ask "what cell is at column 0 of row N" and get
   * the answer the browser would render.
   */
  static List<List<TagNode>> buildGrid(List<List<TagNode>> sourceRows) {
    List<List<TagNode>> grid = new ArrayList<>();
    long placedCells = 0;
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
        long thisCellArea = (long) rowspan * colspan;
        if (placedCells + thisCellArea > MAX_GRID_CELLS) {
          return grid;
        }
        placeCell(grid, cell, r, col, rowspan, colspan);
        placedCells += thisCellArea;
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
      return Math.min(n, MAX_SPAN);
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

  /** True iff the node is a {@code <th>} element (case-insensitive). */
  public static boolean isTh(TagNode node) {
    return "th".equalsIgnoreCase(node.getNodeName());
  }

  /** True iff the node is a {@code <td>} element (case-insensitive). */
  public static boolean isTd(TagNode node) {
    return "td".equalsIgnoreCase(node.getNodeName());
  }
}
