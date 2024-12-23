/*
 * SonarQube HTML Plugin :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA
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
package org.sonar.plugins.html.node;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TagNodeTest {

  @Test
  public void property() {
    TagNode node = createNode();
    assertThat(node.getAttributes()).hasSize(4);

    assertThat(node.getAttribute("name1")).isEqualTo("value1");
    assertThat(node.getPropertyValue("name1")).isEqualTo("value1");
    assertThat(node.hasProperty("name1")).isTrue();

    assertThat(node.getAttribute("[name2]")).isEqualTo("value2");
    assertThat(node.getPropertyValue("[name2]")).isEqualTo("value2");
    assertThat(node.hasProperty("[name2]")).isTrue();
    assertThat(node.getAttribute("name2")).isNull();
    assertThat(node.getPropertyValue("name2")).isEqualTo("value2");
    assertThat(node.hasProperty("name2")).isTrue();

    assertThat(node.hasProperty("[name1]")).isFalse();
    assertThat(node.getAttribute("[name1]")).isNull();

    assertThat(node.getAttribute("[attr.name3]")).isEqualTo("value3");
    assertThat(node.getPropertyValue("[attr.name3]")).isEqualTo("value3");
    assertThat(node.hasProperty("[attr.name3]")).isTrue();

    assertThat(node.getAttribute("attr.name4")).isEqualTo("value4");
    assertThat(node.getPropertyValue("attr.name4")).isEqualTo("value4");
    assertThat(node.hasProperty("attr.name4")).isTrue();
    assertThat(node.hasProperty("name4")).isTrue();

    assertThat(node.getAttribute("name3")).isNull();
    assertThat(node.getPropertyValue("name3")).isEqualTo("value3");
    assertThat(node.hasProperty("name3")).isTrue();
  }

  @Test
  public void emptyTagNode() {
    TagNode tagNode = new TagNode();
    assertThat(tagNode.getNodeName()).isNotNull();
    assertThat(tagNode.getLocalName()).isNotNull();
    assertThat(tagNode.getCode()).isNotNull();
  }

  private TagNode createNode() {
    Attribute attribute1 = new Attribute("name1", "value1");
    Attribute attribute2 = new Attribute("[name2]", "value2");
    Attribute attribute3 = new Attribute("[attr.name3]", "value3");
    Attribute attribute4 = new Attribute("attr.name4", "value4");

    TagNode node = new TagNode();
    node.getAttributes().add(attribute1);
    node.getAttributes().add(attribute2);
    node.getAttributes().add(attribute3);
    node.getAttributes().add(attribute4);
    return node;
  }

}
