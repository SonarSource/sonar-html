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

package org.sonar.plugins.web.language;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.resources.AbstractLanguage;
import org.sonar.api.resources.Project;
import org.sonar.plugins.web.ProjectConfiguration;

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

    List<?> extensions = project.getConfiguration().getList(ProjectConfiguration.FILE_EXTENSIONS);

    if (extensions != null && extensions.size() > 0 && !StringUtils.isEmpty((String) extensions.get(0))) {
      fileSuffixes = new String[extensions.size()];
      for (int i = 0; i < extensions.size(); i++) {
        fileSuffixes[i] = extensions.get(i).toString().trim();
      }
    }
  }

  /**
   * Gets the file suffixes.
   *
   * @return the file suffixes
   * @see org.sonar.api.resources.Language#getFileSuffixes()
   */
  public String[] getFileSuffixes() {
    return fileSuffixes == null ? DEFAULT_SUFFIXES : fileSuffixes;
  }
}
