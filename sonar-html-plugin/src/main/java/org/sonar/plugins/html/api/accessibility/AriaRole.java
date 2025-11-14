/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

public enum AriaRole {
  COMMAND("command"),
  COMPOSITE("composite"),
  INPUT("input"),
  LANDMARK("landmark"),
  RANGE("range"),
  ROLETYPE("roletype"),
  SECTION("section"),
  SECTIONHEAD("sectionhead"),
  SELECT("select"),
  STRUCTURE("structure"),
  WIDGET("widget"),
  WINDOW("window"),
  ALERT("alert"),
  ALERTDIALOG("alertdialog"),
  APPLICATION("application"),
  ARTICLE("article"),
  BANNER("banner"),
  BLOCKQUOTE("blockquote"),
  BUTTON("button"),
  CAPTION("caption"),
  CELL("cell"),
  CHECKBOX("checkbox"),
  CODE("code"),
  COLUMNHEADER("columnheader"),
  COMBOBOX("combobox"),
  COMPLEMENTARY("complementary"),
  CONTENTINFO("contentinfo"),
  DEFINITION("definition"),
  DELETION("deletion"),
  DIALOG("dialog"),
  DIRECTORY("directory"),
  DOCUMENT("document"),
  EMPHASIS("emphasis"),
  FEED("feed"),
  FIGURE("figure"),
  FORM("form"),
  GENERIC("generic"),
  GRID("grid"),
  GRIDCELL("gridcell"),
  GROUP("group"),
  HEADING("heading"),
  IMG("img"),
  INSERTION("insertion"),
  LINK("link"),
  LIST("list"),
  LISTBOX("listbox"),
  LISTITEM("listitem"),
  LOG("log"),
  MAIN("main"),
  MARK("mark"),
  MARQUEE("marquee"),
  MATH("math"),
  MENU("menu"),
  MENUBAR("menubar"),
  MENUITEM("menuitem"),
  MENUITEMCHECKBOX("menuitemcheckbox"),
  MENUITEMRADIO("menuitemradio"),
  METER("meter"),
  NAVIGATION("navigation"),
  NOTE("note"),
  OPTION("option"),
  PARAGRAPH("paragraph"),
  PRESENTATION("presentation"),
  PROGRESSBAR("progressbar"),
  RADIO("radio"),
  RADIOGROUP("radiogroup"),
  REGION("region"),
  ROW("row"),
  ROWGROUP("rowgroup"),
  ROWHEADER("rowheader"),
  SCROLLBAR("scrollbar"),
  SEARCH("search"),
  SEARCHBOX("searchbox"),
  SEPARATOR("separator"),
  SLIDER("slider"),
  SPINBUTTON("spinbutton"),
  STATUS("status"),
  STRONG("strong"),
  SUBSCRIPT("subscript"),
  SUPERSCRIPT("superscript"),
  SWITCH("switch"),
  TAB("tab"),
  TABLE("table"),
  TABLIST("tablist"),
  TABPANEL("tabpanel"),
  TERM("term"),
  TEXTBOX("textbox"),
  TIME("time"),
  TIMER("timer"),
  TOOLBAR("toolbar"),
  TOOLTIP("tooltip"),
  TREE("tree"),
  TREEGRID("treegrid"),
  TREEITEM("treeitem"),
  DOC_ABSTRACT("doc-abstract"),
  DOC_ACKNOWLEDGMENTS("doc-acknowledgments"),
  DOC_AFTERWORD("doc-afterword"),
  DOC_APPENDIX("doc-appendix"),
  DOC_BACKLINK("doc-backlink"),
  DOC_BIBLIOENTRY("doc-biblioentry"),
  DOC_BIBLIOGRAPHY("doc-bibliography"),
  DOC_BIBLIOREF("doc-biblioref"),
  DOC_CHAPTER("doc-chapter"),
  DOC_COLOPHON("doc-colophon"),
  DOC_CONCLUSION("doc-conclusion"),
  DOC_COVER("doc-cover"),
  DOC_CREDIT("doc-credit"),
  DOC_CREDITS("doc-credits"),
  DOC_DEDICATION("doc-dedication"),
  DOC_ENDNOTE("doc-endnote"),
  DOC_ENDNOTES("doc-endnotes"),
  DOC_EPIGRAPH("doc-epigraph"),
  DOC_EPILOGUE("doc-epilogue"),
  DOC_ERRATA("doc-errata"),
  DOC_EXAMPLE("doc-example"),
  DOC_FOOTNOTE("doc-footnote"),
  DOC_FOREWORD("doc-foreword"),
  DOC_GLOSSARY("doc-glossary"),
  DOC_GLOSSREF("doc-glossref"),
  DOC_INDEX("doc-index"),
  DOC_INTRODUCTION("doc-introduction"),
  DOC_NOTEREF("doc-noteref"),
  DOC_NOTICE("doc-notice"),
  DOC_PAGEBREAK("doc-pagebreak"),
  DOC_PAGELIST("doc-pagelist"),
  DOC_PART("doc-part"),
  DOC_PREFACE("doc-preface"),
  DOC_PROLOGUE("doc-prologue"),
  DOC_QNA("doc-qna"),
  DOC_SUBTITLE("doc-subtitle"),
  DOC_TIP("doc-tip"),
  DOC_TOC("doc-toc"),
  GRAPHICS_DOCUMENT("graphics-document"),
  GRAPHICS_OBJECT("graphics-object"),
  GRAPHICS_SYMBOL("graphics-symbol");

  private final String value;

  AriaRole(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

  private static final Map<String, AriaRole> stringMap = Arrays.stream(values())
    .collect(Collectors.toMap(Enum::toString, Function.identity()));

  public static AriaRole of(String value) {
    return stringMap.get(value);
  }
}
