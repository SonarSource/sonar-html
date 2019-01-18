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
import com.sonar.orchestrator.locator.FileLocation;
import com.sonar.orchestrator.locator.MavenLocation;
import java.io.File;
import java.util.List;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.issue.Issue;
import org.sonar.wsclient.issue.IssueClient;
import org.sonar.wsclient.issue.IssueQuery;

import static com.sonar.it.web.HtmlTestSuite.getMeasureAsInt;
import static org.assertj.core.api.Assertions.assertThat;

public class PhpFilesTest {

  private static final String PROJECT_KEY = "PhpFilesTest";
  private static final String FILES_METRIC = "files";
  @ClassRule
  public static Orchestrator orchestrator =  HtmlTestSuite.orchestrator;

  @ClassRule
  public static Orchestrator orchestratorWithPhp =  Orchestrator.builderEnv()
    .setSonarVersion(Optional.ofNullable(System.getProperty("sonar.runtimeVersion")).orElse("LATEST_RELEASE[6.7]"))
    .addPlugin(MavenLocation.of("org.sonarsource.php", "sonar-php-plugin", Optional.ofNullable(System.getProperty("sonarPhp.version")).orElse("LATEST_RELEASE")))
    .addPlugin(FileLocation.byWildcardMavenFilename(new File("../../sonar-html-plugin/target"), "sonar-html-plugin-*.jar"))
    .restoreProfileAtStartup(FileLocation.of("profiles/IllegalTab_profile.xml"))
    .build();

  @BeforeClass
  public static void init() {
    orchestratorWithPhp.resetData();
    orchestratorWithPhp.getServer().provisionProject(PROJECT_KEY, PROJECT_KEY);
    orchestratorWithPhp.getServer().associateProjectToQualityProfile(PROJECT_KEY, "web", "illegal_tab");
    orchestrator.getServer().provisionProject(PROJECT_KEY, PROJECT_KEY);
    orchestrator.getServer().associateProjectToQualityProfile(PROJECT_KEY, "web", "illegal_tab");
  }

  private static SonarScanner getSonarRunner() {
    return HtmlTestSuite.createSonarScanner()
      .setProjectDir(new File("projects/PhpFilesTest/"))
      .setProjectKey(PROJECT_KEY)
      .setProjectName(PROJECT_KEY)
      .setProjectVersion("1.0")
      .setSourceDirs(".")
      .setProperty("sonar.sourceEncoding", "UTF-8");
  }

  @Test
  public void plugin_should_not_conflict_with_php_analyzer() {
    analyzeFileAndCheckIssues(orchestratorWithPhp);
  }

  @Test
  public void file_should_get_analyzed_even_without_php() {
    analyzeFileAndCheckIssues(orchestrator);
  }

  private void analyzeFileAndCheckIssues(Orchestrator orchestrator) {
    SonarScanner build = getSonarRunner();
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber(orchestrator)).isEqualTo(2);
    IssueClient issueClient = orchestrator.getServer().wsClient().issueClient();
    List<Issue> issues = issueClient.find(
      IssueQuery.create()
        .components(PROJECT_KEY+":foo.php")
        .rules("Web:IllegalTabCheck"))
      .list();
    assertThat(issues.size()).isEqualTo(1);
  }

  private Integer getAnalyzedFilesNumber(Orchestrator orchestrator) {
    return getMeasureAsInt(orchestrator, PROJECT_KEY, FILES_METRIC);
  }

}
