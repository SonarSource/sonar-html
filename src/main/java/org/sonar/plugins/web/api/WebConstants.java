/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
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
package org.sonar.plugins.web.api;

/**
 * Constants for the project configuration.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public interface WebConstants {

  /** The language key. */
  String LANGUAGE_KEY = "web";
  String LANGUAGE_NAME = "Web";

  // ================ Plugin properties ================

  String FILE_EXTENSIONS_PROP_KEY = "sonar.web.fileExtensions";
  String FILE_EXTENSIONS_DEF_VALUE = "xhtml,jspf,jsp";

  /**
   * This property is deprecated in version 1.2 in favor of the standard ways to declare source folders.
   * @deprecated since 1.2
   */
  @Deprecated
  String SOURCE_DIRECTORY_PROP_KEY = "sonar.web.sourceDirectory";
  String SOURCE_DIRECTORY_DEF_VALUE = "src/main/webapp";

}
