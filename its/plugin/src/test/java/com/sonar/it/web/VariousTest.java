/*
 * SonarQube HTML
 * Copyright (C) 2011-2025 SonarSource SA
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
import java.util.Collections;
import java.util.List;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonarqube.ws.Issues;
import org.sonarqube.ws.client.issues.SearchRequest;

import static com.sonar.it.web.HtmlTestSuite.getMeasureAsInt;
import static com.sonar.it.web.HtmlTestSuite.newWsClient;
import static org.assertj.core.api.Assertions.assertThat;

public class VariousTest {

  @ClassRule
  public static Orchestrator orchestrator = HtmlTestSuite.orchestrator;

  @Test
  public void testExclusions() {
    String projectKey = "exclusions";
    orchestrator.getServer().provisionProject(projectKey, projectKey);
    orchestrator.getServer().associateProjectToQualityProfile(projectKey, "web", "IT");
    SonarScanner build = HtmlTestSuite.createSonarScanner()
      .setProjectDir(new File("projects/exclusions/"))
      .setProjectKey(projectKey)
      .setProjectName(projectKey)
      .setProjectVersion("1.0")
      .setSourceDirs("src")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.exclusions", "**/*Excluded*");
    orchestrator.executeBuild(build);

    assertThat(HtmlTestSuite.searchComponent(orchestrator, projectKey + ":src/httpError.jsp")).isNotNull();
    assertThat(HtmlTestSuite.searchComponent(orchestrator, projectKey + ":src/httpErrorExcluded.jsp")).isNull();
    assertThat(getMeasureAsInt(orchestrator, projectKey, "files")).isEqualTo(1);
  }

  /**
   * SONARPLUGINS-1897
   */
  @Test
  public void testCommentedOutCodeDetection() {
    String projectKey = "test";
    orchestrator.getServer().provisionProject(projectKey, projectKey);
    orchestrator.getServer().associateProjectToQualityProfile(projectKey, "web", "IT");
    SonarScanner build = HtmlTestSuite.createSonarScanner()
      .setProjectDir(new File("projects/continuum-webapp/"))
      .setProjectKey(projectKey)
      .setProjectName(projectKey)
      .setProjectVersion("1.0")
      .setSourceDirs("src")
      .setProperty("sonar.sourceEncoding", "UTF-8");
    orchestrator.executeBuild(build);

    SearchRequest request = new SearchRequest();
    request
      .setComponentKeys(Collections.singletonList(keyFor(projectKey, "WEB-INF/jsp/components/projectGroupNotifierSummaryComponent.jsp")))
      .setRules(Collections.singletonList("Web:AvoidCommentedOutCodeCheck"));


    List<Issues.Issue> issues = newWsClient(orchestrator).issues().search(request).getIssuesList();

    assertThat(issues).hasSize(2);
  }

  private static String keyFor(String project, String resource) {
    return project + ":src/" + resource;
  }

}
