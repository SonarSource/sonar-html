/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.internal.DefaultActiveRules;
import org.sonar.api.batch.rule.internal.NewActiveRule;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.internal.DefaultNoSonarFilter;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.Version;
import org.sonar.api.utils.log.LogTester;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.rules.HtmlRulesDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HtmlSensorTest {

  private static final Path TEST_DIR = Paths.get("src/test/resources/src/main/webapp");

  private HtmlSensor sensor;
  private SensorContextTester tester;

  @Rule
  public LogTester logTester = new LogTester();

  @Before
  public void setUp() {
    final SonarRuntime sonarRuntime = SonarRuntimeImpl.forSonarQube(Version.create(8, 9), SonarQubeSide.SCANNER, SonarEdition.COMMUNITY);
    HtmlRulesDefinition rulesDefinition = new HtmlRulesDefinition(sonarRuntime);
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);
    RulesDefinition.Repository repository = context.repository(HtmlRulesDefinition.REPOSITORY_KEY);

    List<NewActiveRule> ar = new ArrayList<>();
    for (RulesDefinition.Rule rule : repository.rules()) {
      ar.add(new NewActiveRule.Builder().setRuleKey(RuleKey.of(HtmlRulesDefinition.REPOSITORY_KEY, rule.key())).build());
    }
    ActiveRules activeRules = new DefaultActiveRules(ar);

    CheckFactory checkFactory = new CheckFactory(activeRules);
    FileLinesContextFactory fileLinesContextFactory = mock(FileLinesContextFactory.class);
    when(fileLinesContextFactory.createFor(Mockito.any(InputFile.class))).thenReturn(mock(FileLinesContext.class));
    sensor = new HtmlSensor(sonarRuntime, new DefaultNoSonarFilter(), fileLinesContextFactory, checkFactory);
    tester = SensorContextTester.create(TEST_DIR).setRuntime(sonarRuntime);
  }

  /**
   * Unit test which is more kind of an integration test. The purpose of this test is to get early feedback on changes in
   * the number of issues.
   */
  @Test
  public void testSensor() throws Exception {
    DefaultInputFile inputFile = createInputFile(TEST_DIR, "user-properties.jsp");
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    String componentKey = inputFile.key();
    assertThat(tester.measure(componentKey, CoreMetrics.NCLOC).value()).isEqualTo(224);
    assertThat(tester.measure(componentKey, CoreMetrics.COMMENT_LINES).value()).isEqualTo(14);
    assertThat(tester.measure(componentKey, CoreMetrics.COMPLEXITY).value()).isEqualTo(1);

    assertThat(tester.cpdTokens(componentKey)).hasSize(224);

    assertThat(tester.highlightingTypeAt(componentKey, 1, 0)).containsOnly(TypeOfText.COMMENT);
    assertThat(tester.highlightingTypeAt(componentKey, 18, 0)).containsOnly(TypeOfText.COMMENT);
    assertThat(tester.highlightingTypeAt(componentKey, 19, 0)).containsOnly(TypeOfText.ANNOTATION);
    assertThat(tester.highlightingTypeAt(componentKey, 29, 17)).containsOnly(TypeOfText.STRING);
    assertThat(tester.highlightingTypeAt(componentKey, 29, 0)).containsOnly(TypeOfText.KEYWORD);

    assertThat(tester.allIssues()).hasSize(106);
    assertThat(tester.allAnalysisErrors()).isEmpty();
  }

  @Test
  public void cancellation() throws Exception {
    DefaultInputFile inputFile = createInputFile(TEST_DIR, "user-properties.jsp");
    tester.fileSystem().add(inputFile);
    tester.setCancelled(true);
    sensor.execute(tester);
    assertThat(tester.allIssues()).isEmpty();
  }

  @Test
  public void sonarlint() throws Exception {
    tester.setRuntime(SonarRuntimeImpl.forSonarLint(Version.create(6, 7)));
    DefaultInputFile inputFile = createInputFile(TEST_DIR, "user-properties.jsp");
    tester.fileSystem().add(inputFile);
    sensor.execute(tester);
    String componentKey = inputFile.key();
    assertThat(tester.allIssues()).isNotEmpty();
    assertThat(tester.cpdTokens(componentKey)).isNull();
    assertThat(tester.highlightingTypeAt(componentKey, 1, 0)).isEmpty();
  }

  @Test
  public void unreadable_file() {
    tester.setRuntime(SonarRuntimeImpl.forSonarLint(Version.create(6, 7)));
    DefaultInputFile inputFile = new TestInputFileBuilder("key", "user-properties.jsp")
      .setModuleBaseDir(TEST_DIR)
      .setLanguage(HtmlConstants.LANGUAGE_KEY)
      .setType(InputFile.Type.MAIN)
      .setCharset(StandardCharsets.UTF_8)
      .build();
    tester.fileSystem().add(inputFile);
    sensor.execute(tester);
    String componentKey = inputFile.key();
    assertThat(tester.cpdTokens(componentKey)).isNull();
    assertThat(tester.allAnalysisErrors()).hasSize(1);
    assertThat(tester.allAnalysisErrors().iterator().next().inputFile()).isEqualTo(inputFile);
  }

  @Test
  public void testDescriptor() {
    DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
    sensor.describe(descriptor);
    assertThat(descriptor.name()).isEqualTo("HTML");
    assertThat(descriptor.languages()).isEmpty();
  }

  @Test
  public void test_descriptor_sonarlint() {
    DefaultSensorDescriptor sensorDescriptor = new DefaultSensorDescriptor();
    SonarRuntime sonarRuntime = SonarRuntimeImpl.forSonarLint(Version.create(6, 5));
    new HtmlSensor(sonarRuntime, null, null, new CheckFactory(new DefaultActiveRules(Collections.emptyList()))).describe(sensorDescriptor);
    assertThat(sensorDescriptor.name()).isEqualTo("HTML");
    assertThat(sensorDescriptor.languages()).isEmpty();
  }

  @Test
  public void test_descriptor_sonarqube_9_3() {
    final boolean[] called = {false};
    DefaultSensorDescriptor sensorDescriptor = new DefaultSensorDescriptor() {
      public SensorDescriptor processesFilesIndependently() {
        called[0] = true;
        return this;
      }
    };
    SonarRuntime sonarRuntime = SonarRuntimeImpl.forSonarQube(Version.create(9, 3), SonarQubeSide.SCANNER, SonarEdition.COMMUNITY);
    new HtmlSensor(sonarRuntime, null, null, new CheckFactory(new DefaultActiveRules(Collections.emptyList()))).describe(sensorDescriptor);
    assertThat(sensorDescriptor.name()).isEqualTo("HTML");
    assertThat(sensorDescriptor.languages()).isEmpty();
    assertTrue(called[0]);
  }

  @Test
  public void test_descriptor_sonarqube_9_3_reflection_failure() {
    DefaultSensorDescriptor sensorDescriptor = new DefaultSensorDescriptor();
    SonarRuntime sonarRuntime = SonarRuntimeImpl.forSonarQube(Version.create(9, 3), SonarQubeSide.SCANNER, SonarEdition.COMMUNITY);
    new HtmlSensor(sonarRuntime, null, null, new CheckFactory(new DefaultActiveRules(Collections.emptyList()))).describe(sensorDescriptor);
    assertThat(sensorDescriptor.name()).isEqualTo("HTML");
    assertThat(sensorDescriptor.languages()).isEmpty();
    assertTrue(logTester.logs().contains("Could not call SensorDescriptor.processesFilesIndependently() method"));
  }

  @Test
  public void php_file_should_not_have_metrics() {
    tester.setRuntime(SonarRuntimeImpl.forSonarQube(Version.create(7, 9), SonarQubeSide.SERVER, SonarEdition.COMMUNITY));
    DefaultInputFile inputFile = new TestInputFileBuilder("key", "foo.php")
      .setModuleBaseDir(TEST_DIR).setContents("<html>\n" +
        "<?php  ?>\n" +
        "<html>\n")
      .setLanguage("php")
      .setType(InputFile.Type.MAIN)
      .setCharset(StandardCharsets.UTF_8)
      .build();
    tester.fileSystem().add(inputFile);
    sensor.execute(tester);
    String componentKey = inputFile.key();
    assertThat(tester.cpdTokens(componentKey)).isNull();
    assertThat(tester.measures(componentKey)).isEmpty();
  }

  @Test
  public void vue_file_should_be_analyzed() throws Exception {
    DefaultInputFile inputFile = createInputFile(TEST_DIR, "foo.vue");
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    String componentKey = inputFile.key();
    assertThat(tester.measure(componentKey, CoreMetrics.NCLOC).value()).isEqualTo(6);
    assertThat(tester.measure(componentKey, CoreMetrics.COMMENT_LINES).value()).isEqualTo(1);

    assertThat(tester.allIssues()).hasSize(4);
    assertThat(tester.allAnalysisErrors()).isEmpty();
  }

  @Test
  public void twig_file_should_be_analyzed() throws Exception {
    DefaultInputFile inputFile = createInputFile(TEST_DIR, "foo.twig");
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    String componentKey = inputFile.key();
    assertThat(tester.measure(componentKey, CoreMetrics.NCLOC).value()).isEqualTo(15);
    assertThat(tester.measure(componentKey, CoreMetrics.COMMENT_LINES).value()).isEqualTo(1);

    assertThat(tester.allIssues()).hasSize(3);
    assertThat(tester.allAnalysisErrors()).isEmpty();
  }

  private DefaultInputFile createInputFile(Path dir, String fileName) throws IOException {
    return new TestInputFileBuilder("key", fileName)
      .setModuleBaseDir(dir)
      .setLanguage(HtmlConstants.LANGUAGE_KEY)
      .setType(InputFile.Type.MAIN)
      .initMetadata(new String(Files.readAllBytes(dir.resolve(fileName)), StandardCharsets.UTF_8))
      .setCharset(StandardCharsets.UTF_8)
      .build();
  }
}
