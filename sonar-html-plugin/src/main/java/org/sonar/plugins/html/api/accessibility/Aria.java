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

  protected static final Map<Property, AriaProperty> ARIA_PROPERTIES =
    new HashMap<>();
  protected static final Map<Role, RoleProperties> ROLES = new HashMap<>();
  protected static final Map<Element, ElementRoles> ELEMENTS = new HashMap<>();

  static {
    ARIA_PROPERTIES.put(
      Property.ACTIVEDESCENDANT,
      new AriaProperty(Property.ACTIVEDESCENDANT, AriaPropertyType.ID)
    );
    ARIA_PROPERTIES.put(
      Property.ATOMIC,
      new AriaProperty(Property.ATOMIC, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      Property.AUTOCOMPLETE,
      new AriaProperty(
        Property.AUTOCOMPLETE,
        AriaPropertyType.TOKEN,
        "inline",
        "list",
        "both",
        "none"
      )
    );
    ARIA_PROPERTIES.put(
      Property.BRAILLELABEL,
      new AriaProperty(Property.BRAILLELABEL, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      Property.BRAILLEROLEDESCRIPTION,
      new AriaProperty(Property.BRAILLEROLEDESCRIPTION, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      Property.BUSY,
      new AriaProperty(Property.BUSY, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      Property.CHECKED,
      new AriaProperty(Property.CHECKED, AriaPropertyType.TRISTATE)
    );
    ARIA_PROPERTIES.put(
      Property.COLCOUNT,
      new AriaProperty(Property.COLCOUNT, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      Property.COLINDEX,
      new AriaProperty(Property.COLINDEX, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      Property.COLSPAN,
      new AriaProperty(Property.COLSPAN, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      Property.CONTROLS,
      new AriaProperty(Property.CONTROLS, AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      Property.CURRENT,
      new AriaProperty(
        Property.CURRENT,
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
      Property.DESCRIBEDBY,
      new AriaProperty(Property.DESCRIBEDBY, AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      Property.DESCRIPTION,
      new AriaProperty(Property.DESCRIPTION, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      Property.DETAILS,
      new AriaProperty(Property.DETAILS, AriaPropertyType.ID)
    );
    ARIA_PROPERTIES.put(
      Property.DISABLED,
      new AriaProperty(Property.DISABLED, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      Property.DROPEFFECT,
      new AriaProperty(
        Property.DROPEFFECT,
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
      Property.ERRORMESSAGE,
      new AriaProperty(Property.ERRORMESSAGE, AriaPropertyType.ID)
    );
    ARIA_PROPERTIES.put(
      Property.EXPANDED,
      new AriaProperty(
        Property.EXPANDED,
        AriaPropertyType.BOOLEAN,
        true,
        "true",
        "false"
      )
    );
    ARIA_PROPERTIES.put(
      Property.FLOWTO,
      new AriaProperty(Property.FLOWTO, AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      Property.GRABBED,
      new AriaProperty(
        Property.GRABBED,
        AriaPropertyType.BOOLEAN,
        true,
        "true",
        "false",
        "undefined"
      )
    );
    ARIA_PROPERTIES.put(
      Property.HASPOPUP,
      new AriaProperty(
        Property.HASPOPUP,
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
      Property.HIDDEN,
      new AriaProperty(
        Property.HIDDEN,
        AriaPropertyType.BOOLEAN,
        true,
        "true",
        "false"
      )
    );
    ARIA_PROPERTIES.put(
      Property.INVALID,
      new AriaProperty(
        Property.INVALID,
        AriaPropertyType.TOKEN,
        "true",
        "false",
        "grammar",
        "spelling"
      )
    );
    ARIA_PROPERTIES.put(
      Property.KEYSHORTCUTS,
      new AriaProperty(Property.KEYSHORTCUTS, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      Property.LABEL,
      new AriaProperty(Property.LABEL, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      Property.LABELLEDBY,
      new AriaProperty(Property.LABELLEDBY, AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      Property.LEVEL,
      new AriaProperty(Property.LEVEL, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      Property.LIVE,
      new AriaProperty(
        Property.LIVE,
        AriaPropertyType.TOKEN,
        "off",
        "assertive",
        "polite"
      )
    );
    ARIA_PROPERTIES.put(
      Property.MODAL,
      new AriaProperty(Property.MODAL, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      Property.MULTILINE,
      new AriaProperty(Property.MULTILINE, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      Property.MULTISELECTABLE,
      new AriaProperty(Property.MULTISELECTABLE, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      Property.ORIENTATION,
      new AriaProperty(
        Property.ORIENTATION,
        AriaPropertyType.TOKEN,
        "horizontal",
        "vertical",
        "undefined"
      )
    );
    ARIA_PROPERTIES.put(
      Property.OWNS,
      new AriaProperty(Property.OWNS, AriaPropertyType.IDLIST)
    );
    ARIA_PROPERTIES.put(
      Property.PLACEHOLDER,
      new AriaProperty(Property.PLACEHOLDER, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      Property.POSINSET,
      new AriaProperty(Property.POSINSET, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      Property.PRESSED,
      new AriaProperty(Property.PRESSED, AriaPropertyType.TRISTATE)
    );
    ARIA_PROPERTIES.put(
      Property.READONLY,
      new AriaProperty(Property.READONLY, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      Property.RELEVANT,
      new AriaProperty(
        Property.RELEVANT,
        AriaPropertyType.TOKENLIST,
        "additions",
        "removals",
        "text",
        "all"
      )
    );
    ARIA_PROPERTIES.put(
      Property.REQUIRED,
      new AriaProperty(Property.REQUIRED, AriaPropertyType.BOOLEAN)
    );
    ARIA_PROPERTIES.put(
      Property.ROLEDESCRIPTION,
      new AriaProperty(Property.ROLEDESCRIPTION, AriaPropertyType.STRING)
    );
    ARIA_PROPERTIES.put(
      Property.ROWCOUNT,
      new AriaProperty(Property.ROWCOUNT, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      Property.ROWINDEX,
      new AriaProperty(Property.ROWINDEX, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      Property.ROWSPAN,
      new AriaProperty(Property.ROWSPAN, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      Property.SELECTED,
      new AriaProperty(
        Property.SELECTED,
        AriaPropertyType.BOOLEAN,
        true,
        "true",
        "false"
      )
    );
    ARIA_PROPERTIES.put(
      Property.SETSIZE,
      new AriaProperty(Property.SETSIZE, AriaPropertyType.INTEGER)
    );
    ARIA_PROPERTIES.put(
      Property.SORT,
      new AriaProperty(
        Property.SORT,
        AriaPropertyType.TOKEN,
        "ascending",
        "descending",
        "none",
        "other"
      )
    );
    ARIA_PROPERTIES.put(
      Property.VALUEMAX,
      new AriaProperty(Property.VALUEMAX, AriaPropertyType.NUMBER)
    );
    ARIA_PROPERTIES.put(
      Property.VALUEMIN,
      new AriaProperty(Property.VALUEMIN, AriaPropertyType.NUMBER)
    );
    ARIA_PROPERTIES.put(
      Property.VALUENOW,
      new AriaProperty(Property.VALUENOW, AriaPropertyType.NUMBER)
    );
    ARIA_PROPERTIES.put(
      Property.VALUETEXT,
      new AriaProperty(Property.VALUETEXT, AriaPropertyType.STRING)
    );

    ROLES.put(Role.COMMAND,
      new RoleProperties(Role.COMMAND)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.COMPOSITE,
      new RoleProperties(Role.COMPOSITE)
        .setProperties(
          Property.ACTIVEDESCENDANT,
          Property.DISABLED,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.INPUT,
      new RoleProperties(Role.INPUT)
        .setProperties(
          Property.DISABLED,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.LANDMARK,
      new RoleProperties(Role.LANDMARK)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.RANGE,
      new RoleProperties(Role.RANGE)
        .setProperties(
          Property.VALUEMAX,
          Property.VALUEMIN,
          Property.VALUENOW,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.ROLETYPE,
      new RoleProperties(Role.ROLETYPE)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.SECTION,
      new RoleProperties(Role.SECTION)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.SECTIONHEAD,
      new RoleProperties(Role.SECTIONHEAD)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.SELECT,
      new RoleProperties(Role.SELECT)
        .setProperties(
          Property.ORIENTATION,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.ACTIVEDESCENDANT,
          Property.DISABLED
        )
    );
    ROLES.put(Role.STRUCTURE,
      new RoleProperties(Role.STRUCTURE)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.WIDGET,
      new RoleProperties(Role.WIDGET)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.WINDOW,
      new RoleProperties(Role.WINDOW)
        .setProperties(
          Property.MODAL,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.ALERT,
      new RoleProperties(Role.ALERT)
        .setProperties(
          Property.ATOMIC,
          Property.LIVE,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.ALERTDIALOG,
      new RoleProperties(Role.ALERTDIALOG)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.MODAL
        )
    );
    ROLES.put(Role.APPLICATION,
      new RoleProperties(Role.APPLICATION)
        .setProperties(
          Property.ACTIVEDESCENDANT,
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.ARTICLE,
      new RoleProperties(Role.ARTICLE)
        .setProperties(
          Property.POSINSET,
          Property.SETSIZE,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.BANNER,
      new RoleProperties(Role.BANNER)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.BLOCKQUOTE,
      new RoleProperties(Role.BLOCKQUOTE)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.BUTTON,
      new RoleProperties(Role.BUTTON)
        .setProperties(
          Property.DISABLED,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.PRESSED,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.CAPTION,
      new RoleProperties(Role.CAPTION)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.CELL,
      new RoleProperties(Role.CELL)
        .setProperties(
          Property.COLINDEX,
          Property.COLSPAN,
          Property.ROWINDEX,
          Property.ROWSPAN,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.CHECKBOX,
      new RoleProperties(Role.CHECKBOX)
        .setProperties(
          Property.CHECKED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.INVALID,
          Property.READONLY,
          Property.REQUIRED,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED
        )
    );
    ROLES.put(Role.CODE,
      new RoleProperties(Role.CODE)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.COLUMNHEADER,
      new RoleProperties(Role.COLUMNHEADER)
        .setProperties(
          Property.SORT,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.COLINDEX,
          Property.COLSPAN,
          Property.ROWINDEX,
          Property.ROWSPAN,
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.READONLY,
          Property.REQUIRED,
          Property.SELECTED
        )
    );
    ROLES.put(Role.COMBOBOX,
      new RoleProperties(Role.COMBOBOX)
        .setProperties(
          Property.ACTIVEDESCENDANT,
          Property.AUTOCOMPLETE,
          Property.ERRORMESSAGE,
          Property.INVALID,
          Property.READONLY,
          Property.REQUIRED,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED
        )
    );
    ROLES.put(Role.COMPLEMENTARY,
      new RoleProperties(Role.COMPLEMENTARY)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.CONTENTINFO,
      new RoleProperties(Role.CONTENTINFO)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DEFINITION,
      new RoleProperties(Role.DEFINITION)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DELETION,
      new RoleProperties(Role.DELETION)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DIALOG,
      new RoleProperties(Role.DIALOG)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.MODAL
        )
    );
    ROLES.put(Role.DIRECTORY,
      new RoleProperties(Role.DIRECTORY)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOCUMENT,
      new RoleProperties(Role.DOCUMENT)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.EMPHASIS,
      new RoleProperties(Role.EMPHASIS)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.FEED,
      new RoleProperties(Role.FEED)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.FIGURE,
      new RoleProperties(Role.FIGURE)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.FORM,
      new RoleProperties(Role.FORM)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.GENERIC,
      new RoleProperties(Role.GENERIC)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.GRID,
      new RoleProperties(Role.GRID)
        .setProperties(
          Property.MULTISELECTABLE,
          Property.READONLY,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.ACTIVEDESCENDANT,
          Property.DISABLED,
          Property.COLCOUNT,
          Property.ROWCOUNT
        )
    );
    ROLES.put(Role.GRIDCELL,
      new RoleProperties(Role.GRIDCELL)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.READONLY,
          Property.REQUIRED,
          Property.SELECTED,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.COLINDEX,
          Property.COLSPAN,
          Property.ROWINDEX,
          Property.ROWSPAN
        )
    );
    ROLES.put(Role.GROUP,
      new RoleProperties(Role.GROUP)
        .setProperties(
          Property.ACTIVEDESCENDANT,
          Property.DISABLED,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.HEADING,
      new RoleProperties(Role.HEADING)
        .setProperties(
          Property.LEVEL,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.IMG,
      new RoleProperties(Role.IMG)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.INSERTION,
      new RoleProperties(Role.INSERTION)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.LINK,
      new RoleProperties(Role.LINK)
        .setProperties(
          Property.DISABLED,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.LIST,
      new RoleProperties(Role.LIST)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.LISTBOX,
      new RoleProperties(Role.LISTBOX)
        .setProperties(
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.INVALID,
          Property.MULTISELECTABLE,
          Property.READONLY,
          Property.REQUIRED,
          Property.ORIENTATION,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.ACTIVEDESCENDANT,
          Property.DISABLED
        )
    );
    ROLES.put(Role.LISTITEM,
      new RoleProperties(Role.LISTITEM)
        .setProperties(
          Property.LEVEL,
          Property.POSINSET,
          Property.SETSIZE,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.LOG,
      new RoleProperties(Role.LOG)
        .setProperties(
          Property.LIVE,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.MAIN,
      new RoleProperties(Role.MAIN)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.MARK,
      new RoleProperties(Role.MARK)
        .setProperties(
          Property.BRAILLELABEL,
          Property.BRAILLEROLEDESCRIPTION,
          Property.DESCRIPTION,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.MARQUEE,
      new RoleProperties(Role.MARQUEE)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.MATH,
      new RoleProperties(Role.MATH)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.MENU,
      new RoleProperties(Role.MENU)
        .setProperties(
          Property.ORIENTATION,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.ACTIVEDESCENDANT,
          Property.DISABLED
        )
    );
    ROLES.put(Role.MENUBAR,
      new RoleProperties(Role.MENUBAR)
        .setProperties(
          Property.ORIENTATION,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.ACTIVEDESCENDANT,
          Property.DISABLED
        )
    );
    ROLES.put(Role.MENUITEM,
      new RoleProperties(Role.MENUITEM)
        .setProperties(
          Property.DISABLED,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.POSINSET,
          Property.SETSIZE,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.MENUITEMCHECKBOX,
      new RoleProperties(Role.MENUITEMCHECKBOX)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED,
          Property.CHECKED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.INVALID,
          Property.READONLY,
          Property.REQUIRED,
          Property.HASPOPUP,
          Property.POSINSET,
          Property.SETSIZE
        )
    );
    ROLES.put(Role.MENUITEMRADIO,
      new RoleProperties(Role.MENUITEMRADIO)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED,
          Property.CHECKED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.INVALID,
          Property.READONLY,
          Property.REQUIRED,
          Property.HASPOPUP,
          Property.POSINSET,
          Property.SETSIZE
        )
    );
    ROLES.put(Role.METER,
      new RoleProperties(Role.METER)
        .setProperties(
          Property.VALUETEXT,
          Property.VALUEMAX,
          Property.VALUEMIN,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.VALUENOW
        )
    );
    ROLES.put(Role.NAVIGATION,
      new RoleProperties(Role.NAVIGATION)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.NOTE,
      new RoleProperties(Role.NOTE)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.OPTION,
      new RoleProperties(Role.OPTION)
        .setProperties(
          Property.CHECKED,
          Property.POSINSET,
          Property.SETSIZE,
          Property.SELECTED,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED
        )
    );
    ROLES.put(Role.PARAGRAPH,
      new RoleProperties(Role.PARAGRAPH)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.PRESENTATION,
      new RoleProperties(Role.PRESENTATION)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.PROGRESSBAR,
      new RoleProperties(Role.PROGRESSBAR)
        .setProperties(
          Property.VALUETEXT,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.VALUEMAX,
          Property.VALUEMIN,
          Property.VALUENOW
        )
    );
    ROLES.put(Role.RADIO,
      new RoleProperties(Role.RADIO)
        .setProperties(
          Property.CHECKED,
          Property.POSINSET,
          Property.SETSIZE,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED
        )
    );
    ROLES.put(Role.RADIOGROUP,
      new RoleProperties(Role.RADIOGROUP)
        .setProperties(
          Property.ERRORMESSAGE,
          Property.INVALID,
          Property.READONLY,
          Property.REQUIRED,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.ACTIVEDESCENDANT,
          Property.DISABLED,
          Property.ORIENTATION
        )
    );
    ROLES.put(Role.REGION,
      new RoleProperties(Role.REGION)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.ROW,
      new RoleProperties(Role.ROW)
        .setProperties(
          Property.COLINDEX,
          Property.EXPANDED,
          Property.LEVEL,
          Property.POSINSET,
          Property.ROWINDEX,
          Property.SELECTED,
          Property.SETSIZE,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.ACTIVEDESCENDANT,
          Property.DISABLED
        )
    );
    ROLES.put(Role.ROWGROUP,
      new RoleProperties(Role.ROWGROUP)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.ROWHEADER,
      new RoleProperties(Role.ROWHEADER)
        .setProperties(
          Property.SORT,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.COLINDEX,
          Property.COLSPAN,
          Property.ROWINDEX,
          Property.ROWSPAN,
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.READONLY,
          Property.REQUIRED,
          Property.SELECTED
        )
    );
    ROLES.put(Role.SCROLLBAR,
      new RoleProperties(Role.SCROLLBAR)
        .setProperties(
          Property.DISABLED,
          Property.VALUETEXT,
          Property.ORIENTATION,
          Property.VALUEMAX,
          Property.VALUEMIN,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.VALUENOW
        )
    );
    ROLES.put(Role.SEARCH,
      new RoleProperties(Role.SEARCH)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.SEARCHBOX,
      new RoleProperties(Role.SEARCHBOX)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED,
          Property.ACTIVEDESCENDANT,
          Property.AUTOCOMPLETE,
          Property.ERRORMESSAGE,
          Property.HASPOPUP,
          Property.INVALID,
          Property.MULTILINE,
          Property.PLACEHOLDER,
          Property.READONLY,
          Property.REQUIRED
        )
    );
    ROLES.put(Role.SEPARATOR,
      new RoleProperties(Role.SEPARATOR)
        .setProperties(
          Property.DISABLED,
          Property.ORIENTATION,
          Property.VALUEMAX,
          Property.VALUEMIN,
          Property.VALUENOW,
          Property.VALUETEXT,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.SLIDER,
      new RoleProperties(Role.SLIDER)
        .setProperties(
          Property.ERRORMESSAGE,
          Property.HASPOPUP,
          Property.INVALID,
          Property.READONLY,
          Property.VALUETEXT,
          Property.ORIENTATION,
          Property.VALUEMAX,
          Property.VALUEMIN,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED,
          Property.VALUENOW
        )
    );
    ROLES.put(Role.SPINBUTTON,
      new RoleProperties(Role.SPINBUTTON)
        .setProperties(
          Property.ERRORMESSAGE,
          Property.INVALID,
          Property.READONLY,
          Property.REQUIRED,
          Property.VALUETEXT,
          Property.VALUENOW,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.ACTIVEDESCENDANT,
          Property.DISABLED,
          Property.VALUEMAX,
          Property.VALUEMIN
        )
    );
    ROLES.put(Role.STATUS,
      new RoleProperties(Role.STATUS)
        .setProperties(
          Property.ATOMIC,
          Property.LIVE,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.STRONG,
      new RoleProperties(Role.STRONG)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.SUBSCRIPT,
      new RoleProperties(Role.SUBSCRIPT)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.SUPERSCRIPT,
      new RoleProperties(Role.SUPERSCRIPT)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.SWITCH,
      new RoleProperties(Role.SWITCH)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED,
          Property.CHECKED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.INVALID,
          Property.READONLY,
          Property.REQUIRED
        )
    );
    ROLES.put(Role.TAB,
      new RoleProperties(Role.TAB)
        .setProperties(
          Property.DISABLED,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.POSINSET,
          Property.SETSIZE,
          Property.SELECTED,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.TABLE,
      new RoleProperties(Role.TABLE)
        .setProperties(
          Property.COLCOUNT,
          Property.ROWCOUNT,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.TABLIST,
      new RoleProperties(Role.TABLIST)
        .setProperties(
          Property.LEVEL,
          Property.MULTISELECTABLE,
          Property.ORIENTATION,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.ACTIVEDESCENDANT,
          Property.DISABLED
        )
    );
    ROLES.put(Role.TABPANEL,
      new RoleProperties(Role.TABPANEL)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.TERM,
      new RoleProperties(Role.TERM)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.TEXTBOX,
      new RoleProperties(Role.TEXTBOX)
        .setProperties(
          Property.ACTIVEDESCENDANT,
          Property.AUTOCOMPLETE,
          Property.ERRORMESSAGE,
          Property.HASPOPUP,
          Property.INVALID,
          Property.MULTILINE,
          Property.PLACEHOLDER,
          Property.READONLY,
          Property.REQUIRED,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED
        )
    );
    ROLES.put(Role.TIME,
      new RoleProperties(Role.TIME)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.TIMER,
      new RoleProperties(Role.TIMER)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.TOOLBAR,
      new RoleProperties(Role.TOOLBAR)
        .setProperties(
          Property.ORIENTATION,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.ACTIVEDESCENDANT,
          Property.DISABLED
        )
    );
    ROLES.put(Role.TOOLTIP,
      new RoleProperties(Role.TOOLTIP)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.TREE,
      new RoleProperties(Role.TREE)
        .setProperties(
          Property.ERRORMESSAGE,
          Property.INVALID,
          Property.MULTISELECTABLE,
          Property.REQUIRED,
          Property.ORIENTATION,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.ACTIVEDESCENDANT,
          Property.DISABLED
        )
    );
    ROLES.put(Role.TREEGRID,
      new RoleProperties(Role.TREEGRID)
        .setProperties(
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.ACTIVEDESCENDANT,
          Property.DISABLED,
          Property.MULTISELECTABLE,
          Property.READONLY,
          Property.COLCOUNT,
          Property.ROWCOUNT,
          Property.ORIENTATION,
          Property.ERRORMESSAGE,
          Property.INVALID,
          Property.REQUIRED
        )
    );
    ROLES.put(Role.TREEITEM,
      new RoleProperties(Role.TREEITEM)
        .setProperties(
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.LEVEL,
          Property.POSINSET,
          Property.SETSIZE,
          Property.DISABLED,
          Property.CHECKED,
          Property.SELECTED
        )
    );
    ROLES.put(Role.DOC_ABSTRACT,
      new RoleProperties(Role.DOC_ABSTRACT)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_ACKNOWLEDGMENTS,
      new RoleProperties(Role.DOC_ACKNOWLEDGMENTS)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
      ROLES.put(Role.DOC_AFTERWORD,
      new RoleProperties(Role.DOC_AFTERWORD)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_APPENDIX,
      new RoleProperties(Role.DOC_APPENDIX)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_BACKLINK,
      new RoleProperties(Role.DOC_BACKLINK)
        .setProperties(
          Property.ERRORMESSAGE,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED,
          Property.EXPANDED,
          Property.HASPOPUP
        )
    );
    ROLES.put(Role.DOC_BIBLIOENTRY,
      new RoleProperties(Role.DOC_BIBLIOENTRY)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.LEVEL,
          Property.POSINSET,
          Property.SETSIZE
        )
    );
    ROLES.put(Role.DOC_BIBLIOGRAPHY,
      new RoleProperties(Role.DOC_BIBLIOGRAPHY)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_BIBLIOREF,
      new RoleProperties(Role.DOC_BIBLIOREF)
        .setProperties(
          Property.ERRORMESSAGE,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED,
          Property.EXPANDED,
          Property.HASPOPUP
        )
    );
    ROLES.put(Role.DOC_CHAPTER,
      new RoleProperties(Role.DOC_CHAPTER)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_COLOPHON,
      new RoleProperties(Role.DOC_COLOPHON)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_CONCLUSION,
      new RoleProperties(Role.DOC_CONCLUSION)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_COVER,
      new RoleProperties(Role.DOC_COVER)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_CREDIT,
      new RoleProperties(Role.DOC_CREDIT)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_CREDITS,
      new RoleProperties(Role.DOC_CREDITS)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_DEDICATION,
      new RoleProperties(Role.DOC_DEDICATION)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_ENDNOTE,
      new RoleProperties(Role.DOC_ENDNOTE)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.LEVEL,
          Property.POSINSET,
          Property.SETSIZE
        )
    );
    ROLES.put(Role.DOC_ENDNOTES,
      new RoleProperties(Role.DOC_ENDNOTES)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_EPIGRAPH,
      new RoleProperties(Role.DOC_EPIGRAPH)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_EPILOGUE,
      new RoleProperties(Role.DOC_EPILOGUE)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_ERRATA,
      new RoleProperties(Role.DOC_ERRATA)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_EXAMPLE,
      new RoleProperties(Role.DOC_EXAMPLE)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_FOOTNOTE,
      new RoleProperties(Role.DOC_FOOTNOTE)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_FOREWORD,
      new RoleProperties(Role.DOC_FOREWORD)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_GLOSSARY,
      new RoleProperties(Role.DOC_GLOSSARY)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_GLOSSREF,
      new RoleProperties(Role.DOC_GLOSSREF)
        .setProperties(
          Property.ERRORMESSAGE,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED,
          Property.EXPANDED,
          Property.HASPOPUP
        )
    );
    ROLES.put(Role.DOC_INDEX,
      new RoleProperties(Role.DOC_INDEX)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_INTRODUCTION,
      new RoleProperties(Role.DOC_INTRODUCTION)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_NOTEREF,
      new RoleProperties(Role.DOC_NOTEREF)
        .setProperties(
          Property.ERRORMESSAGE,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED,
          Property.EXPANDED,
          Property.HASPOPUP
        )
    );
    ROLES.put(Role.DOC_NOTICE,
      new RoleProperties(Role.DOC_NOTICE)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_PAGEBREAK,
      new RoleProperties(Role.DOC_PAGEBREAK)
        .setProperties(
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.DISABLED,
          Property.ORIENTATION,
          Property.VALUEMAX,
          Property.VALUEMIN,
          Property.VALUENOW,
          Property.VALUETEXT
        )
    );
    ROLES.put(Role.DOC_PAGELIST,
      new RoleProperties(Role.DOC_PAGELIST)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_PART,
      new RoleProperties(Role.DOC_PART)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_PREFACE,
      new RoleProperties(Role.DOC_PREFACE)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_PROLOGUE,
      new RoleProperties(Role.DOC_PROLOGUE)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_QNA,
      new RoleProperties(Role.DOC_QNA)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_SUBTITLE,
      new RoleProperties(Role.DOC_SUBTITLE)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_TIP,
      new RoleProperties(Role.DOC_TIP)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.DOC_TOC,
      new RoleProperties(Role.DOC_TOC)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.GRAPHICS_DOCUMENT,
      new RoleProperties(Role.GRAPHICS_DOCUMENT)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ROLES.put(Role.GRAPHICS_OBJECT,
      new RoleProperties(Role.GRAPHICS_OBJECT)
        .setProperties(
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION,
          Property.ACTIVEDESCENDANT,
          Property.DISABLED
        )
    );
    ROLES.put(Role.GRAPHICS_SYMBOL,
      new RoleProperties(Role.GRAPHICS_SYMBOL)
        .setProperties(
          Property.DISABLED,
          Property.ERRORMESSAGE,
          Property.EXPANDED,
          Property.HASPOPUP,
          Property.INVALID,
          Property.ATOMIC,
          Property.BUSY,
          Property.CONTROLS,
          Property.CURRENT,
          Property.DESCRIBEDBY,
          Property.DETAILS,
          Property.DROPEFFECT,
          Property.FLOWTO,
          Property.GRABBED,
          Property.HIDDEN,
          Property.KEYSHORTCUTS,
          Property.LABEL,
          Property.LABELLEDBY,
          Property.LIVE,
          Property.OWNS,
          Property.RELEVANT,
          Property.ROLEDESCRIPTION
        )
    );
    ELEMENTS.put(Element.ARTICLE, new ElementRoles(Element.ARTICLE).setRoles(Role.ARTICLE));
    ELEMENTS.put(Element.HEADER, new ElementRoles(Element.HEADER).setRoles(Role.BANNER,Role.GENERIC));
    ELEMENTS.put(Element.BLOCKQUOTE, new ElementRoles(Element.BLOCKQUOTE).setRoles(Role.BLOCKQUOTE));
    ELEMENTS.put(Element.INPUT,
      new ElementRoles(Element.INPUT).setRoles(Role.BUTTON,Role.CHECKBOX,Role.COMBOBOX,Role.RADIO,Role.SEARCHBOX,Role.SLIDER,Role.SPINBUTTON,Role.TEXTBOX));
    ELEMENTS.put(Element.BUTTON, new ElementRoles(Element.BUTTON).setRoles(Role.BUTTON));
    ELEMENTS.put(Element.CAPTION, new ElementRoles(Element.CAPTION).setRoles(Role.CAPTION));
    ELEMENTS.put(Element.TD, new ElementRoles(Element.TD).setRoles(Role.CELL,Role.GRIDCELL));
    ELEMENTS.put(Element.CODE, new ElementRoles(Element.CODE).setRoles(Role.CODE));
    ELEMENTS.put(Element.TH, new ElementRoles(Element.TH).setRoles(Role.COLUMNHEADER,Role.ROWHEADER));
    ELEMENTS.put(Element.SELECT, new ElementRoles(Element.SELECT).setRoles(Role.COMBOBOX, Role.LISTBOX));
    ELEMENTS.put(Element.ASIDE, new ElementRoles(Element.ASIDE).setRoles(Role.GENERIC, Role.COMPLEMENTARY));
    ELEMENTS.put(Element.FOOTER, new ElementRoles(Element.FOOTER).setRoles(Role.GENERIC, Role.CONTENTINFO));
    ELEMENTS.put(Element.DD, new ElementRoles(Element.DD).setRoles(Role.DEFINITION));
    ELEMENTS.put(Element.DEL, new ElementRoles(Element.DEL).setRoles(Role.DELETION));
    ELEMENTS.put(Element.DIALOG, new ElementRoles(Element.DIALOG).setRoles(Role.DIALOG));
    ELEMENTS.put(Element.HTML, new ElementRoles(Element.HTML).setRoles(Role.DOCUMENT));
    ELEMENTS.put(Element.EM, new ElementRoles(Element.EM).setRoles(Role.EMPHASIS));
    ELEMENTS.put(Element.FIGURE, new ElementRoles(Element.FIGURE).setRoles(Role.FIGURE));
    ELEMENTS.put(Element.FORM, new ElementRoles(Element.FORM).setRoles(Role.FORM));
    ELEMENTS.put(Element.A, new ElementRoles(Element.A).setRoles(Role.GENERIC, Role.LINK));
    ELEMENTS.put(Element.AREA, new ElementRoles(Element.AREA).setRoles(Role.GENERIC, Role.LINK));
    ELEMENTS.put(Element.B, new ElementRoles(Element.B).setRoles(Role.GENERIC));
    ELEMENTS.put(Element.BDO, new ElementRoles(Element.BDO).setRoles(Role.GENERIC));
    ELEMENTS.put(Element.BODY, new ElementRoles(Element.BODY).setRoles(Role.GENERIC));
    ELEMENTS.put(Element.DATA, new ElementRoles(Element.DATA).setRoles(Role.GENERIC));
    ELEMENTS.put(Element.DIV, new ElementRoles(Element.DIV).setRoles(Role.GENERIC));
    ELEMENTS.put(Element.HGROUP, new ElementRoles(Element.HGROUP).setRoles(Role.GENERIC));
    ELEMENTS.put(Element.I, new ElementRoles(Element.I).setRoles(Role.GENERIC));
    ELEMENTS.put(Element.PRE, new ElementRoles(Element.PRE).setRoles(Role.GENERIC));
    ELEMENTS.put(Element.Q, new ElementRoles(Element.Q).setRoles(Role.GENERIC));
    ELEMENTS.put(Element.SAMP, new ElementRoles(Element.SAMP).setRoles(Role.GENERIC));
    ELEMENTS.put(Element.SECTION, new ElementRoles(Element.SECTION).setRoles(Role.GENERIC, Role.REGION));
    ELEMENTS.put(Element.SMALL, new ElementRoles(Element.SMALL).setRoles(Role.GENERIC));
    ELEMENTS.put(Element.SPAN, new ElementRoles(Element.SPAN).setRoles(Role.GENERIC));
    ELEMENTS.put(Element.U, new ElementRoles(Element.U).setRoles(Role.GENERIC));
    ELEMENTS.put(Element.DETAILS, new ElementRoles(Element.DETAILS).setRoles(Role.GROUP));
    ELEMENTS.put(Element.FIELDSET, new ElementRoles(Element.FIELDSET).setRoles(Role.GROUP));
    ELEMENTS.put(Element.OPTGROUP, new ElementRoles(Element.OPTGROUP).setRoles(Role.GROUP));
    ELEMENTS.put(Element.ADDRESS, new ElementRoles(Element.ADDRESS).setRoles(Role.GROUP));
    ELEMENTS.put(Element.H1, new ElementRoles(Element.H1).setRoles(Role.HEADING));
    ELEMENTS.put(Element.H2, new ElementRoles(Element.H2).setRoles(Role.HEADING));
    ELEMENTS.put(Element.H3, new ElementRoles(Element.H3).setRoles(Role.HEADING));
    ELEMENTS.put(Element.H4, new ElementRoles(Element.H4).setRoles(Role.HEADING));
    ELEMENTS.put(Element.H5, new ElementRoles(Element.H5).setRoles(Role.HEADING));
    ELEMENTS.put(Element.H6, new ElementRoles(Element.H6).setRoles(Role.HEADING));
    ELEMENTS.put(Element.IMG, new ElementRoles(Element.IMG).setRoles(Role.IMG, Role.PRESENTATION));
    ELEMENTS.put(Element.INS, new ElementRoles(Element.INS).setRoles(Role.INSERTION));
    ELEMENTS.put(Element.MENU, new ElementRoles(Element.MENU).setRoles(Role.LIST));
    ELEMENTS.put(Element.OL, new ElementRoles(Element.OL).setRoles(Role.LIST));
    ELEMENTS.put(Element.UL, new ElementRoles(Element.UL).setRoles(Role.LIST));
    ELEMENTS.put(Element.DATALIST, new ElementRoles(Element.DATALIST).setRoles(Role.LISTBOX));
    ELEMENTS.put(Element.LI, new ElementRoles(Element.LI).setRoles(Role.LISTITEM));
    ELEMENTS.put(Element.MAIN, new ElementRoles(Element.MAIN).setRoles(Role.MAIN));
    ELEMENTS.put(Element.MARK, new ElementRoles(Element.MARK).setRoles(Role.MARK));
    ELEMENTS.put(Element.MATH, new ElementRoles(Element.MATH).setRoles(Role.MATH));
    ELEMENTS.put(Element.METER, new ElementRoles(Element.METER).setRoles(Role.METER));
    ELEMENTS.put(Element.NAV, new ElementRoles(Element.NAV).setRoles(Role.NAVIGATION));
    ELEMENTS.put(Element.OPTION, new ElementRoles(Element.OPTION).setRoles(Role.OPTION));
    ELEMENTS.put(Element.P, new ElementRoles(Element.P).setRoles(Role.PARAGRAPH));
    ELEMENTS.put(Element.PROGRESS, new ElementRoles(Element.PROGRESS).setRoles(Role.PROGRESSBAR));
    ELEMENTS.put(Element.TR, new ElementRoles(Element.TR).setRoles(Role.ROW));
    ELEMENTS.put(Element.TBODY, new ElementRoles(Element.TBODY).setRoles(Role.ROWGROUP));
    ELEMENTS.put(Element.TFOOT, new ElementRoles(Element.TFOOT).setRoles(Role.ROWGROUP));
    ELEMENTS.put(Element.THEAD, new ElementRoles(Element.THEAD).setRoles(Role.ROWGROUP));
    ELEMENTS.put(Element.HR, new ElementRoles(Element.HR).setRoles(Role.SEPARATOR));
    ELEMENTS.put(Element.OUTPUT, new ElementRoles(Element.OUTPUT).setRoles(Role.STATUS));
    ELEMENTS.put(Element.STRONG, new ElementRoles(Element.STRONG).setRoles(Role.STRONG));
    ELEMENTS.put(Element.SUB, new ElementRoles(Element.SUB).setRoles(Role.SUBSCRIPT));
    ELEMENTS.put(Element.SUP, new ElementRoles(Element.SUP).setRoles(Role.SUPERSCRIPT));
    ELEMENTS.put(Element.TABLE, new ElementRoles(Element.TABLE).setRoles(Role.TABLE));
    ELEMENTS.put(Element.DFN, new ElementRoles(Element.DFN).setRoles(Role.TERM));
    ELEMENTS.put(Element.DT, new ElementRoles(Element.DT).setRoles(Role.TERM));
    ELEMENTS.put(Element.TEXTAREA, new ElementRoles(Element.TEXTAREA).setRoles(Role.TEXTBOX));
    ELEMENTS.put(Element.TIME, new ElementRoles(Element.TIME).setRoles(Role.TIME));

  }

  public static AriaProperty getProperty(Property name) {
    return ARIA_PROPERTIES.get(name);
  }

  public static RoleProperties getRole(Role name) {
    return ROLES.get(name);
  }

  public static ElementRoles getElement(String name) {
    return ELEMENTS.get(name);
  }

  public static class AriaProperty {

    private final Property name;
    private final AriaPropertyType type;
    private final Optional<Boolean> allowUndefined;
    private final Set<String> values;

    public AriaProperty(Property name, AriaPropertyType type, String... values) {
      this(name, type, false, values);
    }

    public AriaProperty(
      Property name,
      AriaPropertyType type,
      boolean allowUndefined,
      String... values
    ) {
      this.name = name;
      this.type = type;
      this.allowUndefined = Optional.of(allowUndefined);
      this.values = Set.of(values);
    }

    public Property getName() {
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
    private final Role name;
    private Set<Property> ariaProperties;

    public RoleProperties(Role name) {
      this.name = name;
      this.ariaProperties = Set.of();
    }

    public Role getName() {
      return name;
    }

    public RoleProperties setProperties(Property... values) {
      this.ariaProperties = Set.of(values);
      return this;
    }

    public boolean propertyIsAllowed(Property name) {
      return ariaProperties.contains(name);
    }
  }

  public static class ElementRoles {
    private final Element name;
    private Set<Role> roles;

    public ElementRoles(Element name) {
      this.name = name;
      this.roles = Set.of();
    }

    public Element getName() {
      return name;
    }

    public ElementRoles setRoles(Role... values) {
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
        return null;
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
          return null;
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
        return null;
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
        return null;
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
        return null;
    }
  }
}
