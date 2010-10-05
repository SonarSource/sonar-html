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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.AbstractSourceImporter;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.web.language.ConfigurableWeb;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.language.WebProperties;

/**
 * @author Matthijs Galesloot
 */
public final class WebSourceImporter extends AbstractSourceImporter {

  private static final Logger LOG = LoggerFactory.getLogger(WebSourceImporter.class);

  public WebSourceImporter(Project project) {
    super(new ConfigurableWeb(project));
  }

  public static void addSourceDir(Project project) {
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

  @Override
  public void analyse(Project project, SensorContext context) {
    addSourceDir(project);

    super.analyse(project, context);
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return isEnabled(project) && getLanguage().equals(project.getLanguage());
  }

  @Override
  protected Resource<?> createResource(File file, List<File> sourceDirs, boolean unitTest) {
    LOG.debug("WebSourceImporter:" + file.getPath());
    return WebFile.fromIOFile(file, sourceDirs);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}