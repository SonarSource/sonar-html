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
package org.sonar.plugins.html.api;

import java.util.List;
import java.util.Set;

public class HtmlConstants {

  /** The language key. */
  public static final String LANGUAGE_KEY = "web";
  public static final String LANGUAGE_NAME = "HTML";

  /** JSP language key. */
  public static final String JSP_LANGUAGE_KEY = "jsp";
  public static final String JSP_LANGUAGE_NAME = "JSP";

  // ================ Plugin properties ================

  public static final String FILE_EXTENSIONS_PROP_KEY = "sonar.html.file.suffixes";
  public static final String FILE_EXTENSIONS_DEF_VALUE = ".html,.xhtml,.cshtml,.vbhtml,.aspx,.ascx,.rhtml,.erb,.shtm,.shtml,.cmp,.twig";
  public static final String JSP_FILE_EXTENSIONS_PROP_KEY = "sonar.jsp.file.suffixes";
  public static final String JSP_FILE_EXTENSIONS_DEF_VALUE = ".jsp,.jspf,.jspx";

  public static final String NAME_INPUT = "input";

  public static final String NAME_TEXTAREA = "textarea";

  public static final String TYPE_HIDDEN = "hidden";

  public static final List<String> KNOWN_HTML_TAGS = List.of(
    "a",
    "acronym", // deprecated
    "area",
    "abbr",
    "address",
    "article",
    "aside",
    "audio",
    "b",
    "base",
    "big", // deprecated
    "blockquote",
    "body",
    "br",
    "button",
    "canvas",
    "caption",
    "center", // deprecated
    "cite",
    "code",
    "col",
    "colgroup",
    "content", // deprecated
    "data",
    "datalist",
    "dd",
    "del",
    "details",
    "dfn",
    "dialog",
    "dir", // deprecated
    "div",
    "dl",
    "dt",
    "em",
    "embed",
    "fieldset",
    "figcaption",
    "figure",
    "footer",
    "font", // deprecated
    "form",
    "frame", // deprecated
    "frameset", // deprecated
    "h1",
    "h2",
    "h3",
    "h4",
    "h5",
    "h6",
    "head",
    "header",
    "hgroup",
    "hr",
    "html",
    "i",
    "iframe",
    "image", // deprecated
    "img",
    NAME_INPUT,
    "ins",
    "kbd",
    "label",
    "legend",
    "li",
    "link",
    "main",
    "map",
    "mark",
    "marquee", // deprecated
    "menu",
    "menuitem", // deprecated
    "meta",
    "meter",
    "nav",
    "nobr", // deprecated
    "noembed", // deprecated
    "noframes", // deprecated
    "noscript",
    "object",
    "ol",
    "optgroup",
    "option",
    "output",
    "p",
    "param", // deprecated
    "picture",
    "plaintext", // deprecated
    "pre",
    "progress",
    "q",
    "rb", // deprecated
    "rp",
    "rt", // deprecated
    "ruby",
    "s",
    "samp",
    "script",
    "search",
    "section",
    "select",
    "shadow", // deprecated
    "small",
    "source",
    "span",
    "strike", // deprecated
    "strong",
    "style",
    "sub",
    "summary",
    "sup",
    "svg",
    "table",
    "tbody",
    "td",
    "template",
    NAME_TEXTAREA,
    "tfoot",
    "th",
    "thead",
    "time",
    "title",
    "tr",
    "track",
    "tt", // deprecated
    "u",
    "ul",
    "var",
    "video",
    "wbr",
    "xmp" // deprecated
  );

  // computed from https://github.com/jsx-eslint/eslint-plugin-jsx-a11y/blob/main/src/util/isNonInteractiveElement.js
  public static final Set<String> NON_INTERACTIVE_ELEMENTS = Set.of("abbr", "address", "article", "aside", "blockquote", "br", "caption", "code", "dd", "del", "details", "dfn",
    "dialog", "dir", "dl", "dt", "em", "fieldset", "figcaption", "figure", "footer", "form", "h1", "h2", "h3", "h4", "h5", "h6", "hr", "html", "iframe", "img", "ins", "label",
    "legend", "li", "main", "mark", "marquee", "menu", "meter", "nav", "ol", "optgroup", "output", "p", "pre", "progress", "ruby", "strong", "sub", "sup", "table", "tbody",
    "tfoot", "thead", "time", "ul");

  // computed as https://github.com/jsx-eslint/eslint-plugin-jsx-a11y/blob/main/src/util/isInteractiveRole.js
  public static final Set<String> INTERACTIVE_ROLES = Set.of("button", "checkbox", "columnheader", "combobox", "grid", "gridcell", "link", "listbox", "menu", "menubar", "menuitem",
    "menuitemcheckbox", "menuitemradio", "option", "progressbar", "radio", "radiogroup", "row", "rowheader", "scrollbar", "searchbox", "slider", "spinbutton", "switch", "tab",
    "tablist", "textbox", "tree", "treegrid", "treeitem", "doc-backlink", "doc-biblioref", "doc-glossref", "doc-noteref");

  private HtmlConstants() {
  }

}
