/*
 * SonarSource :: Web :: ITs :: Plugin
 * Copyright (c) 2011-2017 SonarSource SA and Matthijs Galesloot
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
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.sonarqube.ws.WsComponents.Component;
import org.sonarqube.ws.WsMeasures;
import org.sonarqube.ws.WsMeasures.Measure;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.WsClientFactories;
import org.sonarqube.ws.client.component.ShowWsRequest;
import org.sonarqube.ws.client.component.TreeWsRequest;
import org.sonarqube.ws.client.measure.ComponentWsRequest;

import javax.annotation.CheckForNull;
import java.util.Collections;
import java.util.List;

@RunWith(Suite.class)
@SuiteClasses({
  FileSuffixesTest.class,
  StandardMeasuresTest.class,
  VariousTest.class,
  SonarLintTest.class
})
public class WebTestSuite {

  public static SonarScanner createSonarScanner() {
    return SonarScanner.create();
  }

  @CheckForNull
  static Measure getMeasure(Orchestrator orchestrator, String componentKey, String metricKey) {
    WsMeasures.ComponentWsResponse response = newWsClient(orchestrator).measures().component(new ComponentWsRequest()
      .setComponentKey(componentKey)
      .setMetricKeys(Collections.singletonList(metricKey)));
    List<Measure> measures = response.getComponent().getMeasuresList();
    return measures.size() == 1 ? measures.get(0) : null;
  }

  @CheckForNull
  static Integer getMeasureAsInt(Orchestrator orchestrator, String componentKey, String metricKey) {
    Measure measure = getMeasure(orchestrator, componentKey, metricKey);
    return (measure == null) ? null : Integer.parseInt(measure.getValue());
  }

  @CheckForNull
  static Double getMeasureAsDouble(Orchestrator orchestrator, String componentKey, String metricKey) {
    Measure measure = getMeasure(orchestrator, componentKey, metricKey);
    return (measure == null) ? null : Double.parseDouble(measure.getValue());
  }

  @CheckForNull
  static Component searchComponent(Orchestrator orchestrator, String projectKey, String componentKey) {
    List<Component> components = newWsClient(orchestrator).components().tree(
      new TreeWsRequest()
        .setBaseComponentKey(projectKey)
        .setQuery(componentKey))
      .getComponentsList();
    return components.size() == 1 ? components.get(0) : null;
  }

  static Component getComponent(Orchestrator orchestrator, String componentKey) {
    return newWsClient(orchestrator).components().show(new ShowWsRequest().setKey(componentKey)).getComponent();
  }

  static WsClient newWsClient(Orchestrator orchestrator) {
    return WsClientFactories.getDefault().newClient(HttpConnector.newBuilder()
      .url(orchestrator.getServer().getUrl())
      .build());
  }
}
