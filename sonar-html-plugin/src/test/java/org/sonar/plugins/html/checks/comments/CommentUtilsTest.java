/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.comments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.plugins.html.node.CommentNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.sonar.plugins.html.checks.comments.CommentUtils.lineNumber;

public class CommentUtilsTest {


  private CommentNode node;

  @BeforeEach
  public void before() {
    node = new CommentNode();
    node.setStartLinePosition(1);
    node.setCode("<!--A\nB\nC-->");
  }

  @Test
  public void positive_offset() {
    assertThat(lineNumber(node, 4)).isEqualTo(1);
    assertThat(lineNumber(node, 6)).isEqualTo(2);
    assertThat(lineNumber(node, 8)).isEqualTo(3);
    assertThat(lineNumber(node, node.getCode().length())).isEqualTo(3);
  }

  @Test
  public void negative_offset() {
    var e = assertThrows(IllegalArgumentException.class, () -> lineNumber(node, -1));
    assertEquals("Out of range offset: -1 for comment content (size: 12)", e.getMessage());
  }

  @Test
  public void overflow_offset() {
    var e = assertThrows(IllegalArgumentException.class, () -> lineNumber(node, 100));
    assertEquals("Out of range offset: 100 for comment content (size: 12)", e.getMessage());
  }

}
