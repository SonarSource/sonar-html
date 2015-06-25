/*
 * Copyright (C) 2011-2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.it.web;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarRunner;
import com.sonar.orchestrator.locator.FileLocation;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.issue.Issue;
import org.sonar.wsclient.issue.IssueClient;
import org.sonar.wsclient.issue.IssueQuery;
import org.sonar.wsclient.services.ResourceQuery;

import java.io.File;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assume.assumeNotNull;

public class VariousTest {

  private static String webPluginVersion;

  @ClassRule
  public static Orchestrator orchestrator = Orchestrator.builderEnv()
    .addPlugin(WebTestSuite.PLUGIN_KEY)
    .setMainPluginKey(WebTestSuite.PLUGIN_KEY)
    .restoreProfileAtStartup(FileLocation.ofClasspath("/com/sonar/it/web/backup.xml"))
    .build();

  @BeforeClass
  public static void init() throws Exception {
    webPluginVersion = orchestrator.getConfiguration().getString("webVersion");
    assumeNotNull(webPluginVersion);

    orchestrator.resetData();
  }

  @Test
  public void testExclusions() {
    SonarRunner build = WebTestSuite.createSonarRunner()
      .setProjectDir(new File("projects/exclusions/"))
      .setProjectKey("exclusions")
      .setProjectName("exclusions")
      .setProjectVersion("1.0")
      .setSourceDirs("src")
      .setProperty("sonar.web.file.suffixes", "jsp")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.exclusions", "**/*Excluded*")
      .setProfile("IT");
    orchestrator.executeBuild(build);

    Sonar wsClient = orchestrator.getServer().getWsClient();
    if (orchestrator.getServer().version().isGreaterThanOrEquals("4.2")) {
      assertThat(wsClient.find(new ResourceQuery("exclusions:src/httpError.jsp"))).isNotNull();
      assertThat(wsClient.find(new ResourceQuery("exclusions:src/httpErrorExcluded.jsp"))).isNull();
    } else {
      assertThat(wsClient.find(new ResourceQuery("exclusions:httpError.jsp"))).isNotNull();
      assertThat(wsClient.find(new ResourceQuery("exclusions:httpErrorExcluded.jsp"))).isNull();
    }
    assertThat(wsClient.find(ResourceQuery.createForMetrics("exclusions", "files")).getMeasureIntValue("files")).isEqualTo(1);
  }

  /**
   * SONARPLUGINS-1897
   */
  @Test
  public void testCommentedOutCodeDetection() {
    SonarRunner build = WebTestSuite.createSonarRunner()
      .setProjectDir(new File("projects/continuum-webapp/"))
      .setProjectKey("test")
      .setProjectName("test")
      .setProjectVersion("1.0")
      .setSourceDirs("src")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.web.fileExtensions", ".xhtml,.jspf,.jsp")
      .setProfile("IT");
    orchestrator.executeBuild(build);

    IssueClient issueClient = orchestrator.getServer().wsClient().issueClient();

    List<Issue> issues = issueClient.find(
      IssueQuery.create()
        .components(keyFor("test", "WEB-INF/jsp/components/projectGroupNotifierSummaryComponent.jsp"))
        .rules("Web:AvoidCommentedOutCodeCheck")).list();
    assertThat(issues.size()).isEqualTo(2);
  }

  private static String keyFor(String project, String resource) {
    return project + ":" + (orchestrator.getConfiguration().getSonarVersion().isGreaterThanOrEquals("4.2") ? "src/" : "") + resource;
  }

}
