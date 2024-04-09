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
import org.sonar.plugins.html.node.TagNode;

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
  public static final List<String> KNOWN_HTML_TAGS = List.of(
    "a",
    "acronym", // deprecated
    "area",
    "abbr",
    "address",
    "applet", // deprecated
    "article",
    "aside",
    "audio",
    "b",
    "base",
    "bdi",
    "bdo",
    "big", // deprecated
    "blink", // deprecated
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
    "input",
    "ins",
    "kbd",
    "keygen", // deprecated
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
    "rtc", // deprecated
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
    "spacer", // deprecated
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
    "textarea",
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

  // computed from https://github.com/jsx-eslint/eslint-plugin-jsx-a11y/blob/main/src/util/isInteractiveElement.js
  public static final Set<String> INTERACTIVE_ELEMENTS = Set.of("a", "audio", "button", "canvas", "datalist", "embed", "input", "menuitem", "option", "select", "td", "textarea",
    "th", "tr", "video");

  // computed from https://github.com/jsx-eslint/eslint-plugin-jsx-a11y/blob/main/src/util/isNonInteractiveElement.js
  public static final Set<String> NON_INTERACTIVE_ELEMENTS = Set.of("abbr", "address", "article", "aside", "blockquote", "br", "caption", "code", "dd", "del", "details", "dfn",
    "dialog", "dir", "dl", "dt", "em", "fieldset", "figcaption", "figure", "footer", "form", "h1", "h2", "h3", "h4", "h5", "h6", "hr", "html", "iframe", "img", "ins", "label",
    "legend", "li", "main", "mark", "marquee", "menu", "meter", "nav", "ol", "optgroup", "output", "p", "pre", "progress", "ruby", "strong", "sub", "sup", "table", "tbody",
    "tfoot", "thead", "time", "ul");

  // computed as https://github.com/jsx-eslint/eslint-plugin-jsx-a11y/blob/main/src/util/isInteractiveRole.js
  public static final Set<String> INTERACTIVE_ROLES = Set.of("button", "checkbox", "columnheader", "combobox", "grid", "gridcell", "link", "listbox", "menu", "menubar", "menuitem",
    "menuitemcheckbox", "menuitemradio", "option", "progressbar", "radio", "radiogroup", "row", "rowheader", "scrollbar", "searchbox", "slider", "spinbutton", "switch", "tab",
    "tablist", "textbox", "tree", "treegrid", "treeitem", "doc-backlink", "doc-biblioref", "doc-glossref", "doc-noteref");

  // computed from https://github.com/jsx-eslint/eslint-plugin-jsx-a11y/blob/main/src/util/isNonInteractiveRole.js
  public static final Set<String> NON_INTERACTIVE_ROLES = Set.of("alert", "alertdialog", "application", "article", "banner", "blockquote", "caption", "cell", "code",
    "complementary", "contentinfo", "definition", "deletion", "dialog", "directory", "document", "emphasis", "feed", "figure", "form", "generic", "group", "heading", "img",
    "insertion", "list", "listitem", "log", "main", "mark", "marquee", "math", "meter", "navigation", "none", "note", "paragraph", "presentation", "region", "rowgroup", "search",
    "separator", "status", "strong", "subscript", "superscript", "table", "tabpanel", "term", "time", "timer", "toolbar", "tooltip", "doc-abstract", "doc-acknowledgments",
    "doc-afterword", "doc-appendix", "doc-biblioentry", "doc-bibliography", "doc-chapter", "doc-colophon", "doc-conclusion", "doc-cover", "doc-credit", "doc-credits",
    "doc-dedication", "doc-endnote", "doc-endnotes", "doc-epigraph", "doc-epilogue", "doc-errata", "doc-example", "doc-footnote", "doc-foreword", "doc-glossary", "doc-index",
    "doc-introduction", "doc-notice", "doc-pagebreak", "doc-pagelist", "doc-part", "doc-preface", "doc-prologue", "doc-pullquote", "doc-qna", "doc-subtitle", "doc-tip", "doc-toc",
    "graphics-document", "graphics-object", "graphics-symbol");

  // inspired by https://github.com/jsx-eslint/eslint-plugin-jsx-a11y/blob/main/src/util/isPresentationRole.js
  public static final Set<String> PRESENTATION_ROLES = Set.of("none", "presentation");

  // computed from https://github.com/A11yance/aria-query/blob/main/src/etc/roles/ariaAbstractRoles.js
  public static final Set<String> ABSTRACT_ROLES = Set.of(
    "command", "composite", "input", "landmark", "range", "roletype", "section", "sectionhead", "select", "structure", "toolbar", "widget", "window"
  );

  // computed from https://github.com/A11yance/aria-query/blob/main/src/domMap.js
  public static final Set<String> RESERVED_NODE_SET = Set.of(
    "base", "col", "colgroup", "head", "html", "link", "meta", "noembed", "noscript", "param", "picture", "script", "source", "style", "title", "track"
  );

  public static boolean isInteractiveElement(TagNode element) {
    var tagName = element.getNodeName();
    return INTERACTIVE_ELEMENTS.stream().anyMatch(tagName::equalsIgnoreCase);
  }

  public static boolean isNonInteractiveElement(TagNode element) {
    var tagName = element.getNodeName();
    return NON_INTERACTIVE_ELEMENTS.stream().anyMatch(tagName::equalsIgnoreCase);
  }

  public static boolean hasInteractiveRole(TagNode element) {
    var role = element.getAttribute("role");
    return role != null && INTERACTIVE_ROLES.stream().anyMatch(role::equalsIgnoreCase);
  }

  public static boolean hasNonInteractiveRole(TagNode element) {
    var role = element.getAttribute("role");
    return role != null && NON_INTERACTIVE_ROLES.stream().anyMatch(role::equalsIgnoreCase);
  }

  public static boolean hasPresentationRole(TagNode element) {
    var role = element.getAttribute("role");
    return role != null && PRESENTATION_ROLES.stream().anyMatch(role::equalsIgnoreCase);
  }

  public static boolean hasAbstractRole(TagNode element) {
    var role = element.getAttribute("role");
    return role != null && ABSTRACT_ROLES.stream().anyMatch(role::equalsIgnoreCase);
  }

  public static boolean hasKnownHTMLTag(TagNode element) {
    return KNOWN_HTML_TAGS.stream().anyMatch(tag -> tag.equalsIgnoreCase(element.getNodeName()));
  }

  public static boolean isReservedNode(TagNode element) {
    return RESERVED_NODE_SET.contains(element.getNodeName());
  }

  private HtmlConstants() {
  }
}
