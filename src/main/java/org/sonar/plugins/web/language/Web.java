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
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;

/**
 * This class defines the Web language.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public class Web extends AbstractLanguage {

  /** All the valid web files suffixes. */
  private static final String[] DEFAULT_SUFFIXES = { "xhtml", "jspf", "jsp" };

  /** A web instance. */
  public static final Web INSTANCE = new Web();

  /** The web language key. */
  public static final String KEY = "web";

  /** The web language name */
  private static final String WEB_LANGUAGE_NAME = "Web";

  private String[] fileSuffixes;

  /**
   * Default constructor.
   */
  public Web() {
    super(KEY, WEB_LANGUAGE_NAME);
  }

  public Web(Project project) {
    this();

    String extensions = (String) project.getProperty(WebProperties.FILE_EXTENSIONS);

    if (extensions != null) {
      final String[] list = extensions.split(",");
      if (list.length > 0) {
        for (int i = 0; i < list.length; i++) {
          list[i] = list[i].trim();
        }
        fileSuffixes = list;
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Language) {
      Language language = (Language) o;
      return getKey().equals(language.getKey());
    }
    return false;
  }

  /**
   * Gets the file suffixes.
   *
   * @return the file suffixes
   * @see org.sonar.api.resources.Language#getFileSuffixes()
   */
  public String[] getFileSuffixes() {
    return fileSuffixes == null ?  DEFAULT_SUFFIXES : fileSuffixes;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
