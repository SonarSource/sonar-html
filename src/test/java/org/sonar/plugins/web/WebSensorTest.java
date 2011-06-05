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

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.checks.NoSonarFilter;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.rules.Violation;

/**
 * @author Matthijs Galesloot
 */
public class WebSensorTest extends AbstractWebPluginTester {

  private WebSensor sensor;


  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Mock
  private ProjectFileSystem projectFileSystem;

  @Mock
  private SensorContext sensorContext;

  @Before
  public void setup() {
     sensor = new WebSensor(createStandardRulesProfile(), new NoSonarFilter());
  }

  @Test
  public void testSensor() throws Exception {
    File pomFile = new File(WebSensorTest.class.getResource("/pom.xml").toURI());

    final Project project = loadProjectFromPom(pomFile);
    project.setFileSystem(projectFileSystem);
    Mockito.when(projectFileSystem.getBasedir()).thenReturn(new File("src/test/resources"));

    assertTrue(sensor.shouldExecuteOnProject(project));

    sensor.analyse(project, sensorContext);

    Mockito.verify(sensorContext, Mockito.atLeastOnce()).saveViolation((Violation) Mockito.any());
  }

  /**
   * Simple Unit test version of the integration test StandardMeasuresIT. The purpose of this test is to get early feedback on changes in
   * the nr of violations.
   */
  @Test
  public void testStandardMeasuresIntegrationTest() throws Exception {

    final File pomFile = new File("source-its/projects/continuum-webapp/pom.xml");
    Project project = loadProjectFromPom(pomFile);
    project.setFileSystem(projectFileSystem);
    Mockito.when(projectFileSystem.getSourceDirs()).thenReturn(Arrays.asList(new File(pomFile.getParentFile(), "src")));

    assertTrue(sensor.shouldExecuteOnProject(project));
    sensor.analyse(project, sensorContext);

    Mockito.verify(sensorContext, Mockito.times(992)).saveViolation((Violation) Mockito.any());
  }
}
