/*
 * SonarSource :: Web :: ITs :: Plugin
 * Copyright (c) 2011-2018 SonarSource SA and Matthijs Galesloot
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
package com.sonar.it.web;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;
import com.sonar.orchestrator.locator.FileLocation;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonarqube.ws.WsMeasures.Measure;

import java.io.File;

import static com.sonar.it.web.WebTestSuite.createSonarScanner;
import static com.sonar.it.web.WebTestSuite.getMeasure;
import static org.assertj.core.api.Assertions.assertThat;

public class StandardMeasuresTest {

  @ClassRule
  public static Orchestrator orchestrator = Orchestrator.builderEnv()
    .addPlugin(FileLocation.byWildcardMavenFilename(new File("../../sonar-web-plugin/target"), "sonar-web-plugin-*.jar"))
    .restoreProfileAtStartup(FileLocation.ofClasspath("/com/sonar/it/web/backup.xml"))
    .build();

  private static final String PROJECT = "TestOfWebPlugin";
  private static final String DIR_ROOT = keyFor("TestOfWebPlugin", "WEB-INF/jsp");
  private static final String FILE = keyFor("TestOfWebPlugin", "WEB-INF/jsp/admin/buildQueueView.jsp");

  private static String keyFor(String project, String resource) {
    return project + ":src/" + resource;
  }

  @BeforeClass
  public static void init() throws Exception {
    orchestrator.resetData();
    String projectKey = "TestOfWebPlugin";
    orchestrator.getServer().provisionProject(projectKey, projectKey);
    orchestrator.getServer().associateProjectToQualityProfile(projectKey, "web", "IT");
    SonarScanner build = createSonarScanner()
      .setProjectDir(new File("projects/continuum-webapp/"))
      .setProjectKey(projectKey)
      .setProjectName(projectKey)
      .setProjectVersion("1.0")
      .setSourceDirs("src")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.web.fileExtensions", ".xhtml,.jspf,.jsp");
    orchestrator.executeBuild(build);
  }

  @Test
  public void testProjectMeasures() {
    assertThat(getProjectMeasureAsDouble("ncloc")).isEqualTo(6853d);
    assertThat(getProjectMeasureAsDouble("lines")).isEqualTo(9252d);
    assertThat(getProjectMeasureAsDouble("files")).isEqualTo(103d);
    assertThat(getProjectMeasureAsDouble("directories")).isEqualTo(8d);
    assertThat(getProjectMeasureAsDouble("functions")).isNull();
    assertThat(getProjectMeasureAsDouble("statements")).isNull();
    assertThat(getProjectMeasureAsDouble("comment_lines_density")).isEqualTo(0.3);
    assertThat(getProjectMeasureAsDouble("comment_lines")).isEqualTo(23d);

    assertThat(getProjectMeasureAsDouble("public_api")).isNull();
    assertThat(getProjectMeasureAsDouble("complexity")).isEqualTo(391d);
    assertThat(getProjectMeasureAsDouble("function_complexity")).isNull();
    assertThat(getProjectMeasureAsDouble("function_complexity_distribution")).isNull();
    assertThat(getProjectMeasureAsDouble("file_complexity")).isEqualTo(3.8);
    assertThat(getProjectMeasure("file_complexity_distribution").getValue()).isEqualTo("0=73;5=22;10=7;20=1;30=0;60=0;90=0");
  }

  @Test
  public void projectDuplications() {
    assertThat(getProjectMeasureAsDouble("duplicated_lines")).isEqualTo(170d);
    assertThat(getProjectMeasureAsDouble("duplicated_blocks")).isEqualTo(8d);
    assertThat(getProjectMeasureAsDouble("duplicated_files")).isEqualTo(7d);
    assertThat(getProjectMeasureAsDouble("duplicated_lines_density")).isEqualTo(1.8);
  }

  @Test
  public void testDirectoryMeasures() {
    assertThat(getMeasureAsDouble("ncloc", DIR_ROOT)).isEqualTo(2878d);
    assertThat(getMeasureAsDouble("comment_lines_density", DIR_ROOT)).isEqualTo(0.3);
    assertThat(getMeasureAsDouble("duplicated_lines_density", DIR_ROOT)).isEqualTo(1.4);
    assertThat(getMeasureAsDouble("complexity", DIR_ROOT)).isEqualTo(150d);
  }

  @Test
  public void testFileMeasures() {
    assertThat(getFileMeasureAsDouble("ncloc")).isEqualTo(311d);
    assertThat(getFileMeasureAsDouble("lines")).isEqualTo(338d);
    assertThat(getFileMeasureAsDouble("files")).isEqualTo(1d);
    assertThat(getFileMeasureAsDouble("directories")).isNull();
    assertThat(getFileMeasureAsDouble("functions")).isNull();
    assertThat(getFileMeasureAsDouble("comment_lines_density")).isEqualTo(0.3);
    assertThat(getFileMeasureAsDouble("comment_lines")).isEqualTo(1d);
    assertThat(getFileMeasureAsDouble("public_api")).isNull();
    assertThat(getFileMeasureAsDouble("duplicated_lines")).isZero();
    assertThat(getFileMeasureAsDouble("duplicated_blocks")).isZero();
    assertThat(getFileMeasureAsDouble("duplicated_files")).isZero();
    assertThat(getFileMeasureAsDouble("duplicated_lines_density")).isZero();
    assertThat(getFileMeasureAsDouble("statements")).isNull();
    assertThat(getFileMeasureAsDouble("complexity")).isEqualTo(16d);
    assertThat(getFileMeasureAsDouble("function_complexity")).isNull();
    assertThat(getFileMeasureAsDouble("function_complexity_distribution")).isNull();
    assertThat(getFileMeasureAsDouble("file_complexity")).isEqualTo(16.0d);
    assertThat(getFileMeasureAsDouble("file_complexity_distribution")).isNull();
  }

  @Test
  public void lineLevelMeasures() throws Exception {
    String value = getFileMeasure("ncloc_data").getValue();
    assertThat(value).contains("20=1");
    assertThat(value).contains(";38=1");
    assertThat(value).contains(";58=1");
    // SonarQube >= 5.6 removed =0 entries
    assertThat(value.replaceAll("=0", "").replaceAll("[^=]", "")).hasSize(311);

    assertThat(getFileMeasure("comment_lines_data").getValue()).contains("142=1");
  }

  private Measure getProjectMeasure(String metricKey) {
    return getMeasure(orchestrator, PROJECT, metricKey);
  }

  private Double getProjectMeasureAsDouble(String metricKey) {
    return WebTestSuite.getMeasureAsDouble(orchestrator, PROJECT, metricKey);
  }

  private Measure getFileMeasure(String metricKey) {
    return getMeasure(orchestrator, FILE, metricKey);
  }

  private Double getFileMeasureAsDouble(String metricKey) {
    return WebTestSuite.getMeasureAsDouble(orchestrator, FILE, metricKey);
  }

  private Double getMeasureAsDouble(String metricKey, String resourceKey) {
    return WebTestSuite.getMeasureAsDouble(orchestrator, resourceKey, metricKey);
  }

}
