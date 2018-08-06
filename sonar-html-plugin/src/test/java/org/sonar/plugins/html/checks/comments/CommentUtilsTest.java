/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.comments;

import org.junit.Before;
import org.junit.Test;
import org.sonar.plugins.html.node.CommentNode;

import static org.fest.assertions.Assertions.assertThat;
import static org.sonar.plugins.html.checks.comments.CommentUtils.lineNumber;

public class CommentUtilsTest {

  private CommentNode node;

  @Before
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
  }

  @Test(expected = StringIndexOutOfBoundsException.class)
  public void negative_offset() {
    lineNumber(node, -1);
  }

}
