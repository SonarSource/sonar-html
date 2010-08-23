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

import org.sonar.api.resources.Project;

/**
 * Configurable web language. Reads list of file suffixes from Project configuration.
 * 
 * @author Matthijs Galesloot
 * @since 1.0
 *
 */
public class ConfigurableWeb extends Web {

  private String[] fileSuffixes;

  public ConfigurableWeb(Project project) {

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
  public String[] getFileSuffixes() {
    return fileSuffixes == null ? super.getFileSuffixes() : fileSuffixes;
  }
}
