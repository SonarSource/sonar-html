/*
 * SonarSource :: Web :: Sonar Plugin
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
package org.sonar.plugins.web.core;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.FileMetadata;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.rule.internal.DefaultActiveRules;
import org.sonar.api.batch.rule.internal.NewActiveRule;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.web.api.WebConstants;
import org.sonar.plugins.web.rules.WebRulesDefinition;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebSensorTest {

  private static final File TEST_DIR = new File("src/test/resources/src/main/webapp");

  private WebSensor sensor;
  private SensorContextTester tester;

  @Before
  public void setUp() throws Exception {
    WebRulesDefinition rulesDefinition = new WebRulesDefinition();
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);
    RulesDefinition.Repository repository = context.repository(WebRulesDefinition.REPOSITORY_KEY);

    List<NewActiveRule> ar = new ArrayList<>();
    for (RulesDefinition.Rule rule : repository.rules()) {
      ar.add(new ActiveRulesBuilder().create(RuleKey.of(WebRulesDefinition.REPOSITORY_KEY, rule.key())));
    }
    ActiveRules activeRules = new DefaultActiveRules(ar);

    CheckFactory checkFactory = new CheckFactory(activeRules);
    FileLinesContextFactory fileLinesContextFactory = mock(FileLinesContextFactory.class);
    when(fileLinesContextFactory.createFor(Mockito.any(InputFile.class))).thenReturn(mock(FileLinesContext.class));
    sensor = new WebSensor(new NoSonarFilter(), fileLinesContextFactory, checkFactory);
    tester = SensorContextTester.create(TEST_DIR);
  }

  /**
   * Unit test which is more kind of an integration test. The purpose of this test is to get early feedback on changes in
   * the number of issues.
   */
  @Test
  public void testSensor() throws Exception {
    DefaultInputFile inputFile = new DefaultInputFile("key", "user-properties.jsp")
      .setLanguage(WebConstants.LANGUAGE_KEY)
      .setType(InputFile.Type.MAIN)
      .initMetadata(new FileMetadata().readMetadata(new FileReader(new File(TEST_DIR, "user-properties.jsp"))));

    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    String componentKey = inputFile.key();
    assertThat(tester.measure(componentKey, CoreMetrics.NCLOC).value()).isEqualTo(227);
    assertThat(tester.measure(componentKey, CoreMetrics.COMMENT_LINES).value()).isEqualTo(14);
    assertThat(tester.measure(componentKey, CoreMetrics.COMPLEXITY).value()).isEqualTo(1);

    assertThat(tester.cpdTokens(componentKey)).hasSize(224);

    assertThat(tester.highlightingTypeAt(componentKey, 1, 0)).containsOnly(TypeOfText.COMMENT);
    assertThat(tester.highlightingTypeAt(componentKey, 18, 0)).containsOnly(TypeOfText.COMMENT);
    assertThat(tester.highlightingTypeAt(componentKey, 19, 0)).containsOnly(TypeOfText.ANNOTATION);
    assertThat(tester.highlightingTypeAt(componentKey, 29, 17)).containsOnly(TypeOfText.STRING);
    assertThat(tester.highlightingTypeAt(componentKey, 29, 0)).containsOnly(TypeOfText.KEYWORD);

    assertThat(tester.allIssues()).hasSize(84);
  }

  @Test
  public void testDescriptor() {
    DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
    sensor.describe(descriptor);
    assertThat(descriptor.name()).isEqualTo("Web");
    assertThat(descriptor.languages()).containsOnly("web");
  }
}
