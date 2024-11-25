/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.api.accessibility;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.sonar.plugins.html.node.TagNode;

public class Aria {

  protected static final Map<AriaProperty, AriaPropertyValues> ARIA_PROPERTIES =
    new EnumMap<>(AriaProperty.class);
  protected static final Map<AriaRole, RoleDefinition> ROLES = new EnumMap<>(AriaRole.class);
  protected static final Map<Element, ElementRoles> ELEMENTS = new EnumMap<>(Element.class);

  static {
    ARIA_PROPERTIES.put(
      AriaProperty.ACTIVEDESCENDANT,
      new AriaPropertyValues(AriaProperty.ACTIVEDESCENDANT, AriaPropertyType.ID)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.ATOMIC,
      new AriaPropertyValues(AriaProperty.ATOMIC, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.AUTOCOMPLETE,
      new AriaPropertyValues(
        AriaProperty.AUTOCOMPLETE,
        AriaPropertyType.TOKEN,
        "inline",
        "list",
        "both",
        "none"
      )
    );
    ARIA_PROPERTIES.put(
      AriaProperty.BRAILLELABEL,
      new AriaPropertyValues(AriaProperty.BRAILLELABEL, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.BRAILLEROLEDESCRIPTION,
      new AriaPropertyValues(AriaProperty.BRAILLEROLEDESCRIPTION, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.BUSY,
      new AriaPropertyValues(AriaProperty.BUSY, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.CHECKED,
      new AriaPropertyValues(AriaProperty.CHECKED, AriaPropertyType.TRISTATE)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.COLCOUNT,
      new AriaPropertyValues(AriaProperty.COLCOUNT, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.COLINDEX,
      new AriaPropertyValues(AriaProperty.COLINDEX, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.COLSPAN,
      new AriaPropertyValues(AriaProperty.COLSPAN, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.CONTROLS,
      new AriaPropertyValues(AriaProperty.CONTROLS, AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.CURRENT,
      new AriaPropertyValues(
        AriaProperty.CURRENT,
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
      AriaProperty.DESCRIBEDBY,
      new AriaPropertyValues(AriaProperty.DESCRIBEDBY, AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.DESCRIPTION,
      new AriaPropertyValues(AriaProperty.DESCRIPTION, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.DETAILS,
      new AriaPropertyValues(AriaProperty.DETAILS, AriaPropertyType.ID)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.DISABLED,
      new AriaPropertyValues(AriaProperty.DISABLED, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.DROPEFFECT,
      new AriaPropertyValues(
        AriaProperty.DROPEFFECT,
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
      AriaProperty.ERRORMESSAGE,
      new AriaPropertyValues(AriaProperty.ERRORMESSAGE, AriaPropertyType.ID)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.EXPANDED,
      new AriaPropertyValues(
        AriaProperty.EXPANDED,
        AriaPropertyType.BOOLEAN,
        true,
        "true",
        "false"
      )
    );
    ARIA_PROPERTIES.put(
      AriaProperty.FLOWTO,
      new AriaPropertyValues(AriaProperty.FLOWTO, AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.GRABBED,
      new AriaPropertyValues(
        AriaProperty.GRABBED,
        AriaPropertyType.BOOLEAN,
        true,
        "true",
        "false",
        "undefined"
      )
    );
    ARIA_PROPERTIES.put(
      AriaProperty.HASPOPUP,
      new AriaPropertyValues(
        AriaProperty.HASPOPUP,
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
      AriaProperty.HIDDEN,
      new AriaPropertyValues(
        AriaProperty.HIDDEN,
        AriaPropertyType.BOOLEAN,
        true,
        "true",
        "false"
      )
    );
    ARIA_PROPERTIES.put(
      AriaProperty.INVALID,
      new AriaPropertyValues(
        AriaProperty.INVALID,
        AriaPropertyType.TOKEN,
        "true",
        "false",
        "grammar",
        "spelling"
      )
    );
    ARIA_PROPERTIES.put(
      AriaProperty.KEYSHORTCUTS,
      new AriaPropertyValues(AriaProperty.KEYSHORTCUTS, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.LABEL,
      new AriaPropertyValues(AriaProperty.LABEL, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.LABELLEDBY,
      new AriaPropertyValues(AriaProperty.LABELLEDBY, AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.LEVEL,
      new AriaPropertyValues(AriaProperty.LEVEL, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.LIVE,
      new AriaPropertyValues(
        AriaProperty.LIVE,
        AriaPropertyType.TOKEN,
        "off",
        "assertive",
        "polite"
      )
    );
    ARIA_PROPERTIES.put(
      AriaProperty.MODAL,
      new AriaPropertyValues(AriaProperty.MODAL, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.MULTILINE,
      new AriaPropertyValues(AriaProperty.MULTILINE, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.MULTISELECTABLE,
      new AriaPropertyValues(AriaProperty.MULTISELECTABLE, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.ORIENTATION,
      new AriaPropertyValues(
        AriaProperty.ORIENTATION,
        AriaPropertyType.TOKEN,
        "horizontal",
        "vertical",
        "undefined"
      )
    );
    ARIA_PROPERTIES.put(
      AriaProperty.OWNS,
      new AriaPropertyValues(AriaProperty.OWNS, AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.PLACEHOLDER,
      new AriaPropertyValues(AriaProperty.PLACEHOLDER, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.POSINSET,
      new AriaPropertyValues(AriaProperty.POSINSET, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.PRESSED,
      new AriaPropertyValues(AriaProperty.PRESSED, AriaPropertyType.TRISTATE)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.READONLY,
      new AriaPropertyValues(AriaProperty.READONLY, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.RELEVANT,
      new AriaPropertyValues(
        AriaProperty.RELEVANT,
        AriaPropertyType.TOKENLIST,
        "additions",
        "removals",
        "text",
        "all"
      )
    );
    ARIA_PROPERTIES.put(
      AriaProperty.REQUIRED,
      new AriaPropertyValues(AriaProperty.REQUIRED, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.ROLEDESCRIPTION,
      new AriaPropertyValues(AriaProperty.ROLEDESCRIPTION, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.ROWCOUNT,
      new AriaPropertyValues(AriaProperty.ROWCOUNT, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.ROWINDEX,
      new AriaPropertyValues(AriaProperty.ROWINDEX, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.ROWSPAN,
      new AriaPropertyValues(AriaProperty.ROWSPAN, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.SELECTED,
      new AriaPropertyValues(
        AriaProperty.SELECTED,
        AriaPropertyType.BOOLEAN,
        true,
        "true",
        "false"
      )
    );
    ARIA_PROPERTIES.put(
      AriaProperty.SETSIZE,
      new AriaPropertyValues(AriaProperty.SETSIZE, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.SORT,
      new AriaPropertyValues(
        AriaProperty.SORT,
        AriaPropertyType.TOKEN,
        "ascending",
        "descending",
        "none",
        "other"
      )
    );
    ARIA_PROPERTIES.put(
      AriaProperty.VALUEMAX,
      new AriaPropertyValues(AriaProperty.VALUEMAX, AriaPropertyType.NUMBER)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.VALUEMIN,
      new AriaPropertyValues(AriaProperty.VALUEMIN, AriaPropertyType.NUMBER)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.VALUENOW,
      new AriaPropertyValues(AriaProperty.VALUENOW, AriaPropertyType.NUMBER)
    );
    ARIA_PROPERTIES.put(
      AriaProperty.VALUETEXT,
      new AriaPropertyValues(AriaProperty.VALUETEXT, AriaPropertyType.STRING)
    );

    ROLES.put(AriaRole.COMMAND,
      new RoleDefinition(AriaRole.COMMAND)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.COMPOSITE,
      new RoleDefinition(AriaRole.COMPOSITE)
        .setProperties(
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.INPUT,
      new RoleDefinition(AriaRole.INPUT)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.LANDMARK,
      new RoleDefinition(AriaRole.LANDMARK)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.RANGE,
      new RoleDefinition(AriaRole.RANGE)
        .setProperties(
          AriaProperty.VALUEMAX,
          AriaProperty.VALUEMIN,
          AriaProperty.VALUENOW,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.ROLETYPE,
      new RoleDefinition(AriaRole.ROLETYPE)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.SECTION,
      new RoleDefinition(AriaRole.SECTION)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.SECTIONHEAD,
      new RoleDefinition(AriaRole.SECTIONHEAD)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.SELECT,
      new RoleDefinition(AriaRole.SELECT)
        .setProperties(
          AriaProperty.ORIENTATION,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED
        )
    );
    ROLES.put(AriaRole.STRUCTURE,
      new RoleDefinition(AriaRole.STRUCTURE)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.WIDGET,
      new RoleDefinition(AriaRole.WIDGET)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.WINDOW,
      new RoleDefinition(AriaRole.WINDOW)
        .setProperties(
          AriaProperty.MODAL,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.ALERT,
      new RoleDefinition(AriaRole.ALERT)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.LIVE,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.ALERTDIALOG,
      new RoleDefinition(AriaRole.ALERTDIALOG)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.MODAL
        )
    );
    ROLES.put(AriaRole.APPLICATION,
      new RoleDefinition(AriaRole.APPLICATION)
        .setProperties(
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.ARTICLE,
      new RoleDefinition(AriaRole.ARTICLE)
        .setProperties(
          AriaProperty.POSINSET,
          AriaProperty.SETSIZE,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.ARTICLE)
    );
    ROLES.put(AriaRole.BANNER,
      new RoleDefinition(AriaRole.BANNER)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.HEADER)
    );
    ROLES.put(AriaRole.BLOCKQUOTE,
      new RoleDefinition(AriaRole.BLOCKQUOTE)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.BLOCKQUOTE)
    );
    ROLES.put(AriaRole.BUTTON,
      new RoleDefinition(AriaRole.BUTTON)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.PRESSED,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.INPUT, Element.BUTTON)
    );
    ROLES.put(AriaRole.CAPTION,
      new RoleDefinition(AriaRole.CAPTION)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.CAPTION)
    );
    ROLES.put(AriaRole.CELL,
      new RoleDefinition(AriaRole.CELL)
        .setProperties(
          AriaProperty.COLINDEX,
          AriaProperty.COLSPAN,
          AriaProperty.ROWINDEX,
          AriaProperty.ROWSPAN,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.TD)
    );
    ROLES.put(AriaRole.CHECKBOX,
      new RoleDefinition(AriaRole.CHECKBOX)
        .setProperties(
          AriaProperty.CHECKED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.INVALID,
          AriaProperty.READONLY,
          AriaProperty.REQUIRED,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED
        )
        .setElements(Element.INPUT)
        .setRequiredProperties(
          AriaProperty.CHECKED
        )
    );
    ROLES.put(AriaRole.CODE,
      new RoleDefinition(AriaRole.CODE)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.CODE)
    );
    ROLES.put(AriaRole.COLUMNHEADER,
      new RoleDefinition(AriaRole.COLUMNHEADER)
        .setProperties(
          AriaProperty.SORT,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.COLINDEX,
          AriaProperty.COLSPAN,
          AriaProperty.ROWINDEX,
          AriaProperty.ROWSPAN,
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.READONLY,
          AriaProperty.REQUIRED,
          AriaProperty.SELECTED
        )
        .setElements(Element.TH)
    );
    ROLES.put(AriaRole.COMBOBOX,
      new RoleDefinition(AriaRole.COMBOBOX)
        .setProperties(
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.AUTOCOMPLETE,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.INVALID,
          AriaProperty.READONLY,
          AriaProperty.REQUIRED,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED
        )
        .setElements(Element.INPUT, Element.SELECT)
        .setRequiredProperties(
          AriaProperty.CONTROLS,
          AriaProperty.EXPANDED
        )
    );
    ROLES.put(AriaRole.COMPLEMENTARY,
      new RoleDefinition(AriaRole.COMPLEMENTARY)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.ASIDE)
    );
    ROLES.put(AriaRole.CONTENTINFO,
      new RoleDefinition(AriaRole.CONTENTINFO)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.FOOTER)
    );
    ROLES.put(AriaRole.DEFINITION,
      new RoleDefinition(AriaRole.DEFINITION)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.DD)
    );
    ROLES.put(AriaRole.DELETION,
      new RoleDefinition(AriaRole.DELETION)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.DEL)
    );
    ROLES.put(AriaRole.DIALOG,
      new RoleDefinition(AriaRole.DIALOG)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.MODAL
        )
        .setElements(Element.DIALOG)
    );
    ROLES.put(AriaRole.DIRECTORY,
      new RoleDefinition(AriaRole.DIRECTORY)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOCUMENT,
      new RoleDefinition(AriaRole.DOCUMENT)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.HTML)
    );
    ROLES.put(AriaRole.EMPHASIS,
      new RoleDefinition(AriaRole.EMPHASIS)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.EM)
    );
    ROLES.put(AriaRole.FEED,
      new RoleDefinition(AriaRole.FEED)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.FIGURE,
      new RoleDefinition(AriaRole.FIGURE)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.FIGURE)
    );
    ROLES.put(AriaRole.FORM,
      new RoleDefinition(AriaRole.FORM)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.FORM)
    );
    ROLES.put(AriaRole.GENERIC,
      new RoleDefinition(AriaRole.GENERIC)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(
          Element.A,
          Element.AREA,
          Element.ASIDE,
          Element.B,
          Element.BDO,
          Element.BODY,
          Element.DATA,
          Element.DIV,
          Element.FOOTER,
          Element.HEADER,
          Element.HGROUP,
          Element.I,
          Element.PRE,
          Element.Q,
          Element.SAMP,
          Element.SECTION,
          Element.SMALL,
          Element.SPAN,
          Element.U
        )
    );
    ROLES.put(AriaRole.GRID,
      new RoleDefinition(AriaRole.GRID)
        .setProperties(
          AriaProperty.MULTISELECTABLE,
          AriaProperty.READONLY,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED,
          AriaProperty.COLCOUNT,
          AriaProperty.ROWCOUNT
        )
    );
    ROLES.put(AriaRole.GRIDCELL,
      new RoleDefinition(AriaRole.GRIDCELL)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.READONLY,
          AriaProperty.REQUIRED,
          AriaProperty.SELECTED,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.COLINDEX,
          AriaProperty.COLSPAN,
          AriaProperty.ROWINDEX,
          AriaProperty.ROWSPAN
        )
        .setElements(Element.TD)
    );
    ROLES.put(AriaRole.GROUP,
      new RoleDefinition(AriaRole.GROUP)
        .setProperties(
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.DETAILS, Element.FIELDSET, Element.OPTGROUP, Element.ADDRESS)
    );
    ROLES.put(AriaRole.HEADING,
      new RoleDefinition(AriaRole.HEADING)
        .setProperties(
          AriaProperty.LEVEL,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.H1, Element.H2, Element.H3, Element.H4, Element.H5, Element.H6)
        .setRequiredProperties(
          AriaProperty.LEVEL
        )
    );
    ROLES.put(AriaRole.IMG,
      new RoleDefinition(AriaRole.IMG)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.IMG)
    );
    ROLES.put(AriaRole.INSERTION,
      new RoleDefinition(AriaRole.INSERTION)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.INS)
    );
    ROLES.put(AriaRole.LINK,
      new RoleDefinition(AriaRole.LINK)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.A, Element.AREA)
    );
    ROLES.put(AriaRole.LIST,
      new RoleDefinition(AriaRole.LIST)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.MENU, Element.OL, Element.UL)
    );
    ROLES.put(AriaRole.LISTBOX,
      new RoleDefinition(AriaRole.LISTBOX)
        .setProperties(
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.INVALID,
          AriaProperty.MULTISELECTABLE,
          AriaProperty.READONLY,
          AriaProperty.REQUIRED,
          AriaProperty.ORIENTATION,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED
        )
        .setElements(Element.SELECT, Element.DATALIST)
    );
    ROLES.put(AriaRole.LISTITEM,
      new RoleDefinition(AriaRole.LISTITEM)
        .setProperties(
          AriaProperty.LEVEL,
          AriaProperty.POSINSET,
          AriaProperty.SETSIZE,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.LI)
    );
    ROLES.put(AriaRole.LOG,
      new RoleDefinition(AriaRole.LOG)
        .setProperties(
          AriaProperty.LIVE,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.MAIN,
      new RoleDefinition(AriaRole.MAIN)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.MAIN)
    );
    ROLES.put(AriaRole.MARK,
      new RoleDefinition(AriaRole.MARK)
        .setProperties(
          AriaProperty.BRAILLELABEL,
          AriaProperty.BRAILLEROLEDESCRIPTION,
          AriaProperty.DESCRIPTION,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.MARK)
    );
    ROLES.put(AriaRole.MARQUEE,
      new RoleDefinition(AriaRole.MARQUEE)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.MATH,
      new RoleDefinition(AriaRole.MATH)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.MATH)
    );
    ROLES.put(AriaRole.MENU,
      new RoleDefinition(AriaRole.MENU)
        .setProperties(
          AriaProperty.ORIENTATION,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED
        )
    );
    ROLES.put(AriaRole.MENUBAR,
      new RoleDefinition(AriaRole.MENUBAR)
        .setProperties(
          AriaProperty.ORIENTATION,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED
        )
    );
    ROLES.put(AriaRole.MENUITEM,
      new RoleDefinition(AriaRole.MENUITEM)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.POSINSET,
          AriaProperty.SETSIZE,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.MENUITEMCHECKBOX,
      new RoleDefinition(AriaRole.MENUITEMCHECKBOX)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED,
          AriaProperty.CHECKED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.INVALID,
          AriaProperty.READONLY,
          AriaProperty.REQUIRED,
          AriaProperty.HASPOPUP,
          AriaProperty.POSINSET,
          AriaProperty.SETSIZE
        )
        .setRequiredProperties(
          AriaProperty.CHECKED
        )
    );
    ROLES.put(AriaRole.MENUITEMRADIO,
      new RoleDefinition(AriaRole.MENUITEMRADIO)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED,
          AriaProperty.CHECKED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.INVALID,
          AriaProperty.READONLY,
          AriaProperty.REQUIRED,
          AriaProperty.HASPOPUP,
          AriaProperty.POSINSET,
          AriaProperty.SETSIZE
        )
        .setRequiredProperties(
          AriaProperty.CHECKED
        )
    );
    ROLES.put(AriaRole.METER,
      new RoleDefinition(AriaRole.METER)
        .setProperties(
          AriaProperty.VALUETEXT,
          AriaProperty.VALUEMAX,
          AriaProperty.VALUEMIN,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.VALUENOW
        )
        .setElements(Element.METER)
        .setRequiredProperties(
          AriaProperty.VALUENOW
        )
    );
    ROLES.put(AriaRole.NAVIGATION,
      new RoleDefinition(AriaRole.NAVIGATION)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.NAV)
    );
    ROLES.put(AriaRole.NOTE,
      new RoleDefinition(AriaRole.NOTE)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.OPTION,
      new RoleDefinition(AriaRole.OPTION)
        .setProperties(
          AriaProperty.CHECKED,
          AriaProperty.POSINSET,
          AriaProperty.SETSIZE,
          AriaProperty.SELECTED,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED
        )
        .setElements(Element.OPTION)
        .setRequiredProperties(
          AriaProperty.SELECTED
        )
    );
    ROLES.put(AriaRole.PARAGRAPH,
      new RoleDefinition(AriaRole.PARAGRAPH)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.P)
    );
    ROLES.put(AriaRole.PRESENTATION,
      new RoleDefinition(AriaRole.PRESENTATION)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.IMG)
    );
    ROLES.put(AriaRole.PROGRESSBAR,
      new RoleDefinition(AriaRole.PROGRESSBAR)
        .setProperties(
          AriaProperty.VALUETEXT,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.VALUEMAX,
          AriaProperty.VALUEMIN,
          AriaProperty.VALUENOW
        )
        .setElements(Element.PROGRESS)
    );
    ROLES.put(AriaRole.RADIO,
      new RoleDefinition(AriaRole.RADIO)
        .setProperties(
          AriaProperty.CHECKED,
          AriaProperty.POSINSET,
          AriaProperty.SETSIZE,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED
        )
        .setElements(Element.INPUT)
        .setRequiredProperties(
          AriaProperty.CHECKED
        )
    );
    ROLES.put(AriaRole.RADIOGROUP,
      new RoleDefinition(AriaRole.RADIOGROUP)
        .setProperties(
          AriaProperty.ERRORMESSAGE,
          AriaProperty.INVALID,
          AriaProperty.READONLY,
          AriaProperty.REQUIRED,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED,
          AriaProperty.ORIENTATION
        )
    );
    ROLES.put(AriaRole.REGION,
      new RoleDefinition(AriaRole.REGION)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.SECTION)
    );
    ROLES.put(AriaRole.ROW,
      new RoleDefinition(AriaRole.ROW)
        .setProperties(
          AriaProperty.COLINDEX,
          AriaProperty.EXPANDED,
          AriaProperty.LEVEL,
          AriaProperty.POSINSET,
          AriaProperty.ROWINDEX,
          AriaProperty.SELECTED,
          AriaProperty.SETSIZE,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED
        )
        .setElements(Element.TR)
    );
    ROLES.put(AriaRole.ROWGROUP,
      new RoleDefinition(AriaRole.ROWGROUP)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.TBODY, Element.TFOOT, Element.THEAD)
    );
    ROLES.put(AriaRole.ROWHEADER,
      new RoleDefinition(AriaRole.ROWHEADER)
        .setProperties(
          AriaProperty.SORT,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.COLINDEX,
          AriaProperty.COLSPAN,
          AriaProperty.ROWINDEX,
          AriaProperty.ROWSPAN,
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.READONLY,
          AriaProperty.REQUIRED,
          AriaProperty.SELECTED
        )
        .setElements(Element.TH)
    );
    ROLES.put(AriaRole.SCROLLBAR,
      new RoleDefinition(AriaRole.SCROLLBAR)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.VALUETEXT,
          AriaProperty.ORIENTATION,
          AriaProperty.VALUEMAX,
          AriaProperty.VALUEMIN,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.VALUENOW
        )
        .setRequiredProperties(
          AriaProperty.CONTROLS,
          AriaProperty.VALUENOW
        )
    );
    ROLES.put(AriaRole.SEARCH,
      new RoleDefinition(AriaRole.SEARCH)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.SEARCHBOX,
      new RoleDefinition(AriaRole.SEARCHBOX)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.AUTOCOMPLETE,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.MULTILINE,
          AriaProperty.PLACEHOLDER,
          AriaProperty.READONLY,
          AriaProperty.REQUIRED
        )
        .setElements(Element.INPUT)
    );
    ROLES.put(AriaRole.SEPARATOR,
      new RoleDefinition(AriaRole.SEPARATOR)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ORIENTATION,
          AriaProperty.VALUEMAX,
          AriaProperty.VALUEMIN,
          AriaProperty.VALUENOW,
          AriaProperty.VALUETEXT,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.HR)
    );
    ROLES.put(AriaRole.SLIDER,
      new RoleDefinition(AriaRole.SLIDER)
        .setProperties(
          AriaProperty.ERRORMESSAGE,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.READONLY,
          AriaProperty.VALUETEXT,
          AriaProperty.ORIENTATION,
          AriaProperty.VALUEMAX,
          AriaProperty.VALUEMIN,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED,
          AriaProperty.VALUENOW
        )
        .setElements(Element.INPUT)
        .setRequiredProperties(
          AriaProperty.VALUENOW
        )
    );
    ROLES.put(AriaRole.SPINBUTTON,
      new RoleDefinition(AriaRole.SPINBUTTON)
        .setProperties(
          AriaProperty.ERRORMESSAGE,
          AriaProperty.INVALID,
          AriaProperty.READONLY,
          AriaProperty.REQUIRED,
          AriaProperty.VALUETEXT,
          AriaProperty.VALUENOW,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED,
          AriaProperty.VALUEMAX,
          AriaProperty.VALUEMIN
        )
        .setElements(Element.INPUT)
    );
    ROLES.put(AriaRole.STATUS,
      new RoleDefinition(AriaRole.STATUS)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.LIVE,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.OUTPUT)
    );
    ROLES.put(AriaRole.STRONG,
      new RoleDefinition(AriaRole.STRONG)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.STRONG)
    );
    ROLES.put(AriaRole.SUBSCRIPT,
      new RoleDefinition(AriaRole.SUBSCRIPT)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.SUB)
    );
    ROLES.put(AriaRole.SUPERSCRIPT,
      new RoleDefinition(AriaRole.SUPERSCRIPT)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.SUP)
    );
    ROLES.put(AriaRole.SWITCH,
      new RoleDefinition(AriaRole.SWITCH)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED,
          AriaProperty.CHECKED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.INVALID,
          AriaProperty.READONLY,
          AriaProperty.REQUIRED
        )
        .setRequiredProperties(
          AriaProperty.CHECKED
        )
    );
    ROLES.put(AriaRole.TAB,
      new RoleDefinition(AriaRole.TAB)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.POSINSET,
          AriaProperty.SETSIZE,
          AriaProperty.SELECTED,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.TABLE,
      new RoleDefinition(AriaRole.TABLE)
        .setProperties(
          AriaProperty.COLCOUNT,
          AriaProperty.ROWCOUNT,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.TABLE)
    );
    ROLES.put(AriaRole.TABLIST,
      new RoleDefinition(AriaRole.TABLIST)
        .setProperties(
          AriaProperty.LEVEL,
          AriaProperty.MULTISELECTABLE,
          AriaProperty.ORIENTATION,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED
        )
    );
    ROLES.put(AriaRole.TABPANEL,
      new RoleDefinition(AriaRole.TABPANEL)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.TERM,
      new RoleDefinition(AriaRole.TERM)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.DFN, Element.DT)
    );
    ROLES.put(AriaRole.TEXTBOX,
      new RoleDefinition(AriaRole.TEXTBOX)
        .setProperties(
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.AUTOCOMPLETE,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.MULTILINE,
          AriaProperty.PLACEHOLDER,
          AriaProperty.READONLY,
          AriaProperty.REQUIRED,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED
        )
        .setElements(Element.INPUT, Element.TEXTAREA)
    );
    ROLES.put(AriaRole.TIME,
      new RoleDefinition(AriaRole.TIME)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
        .setElements(Element.TIME)
    );
    ROLES.put(AriaRole.TIMER,
      new RoleDefinition(AriaRole.TIMER)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.TOOLBAR,
      new RoleDefinition(AriaRole.TOOLBAR)
        .setProperties(
          AriaProperty.ORIENTATION,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED
        )
    );
    ROLES.put(AriaRole.TOOLTIP,
      new RoleDefinition(AriaRole.TOOLTIP)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.TREE,
      new RoleDefinition(AriaRole.TREE)
        .setProperties(
          AriaProperty.ERRORMESSAGE,
          AriaProperty.INVALID,
          AriaProperty.MULTISELECTABLE,
          AriaProperty.REQUIRED,
          AriaProperty.ORIENTATION,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED
        )
    );
    ROLES.put(AriaRole.TREEGRID,
      new RoleDefinition(AriaRole.TREEGRID)
        .setProperties(
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED,
          AriaProperty.MULTISELECTABLE,
          AriaProperty.READONLY,
          AriaProperty.COLCOUNT,
          AriaProperty.ROWCOUNT,
          AriaProperty.ORIENTATION,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.INVALID,
          AriaProperty.REQUIRED
        )
    );
    ROLES.put(AriaRole.TREEITEM,
      new RoleDefinition(AriaRole.TREEITEM)
        .setProperties(
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.LEVEL,
          AriaProperty.POSINSET,
          AriaProperty.SETSIZE,
          AriaProperty.DISABLED,
          AriaProperty.CHECKED,
          AriaProperty.SELECTED
        )
        .setRequiredProperties(
          AriaProperty.SELECTED
        )
    );
    ROLES.put(AriaRole.DOC_ABSTRACT,
      new RoleDefinition(AriaRole.DOC_ABSTRACT)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_ACKNOWLEDGMENTS,
      new RoleDefinition(AriaRole.DOC_ACKNOWLEDGMENTS)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_AFTERWORD,
      new RoleDefinition(AriaRole.DOC_AFTERWORD)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_APPENDIX,
      new RoleDefinition(AriaRole.DOC_APPENDIX)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_BACKLINK,
      new RoleDefinition(AriaRole.DOC_BACKLINK)
        .setProperties(
          AriaProperty.ERRORMESSAGE,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP
        )
    );
    ROLES.put(AriaRole.DOC_BIBLIOENTRY,
      new RoleDefinition(AriaRole.DOC_BIBLIOENTRY)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.LEVEL,
          AriaProperty.POSINSET,
          AriaProperty.SETSIZE
        )
    );
    ROLES.put(AriaRole.DOC_BIBLIOGRAPHY,
      new RoleDefinition(AriaRole.DOC_BIBLIOGRAPHY)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_BIBLIOREF,
      new RoleDefinition(AriaRole.DOC_BIBLIOREF)
        .setProperties(
          AriaProperty.ERRORMESSAGE,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP
        )
    );
    ROLES.put(AriaRole.DOC_CHAPTER,
      new RoleDefinition(AriaRole.DOC_CHAPTER)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_COLOPHON,
      new RoleDefinition(AriaRole.DOC_COLOPHON)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_CONCLUSION,
      new RoleDefinition(AriaRole.DOC_CONCLUSION)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_COVER,
      new RoleDefinition(AriaRole.DOC_COVER)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_CREDIT,
      new RoleDefinition(AriaRole.DOC_CREDIT)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_CREDITS,
      new RoleDefinition(AriaRole.DOC_CREDITS)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_DEDICATION,
      new RoleDefinition(AriaRole.DOC_DEDICATION)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_ENDNOTE,
      new RoleDefinition(AriaRole.DOC_ENDNOTE)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.LEVEL,
          AriaProperty.POSINSET,
          AriaProperty.SETSIZE
        )
    );
    ROLES.put(AriaRole.DOC_ENDNOTES,
      new RoleDefinition(AriaRole.DOC_ENDNOTES)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_EPIGRAPH,
      new RoleDefinition(AriaRole.DOC_EPIGRAPH)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_EPILOGUE,
      new RoleDefinition(AriaRole.DOC_EPILOGUE)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_ERRATA,
      new RoleDefinition(AriaRole.DOC_ERRATA)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_EXAMPLE,
      new RoleDefinition(AriaRole.DOC_EXAMPLE)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_FOOTNOTE,
      new RoleDefinition(AriaRole.DOC_FOOTNOTE)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_FOREWORD,
      new RoleDefinition(AriaRole.DOC_FOREWORD)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_GLOSSARY,
      new RoleDefinition(AriaRole.DOC_GLOSSARY)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_GLOSSREF,
      new RoleDefinition(AriaRole.DOC_GLOSSREF)
        .setProperties(
          AriaProperty.ERRORMESSAGE,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP
        )
    );
    ROLES.put(AriaRole.DOC_INDEX,
      new RoleDefinition(AriaRole.DOC_INDEX)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_INTRODUCTION,
      new RoleDefinition(AriaRole.DOC_INTRODUCTION)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_NOTEREF,
      new RoleDefinition(AriaRole.DOC_NOTEREF)
        .setProperties(
          AriaProperty.ERRORMESSAGE,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP
        )
    );
    ROLES.put(AriaRole.DOC_NOTICE,
      new RoleDefinition(AriaRole.DOC_NOTICE)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_PAGEBREAK,
      new RoleDefinition(AriaRole.DOC_PAGEBREAK)
        .setProperties(
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.DISABLED,
          AriaProperty.ORIENTATION,
          AriaProperty.VALUEMAX,
          AriaProperty.VALUEMIN,
          AriaProperty.VALUENOW,
          AriaProperty.VALUETEXT
        )
    );
    ROLES.put(AriaRole.DOC_PAGELIST,
      new RoleDefinition(AriaRole.DOC_PAGELIST)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_PART,
      new RoleDefinition(AriaRole.DOC_PART)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_PREFACE,
      new RoleDefinition(AriaRole.DOC_PREFACE)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_PROLOGUE,
      new RoleDefinition(AriaRole.DOC_PROLOGUE)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_QNA,
      new RoleDefinition(AriaRole.DOC_QNA)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_SUBTITLE,
      new RoleDefinition(AriaRole.DOC_SUBTITLE)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_TIP,
      new RoleDefinition(AriaRole.DOC_TIP)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.DOC_TOC,
      new RoleDefinition(AriaRole.DOC_TOC)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.GRAPHICS_DOCUMENT,
      new RoleDefinition(AriaRole.GRAPHICS_DOCUMENT)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ROLES.put(AriaRole.GRAPHICS_OBJECT,
      new RoleDefinition(AriaRole.GRAPHICS_OBJECT)
        .setProperties(
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION,
          AriaProperty.ACTIVEDESCENDANT,
          AriaProperty.DISABLED
        )
    );
    ROLES.put(AriaRole.GRAPHICS_SYMBOL,
      new RoleDefinition(AriaRole.GRAPHICS_SYMBOL)
        .setProperties(
          AriaProperty.DISABLED,
          AriaProperty.ERRORMESSAGE,
          AriaProperty.EXPANDED,
          AriaProperty.HASPOPUP,
          AriaProperty.INVALID,
          AriaProperty.ATOMIC,
          AriaProperty.BUSY,
          AriaProperty.CONTROLS,
          AriaProperty.CURRENT,
          AriaProperty.DESCRIBEDBY,
          AriaProperty.DETAILS,
          AriaProperty.DROPEFFECT,
          AriaProperty.FLOWTO,
          AriaProperty.GRABBED,
          AriaProperty.HIDDEN,
          AriaProperty.KEYSHORTCUTS,
          AriaProperty.LABEL,
          AriaProperty.LABELLEDBY,
          AriaProperty.LIVE,
          AriaProperty.OWNS,
          AriaProperty.RELEVANT,
          AriaProperty.ROLEDESCRIPTION
        )
    );
    ELEMENTS.put(Element.ARTICLE, new ElementRoles(Element.ARTICLE).setRoles(AriaRole.ARTICLE));
    ELEMENTS.put(Element.HEADER, new ElementRoles(Element.HEADER).setRoles(AriaRole.BANNER, AriaRole.GENERIC));
    ELEMENTS.put(Element.BLOCKQUOTE, new ElementRoles(Element.BLOCKQUOTE).setRoles(AriaRole.BLOCKQUOTE));
    ELEMENTS.put(Element.INPUT, new ElementRoles(Element.INPUT).setRoles(AriaRole.BUTTON, AriaRole.CHECKBOX, AriaRole.COMBOBOX,
      AriaRole.RADIO, AriaRole.SEARCHBOX, AriaRole.SLIDER, AriaRole.SPINBUTTON, AriaRole.TEXTBOX));
    ELEMENTS.put(Element.BUTTON, new ElementRoles(Element.BUTTON).setRoles(AriaRole.BUTTON));
    ELEMENTS.put(Element.CAPTION, new ElementRoles(Element.CAPTION).setRoles(AriaRole.CAPTION));
    ELEMENTS.put(Element.TD, new ElementRoles(Element.TD).setRoles(AriaRole.CELL, AriaRole.GRIDCELL));
    ELEMENTS.put(Element.CODE, new ElementRoles(Element.CODE).setRoles(AriaRole.CODE));
    ELEMENTS.put(Element.TH, new ElementRoles(Element.TH).setRoles(AriaRole.COLUMNHEADER, AriaRole.ROWHEADER));
    ELEMENTS.put(Element.SELECT, new ElementRoles(Element.SELECT).setRoles(AriaRole.COMBOBOX, AriaRole.LISTBOX));
    ELEMENTS.put(Element.ASIDE, new ElementRoles(Element.ASIDE).setRoles(AriaRole.GENERIC, AriaRole.COMPLEMENTARY));
    ELEMENTS.put(Element.FOOTER, new ElementRoles(Element.FOOTER).setRoles(AriaRole.GENERIC, AriaRole.CONTENTINFO));
    ELEMENTS.put(Element.DD, new ElementRoles(Element.DD).setRoles(AriaRole.DEFINITION));
    ELEMENTS.put(Element.DEL, new ElementRoles(Element.DEL).setRoles(AriaRole.DELETION));
    ELEMENTS.put(Element.DIALOG, new ElementRoles(Element.DIALOG).setRoles(AriaRole.DIALOG));
    ELEMENTS.put(Element.HTML, new ElementRoles(Element.HTML).setRoles(AriaRole.DOCUMENT));
    ELEMENTS.put(Element.EM, new ElementRoles(Element.EM).setRoles(AriaRole.EMPHASIS));
    ELEMENTS.put(Element.FIGURE, new ElementRoles(Element.FIGURE).setRoles(AriaRole.FIGURE));
    ELEMENTS.put(Element.FORM, new ElementRoles(Element.FORM).setRoles(AriaRole.FORM));
    ELEMENTS.put(Element.A, new ElementRoles(Element.A).setRoles(AriaRole.GENERIC, AriaRole.LINK));
    ELEMENTS.put(Element.AREA, new ElementRoles(Element.AREA).setRoles(AriaRole.GENERIC, AriaRole.LINK));
    ELEMENTS.put(Element.B, new ElementRoles(Element.B).setRoles(AriaRole.GENERIC));
    ELEMENTS.put(Element.BDO, new ElementRoles(Element.BDO).setRoles(AriaRole.GENERIC));
    ELEMENTS.put(Element.BODY, new ElementRoles(Element.BODY).setRoles(AriaRole.GENERIC));
    ELEMENTS.put(Element.DATA, new ElementRoles(Element.DATA).setRoles(AriaRole.GENERIC));
    ELEMENTS.put(Element.DIV, new ElementRoles(Element.DIV).setRoles(AriaRole.GENERIC));
    ELEMENTS.put(Element.HGROUP, new ElementRoles(Element.HGROUP).setRoles(AriaRole.GENERIC));
    ELEMENTS.put(Element.I, new ElementRoles(Element.I).setRoles(AriaRole.GENERIC));
    ELEMENTS.put(Element.PRE, new ElementRoles(Element.PRE).setRoles(AriaRole.GENERIC));
    ELEMENTS.put(Element.Q, new ElementRoles(Element.Q).setRoles(AriaRole.GENERIC));
    ELEMENTS.put(Element.SAMP, new ElementRoles(Element.SAMP).setRoles(AriaRole.GENERIC));
    ELEMENTS.put(Element.SECTION, new ElementRoles(Element.SECTION).setRoles(AriaRole.GENERIC, AriaRole.REGION));
    ELEMENTS.put(Element.SMALL, new ElementRoles(Element.SMALL).setRoles(AriaRole.GENERIC));
    ELEMENTS.put(Element.SPAN, new ElementRoles(Element.SPAN).setRoles(AriaRole.GENERIC));
    ELEMENTS.put(Element.U, new ElementRoles(Element.U).setRoles(AriaRole.GENERIC));
    ELEMENTS.put(Element.DETAILS, new ElementRoles(Element.DETAILS).setRoles(AriaRole.GROUP));
    ELEMENTS.put(Element.FIELDSET, new ElementRoles(Element.FIELDSET).setRoles(AriaRole.GROUP));
    ELEMENTS.put(Element.OPTGROUP, new ElementRoles(Element.OPTGROUP).setRoles(AriaRole.GROUP));
    ELEMENTS.put(Element.ADDRESS, new ElementRoles(Element.ADDRESS).setRoles(AriaRole.GROUP));
    ELEMENTS.put(Element.H1, new ElementRoles(Element.H1).setRoles(AriaRole.HEADING));
    ELEMENTS.put(Element.H2, new ElementRoles(Element.H2).setRoles(AriaRole.HEADING));
    ELEMENTS.put(Element.H3, new ElementRoles(Element.H3).setRoles(AriaRole.HEADING));
    ELEMENTS.put(Element.H4, new ElementRoles(Element.H4).setRoles(AriaRole.HEADING));
    ELEMENTS.put(Element.H5, new ElementRoles(Element.H5).setRoles(AriaRole.HEADING));
    ELEMENTS.put(Element.H6, new ElementRoles(Element.H6).setRoles(AriaRole.HEADING));
    ELEMENTS.put(Element.IMG, new ElementRoles(Element.IMG).setRoles(AriaRole.IMG, AriaRole.PRESENTATION));
    ELEMENTS.put(Element.INS, new ElementRoles(Element.INS).setRoles(AriaRole.INSERTION));
    ELEMENTS.put(Element.MENU, new ElementRoles(Element.MENU).setRoles(AriaRole.LIST));
    ELEMENTS.put(Element.OL, new ElementRoles(Element.OL).setRoles(AriaRole.LIST));
    ELEMENTS.put(Element.UL, new ElementRoles(Element.UL).setRoles(AriaRole.LIST));
    ELEMENTS.put(Element.DATALIST, new ElementRoles(Element.DATALIST).setRoles(AriaRole.LISTBOX));
    ELEMENTS.put(Element.LI, new ElementRoles(Element.LI).setRoles(AriaRole.LISTITEM));
    ELEMENTS.put(Element.MAIN, new ElementRoles(Element.MAIN).setRoles(AriaRole.MAIN));
    ELEMENTS.put(Element.MARK, new ElementRoles(Element.MARK).setRoles(AriaRole.MARK));
    ELEMENTS.put(Element.MATH, new ElementRoles(Element.MATH).setRoles(AriaRole.MATH));
    ELEMENTS.put(Element.METER, new ElementRoles(Element.METER).setRoles(AriaRole.METER));
    ELEMENTS.put(Element.NAV, new ElementRoles(Element.NAV).setRoles(AriaRole.NAVIGATION));
    ELEMENTS.put(Element.OPTION, new ElementRoles(Element.OPTION).setRoles(AriaRole.OPTION));
    ELEMENTS.put(Element.P, new ElementRoles(Element.P).setRoles(AriaRole.PARAGRAPH));
    ELEMENTS.put(Element.PROGRESS, new ElementRoles(Element.PROGRESS).setRoles(AriaRole.PROGRESSBAR));
    ELEMENTS.put(Element.TR, new ElementRoles(Element.TR).setRoles(AriaRole.ROW));
    ELEMENTS.put(Element.TBODY, new ElementRoles(Element.TBODY).setRoles(AriaRole.ROWGROUP));
    ELEMENTS.put(Element.TFOOT, new ElementRoles(Element.TFOOT).setRoles(AriaRole.ROWGROUP));
    ELEMENTS.put(Element.THEAD, new ElementRoles(Element.THEAD).setRoles(AriaRole.ROWGROUP));
    ELEMENTS.put(Element.HR, new ElementRoles(Element.HR).setRoles(AriaRole.SEPARATOR));
    ELEMENTS.put(Element.OUTPUT, new ElementRoles(Element.OUTPUT).setRoles(AriaRole.STATUS));
    ELEMENTS.put(Element.STRONG, new ElementRoles(Element.STRONG).setRoles(AriaRole.STRONG));
    ELEMENTS.put(Element.SUB, new ElementRoles(Element.SUB).setRoles(AriaRole.SUBSCRIPT));
    ELEMENTS.put(Element.SUP, new ElementRoles(Element.SUP).setRoles(AriaRole.SUPERSCRIPT));
    ELEMENTS.put(Element.TABLE, new ElementRoles(Element.TABLE).setRoles(AriaRole.TABLE));
    ELEMENTS.put(Element.DFN, new ElementRoles(Element.DFN).setRoles(AriaRole.TERM));
    ELEMENTS.put(Element.DT, new ElementRoles(Element.DT).setRoles(AriaRole.TERM));
    ELEMENTS.put(Element.TEXTAREA, new ElementRoles(Element.TEXTAREA).setRoles(AriaRole.TEXTBOX));
    ELEMENTS.put(Element.TIME, new ElementRoles(Element.TIME).setRoles(AriaRole.TIME));

  }

  public static AriaPropertyValues getProperty(AriaProperty name) {
    return ARIA_PROPERTIES.get(name);
  }

  public static RoleDefinition getRole(AriaRole name) {
    return ROLES.get(name);
  }

  public static ElementRoles getElement(Element name) {
    return ELEMENTS.get(name);
  }

  public static Set<AriaProperty> getProperties() {
    return ARIA_PROPERTIES.keySet();
  }

  public static class AriaPropertyValues {

    private final AriaProperty name;
    private final AriaPropertyType type;
    private final Optional<Boolean> allowUndefined;
    private final Set<String> values;

    public AriaPropertyValues(AriaProperty name, AriaPropertyType type, String... values) {
      this(name, type, false, values);
    }

    public AriaPropertyValues(
      AriaProperty name,
      AriaPropertyType type,
      boolean allowUndefined,
      String... values
    ) {
      this.name = name;
      this.type = type;
      this.allowUndefined = Optional.of(allowUndefined);
      this.values = Set.of(values);
    }

    public AriaProperty getName() {
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

  public static class RoleDefinition {
    private final AriaRole name;
    private Set<AriaProperty> ariaProperties;
    private Set<Element> elements;
    private Set<AriaProperty> requiredAriaProperties;

    public RoleDefinition(AriaRole name) {
      this.name = name;
      this.ariaProperties = Set.of();
      this.elements = Set.of();
      this.requiredAriaProperties = Set.of();
    }

    public AriaRole getName() {
      return name;
    }

    public RoleDefinition setProperties(AriaProperty... values) {
      this.ariaProperties = Set.of(values);
      return this;
    }

    public RoleDefinition setElements(Element... values) {
      this.elements = Set.of(values);
      return this;
    }

    public Set<Element> getElements() {
      return this.elements;
    }

    public RoleDefinition setRequiredProperties(AriaProperty... values) {
      this.requiredAriaProperties = Set.of(values);
      return this;
    }

    public boolean propertyIsAllowed(AriaProperty name) {
      return ariaProperties.contains(name);
    }

    public Set<AriaProperty> getRequiredProperties() {
      return this.requiredAriaProperties;
    }
  }

  public static class ElementRoles {
    private final Element name;
    private Set<AriaRole> roles;

    public ElementRoles(Element name) {
      this.name = name;
      this.roles = Set.of();
    }

    public Element getName() {
      return name;
    }

    public ElementRoles setRoles(AriaRole... values) {
      this.roles = Set.of(values);
      return this;
    }

    public boolean roleIsAllowed(AriaRole name) {
      return roles.contains(name);
    }
  }

  private Aria() {
    // Utility class
  }

  // from https://github.com/jsx-eslint/eslint-plugin-jsx-a11y/blob/main/src/util/implicitRoles/index.js
  public static AriaRole getImplicitRole(TagNode element) {
    switch (element.getNodeName().toLowerCase(Locale.ROOT)) {
      case "a", "area", "link":
        if (element.getAttribute("href") != null) {
          return AriaRole.LINK;
        }
        return null;
      case "article":
        return AriaRole.ARTICLE;
      case "aside":
        return AriaRole.COMPLEMENTARY;
      case "body":
        return AriaRole.DOCUMENT;
      case "button":
        return AriaRole.BUTTON;
      case "datalist", "select":
        return AriaRole.LISTBOX;
      case "details":
        return AriaRole.GROUP;
      case "dialog":
        return AriaRole.DIALOG;
      case "form":
        return AriaRole.FORM;
      case "h1", "h2", "h3", "h4", "h5", "h6":
        return AriaRole.HEADING;
      case "hr":
        return AriaRole.SEPARATOR;
      case "img":
        var alt = element.getAttribute("href");
        if (alt != null && alt.equalsIgnoreCase("")) {
          return null;
        }
        return AriaRole.IMG;
      case "input":
        var inputType = element.getAttribute("type");
        if (inputType != null) {
          return switch (inputType.toLowerCase(Locale.ROOT)) {
            case "button", "image", "reset", "submit" -> AriaRole.BUTTON;
            case "checkbox" -> AriaRole.CHECKBOX;
            case "radio" -> AriaRole.RADIO;
            case "range" -> AriaRole.SLIDER;
            default -> AriaRole.TEXTBOX;
          };
        }
        return AriaRole.TEXTBOX;
      case "li":
        return AriaRole.LISTITEM;
      case "menu":
        var menuType = element.getAttribute("type");
        if (menuType != null && menuType.equalsIgnoreCase("toolbar")) {
          return AriaRole.TOOLBAR;
        }
        return null;
      case "menuitem":
        var type = element.getAttribute("type");
        if (type != null) {
          if (type.equalsIgnoreCase("command")) {
            return AriaRole.MENUITEM;
          } else if (type.equalsIgnoreCase("checkbox")) {
            return AriaRole.MENUITEMCHECKBOX;
          } else if (type.equalsIgnoreCase("radio")) {
            return AriaRole.MENUITEMRADIO;
          }
        }
        return null;
      case "meter", "progress":
        return AriaRole.PROGRESSBAR;
      case "nav":
        return AriaRole.NAVIGATION;
      case "ol", "ul":
        return AriaRole.LIST;
      case "option":
        return AriaRole.OPTION;
      case "output":
        return AriaRole.STATUS;
      case "section":
        return AriaRole.REGION;
      case "tbody", "tfoot", "thead":
        return AriaRole.ROWGROUP;
      case "textarea":
        return AriaRole.TEXTBOX;
      default:
        return null;
    }
  }
}
