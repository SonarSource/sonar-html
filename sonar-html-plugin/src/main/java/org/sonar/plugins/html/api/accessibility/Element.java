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
