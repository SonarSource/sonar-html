/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;
import java.io.File;
import org.junit.ClassRule;
import org.junit.Test;

import static com.sonar.it.web.HtmlTestSuite.getMeasureAsInt;
import static org.assertj.core.api.Assertions.assertThat;

public class FileSuffixesTest {

  private static final String FILES_METRIC = "files";

  @ClassRule
  public static Orchestrator orchestrator = HtmlTestSuite.orchestrator;

  private static SonarScanner getSonarRunner(String projectKey) {
    orchestrator.getServer().provisionProject(projectKey, projectKey);
    orchestrator.getServer().associateProjectToQualityProfile(projectKey, "web", "no_rule");
    return HtmlTestSuite.createSonarScanner()
      .setProjectDir(new File("projects/FileSuffixesTest/"))
      .setProjectKey(projectKey)
      .setProjectName(projectKey)
      .setProjectVersion("1.0")
      .setSourceDirs(".")
      .setProperty("sonar.sourceEncoding", "UTF-8");
  }

  @Test
  public void filesSuffixesHtml() {
    String projectKey = "FileSuffixesTest-filesSuffixesHtml";
    SonarScanner build = getSonarRunner(projectKey)
      .setProperty("sonar.html.file.suffixes", ".html");
    orchestrator.executeBuild(build);
    // php file extension will be analyzed
    assertThat(getAnalyzedFilesNumber(projectKey)).isEqualTo(2);
  }

  @Test
  public void filesSuffixesHtmlPhp() {
    String projectKey = "FileSuffixesTest-filesSuffixesHtmlPhp";
    SonarScanner build = getSonarRunner(projectKey)
      .setProperty("sonar.html.file.suffixes", ".html,.php");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber(projectKey)).isEqualTo(2);
  }

  private Integer getAnalyzedFilesNumber(String projectKey) {
    return getMeasureAsInt(orchestrator, projectKey, FILES_METRIC);
  }

}
