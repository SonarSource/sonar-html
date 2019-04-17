/*
 * SonarSource :: HTML :: ITs :: Plugin
 * Copyright (c) 2011-2019 SonarSource SA and Matthijs Galesloot
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
import java.io.File;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonarqube.ws.Measures;

import static com.sonar.it.web.HtmlTestSuite.createSonarScanner;
import static com.sonar.it.web.HtmlTestSuite.getMeasure;
import static org.assertj.core.api.Assertions.assertThat;

public class StandardMeasuresTest {

  @ClassRule
  public static Orchestrator orchestrator = HtmlTestSuite.orchestrator;

  private static final String PROJECT = "TestOfWebPlugin";
  private static final String DIR_ROOT = keyFor("TestOfWebPlugin", "WEB-INF/jsp");
  private static final String FILE = keyFor("TestOfWebPlugin", "WEB-INF/jsp/admin/buildQueueView.jsp");

  private static String keyFor(String project, String resource) {
    return project + ":src/" + resource;
  }

  @BeforeClass
  public static void init() {
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
      .setProperty("sonar.html.file.suffixes", ".xhtml")
      .setProperty("sonar.jsp.file.suffixes", ".jspf,.jsp")
      ;
    orchestrator.executeBuild(build);
  }

  @Test
  public void testProjectMeasures() {
    assertThat(getProjectMeasureAsDouble("ncloc")).isEqualTo(6840d);
    assertThat(getProjectMeasureAsDouble("functions")).isNull();
    assertThat(getProjectMeasureAsDouble("statements")).isNull();
    assertThat(getProjectMeasureAsDouble("comment_lines_density")).isEqualTo(0.3);
    assertThat(getProjectMeasureAsDouble("comment_lines")).isEqualTo(23d);

    assertThat(getProjectMeasureAsDouble("public_api")).isNull();
    assertThat(getProjectMeasureAsDouble("complexity")).isEqualTo(391d);
    assertThat(getProjectMeasureAsDouble("function_complexity")).isNull();
    assertThat(getProjectMeasureAsDouble("function_complexity_distribution")).isNull();
    assertThat(getProjectMeasureAsDouble("file_complexity")).isEqualTo(3.8);
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
    if (HtmlTestSuite.sonarqubeGreaterThan75()) {
      assertThat(getMeasureAsDouble("ncloc", DIR_ROOT)).isEqualTo(6838d);
      assertThat(getMeasureAsDouble("comment_lines_density", DIR_ROOT)).isEqualTo(0.3);
      assertThat(getMeasureAsDouble("duplicated_lines_density", DIR_ROOT)).isEqualTo(1.8);
      assertThat(getMeasureAsDouble("complexity", DIR_ROOT)).isEqualTo(389d);
      return;
    }
    assertThat(getMeasureAsDouble("ncloc", DIR_ROOT)).isEqualTo(2870d);
    assertThat(getMeasureAsDouble("comment_lines_density", DIR_ROOT)).isEqualTo(0.3);
    assertThat(getMeasureAsDouble("duplicated_lines_density", DIR_ROOT)).isEqualTo(1.4);
    assertThat(getMeasureAsDouble("complexity", DIR_ROOT)).isEqualTo(150d);
  }

  @Test
  public void testFileMeasures() {
    assertThat(getFileMeasureAsDouble("ncloc")).isEqualTo(311d);
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
  }

  @Test
  public void lineLevelMeasures() {
    String value = getFileMeasure("ncloc_data").getValue();
    assertThat(value).contains("20=1");
    assertThat(value).contains(";38=1");
    assertThat(value).contains(";58=1");
    // SonarQube >= 5.6 removed =0 entries
    assertThat(value.replaceAll("=0", "").replaceAll("[^=]", "")).hasSize(311);
  }

  private Double getProjectMeasureAsDouble(String metricKey) {
    return HtmlTestSuite.getMeasureAsDouble(orchestrator, PROJECT, metricKey);
  }

  private Measures.Measure getFileMeasure(String metricKey) {
    return getMeasure(orchestrator, FILE, metricKey);
  }

  private Double getFileMeasureAsDouble(String metricKey) {
    return HtmlTestSuite.getMeasureAsDouble(orchestrator, FILE, metricKey);
  }

  private Double getMeasureAsDouble(String metricKey, String resourceKey) {
    return HtmlTestSuite.getMeasureAsDouble(orchestrator, resourceKey, metricKey);
  }

}
