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

package org.sonar.plugins.web;

import java.io.File;

import org.sonar.api.resources.Project;
import org.sonar.plugins.web.language.WebProperties;


public class ProjectConfiguration {

  private final Project project;
  public ProjectConfiguration(Project project) {
    this.project = project;
  }
  public void addSourceDir() {
    if (project.getProperty(WebProperties.SOURCE_DIRECTORY) != null) {
      File file = new File(project.getFileSystem().getBasedir() + "/" + project.getProperty(WebProperties.SOURCE_DIRECTORY).toString());
      for (File sourceDir : project.getFileSystem().getSourceDirs()) {
        if (sourceDir.equals(file)) {
          return;
        }
      }
      project.getFileSystem().addSourceDir(file);
    }
  }

  public String getSourceDir() {
    return (String) project.getConfiguration().getProperty(WebProperties.SOURCE_DIRECTORY);
  }
}
