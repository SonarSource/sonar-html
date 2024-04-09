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

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Element {
  ARTICLE("article"),
  HEADER("header"),
  BLOCKQUOTE("blockquote"),
  INPUT("input"),
  BUTTON("button"),
  CAPTION("caption"),
  TD("td"),
  CODE("code"),
  TH("th"),
  SELECT("select"),
  ASIDE("aside"),
  FOOTER("footer"),
  DD("dd"),
  DEL("del"),
  DIALOG("dialog"),
  HTML("html"),
  EM("em"),
  FIGURE("figure"),
  FORM("form"),
  A("a"),
  AREA("area"),
  B("b"),
  BDO("bdo"),
  BODY("body"),
  DATA("data"),
  DIV("div"),
  HGROUP("hgroup"),
  I("i"),
  PRE("pre"),
  Q("q"),
  SAMP("samp"),
  SECTION("section"),
  SMALL("small"),
  SPAN("span"),
  U("u"),
  DETAILS("details"),
  FIELDSET("fieldset"),
  OPTGROUP("optgroup"),
  ADDRESS("address"),
  H1("h1"),
  H2("h2"),
  H3("h3"),
  H4("h4"),
  H5("h5"),
  H6("h6"),
  IMG("img"),
  INS("ins"),
  MENU("menu"),
  OL("ol"),
  UL("ul"),
  DATALIST("datalist"),
  LI("li"),
  MAIN("main"),
  MARK("mark"),
  MATH("math"),
  METER("meter"),
  NAV("nav"),
  OPTION("option"),
  P("p"),
  PROGRESS("progress"),
  TR("tr"),
  TBODY("tbody"),
  TFOOT("tfoot"),
  THEAD("thead"),
  HR("hr"),
  OUTPUT("output"),
  STRONG("strong"),
  SUB("sub"),
  SUP("sup"),
  TABLE("table"),
  DFN("dfn"),
  DT("dt"),
  TEXTAREA("textarea"),
  TIME("time");

  private final String value;

  Element(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

  private static final Map<String, Element> stringMap = Arrays.stream(values())
    .collect(Collectors.toMap(Enum::toString, Function.identity()));

  public static Element of(String value) {
    return stringMap.get(value);
  }
}
