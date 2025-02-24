/*
 * SonarQube HTML
 * Copyright (C) 2010-2024 SonarSource SA
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
import com.sonar.orchestrator.locator.FileLocation;
import com.sonar.orchestrator.locator.MavenLocation;
import java.io.File;
import java.util.List;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonarqube.ws.Issues;
import org.sonarqube.ws.client.issues.SearchRequest;

import static com.sonar.it.web.HtmlTestSuite.getMeasureAsInt;
import static com.sonar.it.web.HtmlTestSuite.newWsClient;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class PhpFilesTest {

  private static final String PROJECT_KEY = "PhpFilesTest";
  private static final String FILES_METRIC = "files";
  @ClassRule
  public static Orchestrator orchestrator =  HtmlTestSuite.orchestrator;

  @ClassRule
  public static Orchestrator orchestratorWithPhp =  Orchestrator.builderEnv()
    .useDefaultAdminCredentialsForBuilds(true)
    // This a second instance of orchestrator with SonarPhp plugin, if 'orchestrator.container.port' is set
    // it should not be used by this instance to not have two sonarqube servers on the same port
    .setOrchestratorProperty("orchestrator.container.port", "")
    .setSonarVersion(HtmlTestSuite.sonarVersion())
    .addPlugin(MavenLocation.of("org.sonarsource.php", "sonar-php-plugin", Optional.ofNullable(System.getProperty("sonarPhp.version")).orElse("LATEST_RELEASE")))
    .addPlugin(HtmlTestSuite.htmlPlugin())
    .restoreProfileAtStartup(FileLocation.of("profiles/IllegalTab_profile.xml"))
    .build();

  @BeforeClass
  public static void init() {
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

  private void analyzeFileAndCheckIssues(Orchestrator orchestrator) {
    SonarScanner build = getSonarRunner();
    orchestrator.executeBuild(build);
    assertThat(getAnalyzedFilesNumber(orchestrator)).isEqualTo(2);

    SearchRequest request = new SearchRequest()
      .setProjects(singletonList(PROJECT_KEY))
      .setComponentKeys(singletonList(PROJECT_KEY + ":" + "foo.php"))
      .setRules(singletonList("Web:IllegalTabCheck"));
    List<Issues.Issue> issues = newWsClient(orchestrator).issues().search(request).getIssuesList();

    assertThat(issues).hasSize(1);
  }

  private Integer getAnalyzedFilesNumber(Orchestrator orchestrator) {
    return getMeasureAsInt(orchestrator, PROJECT_KEY, FILES_METRIC);
  }

}
