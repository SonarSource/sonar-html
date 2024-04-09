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
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.sonar.plugins.html.node.TagNode;

public class Aria {

  protected static final Map<String, AriaProperty> ARIA_PROPERTIES =
    new HashMap<>();
  protected static final Map<String, Role> ROLES = new HashMap<>();
  protected static final Map<String, Element> ELEMENTS = new HashMap<>();

  static {
    ARIA_PROPERTIES.put(
      "aria-activedescendant",
      new AriaProperty("aria-activedescendant", AriaPropertyType.ID)
    );
    ARIA_PROPERTIES.put(
      "aria-atomic",
      new AriaProperty("aria-atomic", AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      "aria-autocomplete",
      new AriaProperty(
        "aria-autocomplete",
        AriaPropertyType.TOKEN,
        "inline",
        "list",
        "both",
        "none"
      )
    );
    ARIA_PROPERTIES.put(
      "aria-braillelabel",
      new AriaProperty("aria-braillelabel", AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      "aria-brailleroledescription",
      new AriaProperty("aria-brailleroledescription", AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      "aria-busy",
      new AriaProperty("aria-busy", AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      "aria-checked",
      new AriaProperty("aria-checked", AriaPropertyType.TRISTATE)
    );
    ARIA_PROPERTIES.put(
      "aria-colcount",
      new AriaProperty("aria-colcount", AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      "aria-colindex",
      new AriaProperty("aria-colindex", AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      "aria-colspan",
      new AriaProperty("aria-colspan", AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      "aria-controls",
      new AriaProperty("aria-controls", AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      "aria-current",
      new AriaProperty(
        "aria-current",
        AriaPropertyType.TOKEN,
        "page",
        "step",
        "location",
        "date",
        "time",
        "true",
        "false"
      )
    );
    ARIA_PROPERTIES.put(
      "aria-describedby",
      new AriaProperty("aria-describedby", AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      "aria-description",
      new AriaProperty("aria-description", AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      "aria-details",
      new AriaProperty("aria-details", AriaPropertyType.ID)
    );
    ARIA_PROPERTIES.put(
      "aria-disabled",
      new AriaProperty("aria-disabled", AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      "aria-dropeffect",
      new AriaProperty(
        "aria-dropeffect",
        AriaPropertyType.TOKENLIST,
        "copy",
        "move",
        "link",
        "execute",
        "none",
        "popup"
      )
    );
    ARIA_PROPERTIES.put(
      "aria-errormessage",
      new AriaProperty("aria-errormessage", AriaPropertyType.ID)
    );
    ARIA_PROPERTIES.put(
      "aria-expanded",
      new AriaProperty(
        "aria-expanded",
        AriaPropertyType.BOOLEAN,
        true,
        "true",
        "false"
      )
    );
    ARIA_PROPERTIES.put(
      "aria-flowto",
      new AriaProperty("aria-flowto", AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      "aria-grabbed",
      new AriaProperty(
        "aria-grabbed",
        AriaPropertyType.BOOLEAN,
        true,
        "true",
        "false",
        "undefined"
      )
    );
    ARIA_PROPERTIES.put(
      "aria-haspopup",
      new AriaProperty(
        "aria-haspopup",
        AriaPropertyType.TOKEN,
        true,
        "true",
        "false",
        "menu",
        "listbox",
        "tree",
        "grid",
        "dialog"
      )
    );
    ARIA_PROPERTIES.put(
      "aria-hidden",
      new AriaProperty(
        "aria-hidden",
        AriaPropertyType.BOOLEAN,
        true,
        "true",
        "false"
      )
    );
    ARIA_PROPERTIES.put(
      "aria-invalid",
      new AriaProperty(
        "aria-invalid",
        AriaPropertyType.TOKEN,
        "true",
        "false",
        "grammar",
        "spelling"
      )
    );
    ARIA_PROPERTIES.put(
      "aria-keyshortcuts",
      new AriaProperty("aria-keyshortcuts", AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      "aria-label",
      new AriaProperty("aria-label", AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      "aria-labelledby",
      new AriaProperty("aria-labelledby", AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      "aria-level",
      new AriaProperty("aria-level", AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      "aria-live",
      new AriaProperty(
        "aria-live",
        AriaPropertyType.TOKEN,
        "off",
        "assertive",
        "polite"
      )
    );
    ARIA_PROPERTIES.put(
      "aria-modal",
      new AriaProperty("aria-modal", AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      "aria-multiline",
      new AriaProperty("aria-multiline", AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      "aria-multiselectable",
      new AriaProperty("aria-multiselectable", AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      "aria-orientation",
      new AriaProperty(
        "aria-orientation",
        AriaPropertyType.TOKEN,
        "horizontal",
        "vertical",
        "undefined"
      )
    );
    ARIA_PROPERTIES.put(
      "aria-owns",
      new AriaProperty("aria-owns", AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      "aria-placeholder",
      new AriaProperty("aria-placeholder", AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      "aria-posinset",
      new AriaProperty("aria-posinset", AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      "aria-pressed",
      new AriaProperty("aria-pressed", AriaPropertyType.TRISTATE)
    );
    ARIA_PROPERTIES.put(
      "aria-readonly",
      new AriaProperty("aria-readonly", AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      "aria-relevant",
      new AriaProperty(
        "aria-relevant",
        AriaPropertyType.TOKENLIST,
        "additions",
        "removals",
        "text",
        "all"
      )
    );
    ARIA_PROPERTIES.put(
      "aria-required",
      new AriaProperty("aria-required", AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      "aria-roledescription",
      new AriaProperty("aria-roledescription", AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      "aria-rowcount",
      new AriaProperty("aria-rowcount", AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      "aria-rowindex",
      new AriaProperty("aria-rowindex", AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      "aria-rowspan",
      new AriaProperty("aria-rowspan", AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      "aria-selected",
      new AriaProperty(
        "aria-selected",
        AriaPropertyType.BOOLEAN,
        true,
        "true",
        "false"
      )
    );
    ARIA_PROPERTIES.put(
      "aria-setsize",
      new AriaProperty("aria-setsize", AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      "aria-sort",
      new AriaProperty(
        "aria-sort",
        AriaPropertyType.TOKEN,
        "ascending",
        "descending",
        "none",
        "other"
      )
    );
    ARIA_PROPERTIES.put(
      "aria-valuemax",
      new AriaProperty("aria-valuemax", AriaPropertyType.NUMBER)
    );
    ARIA_PROPERTIES.put(
      "aria-valuemin",
      new AriaProperty("aria-valuemin", AriaPropertyType.NUMBER)
    );
    ARIA_PROPERTIES.put(
      "aria-valuenow",
      new AriaProperty("aria-valuenow", AriaPropertyType.NUMBER)
    );
    ARIA_PROPERTIES.put(
      "aria-valuetext",
      new AriaProperty("aria-valuetext", AriaPropertyType.STRING)
    );

    ROLES.put(
      "command",
      new Role("command")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "composite",
      new Role("composite")
        .setProperties(
          "aria-activedescendant",
          "aria-disabled",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "input",
      new Role("input")
        .setProperties(
          "aria-disabled",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "landmark",
      new Role("landmark")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "range",
      new Role("range")
        .setProperties(
          "aria-valuemax",
          "aria-valuemin",
          "aria-valuenow",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "roletype",
      new Role("roletype")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "section",
      new Role("section")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "sectionhead",
      new Role("sectionhead")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "select",
      new Role("select")
        .setProperties(
          "aria-orientation",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-activedescendant",
          "aria-disabled"
        )
    );
    ROLES.put(
      "structure",
      new Role("structure")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "widget",
      new Role("widget")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "window",
      new Role("window")
        .setProperties(
          "aria-modal",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "alert",
      new Role("alert")
        .setProperties(
          "aria-atomic",
          "aria-live",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "alertdialog",
      new Role("alertdialog")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-modal"
        )
    );
    ROLES.put(
      "application",
      new Role("application")
        .setProperties(
          "aria-activedescendant",
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "article",
      new Role("article")
        .setProperties(
          "aria-posinset",
          "aria-setsize",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "banner",
      new Role("banner")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "blockquote",
      new Role("blockquote")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "button",
      new Role("button")
        .setProperties(
          "aria-disabled",
          "aria-expanded",
          "aria-haspopup",
          "aria-pressed",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "caption",
      new Role("caption")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "cell",
      new Role("cell")
        .setProperties(
          "aria-colindex",
          "aria-colspan",
          "aria-rowindex",
          "aria-rowspan",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "checkbox",
      new Role("checkbox")
        .setProperties(
          "aria-checked",
          "aria-errormessage",
          "aria-expanded",
          "aria-invalid",
          "aria-readonly",
          "aria-required",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled"
        )
    );
    ROLES.put(
      "code",
      new Role("code")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "columnheader",
      new Role("columnheader")
        .setProperties(
          "aria-sort",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-colindex",
          "aria-colspan",
          "aria-rowindex",
          "aria-rowspan",
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-readonly",
          "aria-required",
          "aria-selected"
        )
    );
    ROLES.put(
      "combobox",
      new Role("combobox")
        .setProperties(
          "aria-activedescendant",
          "aria-autocomplete",
          "aria-errormessage",
          "aria-invalid",
          "aria-readonly",
          "aria-required",
          "aria-expanded",
          "aria-haspopup",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled"
        )
    );
    ROLES.put(
      "complementary",
      new Role("complementary")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "contentinfo",
      new Role("contentinfo")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "definition",
      new Role("definition")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "deletion",
      new Role("deletion")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "dialog",
      new Role("dialog")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-modal"
        )
    );
    ROLES.put(
      "directory",
      new Role("directory")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "document",
      new Role("document")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "emphasis",
      new Role("emphasis")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "feed",
      new Role("feed")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "figure",
      new Role("figure")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "form",
      new Role("form")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "generic",
      new Role("generic")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "grid",
      new Role("grid")
        .setProperties(
          "aria-multiselectable",
          "aria-readonly",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-activedescendant",
          "aria-disabled",
          "aria-colcount",
          "aria-rowcount"
        )
    );
    ROLES.put(
      "gridcell",
      new Role("gridcell")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-readonly",
          "aria-required",
          "aria-selected",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-colindex",
          "aria-colspan",
          "aria-rowindex",
          "aria-rowspan"
        )
    );
    ROLES.put(
      "group",
      new Role("group")
        .setProperties(
          "aria-activedescendant",
          "aria-disabled",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "heading",
      new Role("heading")
        .setProperties(
          "aria-level",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "img",
      new Role("img")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "insertion",
      new Role("insertion")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "link",
      new Role("link")
        .setProperties(
          "aria-disabled",
          "aria-expanded",
          "aria-haspopup",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "list",
      new Role("list")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "listbox",
      new Role("listbox")
        .setProperties(
          "aria-errormessage",
          "aria-expanded",
          "aria-invalid",
          "aria-multiselectable",
          "aria-readonly",
          "aria-required",
          "aria-orientation",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-activedescendant",
          "aria-disabled"
        )
    );
    ROLES.put(
      "listitem",
      new Role("listitem")
        .setProperties(
          "aria-level",
          "aria-posinset",
          "aria-setsize",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "log",
      new Role("log")
        .setProperties(
          "aria-live",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "main",
      new Role("main")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "mark",
      new Role("mark")
        .setProperties(
          "aria-braillelabel",
          "aria-brailleroledescription",
          "aria-description",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "marquee",
      new Role("marquee")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "math",
      new Role("math")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "menu",
      new Role("menu")
        .setProperties(
          "aria-orientation",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-activedescendant",
          "aria-disabled"
        )
    );
    ROLES.put(
      "menubar",
      new Role("menubar")
        .setProperties(
          "aria-orientation",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-activedescendant",
          "aria-disabled"
        )
    );
    ROLES.put(
      "menuitem",
      new Role("menuitem")
        .setProperties(
          "aria-disabled",
          "aria-expanded",
          "aria-haspopup",
          "aria-posinset",
          "aria-setsize",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "menuitemcheckbox",
      new Role("menuitemcheckbox")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled",
          "aria-checked",
          "aria-errormessage",
          "aria-expanded",
          "aria-invalid",
          "aria-readonly",
          "aria-required",
          "aria-haspopup",
          "aria-posinset",
          "aria-setsize"
        )
    );
    ROLES.put(
      "menuitemradio",
      new Role("menuitemradio")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled",
          "aria-checked",
          "aria-errormessage",
          "aria-expanded",
          "aria-invalid",
          "aria-readonly",
          "aria-required",
          "aria-haspopup",
          "aria-posinset",
          "aria-setsize"
        )
    );
    ROLES.put(
      "meter",
      new Role("meter")
        .setProperties(
          "aria-valuetext",
          "aria-valuemax",
          "aria-valuemin",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-valuenow"
        )
    );
    ROLES.put(
      "navigation",
      new Role("navigation")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "note",
      new Role("note")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "option",
      new Role("option")
        .setProperties(
          "aria-checked",
          "aria-posinset",
          "aria-setsize",
          "aria-selected",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled"
        )
    );
    ROLES.put(
      "paragraph",
      new Role("paragraph")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "presentation",
      new Role("presentation")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "progressbar",
      new Role("progressbar")
        .setProperties(
          "aria-valuetext",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-valuemax",
          "aria-valuemin",
          "aria-valuenow"
        )
    );
    ROLES.put(
      "radio",
      new Role("radio")
        .setProperties(
          "aria-checked",
          "aria-posinset",
          "aria-setsize",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled"
        )
    );
    ROLES.put(
      "radiogroup",
      new Role("radiogroup")
        .setProperties(
          "aria-errormessage",
          "aria-invalid",
          "aria-readonly",
          "aria-required",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-activedescendant",
          "aria-disabled",
          "aria-orientation"
        )
    );
    ROLES.put(
      "region",
      new Role("region")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "row",
      new Role("row")
        .setProperties(
          "aria-colindex",
          "aria-expanded",
          "aria-level",
          "aria-posinset",
          "aria-rowindex",
          "aria-selected",
          "aria-setsize",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-activedescendant",
          "aria-disabled"
        )
    );
    ROLES.put(
      "rowgroup",
      new Role("rowgroup")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "rowheader",
      new Role("rowheader")
        .setProperties(
          "aria-sort",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-colindex",
          "aria-colspan",
          "aria-rowindex",
          "aria-rowspan",
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-readonly",
          "aria-required",
          "aria-selected"
        )
    );
    ROLES.put(
      "scrollbar",
      new Role("scrollbar")
        .setProperties(
          "aria-disabled",
          "aria-valuetext",
          "aria-orientation",
          "aria-valuemax",
          "aria-valuemin",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-valuenow"
        )
    );
    ROLES.put(
      "search",
      new Role("search")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "searchbox",
      new Role("searchbox")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled",
          "aria-activedescendant",
          "aria-autocomplete",
          "aria-errormessage",
          "aria-haspopup",
          "aria-invalid",
          "aria-multiline",
          "aria-placeholder",
          "aria-readonly",
          "aria-required"
        )
    );
    ROLES.put(
      "separator",
      new Role("separator")
        .setProperties(
          "aria-disabled",
          "aria-orientation",
          "aria-valuemax",
          "aria-valuemin",
          "aria-valuenow",
          "aria-valuetext",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "slider",
      new Role("slider")
        .setProperties(
          "aria-errormessage",
          "aria-haspopup",
          "aria-invalid",
          "aria-readonly",
          "aria-valuetext",
          "aria-orientation",
          "aria-valuemax",
          "aria-valuemin",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled",
          "aria-valuenow"
        )
    );
    ROLES.put(
      "spinbutton",
      new Role("spinbutton")
        .setProperties(
          "aria-errormessage",
          "aria-invalid",
          "aria-readonly",
          "aria-required",
          "aria-valuetext",
          "aria-valuenow",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-activedescendant",
          "aria-disabled",
          "aria-valuemax",
          "aria-valuemin"
        )
    );
    ROLES.put(
      "status",
      new Role("status")
        .setProperties(
          "aria-atomic",
          "aria-live",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "strong",
      new Role("strong")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "subscript",
      new Role("subscript")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "superscript",
      new Role("superscript")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "switch",
      new Role("switch")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled",
          "aria-checked",
          "aria-errormessage",
          "aria-expanded",
          "aria-invalid",
          "aria-readonly",
          "aria-required"
        )
    );
    ROLES.put(
      "tab",
      new Role("tab")
        .setProperties(
          "aria-disabled",
          "aria-expanded",
          "aria-haspopup",
          "aria-posinset",
          "aria-setsize",
          "aria-selected",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "table",
      new Role("table")
        .setProperties(
          "aria-colcount",
          "aria-rowcount",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "tablist",
      new Role("tablist")
        .setProperties(
          "aria-level",
          "aria-multiselectable",
          "aria-orientation",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-activedescendant",
          "aria-disabled"
        )
    );
    ROLES.put(
      "tabpanel",
      new Role("tabpanel")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "term",
      new Role("term")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "textbox",
      new Role("textbox")
        .setProperties(
          "aria-activedescendant",
          "aria-autocomplete",
          "aria-errormessage",
          "aria-haspopup",
          "aria-invalid",
          "aria-multiline",
          "aria-placeholder",
          "aria-readonly",
          "aria-required",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled"
        )
    );
    ROLES.put(
      "time",
      new Role("time")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "timer",
      new Role("timer")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "toolbar",
      new Role("toolbar")
        .setProperties(
          "aria-orientation",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-activedescendant",
          "aria-disabled"
        )
    );
    ROLES.put(
      "tooltip",
      new Role("tooltip")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "tree",
      new Role("tree")
        .setProperties(
          "aria-errormessage",
          "aria-invalid",
          "aria-multiselectable",
          "aria-required",
          "aria-orientation",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-activedescendant",
          "aria-disabled"
        )
    );
    ROLES.put(
      "treegrid",
      new Role("treegrid")
        .setProperties(
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-activedescendant",
          "aria-disabled",
          "aria-multiselectable",
          "aria-readonly",
          "aria-colcount",
          "aria-rowcount",
          "aria-orientation",
          "aria-errormessage",
          "aria-invalid",
          "aria-required"
        )
    );
    ROLES.put(
      "treeitem",
      new Role("treeitem")
        .setProperties(
          "aria-expanded",
          "aria-haspopup",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-level",
          "aria-posinset",
          "aria-setsize",
          "aria-disabled",
          "aria-checked",
          "aria-selected"
        )
    );
    ROLES.put(
      "doc-abstract",
      new Role("doc-abstract")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-acknowledgments",
      new Role("doc-acknowledgments")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-afterword",
      new Role("doc-afterword")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-appendix",
      new Role("doc-appendix")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-backlink",
      new Role("doc-backlink")
        .setProperties(
          "aria-errormessage",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled",
          "aria-expanded",
          "aria-haspopup"
        )
    );
    ROLES.put(
      "doc-biblioentry",
      new Role("doc-biblioentry")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-level",
          "aria-posinset",
          "aria-setsize"
        )
    );
    ROLES.put(
      "doc-bibliography",
      new Role("doc-bibliography")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-biblioref",
      new Role("doc-biblioref")
        .setProperties(
          "aria-errormessage",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled",
          "aria-expanded",
          "aria-haspopup"
        )
    );
    ROLES.put(
      "doc-chapter",
      new Role("doc-chapter")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-colophon",
      new Role("doc-colophon")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-conclusion",
      new Role("doc-conclusion")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-cover",
      new Role("doc-cover")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-credit",
      new Role("doc-credit")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-credits",
      new Role("doc-credits")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-dedication",
      new Role("doc-dedication")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-endnote",
      new Role("doc-endnote")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-level",
          "aria-posinset",
          "aria-setsize"
        )
    );
    ROLES.put(
      "doc-endnotes",
      new Role("doc-endnotes")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-epigraph",
      new Role("doc-epigraph")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-epilogue",
      new Role("doc-epilogue")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-errata",
      new Role("doc-errata")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-example",
      new Role("doc-example")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-footnote",
      new Role("doc-footnote")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-foreword",
      new Role("doc-foreword")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-glossary",
      new Role("doc-glossary")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-glossref",
      new Role("doc-glossref")
        .setProperties(
          "aria-errormessage",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled",
          "aria-expanded",
          "aria-haspopup"
        )
    );
    ROLES.put(
      "doc-index",
      new Role("doc-index")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-introduction",
      new Role("doc-introduction")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-noteref",
      new Role("doc-noteref")
        .setProperties(
          "aria-errormessage",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled",
          "aria-expanded",
          "aria-haspopup"
        )
    );
    ROLES.put(
      "doc-notice",
      new Role("doc-notice")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-pagebreak",
      new Role("doc-pagebreak")
        .setProperties(
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-disabled",
          "aria-orientation",
          "aria-valuemax",
          "aria-valuemin",
          "aria-valuenow",
          "aria-valuetext"
        )
    );
    ROLES.put(
      "doc-pagelist",
      new Role("doc-pagelist")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-part",
      new Role("doc-part")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-preface",
      new Role("doc-preface")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-prologue",
      new Role("doc-prologue")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-qna",
      new Role("doc-qna")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-subtitle",
      new Role("doc-subtitle")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-tip",
      new Role("doc-tip")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "doc-toc",
      new Role("doc-toc")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "graphics-document",
      new Role("graphics-document")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ROLES.put(
      "graphics-object",
      new Role("graphics-object")
        .setProperties(
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription",
          "aria-activedescendant",
          "aria-disabled"
        )
    );
    ROLES.put(
      "graphics-symbol",
      new Role("graphics-symbol")
        .setProperties(
          "aria-disabled",
          "aria-errormessage",
          "aria-expanded",
          "aria-haspopup",
          "aria-invalid",
          "aria-atomic",
          "aria-busy",
          "aria-controls",
          "aria-current",
          "aria-describedby",
          "aria-details",
          "aria-dropeffect",
          "aria-flowto",
          "aria-grabbed",
          "aria-hidden",
          "aria-keyshortcuts",
          "aria-label",
          "aria-labelledby",
          "aria-live",
          "aria-owns",
          "aria-relevant",
          "aria-roledescription"
        )
    );
    ELEMENTS.put("article", new Element("article").setRoles("article"));
    ELEMENTS.put("header", new Element("header").setRoles("banner","generic"));
    ELEMENTS.put("blockquote", new Element("blockquote").setRoles("blockquote"));
    ELEMENTS.put("input", new Element("input").setRoles("button","checkbox","combobox","radio","searchbox","slider","spinbutton","textbox"));
    ELEMENTS.put("button", new Element("button").setRoles("button"));
    ELEMENTS.put("caption", new Element("caption").setRoles("caption"));
    ELEMENTS.put("td", new Element("td").setRoles("cell","gridcell"));
    ELEMENTS.put("code", new Element("code").setRoles("code"));
    ELEMENTS.put("th", new Element("th").setRoles("columnheader","rowheader"));
    ELEMENTS.put("select", new Element("select").setRoles("combobox","listbox"));
    ELEMENTS.put("aside", new Element("aside").setRoles("complementary","generic"));
    ELEMENTS.put("footer", new Element("footer").setRoles("contentinfo","generic"));
    ELEMENTS.put("dd", new Element("dd").setRoles("definition"));
    ELEMENTS.put("del", new Element("del").setRoles("deletion"));
    ELEMENTS.put("dialog", new Element("dialog").setRoles("dialog"));
    ELEMENTS.put("html", new Element("html").setRoles("document"));
    ELEMENTS.put("em", new Element("em").setRoles("emphasis"));
    ELEMENTS.put("figure", new Element("figure").setRoles("figure"));
    ELEMENTS.put("form", new Element("form").setRoles("form"));
    ELEMENTS.put("a", new Element("a").setRoles("generic","link"));
    ELEMENTS.put("area", new Element("area").setRoles("generic","link"));
    ELEMENTS.put("b", new Element("b").setRoles("generic"));
    ELEMENTS.put("bdo", new Element("bdo").setRoles("generic"));
    ELEMENTS.put("body", new Element("body").setRoles("generic"));
    ELEMENTS.put("data", new Element("data").setRoles("generic"));
    ELEMENTS.put("div", new Element("div").setRoles("generic"));
    ELEMENTS.put("hgroup", new Element("hgroup").setRoles("generic"));
    ELEMENTS.put("i", new Element("i").setRoles("generic"));
    ELEMENTS.put("pre", new Element("pre").setRoles("generic"));
    ELEMENTS.put("q", new Element("q").setRoles("generic"));
    ELEMENTS.put("samp", new Element("samp").setRoles("generic"));
    ELEMENTS.put("section", new Element("section").setRoles("generic","region"));
    ELEMENTS.put("small", new Element("small").setRoles("generic"));
    ELEMENTS.put("span", new Element("span").setRoles("generic"));
    ELEMENTS.put("u", new Element("u").setRoles("generic"));
    ELEMENTS.put("details", new Element("details").setRoles("group"));
    ELEMENTS.put("fieldset", new Element("fieldset").setRoles("group"));
    ELEMENTS.put("optgroup", new Element("optgroup").setRoles("group"));
    ELEMENTS.put("address", new Element("address").setRoles("group"));
    ELEMENTS.put("h1", new Element("h1").setRoles("heading"));
    ELEMENTS.put("h2", new Element("h2").setRoles("heading"));
    ELEMENTS.put("h3", new Element("h3").setRoles("heading"));
    ELEMENTS.put("h4", new Element("h4").setRoles("heading"));
    ELEMENTS.put("h5", new Element("h5").setRoles("heading"));
    ELEMENTS.put("h6", new Element("h6").setRoles("heading"));
    ELEMENTS.put("img", new Element("img").setRoles("img","presentation"));
    ELEMENTS.put("ins", new Element("ins").setRoles("insertion"));
    ELEMENTS.put("menu", new Element("menu").setRoles("list"));
    ELEMENTS.put("ol", new Element("ol").setRoles("list"));
    ELEMENTS.put("ul", new Element("ul").setRoles("list"));
    ELEMENTS.put("datalist", new Element("datalist").setRoles("listbox"));
    ELEMENTS.put("li", new Element("li").setRoles("listitem"));
    ELEMENTS.put("main", new Element("main").setRoles("main"));
    ELEMENTS.put("mark", new Element("mark").setRoles("mark"));
    ELEMENTS.put("math", new Element("math").setRoles("math"));
    ELEMENTS.put("meter", new Element("meter").setRoles("meter"));
    ELEMENTS.put("nav", new Element("nav").setRoles("navigation"));
    ELEMENTS.put("option", new Element("option").setRoles("option"));
    ELEMENTS.put("p", new Element("p").setRoles("paragraph"));
    ELEMENTS.put("progress", new Element("progress").setRoles("progressbar"));
    ELEMENTS.put("tr", new Element("tr").setRoles("row"));
    ELEMENTS.put("tbody", new Element("tbody").setRoles("rowgroup"));
    ELEMENTS.put("tfoot", new Element("tfoot").setRoles("rowgroup"));
    ELEMENTS.put("thead", new Element("thead").setRoles("rowgroup"));
    ELEMENTS.put("hr", new Element("hr").setRoles("separator"));
    ELEMENTS.put("output", new Element("output").setRoles("status"));
    ELEMENTS.put("strong", new Element("strong").setRoles("strong"));
    ELEMENTS.put("sub", new Element("sub").setRoles("subscript"));
    ELEMENTS.put("sup", new Element("sup").setRoles("superscript"));
    ELEMENTS.put("table", new Element("table").setRoles("table"));
    ELEMENTS.put("dfn", new Element("dfn").setRoles("term"));
    ELEMENTS.put("dt", new Element("dt").setRoles("term"));
    ELEMENTS.put("textarea", new Element("textarea").setRoles("textbox"));
    ELEMENTS.put("time", new Element("time").setRoles("time"));

  }

  public static AriaProperty getProperty(String name) {
    return ARIA_PROPERTIES.get(name);
  }

  public static Role getRole(String name) {
    return ROLES.get(name);
  }

  public static Element getElement(String name) {
    return ELEMENTS.get(name);
  }

  public static class AriaProperty {

    private final String name;
    private final AriaPropertyType type;
    private final Optional<Boolean> allowUndefined;
    private final Set<String> values;

    public AriaProperty(String name, AriaPropertyType type, String... values) {
      this(name, type, false, values);
    }

    public AriaProperty(
      String name,
      AriaPropertyType type,
      boolean allowUndefined,
      String... values
    ) {
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

  public static class Role {
    private final String name;
    private Set<String> ariaProperties;

    public Role(String name) {
      this.name = name;
      this.ariaProperties = Set.of();
    }

    public String getName() {
      return name;
    }

    public Role setProperties(String... values) {
      this.ariaProperties = Set.of(values);
      return this;
    }

    public boolean propertyIsAllowed(String name) {
      return ariaProperties.contains(name);
    }
  }

  public static class Element {
    private final String name;
    private Set<String> roles;

    public Element(String name) {
      this.name = name;
      this.roles = Set.of();
    }

    public String getName() {
      return name;
    }

    public Element setRoles(String... values) {
      this.roles = Set.of(values);
      return this;
    }

    public boolean roleIsAllowed(String name) {
      return roles.contains(name);
    }
  }

  private Aria() {
    // Utility class
  }

  // from https://github.com/jsx-eslint/eslint-plugin-jsx-a11y/blob/main/src/util/implicitRoles/index.js
  public static String getImplicitRole(TagNode element) {
    switch (element.getNodeName()) {
      case "a", "area", "link":
        if (element.getAttribute("href") != null) {
          return "link";
        }
        return "";
      case "article":
        return "article";
      case "aside":
        return "complementary";
      case "body":
        return "document";
      case "button":
        return "button";
      case "datalist", "select":
        return "listbox";
      case "details":
        return "group";
      case "dialog":
        return "dialog";
      case "form":
        return "form";
      case "h1", "h2", "h3", "h4", "h5", "h6":
        return "heading";
      case "hr":
        return "separator";
      case "img":
        var alt = element.getAttribute("href");
        if (alt != null && alt.equalsIgnoreCase("")) {
          return "";
        }
        return "img";
      case "input":
        var inputType = element.getAttribute("type");
        if (inputType != null) {
          return switch (inputType.toLowerCase(Locale.ROOT)) {
            case "button", "image", "reset", "submit" -> "button";
            case "checkbox" -> "checkbox";
            case "radio" -> "radio";
            case "range" -> "slider";
            default -> "textbox";
          };
        }
        return "textbox";
      case "li":
        return "listitem";
      case "menu":
        var menuType = element.getAttribute("type");
        if (menuType != null && menuType.equalsIgnoreCase("toolbar")) {
          return "toolbar";
        }
        return "";
      case "menuitem":
        var type = element.getAttribute("type");
        if (type != null) {
          if (type.equalsIgnoreCase("command")) {
            return "menuitem";
          } else if (type.equalsIgnoreCase("checkbox")) {
            return "menuitemcheckbox";
          } else if (type.equalsIgnoreCase("radio")) {
            return "menuitemradio";
          }
        }
        return "";
      case "meter", "progress":
        return "progressbar";
      case "nav":
        return "navigation";
      case "ol", "ul":
        return "list";
      case "option":
        return "option";
      case "output":
        return "status";
      case "section":
        return "region";
      case "tbody", "tfoot", "thead":
        return "rowgroup";
      case "textarea":
        return "textbox";
      default:
        return "";
    }
  }
}
