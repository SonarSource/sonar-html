/*
 * SonarSource :: HTML :: ITs :: Plugin
 * Copyright (c) 2011-2020 SonarSource SA and Matthijs Galesloot
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

  public static boolean sonarqubeGreaterThan75() {
    return orchestrator.getServer().version().isGreaterThanOrEquals(7, 6);
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
