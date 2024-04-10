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

import java.util.EnumMap;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.sonar.plugins.html.node.TagNode;

public class Aria {

  protected static final EnumMap<AriaProperty, AriaPropertyValues> ARIA_PROPERTIES =
    new EnumMap<>(AriaProperty.class);
  protected static final EnumMap<AriaRole, RoleProperties> ROLES = new EnumMap<>(AriaRole.class);
  protected static final EnumMap<Element, ElementRoles> ELEMENTS = new EnumMap<>(Element.class);

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
      new RoleProperties(AriaRole.COMMAND)
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
      new RoleProperties(AriaRole.COMPOSITE)
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
      new RoleProperties(AriaRole.INPUT)
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
      new RoleProperties(AriaRole.LANDMARK)
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
      new RoleProperties(AriaRole.RANGE)
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
      new RoleProperties(AriaRole.ROLETYPE)
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
      new RoleProperties(AriaRole.SECTION)
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
      new RoleProperties(AriaRole.SECTIONHEAD)
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
      new RoleProperties(AriaRole.SELECT)
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
      new RoleProperties(AriaRole.STRUCTURE)
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
      new RoleProperties(AriaRole.WIDGET)
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
      new RoleProperties(AriaRole.WINDOW)
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
      new RoleProperties(AriaRole.ALERT)
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
      new RoleProperties(AriaRole.ALERTDIALOG)
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
      new RoleProperties(AriaRole.APPLICATION)
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
      new RoleProperties(AriaRole.ARTICLE)
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
    );
    ROLES.put(AriaRole.BANNER,
      new RoleProperties(AriaRole.BANNER)
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
    ROLES.put(AriaRole.BLOCKQUOTE,
      new RoleProperties(AriaRole.BLOCKQUOTE)
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
    ROLES.put(AriaRole.BUTTON,
      new RoleProperties(AriaRole.BUTTON)
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
    );
    ROLES.put(AriaRole.CAPTION,
      new RoleProperties(AriaRole.CAPTION)
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
    ROLES.put(AriaRole.CELL,
      new RoleProperties(AriaRole.CELL)
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
    );
    ROLES.put(AriaRole.CHECKBOX,
      new RoleProperties(AriaRole.CHECKBOX)
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
    );
    ROLES.put(AriaRole.CODE,
      new RoleProperties(AriaRole.CODE)
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
    ROLES.put(AriaRole.COLUMNHEADER,
      new RoleProperties(AriaRole.COLUMNHEADER)
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
    );
    ROLES.put(AriaRole.COMBOBOX,
      new RoleProperties(AriaRole.COMBOBOX)
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
    );
    ROLES.put(AriaRole.COMPLEMENTARY,
      new RoleProperties(AriaRole.COMPLEMENTARY)
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
    ROLES.put(AriaRole.CONTENTINFO,
      new RoleProperties(AriaRole.CONTENTINFO)
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
    ROLES.put(AriaRole.DEFINITION,
      new RoleProperties(AriaRole.DEFINITION)
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
    ROLES.put(AriaRole.DELETION,
      new RoleProperties(AriaRole.DELETION)
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
    ROLES.put(AriaRole.DIALOG,
      new RoleProperties(AriaRole.DIALOG)
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
    ROLES.put(AriaRole.DIRECTORY,
      new RoleProperties(AriaRole.DIRECTORY)
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
      new RoleProperties(AriaRole.DOCUMENT)
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
    ROLES.put(AriaRole.EMPHASIS,
      new RoleProperties(AriaRole.EMPHASIS)
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
    ROLES.put(AriaRole.FEED,
      new RoleProperties(AriaRole.FEED)
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
      new RoleProperties(AriaRole.FIGURE)
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
    ROLES.put(AriaRole.FORM,
      new RoleProperties(AriaRole.FORM)
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
    ROLES.put(AriaRole.GENERIC,
      new RoleProperties(AriaRole.GENERIC)
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
    ROLES.put(AriaRole.GRID,
      new RoleProperties(AriaRole.GRID)
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
      new RoleProperties(AriaRole.GRIDCELL)
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
    );
    ROLES.put(AriaRole.GROUP,
      new RoleProperties(AriaRole.GROUP)
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
    ROLES.put(AriaRole.HEADING,
      new RoleProperties(AriaRole.HEADING)
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
    );
    ROLES.put(AriaRole.IMG,
      new RoleProperties(AriaRole.IMG)
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
    ROLES.put(AriaRole.INSERTION,
      new RoleProperties(AriaRole.INSERTION)
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
    ROLES.put(AriaRole.LINK,
      new RoleProperties(AriaRole.LINK)
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
    );
    ROLES.put(AriaRole.LIST,
      new RoleProperties(AriaRole.LIST)
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
    ROLES.put(AriaRole.LISTBOX,
      new RoleProperties(AriaRole.LISTBOX)
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
    );
    ROLES.put(AriaRole.LISTITEM,
      new RoleProperties(AriaRole.LISTITEM)
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
    );
    ROLES.put(AriaRole.LOG,
      new RoleProperties(AriaRole.LOG)
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
      new RoleProperties(AriaRole.MAIN)
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
    ROLES.put(AriaRole.MARK,
      new RoleProperties(AriaRole.MARK)
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
    );
    ROLES.put(AriaRole.MARQUEE,
      new RoleProperties(AriaRole.MARQUEE)
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
      new RoleProperties(AriaRole.MATH)
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
    ROLES.put(AriaRole.MENU,
      new RoleProperties(AriaRole.MENU)
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
      new RoleProperties(AriaRole.MENUBAR)
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
      new RoleProperties(AriaRole.MENUITEM)
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
      new RoleProperties(AriaRole.MENUITEMCHECKBOX)
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
    );
    ROLES.put(AriaRole.MENUITEMRADIO,
      new RoleProperties(AriaRole.MENUITEMRADIO)
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
    );
    ROLES.put(AriaRole.METER,
      new RoleProperties(AriaRole.METER)
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
    );
    ROLES.put(AriaRole.NAVIGATION,
      new RoleProperties(AriaRole.NAVIGATION)
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
    ROLES.put(AriaRole.NOTE,
      new RoleProperties(AriaRole.NOTE)
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
      new RoleProperties(AriaRole.OPTION)
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
    );
    ROLES.put(AriaRole.PARAGRAPH,
      new RoleProperties(AriaRole.PARAGRAPH)
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
    ROLES.put(AriaRole.PRESENTATION,
      new RoleProperties(AriaRole.PRESENTATION)
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
    ROLES.put(AriaRole.PROGRESSBAR,
      new RoleProperties(AriaRole.PROGRESSBAR)
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
    );
    ROLES.put(AriaRole.RADIO,
      new RoleProperties(AriaRole.RADIO)
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
    );
    ROLES.put(AriaRole.RADIOGROUP,
      new RoleProperties(AriaRole.RADIOGROUP)
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
      new RoleProperties(AriaRole.REGION)
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
    ROLES.put(AriaRole.ROW,
      new RoleProperties(AriaRole.ROW)
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
    );
    ROLES.put(AriaRole.ROWGROUP,
      new RoleProperties(AriaRole.ROWGROUP)
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
    ROLES.put(AriaRole.ROWHEADER,
      new RoleProperties(AriaRole.ROWHEADER)
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
    );
    ROLES.put(AriaRole.SCROLLBAR,
      new RoleProperties(AriaRole.SCROLLBAR)
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
    );
    ROLES.put(AriaRole.SEARCH,
      new RoleProperties(AriaRole.SEARCH)
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
      new RoleProperties(AriaRole.SEARCHBOX)
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
    );
    ROLES.put(AriaRole.SEPARATOR,
      new RoleProperties(AriaRole.SEPARATOR)
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
    );
    ROLES.put(AriaRole.SLIDER,
      new RoleProperties(AriaRole.SLIDER)
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
    );
    ROLES.put(AriaRole.SPINBUTTON,
      new RoleProperties(AriaRole.SPINBUTTON)
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
    );
    ROLES.put(AriaRole.STATUS,
      new RoleProperties(AriaRole.STATUS)
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
    ROLES.put(AriaRole.STRONG,
      new RoleProperties(AriaRole.STRONG)
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
    ROLES.put(AriaRole.SUBSCRIPT,
      new RoleProperties(AriaRole.SUBSCRIPT)
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
    ROLES.put(AriaRole.SUPERSCRIPT,
      new RoleProperties(AriaRole.SUPERSCRIPT)
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
    ROLES.put(AriaRole.SWITCH,
      new RoleProperties(AriaRole.SWITCH)
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
    );
    ROLES.put(AriaRole.TAB,
      new RoleProperties(AriaRole.TAB)
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
      new RoleProperties(AriaRole.TABLE)
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
    );
    ROLES.put(AriaRole.TABLIST,
      new RoleProperties(AriaRole.TABLIST)
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
      new RoleProperties(AriaRole.TABPANEL)
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
      new RoleProperties(AriaRole.TERM)
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
    ROLES.put(AriaRole.TEXTBOX,
      new RoleProperties(AriaRole.TEXTBOX)
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
    );
    ROLES.put(AriaRole.TIME,
      new RoleProperties(AriaRole.TIME)
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
    ROLES.put(AriaRole.TIMER,
      new RoleProperties(AriaRole.TIMER)
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
      new RoleProperties(AriaRole.TOOLBAR)
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
      new RoleProperties(AriaRole.TOOLTIP)
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
      new RoleProperties(AriaRole.TREE)
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
      new RoleProperties(AriaRole.TREEGRID)
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
      new RoleProperties(AriaRole.TREEITEM)
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
    );
    ROLES.put(AriaRole.DOC_ABSTRACT,
      new RoleProperties(AriaRole.DOC_ABSTRACT)
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
      new RoleProperties(AriaRole.DOC_ACKNOWLEDGMENTS)
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
      new RoleProperties(AriaRole.DOC_AFTERWORD)
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
      new RoleProperties(AriaRole.DOC_APPENDIX)
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
      new RoleProperties(AriaRole.DOC_BACKLINK)
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
      new RoleProperties(AriaRole.DOC_BIBLIOENTRY)
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
      new RoleProperties(AriaRole.DOC_BIBLIOGRAPHY)
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
      new RoleProperties(AriaRole.DOC_BIBLIOREF)
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
      new RoleProperties(AriaRole.DOC_CHAPTER)
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
      new RoleProperties(AriaRole.DOC_COLOPHON)
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
      new RoleProperties(AriaRole.DOC_CONCLUSION)
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
      new RoleProperties(AriaRole.DOC_COVER)
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
      new RoleProperties(AriaRole.DOC_CREDIT)
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
      new RoleProperties(AriaRole.DOC_CREDITS)
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
      new RoleProperties(AriaRole.DOC_DEDICATION)
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
      new RoleProperties(AriaRole.DOC_ENDNOTE)
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
      new RoleProperties(AriaRole.DOC_ENDNOTES)
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
      new RoleProperties(AriaRole.DOC_EPIGRAPH)
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
      new RoleProperties(AriaRole.DOC_EPILOGUE)
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
      new RoleProperties(AriaRole.DOC_ERRATA)
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
      new RoleProperties(AriaRole.DOC_EXAMPLE)
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
      new RoleProperties(AriaRole.DOC_FOOTNOTE)
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
      new RoleProperties(AriaRole.DOC_FOREWORD)
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
      new RoleProperties(AriaRole.DOC_GLOSSARY)
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
      new RoleProperties(AriaRole.DOC_GLOSSREF)
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
      new RoleProperties(AriaRole.DOC_INDEX)
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
      new RoleProperties(AriaRole.DOC_INTRODUCTION)
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
      new RoleProperties(AriaRole.DOC_NOTEREF)
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
      new RoleProperties(AriaRole.DOC_NOTICE)
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
      new RoleProperties(AriaRole.DOC_PAGEBREAK)
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
      new RoleProperties(AriaRole.DOC_PAGELIST)
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
      new RoleProperties(AriaRole.DOC_PART)
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
      new RoleProperties(AriaRole.DOC_PREFACE)
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
      new RoleProperties(AriaRole.DOC_PROLOGUE)
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
      new RoleProperties(AriaRole.DOC_QNA)
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
      new RoleProperties(AriaRole.DOC_SUBTITLE)
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
      new RoleProperties(AriaRole.DOC_TIP)
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
      new RoleProperties(AriaRole.DOC_TOC)
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
      new RoleProperties(AriaRole.GRAPHICS_DOCUMENT)
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
      new RoleProperties(AriaRole.GRAPHICS_OBJECT)
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
      new RoleProperties(AriaRole.GRAPHICS_SYMBOL)
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

  public static RoleProperties getRole(AriaRole name) {
    return ROLES.get(name);
  }

  public static ElementRoles getElement(Element name) {
    return ELEMENTS.get(name);
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

  public static class RoleProperties {
    private final AriaRole name;
    private Set<AriaProperty> ariaProperties;

    public RoleProperties(AriaRole name) {
      this.name = name;
      this.ariaProperties = Set.of();
    }

    public AriaRole getName() {
      return name;
    }

    public RoleProperties setProperties(AriaProperty... values) {
      this.ariaProperties = Set.of(values);
      return this;
    }

    public boolean propertyIsAllowed(AriaProperty name) {
      return ariaProperties.contains(name);
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
