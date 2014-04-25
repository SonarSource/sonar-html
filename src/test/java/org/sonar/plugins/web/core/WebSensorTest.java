/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.checks.NoSonarFilter;
import org.sonar.api.config.Settings;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.InputFileUtils;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.rules.Violation;
import org.sonar.api.scan.filesystem.FileQuery;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.web.AbstractWebPluginTester;
import org.sonar.test.TestUtils;

import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertTrue;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebSensorTest extends AbstractWebPluginTester {

  /**
   * Unit test which is more kind of an integration test. The purpose of this test is to get early feedback on changes in
   * the number of violations.
   */
  @Test
  public void testSensor() throws Exception {
    ModuleFileSystem fileSystem = mock(ModuleFileSystem.class);
    SensorContext sensorContext = mock(SensorContext.class);
    WebSensor sensor = new WebSensor(new Web(new Settings()), createStandardRulesProfile(), new NoSonarFilter(), fileSystem);
    Project project = loadProjectFromPom();

    File input = InputFileUtils.create(TestUtils.getResource("src/main/webapp"), "user-properties.jsp").getFile();
    when(fileSystem.files(Mockito.any(FileQuery.class))).thenReturn(ImmutableList.<File>of(input));
    when(fileSystem.workingDir()).thenReturn(new File("src/main/webapp"));
    assertTrue(sensor.shouldExecuteOnProject(project));

    sensor.analyse(project, sensorContext);

    verify(sensorContext, times(61)).saveViolation((Violation) Mockito.any());
  }

  private Project loadProjectFromPom() throws Exception {
    MavenProject pom = loadPom(TestUtils.getResource("pom.xml"));
    Project project = new Project(pom.getGroupId() + ":" + pom.getArtifactId()).setPom(pom).setConfiguration(
      new MapConfiguration(pom.getProperties()));
    project.setPom(pom);
    project.setLanguage(new Web(new Settings()));
    ProjectFileSystem projectFileSystem = mock(ProjectFileSystem.class);
    when(projectFileSystem.getSourceCharset()).thenReturn(Charsets.UTF_8);
    project.setFileSystem(projectFileSystem);
    when(projectFileSystem.getSourceDirs()).thenReturn(Lists.newArrayList(TestUtils.getResource("src/main/webapp")));
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

  @Test
  public void test_should_execute_on_project() {
    Settings settings = mock(Settings.class);
    RulesProfile profile = createStandardRulesProfile();
    ModuleFileSystem fileSystem = mock(ModuleFileSystem.class);
    NoSonarFilter noSonarFilter = mock(NoSonarFilter.class);

    Project project = mock(Project.class);

    WebSensor sensor = new WebSensor(
      new Web(settings), profile, noSonarFilter, fileSystem);

    when(fileSystem.files(Mockito.any(FileQuery.class))).thenReturn(ImmutableList.<File>of());
    assertThat(sensor.shouldExecuteOnProject(project)).isFalse();

    when(fileSystem.files(Mockito.any(FileQuery.class))).thenReturn(ImmutableList.of(mock(File.class)));
    assertThat(sensor.shouldExecuteOnProject(project)).isTrue();
  }
}
