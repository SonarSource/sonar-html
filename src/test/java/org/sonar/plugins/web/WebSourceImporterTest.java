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

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.sonar.api.resources.Project;

/**
 * @author Matthijs Galesloot
 */
public class WebSourceImporterTest extends AbstractWebPluginTester {

  @Test
  public void testImporter() throws Exception {

    File pomFile = new File(WebSensorTest.class.getResource("/pom.xml").toURI());
    final Project project = loadProjectFromPom(pomFile);

    WebSourceImporter importer = new WebSourceImporter(project);

    assertTrue("Importer only supports web projects", importer.shouldExecuteOnProject(project));
    MockSensorContext sensorContext = new MockSensorContext();
    importer.analyse(project, sensorContext);
    assertTrue(sensorContext.getNumResources() > 0);
  }

  @Test
  public void importerIsOnlyActiveOnWebProjects() throws Exception {

    final Project project = new Project("test");
    project.setConfiguration(new PropertiesConfiguration());
    project.setLanguage(null);
    project.setLanguageKey("java");

    WebSourceImporter importer = new WebSourceImporter(project);
    assertFalse("Importer only supports web projects", importer.shouldExecuteOnProject(project));
  }
}
