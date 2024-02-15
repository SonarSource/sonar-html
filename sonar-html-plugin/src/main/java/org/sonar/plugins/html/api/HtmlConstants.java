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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    "area",
    "abbr",
    "address",
    "article",
    "aside",
    "audio",
    "b",
    "base",
    "blockquote",
    "body",
    "br",
    "button",
    "canvas",
    "caption",
    "cite",
    "code",
    "col",
    "colgroup",
    "data",
    "datalist",
    "dd",
    "del",
    "details",
    "dfn",
    "dialog",
    "div",
    "dl",
    "dt",
    "em",
    "embed",
    "fieldset",
    "figcaption",
    "figure",
    "footer",
    "form",
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
    "i",
    "iframe",
    "img",
    "input",
    "ins",
    "kbd",
    "label",
    "legend",
    "li",
    "link",
    "main",
    "map",
    "mark",
    "menu",
    "meta",
    "meter",
    "nav",
    "noscript",
    "object",
    "ol",
    "optgroup",
    "option",
    "output",
    "p",
    "param",
    "picture",
    "pre",
    "progress",
    "q",
    "rp",
    "rt",
    "ruby",
    "s",
    "samp",
    "script",
    "search",
    "section",
    "select",
    "small",
    "source",
    "span",
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
    "u",
    "ul",
    "var",
    "video",
    "wbr"
  );

  private HtmlConstants() {
  }

}
