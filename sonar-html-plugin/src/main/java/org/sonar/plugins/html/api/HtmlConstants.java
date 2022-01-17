/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
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

public class HtmlConstants {

  /** The language key. */
  public static final String LANGUAGE_KEY = "web";
  public static final String LANGUAGE_NAME = "HTML";

  /** JSP language key. */
  public static final String JSP_LANGUAGE_KEY = "jsp";
  public static final String JSP_LANGUAGE_NAME = "JSP";

  // ================ Plugin properties ================

  public static final String FILE_EXTENSIONS_PROP_KEY = "sonar.html.file.suffixes";
  public static final String FILE_EXTENSIONS_DEF_VALUE = ".html,.xhtml,.cshtml,.vbhtml,.aspx,.ascx,.rhtml,.erb,.shtm,.shtml,.cmp";
  public static final String JSP_FILE_EXTENSIONS_PROP_KEY = "sonar.jsp.file.suffixes";
  public static final String JSP_FILE_EXTENSIONS_DEF_VALUE = ".jsp,.jspf,.jspx";
  private HtmlConstants() {
  }

}
