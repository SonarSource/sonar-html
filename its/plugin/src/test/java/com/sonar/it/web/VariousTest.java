/*
 * SonarSource :: HTML :: ITs :: Plugin
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
import java.io.File;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.issue.Issue;
import org.sonar.wsclient.issue.IssueClient;
import org.sonar.wsclient.issue.IssueQuery;

import static com.sonar.it.web.HtmlTestSuite.getMeasureAsInt;
import static org.assertj.core.api.Assertions.assertThat;

public class VariousTest {

  @ClassRule
  public static Orchestrator orchestrator = HtmlTestSuite.orchestrator;

  @BeforeClass
  public static void init() {
    orchestrator.resetData();
  }

  @Test
  public void testExclusions() {
    String projectKey = "exclusions";
    orchestrator.getServer().provisionProject(projectKey, projectKey);
    orchestrator.getServer().associateProjectToQualityProfile(projectKey, "html", "IT");
    SonarScanner build = HtmlTestSuite.createSonarScanner()
      .setProjectDir(new File("projects/exclusions/"))
      .setProjectKey(projectKey)
      .setProjectName(projectKey)
      .setProjectVersion("1.0")
      .setSourceDirs("src")
      .setProperty("sonar.html.file.suffixes", "jsp")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.exclusions", "**/*Excluded*");
    orchestrator.executeBuild(build);

    assertThat(HtmlTestSuite.searchComponent(orchestrator, projectKey, "exclusions:src/httpError.jsp")).isNotNull();
    assertThat(HtmlTestSuite.searchComponent(orchestrator, projectKey, "exclusions:src/httpErrorExcluded.jsp")).isNull();
    assertThat(getMeasureAsInt(orchestrator, projectKey, "files")).isEqualTo(1);
  }

  /**
   * SONARPLUGINS-1897
   */
  @Test
  public void testCommentedOutCodeDetection() {
    String projectKey = "test";
    orchestrator.getServer().provisionProject(projectKey, projectKey);
    orchestrator.getServer().associateProjectToQualityProfile(projectKey, "html", "IT");
    SonarScanner build = HtmlTestSuite.createSonarScanner()
      .setProjectDir(new File("projects/continuum-webapp/"))
      .setProjectKey(projectKey)
      .setProjectName(projectKey)
      .setProjectVersion("1.0")
      .setSourceDirs("src")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.html.file.suffixes", ".xhtml,.jspf,.jsp");
    orchestrator.executeBuild(build);

    IssueClient issueClient = orchestrator.getServer().wsClient().issueClient();

    List<Issue> issues = issueClient.find(
      IssueQuery.create()
        .components(keyFor(projectKey, "WEB-INF/jsp/components/projectGroupNotifierSummaryComponent.jsp"))
        .rules("Web:AvoidCommentedOutCodeCheck"))
      .list();
    assertThat(issues.size()).isEqualTo(2);
  }

  private static String keyFor(String project, String resource) {
    return project + ":src/" + resource;
  }

}
