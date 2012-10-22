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
package org.sonar.plugins.web.core;

import com.google.common.collect.Lists;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.checks.NoSonarFilter;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.InputFileUtils;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.web.AbstractWebPluginTester;
import org.sonar.plugins.web.api.WebConstants;
import org.sonar.test.TestUtils;

import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
  private SensorContext sensorContext;

  @Before
  public void setup() {
    sensor = new WebSensor(new Web(new Settings()), createStandardRulesProfile(), new NoSonarFilter());
  }

  /**
   * Unit test which is more kind of an integration test. The purpose of this test is to get early feedback on changes in
   * the number of violations.
   */
  @Test
  public void testSensor() throws Exception {
    Project project = loadProjectFromPom();

    assertTrue(sensor.shouldExecuteOnProject(project));

    sensor.analyse(project, sensorContext);

    verify(sensorContext, times(59)).saveViolation((Violation) Mockito.any());
  }

  private Project loadProjectFromPom() throws Exception {
    MavenProject pom = loadPom(TestUtils.getResource("pom.xml"));
    Project project = new Project(pom.getGroupId() + ":" + pom.getArtifactId()).setPom(pom).setConfiguration(
        new MapConfiguration(pom.getProperties()));
    project.setPom(pom);
    project.setLanguageKey(WebConstants.LANGUAGE_KEY);
    project.setLanguage(new Web(new Settings()));
    ProjectFileSystem projectFileSystem = mock(ProjectFileSystem.class);
    project.setFileSystem(projectFileSystem);
    when(projectFileSystem.getSourceDirs()).thenReturn(Lists.newArrayList(TestUtils.getResource("src/main/webapp")));
    when(projectFileSystem.mainFiles("web")).thenReturn(Lists.newArrayList(InputFileUtils.create(TestUtils.getResource("src/main/webapp"), "user-properties.jsp")));
    return project;
  }

  private static MavenProject loadPom(File pomFile) throws URISyntaxException {

    FileReader fileReader = null;
    try {
      fileReader = new FileReader(pomFile);
      Model model = new MavenXpp3Reader().read(fileReader);
      MavenProject project = new MavenProject(model);
      project.setFile(pomFile);
      project.addCompileSourceRoot(project.getBuild().getSourceDirectory());

      return project;
    } catch (Exception e) {
      throw new SonarException("Failed to read Maven project file : " + pomFile.getPath(), e);
    } finally {
      IOUtils.closeQuietly(fileReader);
    }
  }
}
