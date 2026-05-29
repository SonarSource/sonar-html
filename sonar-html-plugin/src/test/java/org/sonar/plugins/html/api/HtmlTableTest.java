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

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlTableTest {

  @Test
  void isTh_and_isTd_are_case_insensitive() {
    assertThat(HtmlTable.isTh(tag("th"))).isTrue();
    assertThat(HtmlTable.isTh(tag("TH"))).isTrue();
    assertThat(HtmlTable.isTh(tag("Th"))).isTrue();
    assertThat(HtmlTable.isTh(tag("td"))).isFalse();
    assertThat(HtmlTable.isTh(tag("tr"))).isFalse();

    assertThat(HtmlTable.isTd(tag("td"))).isTrue();
    assertThat(HtmlTable.isTd(tag("TD"))).isTrue();
    assertThat(HtmlTable.isTd(tag("th"))).isFalse();
  }

  @Test
  void ordered_rows_emit_head_then_body_then_foot_regardless_of_insertion_order() {
    HtmlTable table = new HtmlTable();
    TagNode footCell = tag("td");
    TagNode headCell = tag("th");
    TagNode bodyCell = tag("td");

    table.enterSection(HtmlTable.Section.FOOT);
    table.addRow(List.of(footCell));
    table.exitSection();

    table.enterSection(HtmlTable.Section.HEAD);
    table.addRow(List.of(headCell));
    table.exitSection();

    table.enterSection(HtmlTable.Section.BODY);
    table.addRow(List.of(bodyCell));
    table.exitSection();

    assertThat(table.orderedRows())
      .containsExactly(List.of(headCell), List.of(bodyCell), List.of(footCell));
  }

  @Test
  void rows_added_without_explicit_section_default_to_body() {
    HtmlTable table = new HtmlTable();
    TagNode cell = tag("th");
    table.addRow(List.of(cell));

    assertThat(table.orderedRows()).containsExactly(List.of(cell));
  }

  @Test
  void exit_section_restores_previous_section() {
    HtmlTable table = new HtmlTable();
    TagNode bodyBefore = tag("td");
    TagNode headInner = tag("th");
    TagNode bodyAfter = tag("td");

    table.addRow(List.of(bodyBefore));        // default BODY
    table.enterSection(HtmlTable.Section.HEAD);
    table.addRow(List.of(headInner));
    table.exitSection();                       // → back to BODY
    table.addRow(List.of(bodyAfter));

    assertThat(table.orderedRows())
      .containsExactly(List.of(headInner), List.of(bodyBefore), List.of(bodyAfter));
  }

  @Test
  void exit_section_without_enter_falls_back_to_body() {
    HtmlTable table = new HtmlTable();
    TagNode cell = tag("th");

    table.exitSection();
    table.addRow(List.of(cell));

    assertThat(table.orderedRows()).containsExactly(List.of(cell));
  }

  @Test
  void first_row_returns_unique_cells_of_grid_row_zero() {
    HtmlTable table = new HtmlTable();
    TagNode a = tag("th");
    TagNode b = tag("th");
    TagNode c = tag("th");
    TagNode d = tag("td");
    table.addRow(List.of(a, b, c));
    table.addRow(List.of(d));

    assertThat(table.firstRow()).containsExactly(a, b, c);
  }

  @Test
  void first_column_returns_unique_cells_of_grid_column_zero() {
    HtmlTable table = new HtmlTable();
    TagNode r0c0 = tag("th");
    TagNode r0c1 = tag("td");
    TagNode r1c0 = tag("th");
    TagNode r2c0 = tag("th");
    table.addRow(List.of(r0c0, r0c1));
    table.addRow(List.of(r1c0));
    table.addRow(List.of(r2c0));

    assertThat(table.firstColumn()).containsExactly(r0c0, r1c0, r2c0);
  }

  @Test
  void rowspan_makes_a_cell_occupy_column_zero_of_subsequent_rows() {
    HtmlTable table = new HtmlTable();
    TagNode spanning = tag("th", "rowspan", "2");
    TagNode q1 = tag("th");
    TagNode q2 = tag("th");
    TagNode innerHeader = tag("th");
    TagNode innerData = tag("td");
    table.addRow(List.of(spanning, q1, q2));
    table.addRow(List.of(innerHeader, innerData));

    // spanning sits at (0,0) AND (1,0) — innerHeader is bumped to (1,1)
    assertThat(table.firstRow()).containsExactly(spanning, q1, q2);
    assertThat(table.firstColumn()).containsExactly(spanning);
  }

  @Test
  void colspan_makes_a_cell_occupy_multiple_first_row_positions() {
    HtmlTable table = new HtmlTable();
    TagNode wide = tag("th", "colspan", "3");
    TagNode r1c0 = tag("td");
    TagNode r1c1 = tag("td");
    TagNode r1c2 = tag("td");
    table.addRow(List.of(wide));
    table.addRow(List.of(r1c0, r1c1, r1c2));

    // wide appears once in firstRow (Set semantics) and is the only first-row cell
    assertThat(table.firstRow()).containsExactly(wide);
    assertThat(table.firstColumn()).containsExactly(wide, r1c0);
  }

  @Test
  void all_headers_collects_th_only_in_logical_order() {
    HtmlTable table = new HtmlTable();
    TagNode headTh = tag("th");
    TagNode bodyTd = tag("td");
    TagNode bodyTh = tag("th");
    TagNode footTh = tag("th");

    table.enterSection(HtmlTable.Section.FOOT);
    table.addRow(List.of(footTh));
    table.exitSection();
    table.enterSection(HtmlTable.Section.BODY);
    table.addRow(List.of(bodyTh, bodyTd));
    table.exitSection();
    table.enterSection(HtmlTable.Section.HEAD);
    table.addRow(List.of(headTh));
    table.exitSection();

    // logical order: head → body → foot
    assertThat(table.allHeaders()).containsExactly(headTh, bodyTh, footTh);
  }

  @Test
  void invalid_span_value_falls_back_to_one() {
    HtmlTable table = new HtmlTable();
    TagNode bogusRowspan = tag("th", "rowspan", "not-a-number");
    TagNode neighbour = tag("th");
    TagNode rowBelowFirst = tag("th");
    table.addRow(List.of(bogusRowspan, neighbour));
    table.addRow(List.of(rowBelowFirst));

    // bogusRowspan stays at (0,0) only; rowBelowFirst legitimately sits at (1,0)
    assertThat(table.firstColumn()).containsExactly(bogusRowspan, rowBelowFirst);
  }

  @Test
  void negative_span_is_clamped_to_one() {
    HtmlTable table = new HtmlTable();
    TagNode negativeSpan = tag("th", "rowspan", "-5");
    TagNode rowBelow = tag("th");
    table.addRow(List.of(negativeSpan));
    table.addRow(List.of(rowBelow));

    assertThat(table.firstColumn()).containsExactly(negativeSpan, rowBelow);
  }

  @Test
  void span_value_above_max_is_capped() {
    HtmlTable table = new HtmlTable();
    TagNode hugeRowspan = tag("th", "rowspan", String.valueOf(HtmlTable.MAX_SPAN + 5));
    table.addRow(List.of(hugeRowspan));
    // Add MAX_SPAN extra rows; hugeRowspan should occupy exactly MAX_SPAN rows,
    // not MAX_SPAN + 5, so the cell at row MAX_SPAN's column 0 is the new tag.
    TagNode firstRowOutsideSpan = tag("th");
    for (int i = 1; i < HtmlTable.MAX_SPAN; i++) {
      table.addRow(List.of());
    }
    table.addRow(List.of(firstRowOutsideSpan));

    assertThat(table.firstColumn()).containsExactly(hugeRowspan, firstRowOutsideSpan);
  }

  @Test
  void grid_construction_stops_once_total_cell_budget_is_exceeded() {
    HtmlTable table = new HtmlTable();
    // First cell fills nearly the entire budget by itself.
    TagNode big = tag("th", "rowspan", String.valueOf(HtmlTable.MAX_SPAN), "colspan", String.valueOf(HtmlTable.MAX_SPAN));
    // Second cell would push placedCells over MAX_GRID_CELLS and must be skipped.
    TagNode skipped = tag("th", "rowspan", "2", "colspan", "2");
    table.addRow(List.of(big));
    table.addRow(List.of(skipped));

    // big is placed; skipped never makes it into the grid, so it is not in
    // firstColumn (or anywhere). It will remain in allHeaders() so the rule
    // ends up flagging it — the safe conservative fallback for adversarial input.
    assertThat(table.firstColumn()).containsExactly(big);
    assertThat(table.allHeaders()).containsExactly(big, skipped);
  }

  @Test
  void section_state_is_isolated_between_instances() {
    HtmlTable a = new HtmlTable();
    HtmlTable b = new HtmlTable();
    TagNode aCell = tag("th");
    TagNode bCell = tag("th");

    a.enterSection(HtmlTable.Section.HEAD);
    b.addRow(List.of(bCell));    // b is still in default BODY
    a.addRow(List.of(aCell));
    a.exitSection();

    assertThat(a.orderedRows()).containsExactly(List.of(aCell));
    assertThat(b.orderedRows()).containsExactly(List.of(bCell));
  }

  private static TagNode tag(String name, String... attrNameValuePairs) {
    if (attrNameValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("attrNameValuePairs must come in name/value pairs");
    }
    TagNode node = new TagNode();
    node.setNodeName(name);
    for (int i = 0; i < attrNameValuePairs.length; i += 2) {
      node.getAttributes().add(new Attribute(attrNameValuePairs[i], attrNameValuePairs[i + 1]));
    }
    return node;
  }
}
