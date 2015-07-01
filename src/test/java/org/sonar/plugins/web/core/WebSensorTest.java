/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
 * sonarqube@googlegroups.com
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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.rule.internal.DefaultActiveRules;
import org.sonar.api.batch.rule.internal.NewActiveRule;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.web.api.WebConstants;
import org.sonar.plugins.web.core.helpers.IssuableMock;
import org.sonar.plugins.web.rules.WebRulesDefinition;
import org.sonar.test.TestUtils;

public class WebSensorTest {

  private static final File PROJECT_BASE_DIR = TestUtils.getResource("src/main/webapp");
  private FileLinesContextFactory fileLinesContextFactory;
  private ResourcePerspectives perspectives;

  @Before
  public void setUp() {
    fileLinesContextFactory = mock(FileLinesContextFactory.class);
    when(fileLinesContextFactory.createFor(any(InputFile.class))).thenReturn(mock(FileLinesContext.class));

    perspectives = mock(ResourcePerspectives.class);
    when(perspectives.as(any(Class.class), any(InputFile.class))).thenReturn(new IssuableMock());
  }

  @Test
  public void test_should_execute_on_project() {
    DefaultFileSystem fs = new DefaultFileSystem(PROJECT_BASE_DIR);
    WebSensor sensor = newWebSensor(fs);

    // Without Web source file
    assertThat(sensor.shouldExecuteOnProject(new Project(""))).isFalse();

    // With Web source file
    fs.add(new DefaultInputFile("dummy.jsp").setLanguage(WebConstants.LANGUAGE_KEY).setType(InputFile.Type.MAIN));
    assertThat(sensor.shouldExecuteOnProject(new Project(""))).isTrue();
  }

  /**
   * Unit test which is more kind of an integration test. The purpose of this test is to get early feedback on changes in
   * the number of issues.
   */
  @Test
  public void testSensor() throws Exception {
    DefaultFileSystem fs = new DefaultFileSystem(PROJECT_BASE_DIR);
    fs.setWorkDir(PROJECT_BASE_DIR);
    fs.setEncoding(Charset.defaultCharset());
    DefaultInputFile inputFile = new DefaultInputFile("user-properties.jsp")
      .setFile(new File(PROJECT_BASE_DIR, "user-properties.jsp"))
      .setLanguage(WebConstants.LANGUAGE_KEY)
      .setType(InputFile.Type.MAIN);

    fs.add(inputFile);

    SensorContext context = mock(SensorContext.class);
    when(context.getResource(any(InputFile.class))).thenReturn(org.sonar.api.resources.File.create(inputFile.relativePath()));

    newWebSensor(fs).analyse(loadProjectFromPom(), context);

    verify(perspectives, times(84)).as(any(Class.class), any(InputFile.class));
  }

  private Project loadProjectFromPom() throws Exception {
    MavenProject pom = loadPom(TestUtils.getResource("pom.xml"));

    return new Project(pom.getGroupId() + ":" + pom.getArtifactId())
      .setPom(pom)
      .setConfiguration(new MapConfiguration(pom.getProperties()))
      .setLanguage(new Web(new Settings()));
  }

  private static MavenProject loadPom(File pomFile) throws URISyntaxException {
    FileReader fileReader = null;

    try {
      fileReader = new FileReader(pomFile);

      MavenProject project = new MavenProject(new MavenXpp3Reader().read(fileReader));
      project.setFile(pomFile);
      project.addCompileSourceRoot(project.getBuild().getSourceDirectory());

      return project;

    } catch (Exception e) {
      throw new SonarException("Failed to read Maven project file : " + pomFile.getPath(), e);

    } finally {
      IOUtils.closeQuietly(fileReader);
    }
  }

  private WebSensor newWebSensor(DefaultFileSystem fs) {
    WebRulesDefinition rulesDefinition = new WebRulesDefinition();
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);
    RulesDefinition.Repository repository = context.repository(WebRulesDefinition.REPOSITORY_KEY);

    List<NewActiveRule> activeRules = new ArrayList<>();

    for (RulesDefinition.Rule rule : repository.rules()) {
      activeRules.add(new ActiveRulesBuilder().create(RuleKey.of(WebRulesDefinition.REPOSITORY_KEY, rule.key())));
    }
    return new WebSensor(
      new NoSonarFilter(),
      fs,
      fileLinesContextFactory,
      new CheckFactory(new DefaultActiveRules(activeRules)),
      perspectives);
  }
}
