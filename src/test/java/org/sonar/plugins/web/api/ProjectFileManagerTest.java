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

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.sonar.api.resources.Project;
import org.sonar.plugins.web.AbstractWebPluginTester;
import org.sonar.plugins.web.WebSensorTest;

/**
 * @author Matthijs Galesloot
 */
public class ProjectFileManagerTest extends AbstractWebPluginTester {

  /**
   * Test file path.
   */
  @Test
  public void testSourcePaths() throws Exception {
    File pomFile = new File(WebSensorTest.class.getResource("/pom.xml").toURI());
    final Project project = loadProjectFromPom(pomFile);

    String path = (String) project.getConfiguration().getProperty(ConfigurationConstants.SOURCE_DIRECTORY);
    ProjectFileManager fileManager = new ProjectFileManager(project);
    assertEquals(1, fileManager.getSourceDirs().size());

    // set absolute path
    project.getConfiguration().setProperty(ConfigurationConstants.SOURCE_DIRECTORY,
        new File(project.getPom().getBasedir(), path).getAbsoluteFile().toString());
    fileManager = new ProjectFileManager(project);
    assertEquals(1, fileManager.getSourceDirs().size());

    // add two more paths
    project.getConfiguration().addProperty(ConfigurationConstants.SOURCE_DIRECTORY, path + "," + path);
    fileManager = new ProjectFileManager(project);
    assertEquals(3, fileManager.getSourceDirs().size());

    // add not existing path
    project.getConfiguration().addProperty(ConfigurationConstants.SOURCE_DIRECTORY, "path-not-exist");
    fileManager = new ProjectFileManager(project);
    assertEquals(3, fileManager.getSourceDirs().size());
  }
}
