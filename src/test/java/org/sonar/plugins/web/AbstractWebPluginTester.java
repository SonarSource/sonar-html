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
import java.io.FileReader;
import java.net.URISyntaxException;

import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.sonar.api.CoreProperties;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.web.language.Web;

/**
 * 
 * @author Matthijs Galesloot
 *
 */
class AbstractWebPluginTester {

  private static MavenProject loadPom() {
    File pomFile;
    try {
      pomFile = new File(WebSensorTest.class.getResource("/pom.xml").toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    FileReader fileReader = null;
    try {
      MavenXpp3Reader pomReader = new MavenXpp3Reader();
      fileReader = new FileReader(pomFile);
      Model model = pomReader.read(fileReader);
      MavenProject project = new MavenProject(model);
      project.setFile(pomFile);
      project.getBuild().setDirectory(pomFile.getParentFile().getPath());
      return project;
    } catch (Exception e) {
      throw new SonarException("Failed to read Maven project file : " + pomFile.getPath(), e);
    } finally {
      IOUtils.closeQuietly(fileReader);
    }
  }

  protected static Project loadProjectFromPom() {
    MavenProject pom = loadPom();
    Project project = new Project(pom.getGroupId() + ":" + pom.getArtifactId()).setPom(pom).setConfiguration(
        new MapConfiguration(pom.getProperties()));
    project.setFileSystem(new DefaultProjectFileSystem(project));
    project.setPom(pom);

    String languageKey = pom.getProperties().getProperty(CoreProperties.PROJECT_LANGUAGE_PROPERTY);

    project.setLanguageKey(languageKey);
    if (Web.INSTANCE.getKey().equals(languageKey)) {
      project.setLanguage(Web.INSTANCE);
    }

    pom.addCompileSourceRoot(pom.getBuild().getSourceDirectory());

    return project;
  }
}
