/*
 * SonarQube :: Web :: ITs :: Plugin
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

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assume.assumeNotNull;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.issue.Issue;
import org.sonar.wsclient.issue.IssueClient;
import org.sonar.wsclient.issue.IssueQuery;
import org.sonar.wsclient.services.ResourceQuery;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarRunner;
import com.sonar.orchestrator.locator.FileLocation;

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
