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

package org.sonar.plugins.web;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.Phase;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.web.api.ProjectFileManager;
import org.sonar.plugins.web.api.WebConstants;

/**
 * Import of source files to sonar database.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@DependsUpon(value="webscanner")
@Phase(name = Phase.Name.PRE)
public final class WebSourceImporter implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(WebSourceImporter.class);
  private final Project project;
  private final ProjectFileManager projectFileManager;

  public WebSourceImporter(Project project) {
    this.project = project;
    this.projectFileManager = new ProjectFileManager(project);
  }

  public void analyse(Project project, SensorContext context) {

    parseDirs(context, projectFileManager.getFiles());
  }

  private Resource<?> createResource(InputFile file) {
    Resource<?> resource = projectFileManager.fromIOFile(file);
    if (resource == null) {
      LOG.debug("HtmlSourceImporter failed for: " + file.getRelativePath());
    } else {
      LOG.debug("HtmlSourceImporter:" + file.getRelativePath());
    }
    return resource;
  }

  private boolean isEnabled(Project project) {
    return project.getConfiguration().getBoolean(CoreProperties.CORE_IMPORT_SOURCES_PROPERTY,
        CoreProperties.CORE_IMPORT_SOURCES_DEFAULT_VALUE);
  }

  private void parseDirs(SensorContext context, List<InputFile> files) {

    Charset sourcesEncoding = project.getFileSystem().getSourceCharset();

    for (InputFile file : files) {
      Resource<?> resource = createResource(file);
      if (resource != null) {
        try {
          context.index(resource);

          String source = FileUtils.readFileToString(file.getFile(), sourcesEncoding.name());
          context.saveSource(resource, source);

        } catch (IOException e) {
          throw new SonarException("Unable to read and import the source file : '" + file.getFile().getAbsolutePath()
              + "' with the charset : '" + sourcesEncoding.name() + "'. You should check the property " + CoreProperties.ENCODING_PROPERTY,
              e);
        }
      }
    }
  }

  public boolean shouldExecuteOnProject(Project project) {
    return isEnabled(project) && WebConstants.LANGUAGE_KEY.equals(project.getLanguageKey());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}