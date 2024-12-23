/*
 * SonarQube HTML Plugin :: ITs :: Plugin
 * Copyright (C) 2011-2024 SonarSource SA
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
import java.io.File;
import java.util.List;
import java.util.Optional;
import javax.annotation.CheckForNull;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.sonarqube.ws.Components;
import org.sonarqube.ws.Measures;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.HttpException;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.WsClientFactories;
import org.sonarqube.ws.client.components.ShowRequest;
import org.sonarqube.ws.client.measures.ComponentRequest;

import static java.util.Collections.singletonList;

@RunWith(Suite.class)
@SuiteClasses({
  FileSuffixesTest.class,
  StandardMeasuresTest.class,
  VariousTest.class,
  SonarLintTest.class,
  PhpFilesTest.class
})
public class HtmlTestSuite {

  @ClassRule
  public static Orchestrator orchestrator = Orchestrator.builderEnv()
    .useDefaultAdminCredentialsForBuilds(true)
    .setSonarVersion(sonarVersion())
    .addPlugin(htmlPlugin())
    .restoreProfileAtStartup(FileLocation.ofClasspath("/com/sonar/it/web/backup.xml"))
    .restoreProfileAtStartup(FileLocation.of("profiles/no_rule.xml"))
    .restoreProfileAtStartup(FileLocation.of("profiles/IllegalTab_profile.xml"))
    .build();

  static FileLocation htmlPlugin() {
    return FileLocation.byWildcardMavenFilename(new File("../../sonar-html-plugin/target"), "sonar-html-plugin-*.jar");
  }

  static String sonarVersion() {
    return Optional.ofNullable(System.getProperty("sonar.runtimeVersion")).orElse("LATEST_RELEASE");
  }

  public static SonarScanner createSonarScanner() {
    return SonarScanner.create();
  }

  static Measures.Measure getMeasure(Orchestrator orchestrator, String componentKey, String metricKey) {
    Measures.ComponentWsResponse response = newWsClient(orchestrator).measures().component(new ComponentRequest()
      .setComponent(componentKey)
      .setMetricKeys(singletonList(metricKey)));
    List<Measures.Measure> measures = response.getComponent().getMeasuresList();
    return measures.size() == 1 ? measures.get(0) : null;
  }

  @CheckForNull
  static Integer getMeasureAsInt(Orchestrator orchestrator, String componentKey, String metricKey) {
    Measures.Measure measure = getMeasure(orchestrator, componentKey, metricKey);
    return (measure == null) ? null : Integer.parseInt(measure.getValue());
  }

  @CheckForNull
  static Double getMeasureAsDouble(Orchestrator orchestrator, String componentKey, String metricKey) {
    Measures.Measure measure = getMeasure(orchestrator, componentKey, metricKey);
    return (measure == null) ? null : Double.parseDouble(measure.getValue());
  }

  /**
   * Since SQ 8.4, it is not possible anymore to use "search" API to get a component when knowing its key.
   * As a workaround, this method tries to "show" a component. If it is not found (404), it returns null.
   * Any other unexpected issue when querying the web API will be thrown.
   */
  @CheckForNull
  static Components.Component searchComponent(Orchestrator orchestrator, String componentKey) {
    ShowRequest showRequest = new ShowRequest().setComponent(componentKey);
    try {
      return newWsClient(orchestrator).components().show(showRequest).getComponent();
    } catch (HttpException e) {
      // has not been able to find the component
      if (e.code() == 404) {
        return null;
      }
      // any other unexpected event
      throw e;
    }
  }

  static WsClient newWsClient(Orchestrator orchestrator) {
    return WsClientFactories.getDefault().newClient(HttpConnector.newBuilder()
      .url(orchestrator.getServer().getUrl())
      .build());
  }
}
