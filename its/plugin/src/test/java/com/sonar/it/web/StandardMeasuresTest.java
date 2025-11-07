/*
 * SonarQube HTML
 * Copyright (C) 2011-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.it.web;

import com.sonar.orchestrator.build.SonarScanner;
import com.sonar.orchestrator.junit4.OrchestratorRule;
import java.io.File;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import static com.sonar.it.web.HtmlTestSuite.createSonarScanner;
import static org.assertj.core.api.Assertions.assertThat;

public class StandardMeasuresTest {

  @ClassRule
  public static OrchestratorRule orchestrator = HtmlTestSuite.orchestrator;

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
    assertThat(getMeasureAsDouble("ncloc", DIR_ROOT)).isEqualTo(6838d);
    assertThat(getMeasureAsDouble("comment_lines_density", DIR_ROOT)).isEqualTo(0.3);
    assertThat(getMeasureAsDouble("duplicated_lines_density", DIR_ROOT)).isEqualTo(1.8);
    assertThat(getMeasureAsDouble("complexity", DIR_ROOT)).isEqualTo(389d);
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
  }

  private Double getProjectMeasureAsDouble(String metricKey) {
    return HtmlTestSuite.getMeasureAsDouble(orchestrator, PROJECT, metricKey);
  }

  private Double getFileMeasureAsDouble(String metricKey) {
    return HtmlTestSuite.getMeasureAsDouble(orchestrator, FILE, metricKey);
  }

  private Double getMeasureAsDouble(String metricKey, String resourceKey) {
    return HtmlTestSuite.getMeasureAsDouble(orchestrator, resourceKey, metricKey);
  }

}
