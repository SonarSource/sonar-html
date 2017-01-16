/*
 * SonarSource :: Web :: ITs :: Plugin
 * Copyright (c) 2011-2017 SonarSource SA and Matthijs Galesloot
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

import java.io.File;

import static com.sonar.it.web.WebTestSuite.getMeasureAsInt;
import static org.assertj.core.api.Assertions.assertThat;

public class FileSuffixesTest {

  private static final String PROJECT_KEY = "FileSuffixesTest";
  private static final String FILES_METRIC = "files";

  @ClassRule
  public static Orchestrator orchestrator = Orchestrator.builderEnv()
    .addPlugin(FileLocation.byWildcardMavenFilename(new File("../../sonar-web-plugin/target"), "sonar-web-plugin-*.jar"))
    .restoreProfileAtStartup(FileLocation.of("profiles/no_rule.xml"))
    .build();

  @BeforeClass
  public static void init() throws Exception {
    orchestrator.resetData();
    orchestrator.getServer().provisionProject(PROJECT_KEY, PROJECT_KEY);
    orchestrator.getServer().associateProjectToQualityProfile(PROJECT_KEY, "web", "no_rule");
  }

  private static SonarScanner getSonarRunner() {
    return WebTestSuite.createSonarScanner()
      .setProjectDir(new File("projects/FileSuffixesTest/"))
      .setProjectKey(PROJECT_KEY)
      .setProjectName(PROJECT_KEY)
      .setProjectVersion("1.0")
      .setSourceDirs(".")
      .setProperty("sonar.sourceEncoding", "UTF-8");
  }

  @Test
  public void filesExtensionsHtml() {
    SonarScanner build = getSonarRunner()
      .setProperty("sonar.web.fileExtensions", ".html");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber()).isEqualTo(1);
  }

  @Test
  public void filesSuffixesHtml() {
    SonarScanner build = getSonarRunner()
      .setProperty("sonar.web.file.suffixes", ".html");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber()).isEqualTo(1);
  }

  @Test
  public void filesExtensionsHtmlPhp() {
    SonarScanner build = getSonarRunner()
      .setProperty("sonar.web.fileExtensions", ".html,.php");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber()).isEqualTo(2);
  }

  @Test
  public void filesSuffixesHtmlPhp() {
    SonarScanner build = getSonarRunner()
      .setProperty("sonar.web.file.suffixes", ".html,.php");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber()).isEqualTo(2);
  }

  @Test
  public void should_analyze_all_files_with_empty_extensions() {
    SonarScanner build = getSonarRunner()
      .setLanguage("web")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.web.fileExtensions", "");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber()).isEqualTo(3);
  }

  @Test
  public void should_analyze_all_files_with_empty_suffixes() {
    SonarScanner build = getSonarRunner()
      .setLanguage("web")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.web.file.suffixes", "");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber()).isEqualTo(3);
  }

  private Integer getAnalyzedFilesNumber() {
    return getMeasureAsInt(orchestrator, PROJECT_KEY, FILES_METRIC);
  }

}
