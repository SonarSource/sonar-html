/*
 * SonarSource :: Web :: ITs :: Plugin
 * Copyright (c) 2011-2016 SonarSource SA and Matthijs Galesloot
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
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import java.io.File;

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
    SonarScanner build = WebTestSuite.createSonarScanner()
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
  public void testProjectInfo() {
    Sonar wsClient = orchestrator.getServer().getWsClient();
    assertThat(wsClient.find(new ResourceQuery(PROJECT)).getName()).isEqualTo("TestOfWebPlugin");
    assertThat(wsClient.find(new ResourceQuery(PROJECT)).getVersion()).isEqualTo("1.0");
  }

  @Test
  public void testProjectMeasures() {
    assertThat(getProjectMeasure("ncloc").getIntValue()).isEqualTo(6853);
    assertThat(getProjectMeasure("lines").getIntValue()).isEqualTo(9252);
    assertThat(getProjectMeasure("files").getIntValue()).isEqualTo(103);
    assertThat(getProjectMeasure("directories").getIntValue()).isEqualTo(8);
    assertThat(getProjectMeasure("functions")).isNull();
    assertThat(getProjectMeasure("statements")).isNull();
    assertThat(getProjectMeasure("comment_lines_density").getValue()).isEqualTo(0.3);
    assertThat(getProjectMeasure("comment_lines").getIntValue()).isEqualTo(23);

    assertThat(getProjectMeasure("public_api")).isNull();
    assertThat(getProjectMeasure("complexity").getIntValue()).isEqualTo(391);
    assertThat(getProjectMeasure("function_complexity")).isNull();
    assertThat(getProjectMeasure("function_complexity_distribution")).isNull();
    assertThat(getProjectMeasure("file_complexity").getValue()).isEqualTo(3.8);
    assertThat(getProjectMeasure("file_complexity_distribution").getData()).isEqualTo("0=73;5=22;10=7;20=1;30=0;60=0;90=0");
  }

  @Test
  public void projectDuplications() {
    assertThat(getProjectMeasure("duplicated_lines").getIntValue()).isEqualTo(170);
    assertThat(getProjectMeasure("duplicated_blocks").getIntValue()).isEqualTo(8);
    assertThat(getProjectMeasure("duplicated_files").getIntValue()).isEqualTo(7);
    assertThat(getProjectMeasure("duplicated_lines_density").getValue()).isEqualTo(1.8);
  }

  @Test
  public void testDirectoryMeasures() {
    assertThat(getMeasure("ncloc", DIR_ROOT).getIntValue()).isEqualTo(2878);
    assertThat(getMeasure("comment_lines_density", DIR_ROOT).getValue()).isEqualTo(0.3);
    assertThat(getMeasure("duplicated_lines_density", DIR_ROOT).getValue()).isEqualTo(1.4);
    assertThat(getMeasure("complexity", DIR_ROOT).getIntValue()).isEqualTo(150);
  }

  @Test
  public void testFileMeasures() {
    assertThat(getFileMeasure("ncloc").getIntValue()).isEqualTo(311);
    assertThat(getFileMeasure("lines").getIntValue()).isEqualTo(338);
    assertThat(getFileMeasure("files").getIntValue()).isEqualTo(1);
    assertThat(getFileMeasure("directories")).isNull();
    assertThat(getFileMeasure("functions")).isNull();
    assertThat(getFileMeasure("comment_lines_density").getValue()).isEqualTo(0.3);
    assertThat(getFileMeasure("comment_lines").getIntValue()).isEqualTo(1);
    assertThat(getFileMeasure("public_api")).isNull();
    assertThat(getFileMeasure("duplicated_lines")).isNull();
    assertThat(getFileMeasure("duplicated_blocks")).isNull();
    assertThat(getFileMeasure("duplicated_files")).isNull();
    assertThat(getFileMeasure("duplicated_lines_density")).isNull();
    assertThat(getFileMeasure("statements")).isNull();
    assertThat(getFileMeasure("complexity").getIntValue()).isEqualTo(16);
    assertThat(getFileMeasure("function_complexity")).isNull();
    assertThat(getFileMeasure("function_complexity_distribution")).isNull();
    assertThat(getFileMeasure("file_complexity").getValue()).isEqualTo(16.0);
    assertThat(getFileMeasure("file_complexity_distribution")).isNull();
  }

  @Test
  public void lineLevelMeasures() throws Exception {
    String value = getFileMeasure("ncloc_data").getData();
    assertThat(value).contains("20=1");
    assertThat(value).contains(";38=1");
    assertThat(value).contains(";58=1");
    // SonarQube >= 5.6 removed =0 entries
    assertThat(value.replaceAll("=0", "").replaceAll("[^=]", "")).hasSize(311);

    assertThat(getFileMeasure("comment_lines_data").getData()).contains("142=1");
  }

  private Measure getProjectMeasure(String metricKey) {
    Resource resource = orchestrator.getServer().getWsClient().find(ResourceQuery.createForMetrics(PROJECT, metricKey));
    return resource != null ? resource.getMeasure(metricKey) : null;
  }

  private Measure getFileMeasure(String metricKey) {
    Resource resource = orchestrator.getServer().getWsClient().find(ResourceQuery.createForMetrics(FILE, metricKey));
    return resource != null ? resource.getMeasure(metricKey) : null;
  }

  private Measure getMeasure(String metricKey, String resourceKey) {
    Resource resource = orchestrator.getServer().getWsClient().find(ResourceQuery.createForMetrics(resourceKey, metricKey));
    return resource != null ? resource.getMeasure(metricKey) : null;
  }

}
