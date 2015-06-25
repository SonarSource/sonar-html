/*
 * Copyright (C) 2011-2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.it.web;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarRunner;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

public class FileSuffixesTest {

  @ClassRule
  public static Orchestrator orchestrator = WebTestSuite.ORCHESTRATOR;

  @BeforeClass
  public static void init() throws Exception {
    orchestrator.resetData();
  }

  @Test
  public void filesExtensionsHtml() {
    SonarRunner build = WebTestSuite.createSonarRunner()
      .setProjectDir(new File("projects/FileSuffixesTest/"))
      .setProjectKey("FileSuffixesTest")
      .setProjectName("FileSuffixesTest")
      .setProjectVersion("1.0")
      .setSourceDirs(".")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.web.fileExtensions", ".html")
      .setProfile("no_rule");
    orchestrator.executeBuild(build);

    Sonar wsClient = orchestrator.getServer().getWsClient();
    assertThat(getProjectMeasure(wsClient, "FileSuffixesTest", "files").getIntValue()).isEqualTo(1);
  }

  @Test
  public void filesSuffixesHtml() {
    assumeTrue(orchestrator.getConfiguration().getPluginVersion("web").isGreaterThanOrEquals("2.2"));

    SonarRunner build = WebTestSuite.createSonarRunner()
      .setProjectDir(new File("projects/FileSuffixesTest/"))
      .setProjectKey("FileSuffixesTest")
      .setProjectName("FileSuffixesTest")
      .setProjectVersion("1.0")
      .setSourceDirs(".")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.web.file.suffixes", ".html")
      .setProfile("no_rule");
    orchestrator.executeBuild(build);

    Sonar wsClient = orchestrator.getServer().getWsClient();
    assertThat(getProjectMeasure(wsClient, "FileSuffixesTest", "files").getIntValue()).isEqualTo(1);
  }

  @Test
  public void filesExtensionsHtmlPhp() {
    SonarRunner build = WebTestSuite.createSonarRunner()
      .setProjectDir(new File("projects/FileSuffixesTest/"))
      .setProjectKey("FileSuffixesTest")
      .setProjectName("FileSuffixesTest")
      .setProjectVersion("1.0")
      .setSourceDirs(".")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.web.fileExtensions", ".html,.php")
      .setProfile("no_rule");
    orchestrator.executeBuild(build);

    Sonar wsClient = orchestrator.getServer().getWsClient();
    assertThat(getProjectMeasure(wsClient, "FileSuffixesTest", "files").getIntValue()).isEqualTo(2);
  }

  @Test
  public void filesSuffixesHtmlPhp() {
    SonarRunner build = WebTestSuite.createSonarRunner()
      .setProjectDir(new File("projects/FileSuffixesTest/"))
      .setProjectKey("FileSuffixesTest")
      .setProjectName("FileSuffixesTest")
      .setProjectVersion("1.0")
      .setSourceDirs(".")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.web.file.suffixes", ".html,.php")
      .setProfile("no_rule");
    orchestrator.executeBuild(build);

    Sonar wsClient = orchestrator.getServer().getWsClient();
    assertThat(getProjectMeasure(wsClient, "FileSuffixesTest", "files").getIntValue()).isEqualTo(2);
  }

  @Test
  public void should_analyze_all_files_with_empty_extensions() {
    assumeTrue(orchestrator.getConfiguration().getPluginVersion("web").isGreaterThanOrEquals("1.3"));

    SonarRunner build = WebTestSuite.createSonarRunner()
      .setProjectDir(new File("projects/FileSuffixesTest/"))
      .setProjectKey("FileSuffixesTest")
      .setProjectName("FileSuffixesTest")
      .setProjectVersion("1.0")
      .setLanguage("web")
      .setSourceDirs(".")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.web.fileExtensions", "")
      .setProfile("no_rule");
    orchestrator.executeBuild(build);

    Sonar wsClient = orchestrator.getServer().getWsClient();
    assertThat(getProjectMeasure(wsClient, "FileSuffixesTest", "files").getIntValue()).isEqualTo(3);
  }

  @Test
  public void should_analyze_all_files_with_empty_suffixes() {
    assumeTrue(orchestrator.getConfiguration().getPluginVersion("web").isGreaterThanOrEquals("2.2"));

    SonarRunner build = WebTestSuite.createSonarRunner()
      .setProjectDir(new File("projects/FileSuffixesTest/"))
      .setProjectKey("FileSuffixesTest")
      .setProjectName("FileSuffixesTest")
      .setProjectVersion("1.0")
      .setLanguage("web")
      .setSourceDirs(".")
      .setProperty("sonar.sourceEncoding", "UTF-8")
      .setProperty("sonar.web.file.suffixes", "")
      .setProfile("no_rule");
    orchestrator.executeBuild(build);

    Sonar wsClient = orchestrator.getServer().getWsClient();
    assertThat(getProjectMeasure(wsClient, "FileSuffixesTest", "files").getIntValue()).isEqualTo(3);
  }

  private Measure getProjectMeasure(Sonar wsClient, String resourceKey, String metricKey) {
    Resource resource = wsClient.find(ResourceQuery.createForMetrics(resourceKey, metricKey));
    return resource == null ? null : resource.getMeasure(metricKey);
  }

}
