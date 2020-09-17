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
package org.sonar.plugins.html.node;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class TagNodeTest {

  @Test
  public void property() {
    TagNode node = createNode();
    assertThat(node.getAttributes()).hasSize(3);

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
    assertThat(node.getAttribute("name3")).isNull();
    assertThat(node.getPropertyValue("name3")).isEqualTo("value3");
    assertThat(node.hasProperty("name3")).isTrue();
  }

  private TagNode createNode() {
    Attribute attribute1 = new Attribute("name1", "value1");
    Attribute attribute2 = new Attribute("[name2]", "value2");
    Attribute attribute3 = new Attribute("[attr.name3]", "value3");
    TagNode node = new TagNode();
    node.getAttributes().add(attribute1);
    node.getAttributes().add(attribute2);
    node.getAttributes().add(attribute3);
    return node;
  }

}
