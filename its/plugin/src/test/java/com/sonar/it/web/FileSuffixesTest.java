/*
 * SonarSource :: Web :: ITs :: Plugin
 * Copyright (C) 2011 SonarSource
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.it.web;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarRunner;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class FileSuffixesTest {

  private static final String PROJECT_KEY = "FileSuffixesTest";
  private static final String FILES_METRIC = "files";

  @ClassRule
  public static Orchestrator orchestrator = WebTestSuite.ORCHESTRATOR;

  @BeforeClass
  public static void init() throws Exception {
    orchestrator.resetData();
    orchestrator.getServer().provisionProject(PROJECT_KEY, PROJECT_KEY);
    orchestrator.getServer().associateProjectToQualityProfile(PROJECT_KEY, "web", "no_rule");
  }

  private static SonarRunner getSonarRunner() {
    return WebTestSuite.createSonarRunner()
      .setProjectDir(new File("projects/FileSuffixesTest/"))
      .setProjectKey(PROJECT_KEY)
      .setProjectName(PROJECT_KEY)
      .setProjectVersion("1.0")
      .setSourceDirs(".")
      .setProperty("sonar.sourceEncoding", "UTF-8");
  }

  @Test
  public void filesExtensionsHtml() {
    SonarRunner build = getSonarRunner()
      .setProperty("sonar.web.fileExtensions", ".html");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber()).isEqualTo(1);
  }

  @Test
  public void filesSuffixesHtml() {
    SonarRunner build = getSonarRunner()
      .setProperty("sonar.web.file.suffixes", ".html");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber()).isEqualTo(1);
  }

  @Test
  public void filesExtensionsHtmlPhp() {
    SonarRunner build = getSonarRunner()
      .setProperty("sonar.web.fileExtensions", ".html,.php");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber()).isEqualTo(2);
  }

  @Test
  public void filesSuffixesHtmlPhp() {
    SonarRunner build = getSonarRunner()
      .setProperty("sonar.web.file.suffixes", ".html,.php");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber()).isEqualTo(2);
  }

  @Test
  public void should_analyze_all_files_with_empty_extensions() {
    SonarRunner build = getSonarRunner()
      .setLanguage("web")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.web.fileExtensions", "");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber()).isEqualTo(3);
  }

  @Test
  public void should_analyze_all_files_with_empty_suffixes() {
    SonarRunner build = getSonarRunner()
      .setLanguage("web")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.web.file.suffixes", "");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber()).isEqualTo(3);
  }

  private Integer getAnalyzedFilesNumber() {
    Resource resource = orchestrator.getServer().getWsClient().find(ResourceQuery.createForMetrics(PROJECT_KEY, FILES_METRIC));
    return resource == null ? null : resource.getMeasure(FILES_METRIC).getIntValue();
  }

}
