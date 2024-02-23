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

package org.sonar.plugins.html.api.accessibility;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Aria {

  public static final Map<String, AriaProperty> ARIA_PROPERTIES = new HashMap<>();

  static {
    ARIA_PROPERTIES.put("aria-activedescendant", new AriaProperty("aria-activedescendant", AriaPropertyType.ID));
    ARIA_PROPERTIES.put("aria-atomic", new AriaProperty("aria-atomic", AriaPropertyType.BOOLEAN));
    ARIA_PROPERTIES.put("aria-autocomplete", new AriaProperty("aria-autocomplete", AriaPropertyType.TOKEN, "inline", "list", "both", "none"));
    ARIA_PROPERTIES.put("aria-braillelabel", new AriaProperty("aria-braillelabel", AriaPropertyType.STRING));
    ARIA_PROPERTIES.put("aria-brailleroledescription", new AriaProperty("aria-brailleroledescription", AriaPropertyType.STRING));
    ARIA_PROPERTIES.put("aria-busy", new AriaProperty("aria-busy", AriaPropertyType.BOOLEAN));
    ARIA_PROPERTIES.put("aria-checked", new AriaProperty("aria-checked", AriaPropertyType.TRISTATE));
    ARIA_PROPERTIES.put("aria-colcount", new AriaProperty("aria-colcount", AriaPropertyType.INTEGER));
    ARIA_PROPERTIES.put("aria-colindex", new AriaProperty("aria-colindex", AriaPropertyType.INTEGER));
    ARIA_PROPERTIES.put("aria-colspan", new AriaProperty("aria-colspan", AriaPropertyType.INTEGER));
    ARIA_PROPERTIES.put("aria-controls", new AriaProperty("aria-controls", AriaPropertyType.IDLIST));
    ARIA_PROPERTIES.put("aria-current", new AriaProperty("aria-current", AriaPropertyType.TOKEN, "page", "step", "location", "date", "time", "true", "false"));
    ARIA_PROPERTIES.put("aria-describedby", new AriaProperty("aria-describedby", AriaPropertyType.IDLIST));
    ARIA_PROPERTIES.put("aria-details", new AriaProperty("aria-details", AriaPropertyType.ID));
    ARIA_PROPERTIES.put("aria-disabled", new AriaProperty("aria-disabled", AriaPropertyType.BOOLEAN));
    ARIA_PROPERTIES.put("aria-dropeffect", new AriaProperty("aria-dropeffect", AriaPropertyType.TOKENLIST, "copy", "move", "link", "execute", "none", "popup"));
    ARIA_PROPERTIES.put("aria-errormessage", new AriaProperty("aria-errormessage", AriaPropertyType.ID));
    ARIA_PROPERTIES.put("aria-expanded", new AriaProperty("aria-expanded", AriaPropertyType.BOOLEAN, true, "true", "false"));
    ARIA_PROPERTIES.put("aria-flowto", new AriaProperty("aria-flowto", AriaPropertyType.IDLIST));
    ARIA_PROPERTIES.put("aria-grabbed", new AriaProperty("aria-grabbed", AriaPropertyType.BOOLEAN, true, "true", "false", "undefined"));
    ARIA_PROPERTIES.put("aria-haspopup", new AriaProperty("aria-haspopup", AriaPropertyType.BOOLEAN, true, "true", "false", "menu", "listbox", "tree", "grid", "dialog"));
    ARIA_PROPERTIES.put("aria-hidden", new AriaProperty("aria-hidden", AriaPropertyType.BOOLEAN, true, "true", "false"));
    ARIA_PROPERTIES.put("aria-invalid", new AriaProperty("aria-invalid", AriaPropertyType.TOKEN, "true", "false", "grammar", "spelling"));
    ARIA_PROPERTIES.put("aria-keyshortcuts", new AriaProperty("aria-keyshortcuts", AriaPropertyType.STRING));
    ARIA_PROPERTIES.put("aria-label", new AriaProperty("aria-label", AriaPropertyType.STRING));
    ARIA_PROPERTIES.put("aria-labelledby", new AriaProperty("aria-labelledby", AriaPropertyType.IDLIST));
    ARIA_PROPERTIES.put("aria-level", new AriaProperty("aria-level", AriaPropertyType.INTEGER));
    ARIA_PROPERTIES.put("aria-live", new AriaProperty("aria-live", AriaPropertyType.TOKEN, "off", "assertive", "polite"));
    ARIA_PROPERTIES.put("aria-modal", new AriaProperty("aria-modal", AriaPropertyType.BOOLEAN));
    ARIA_PROPERTIES.put("aria-multiline", new AriaProperty("aria-multiline", AriaPropertyType.BOOLEAN));
    ARIA_PROPERTIES.put("aria-multiselectable", new AriaProperty("aria-multiselectable", AriaPropertyType.BOOLEAN));
    ARIA_PROPERTIES.put("aria-orientation", new AriaProperty("aria-orientation", AriaPropertyType.TOKEN, "horizontal", "vertical", "undefined"));
    ARIA_PROPERTIES.put("aria-owns", new AriaProperty("aria-owns", AriaPropertyType.IDLIST));
    ARIA_PROPERTIES.put("aria-placeholder", new AriaProperty("aria-placeholder", AriaPropertyType.STRING));
    ARIA_PROPERTIES.put("aria-posinset", new AriaProperty("aria-posinset", AriaPropertyType.INTEGER));
    ARIA_PROPERTIES.put("aria-pressed", new AriaProperty("aria-pressed", AriaPropertyType.TRISTATE));
    ARIA_PROPERTIES.put("aria-readonly", new AriaProperty("aria-readonly", AriaPropertyType.BOOLEAN));
    ARIA_PROPERTIES.put("aria-relevant", new AriaProperty("aria-relevant", AriaPropertyType.TOKENLIST, "additions", "removals", "text", "all"));
    ARIA_PROPERTIES.put("aria-required", new AriaProperty("aria-required", AriaPropertyType.BOOLEAN));
    ARIA_PROPERTIES.put("aria-roledescription", new AriaProperty("aria-roledescription", AriaPropertyType.STRING));
    ARIA_PROPERTIES.put("aria-rowcount", new AriaProperty("aria-rowcount", AriaPropertyType.INTEGER));
    ARIA_PROPERTIES.put("aria-rowindex", new AriaProperty("aria-rowindex", AriaPropertyType.INTEGER));
    ARIA_PROPERTIES.put("aria-rowspan", new AriaProperty("aria-rowspan", AriaPropertyType.INTEGER));
    ARIA_PROPERTIES.put("aria-selected", new AriaProperty("aria-selected", AriaPropertyType.BOOLEAN, true, "true", "false"));
    ARIA_PROPERTIES.put("aria-setsize", new AriaProperty("aria-setsize", AriaPropertyType.INTEGER));
    ARIA_PROPERTIES.put("aria-sort", new AriaProperty("aria-sort", AriaPropertyType.TOKEN, "ascending", "descending", "none", "other"));
    ARIA_PROPERTIES.put("aria-valuemax", new AriaProperty("aria-valuemax", AriaPropertyType.NUMBER));
    ARIA_PROPERTIES.put("aria-valuemin", new AriaProperty("aria-valuemin", AriaPropertyType.NUMBER));
    ARIA_PROPERTIES.put("aria-valuenow", new AriaProperty("aria-valuenow", AriaPropertyType.NUMBER));
    ARIA_PROPERTIES.put("aria-valuetext", new AriaProperty("aria-valuetext", AriaPropertyType.STRING));
  }

  public static class AriaProperty {
    private final String name;
    private final AriaPropertyType type;
    private final Optional<Boolean> allowUndefined;
    private final Set<String> values;

    public AriaProperty(String name, AriaPropertyType type, String... values) {
      this(name, type, false, values);
    }

    public AriaProperty(String name, AriaPropertyType type, boolean allowUndefined, String... values) {
      this.name = name;
      this.type = type;
      this.allowUndefined = Optional.of(allowUndefined);
      this.values = Set.of(values);
    }

    public String getName() {
      return name;
    }

    public AriaPropertyType getType() {
      return type;
    }

    public Optional<Boolean> getAllowUndefined() {
      return allowUndefined;
    }

    public Set<String> getValues() {
      return values;
    }
  }

  public enum AriaPropertyType {
    BOOLEAN("boolean"),
    STRING("string"),
    TOKEN("token"),
    TOKENLIST("tokenlist"),
    ID("id"),
    IDLIST("idlist"),
    INTEGER("integer"),
    NUMBER("number"),
    TRISTATE("tristate");

    private final String value;

    AriaPropertyType(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  private Aria() {
    // Utility class
  }
}
