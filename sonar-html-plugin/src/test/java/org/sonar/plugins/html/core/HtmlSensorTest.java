/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
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

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.rule.internal.DefaultActiveRules;
import org.sonar.api.batch.rule.internal.NewActiveRule;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.Version;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.rules.HtmlRulesDefinition;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HtmlSensorTest {

  private static final File TEST_DIR = new File("src/test/resources/src/main/webapp");

  private HtmlSensor sensor;
  private SensorContextTester tester;

  @Before
  public void setUp() throws Exception {
    HtmlRulesDefinition rulesDefinition = new HtmlRulesDefinition();
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);
    RulesDefinition.Repository repository = context.repository(HtmlRulesDefinition.REPOSITORY_KEY);

    List<NewActiveRule> ar = new ArrayList<>();
    for (RulesDefinition.Rule rule : repository.rules()) {
      ar.add(new ActiveRulesBuilder().create(RuleKey.of(HtmlRulesDefinition.REPOSITORY_KEY, rule.key())));
    }
    ActiveRules activeRules = new DefaultActiveRules(ar);

    CheckFactory checkFactory = new CheckFactory(activeRules);
    FileLinesContextFactory fileLinesContextFactory = mock(FileLinesContextFactory.class);
    when(fileLinesContextFactory.createFor(Mockito.any(InputFile.class))).thenReturn(mock(FileLinesContext.class));
    sensor = new HtmlSensor(new NoSonarFilter(), fileLinesContextFactory, checkFactory);
    tester = SensorContextTester.create(TEST_DIR);
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

    assertThat(tester.allIssues()).hasSize(84);
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
      .setModuleBaseDir(TEST_DIR.toPath())
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
    assertThat(descriptor.languages()).containsOnly("html");
  }

  private DefaultInputFile createInputFile(File dir, String fileName) throws IOException {
    return new TestInputFileBuilder("key", fileName)
      .setModuleBaseDir(dir.toPath())
      .setLanguage(HtmlConstants.LANGUAGE_KEY)
      .setType(InputFile.Type.MAIN)
      .initMetadata(Files.toString(new File(dir, fileName), StandardCharsets.UTF_8))
      .setCharset(StandardCharsets.UTF_8)
      .build();
  }
}
