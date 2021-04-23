/*
 * SonarSource :: HTML :: ITs :: Plugin
 * Copyright (c) 2011-2021 SonarSource SA and Matthijs Galesloot
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

  @Test
  public void should_analyze_only_php_files_with_empty_suffixes() {
    String projectKey = "FileSuffixesTest-empty_suffixes";
    SonarScanner build = getSonarRunner(projectKey)
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.html.file.suffixes", "");
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber(projectKey)).isEqualTo(1);
  }

  private Integer getAnalyzedFilesNumber(String projectKey) {
    return getMeasureAsInt(orchestrator, projectKey, FILES_METRIC);
  }

}
