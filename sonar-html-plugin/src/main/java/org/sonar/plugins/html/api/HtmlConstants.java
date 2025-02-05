/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.plugins.html.api;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.sonar.plugins.html.api.accessibility.AriaRole;
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
  public static final Set<String> INTERACTIVE_ELEMENTS = Set.of("a", "audio", "button", "canvas", "datalist", "embed", "input", "menuitem", "option", "select", "summary",
    "td", "textarea", "th", "tr", "video");

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
  protected static final Set<AriaRole> ABSTRACT_ROLES = EnumSet.of(
    AriaRole.COMMAND, AriaRole.COMPOSITE, AriaRole.INPUT, AriaRole.LANDMARK, AriaRole.RANGE, AriaRole.ROLETYPE,
    AriaRole.SECTION, AriaRole.SECTIONHEAD, AriaRole.SELECT, AriaRole.STRUCTURE, AriaRole.TOOLBAR, AriaRole.WIDGET,
    AriaRole.WINDOW);

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
    if (role == null) {
      return false;
    }
    var ariaRole = AriaRole.of(role.toLowerCase(Locale.ROOT));
    return ariaRole != null && ABSTRACT_ROLES.stream().anyMatch(ariaRole::equals);
  }

  public static boolean hasKnownHTMLTag(TagNode element) {
    return KNOWN_HTML_TAGS.stream().anyMatch(tag -> tag.equalsIgnoreCase(element.getNodeName()));
  }

  public static boolean isReservedNode(TagNode element) {
    return RESERVED_NODE_SET.contains(element.getNodeName());
  }

  public static boolean isAbstractRole(AriaRole ariaRole) {
    return ABSTRACT_ROLES.contains(ariaRole);
  }

  private HtmlConstants() {
  }
}
