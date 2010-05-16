/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.language;

import org.sonar.api.resources.AbstractLanguage;

/**
 * This class defines the Web language.
 * 
 * @author Matthijs Galesloot
 * 
 */
public class Web extends AbstractLanguage {

  private static final String defaultSourceDir = "src/main/webapp";

  /** A web instance. */
  public static final Web INSTANCE = new Web();

  /** The web language key. */
  private static final String KEY = "web";

  /** All the valid web files suffixes. */
  private static final String[] SUFFIXES = { "xhtml", "jspf", "jsp" };

  /** The web language name */
  private static final String WEB_LANGUAGE_NAME = "Web";

  public static String getDefaultSourcedir() {
    return defaultSourceDir;
  }

  /**
   * Default constructor.
   */
  public Web() {
    super(KEY, WEB_LANGUAGE_NAME);
  }

  /**
   * Gets the file suffixes.
   * 
   * @return the file suffixes
   * @see org.sonar.api.resources.Language#getFileSuffixes()
   */
  public String[] getFileSuffixes() {
    return SUFFIXES;
  }

}
