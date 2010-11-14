/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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
