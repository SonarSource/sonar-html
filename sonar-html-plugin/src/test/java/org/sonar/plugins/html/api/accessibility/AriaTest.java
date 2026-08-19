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
package org.sonar.plugins.html.api.accessibility;

import org.junit.jupiter.api.Test;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

import static org.assertj.core.api.Assertions.assertThat;

class AriaTest {

  @Test
  void returnsComboboxForSingleSelect() {
    assertThat(Aria.getImplicitRole(tag("select"))).isEqualTo(AriaRole.COMBOBOX);
    assertThat(Aria.getImplicitRole(tag("select", "size", "1"))).isEqualTo(AriaRole.COMBOBOX);
  }

  @Test
  void returnsListboxForMultiSelect() {
    assertThat(Aria.getImplicitRole(tag("select", "multiple", ""))).isEqualTo(AriaRole.LISTBOX);
    assertThat(Aria.getImplicitRole(tag("select", "size", "2"))).isEqualTo(AriaRole.LISTBOX);
  }

  private static TagNode tag(String name, String... attributeNameValuePairs) {
    TagNode node = new TagNode();
    node.setNodeName(name);
    for (int i = 0; i < attributeNameValuePairs.length; i += 2) {
      node.getAttributes().add(new Attribute(attributeNameValuePairs[i], attributeNameValuePairs[i + 1]));
    }
    return node;
  }
}
