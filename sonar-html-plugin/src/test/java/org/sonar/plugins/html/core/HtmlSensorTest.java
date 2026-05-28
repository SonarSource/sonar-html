/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
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
import org.sonar.api.batch.sensor.issue.IssueResolution;
import org.sonar.api.batch.sensor.issue.internal.DefaultNoSonarFilter;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.notifications.AnalysisWarnings;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.testfixtures.log.LogTesterJUnit5;
import org.sonar.api.utils.Version;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.checks.sonar.AllowedLangAttributeCheck;
import org.sonar.plugins.html.rules.HtmlRulesDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HtmlSensorTest {

  private static final Path TEST_DIR = Paths.get("src/test/resources/src/main/webapp");

  private HtmlSensor sensor;
  private SensorContextTester tester;
  private RecordingAnalysisWarnings analysisWarnings;

  @RegisterExtension
  public LogTesterJUnit5 logTester = new LogTesterJUnit5();

  @BeforeEach
  void setUp() {
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
    analysisWarnings = new RecordingAnalysisWarnings();
    sensor = new HtmlSensor(sonarRuntime, new DefaultNoSonarFilter(), fileLinesContextFactory, checkFactory,
      new AnalysisWarningsWrapper(analysisWarnings));
    tester = SensorContextTester.create(TEST_DIR).setRuntime(sonarRuntime);
  }

  /**
   * Unit test which is more kind of an integration test. The purpose of this test is to get early feedback on changes in
   * the number of issues.
   */
  @Test
  void testSensor() throws IOException {
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

    assertThat(tester.allIssues()).hasSize(107);
    assertThat(tester.allAnalysisErrors()).isEmpty();
  }

  @Test
  void cancellation() throws IOException {
    DefaultInputFile inputFile = createInputFile(TEST_DIR, "user-properties.jsp");
    tester.fileSystem().add(inputFile);
    tester.setCancelled(true);
    sensor.execute(tester);
    assertThat(tester.allIssues()).isEmpty();
  }

  @Test
  void adds_analysis_warning_when_allowed_languages_are_empty() throws IOException {
    DefaultInputFile inputFile = createInputFile(TEST_DIR, "user-properties.jsp");
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    assertThat(analysisWarnings.warnings()).contains(AllowedLangAttributeCheck.EMPTY_ALLOWED_LANGUAGES_WARNING);
  }

  @Test
  void sonarlint() throws IOException {
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
  void sonar_resolve_is_saved_at_minimum_supported_runtime() {
    tester.setRuntime(SonarRuntimeImpl.forSonarQube(Version.create(13, 5), SonarQubeSide.SCANNER, SonarEdition.COMMUNITY));
    DefaultInputFile inputFile = createInputFile("sonar-resolve.html", String.join("\n",
      "<div>",
      "<!-- sonar-resolve [fp] Web:S5256 \"reason\" -->",
      "</div>"));
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    assertThat(issueResolutions(inputFile)).singleElement().satisfies(issueResolution -> {
      assertThat(issueResolution.status()).isEqualTo(IssueResolution.Status.FALSE_POSITIVE);
      assertThat(issueResolution.ruleKeys()).containsExactly(RuleKey.of(HtmlRulesDefinition.REPOSITORY_KEY, "S5256"));
      assertThat(issueResolution.comment()).isEqualTo("reason");
      assertThat(issueResolution.textRange().start().line()).isEqualTo(2);
    });
  }

  @Test
  void sonar_resolve_is_ignored_before_supported_runtime() {
    tester.setRuntime(SonarRuntimeImpl.forSonarQube(Version.create(13, 4), SonarQubeSide.SCANNER, SonarEdition.COMMUNITY));
    DefaultInputFile inputFile = createInputFile("sonar-resolve.html", String.join("\n",
      "<div>",
      "<!-- sonar-resolve Web:S5256 \"reason\" -->",
      "</div>"));
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    assertThat(issueResolutions(inputFile)).isEmpty();
  }

  @Test
  void sonar_resolve_is_ignored_in_sonarlint() {
    tester.setRuntime(SonarRuntimeImpl.forSonarLint(Version.create(13, 6)));
    DefaultInputFile inputFile = createInputFile("sonar-resolve.html", String.join("\n",
      "<div>",
      "<!-- sonar-resolve Web:S5256 \"reason\" -->",
      "</div>"));
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    assertThat(issueResolutions(inputFile)).isEmpty();
  }

  @Test
  void unreadable_file() {
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
  void testDescriptor() {
    DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
    sensor.describe(descriptor);
    assertThat(descriptor.name()).isEqualTo("HTML");
    assertThat(descriptor.languages()).isEmpty();
  }

  @Test
  void test_descriptor_sonarlint() {
    DefaultSensorDescriptor sensorDescriptor = new DefaultSensorDescriptor();
    SonarRuntime sonarRuntime = SonarRuntimeImpl.forSonarLint(Version.create(6, 5));
    new HtmlSensor(sonarRuntime, null, null, new CheckFactory(new DefaultActiveRules(Collections.emptyList())),
      new AnalysisWarningsWrapper()).describe(sensorDescriptor);
    assertThat(sensorDescriptor.name()).isEqualTo("HTML");
    assertThat(sensorDescriptor.languages()).isEmpty();
  }

  @Test
  void test_descriptor_sonarqube_9_3() {
    final boolean[] called = {false};
    DefaultSensorDescriptor sensorDescriptor = new DefaultSensorDescriptor() {
      @Override
      public SensorDescriptor processesFilesIndependently() {
        called[0] = true;
        return this;
      }
    };
    SonarRuntime sonarRuntime = SonarRuntimeImpl.forSonarQube(Version.create(9, 3), SonarQubeSide.SCANNER, SonarEdition.COMMUNITY);
    new HtmlSensor(sonarRuntime, null, null, new CheckFactory(new DefaultActiveRules(Collections.emptyList())),
      new AnalysisWarningsWrapper()).describe(sensorDescriptor);
    assertThat(sensorDescriptor.name()).isEqualTo("HTML");
    assertThat(sensorDescriptor.languages()).isEmpty();
    assertTrue(called[0]);
  }


  @Test
  void php_file_should_not_have_metrics() {
    tester.setRuntime(SonarRuntimeImpl.forSonarQube(Version.create(7, 9), SonarQubeSide.SERVER, SonarEdition.COMMUNITY));
    DefaultInputFile inputFile = new TestInputFileBuilder("key", "foo.php")
      .setModuleBaseDir(TEST_DIR).setContents("""
        <html>
        <?php  ?>
        <html>
        """)
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
  void vue_file_should_be_analyzed() throws IOException {
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
  void htm_file_should_be_analyzed() throws IOException {
    DefaultInputFile inputFile = createInputFile(TEST_DIR, "foo.htm");
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    String componentKey = inputFile.key();
    assertThat(tester.measure(componentKey, CoreMetrics.NCLOC).value()).isEqualTo(9);
    assertThat(tester.measure(componentKey, CoreMetrics.COMMENT_LINES).value()).isEqualTo(1);

    assertThat(tester.allAnalysisErrors()).isEmpty();
  }

  @Test
  void dockerfile_erb_should_be_skipped() {
    DefaultInputFile inputFile = createInputFile("Dockerfile.erb", "FROM ubuntu:<%= version %>\nRUN apt-get update\n");
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    String componentKey = inputFile.key();
    assertThat(tester.measures(componentKey)).isEmpty();
    assertThat(tester.allIssues()).isEmpty();
    assertThat(tester.allAnalysisErrors()).isEmpty();
  }

  @Test
  void yml_erb_should_be_skipped() {
    DefaultInputFile inputFile = createInputFile("config.yml.erb", "server:\n  host: <%= host %>\n");
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    assertThat(tester.measures(inputFile.key())).isEmpty();
    assertThat(tester.allAnalysisErrors()).isEmpty();
  }

  @Test
  void html_erb_should_be_analyzed() {
    DefaultInputFile inputFile = createInputFile("page.html.erb", "<html>\n<body>\n<%= greeting %>\n</body>\n</html>\n");
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    assertThat(tester.measures(inputFile.key())).isNotEmpty();
    assertThat(tester.allAnalysisErrors()).isEmpty();
  }

  @Test
  void bare_erb_with_html_content_should_be_analyzed() {
    DefaultInputFile inputFile = createInputFile("index.erb", "<html>\n<body>\n<%= greeting %>\n</body>\n</html>\n");
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    assertThat(tester.measures(inputFile.key())).isNotEmpty();
    assertThat(tester.allAnalysisErrors()).isEmpty();
  }

  @Test
  void bare_erb_with_non_html_content_should_be_skipped() {
    DefaultInputFile inputFile = createInputFile("notify.erb", "Hello <%= name %>,\nyou have <%= count %> messages.\n");
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    assertThat(tester.measures(inputFile.key())).isEmpty();
    assertThat(tester.allAnalysisErrors()).isEmpty();
  }

  @Test
  void erb_with_recognized_non_html_intermediate_extension_should_be_analyzed() {
    DefaultInputFile inputFile = createInputFile("script.php.erb", "<html>\n<body>\n<%= greeting %>\n</body>\n</html>\n");
    tester.fileSystem().add(inputFile);

    sensor.execute(tester);

    assertThat(tester.measures(inputFile.key())).isNotEmpty();
    assertThat(tester.allAnalysisErrors()).isEmpty();
  }

  @Test
  void twig_file_should_be_analyzed() throws IOException {
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

  private DefaultInputFile createInputFile(String fileName, String contents) {
    return new TestInputFileBuilder("key", fileName)
      .setModuleBaseDir(TEST_DIR)
      .setContents(contents)
      .setLanguage(HtmlConstants.LANGUAGE_KEY)
      .setType(InputFile.Type.MAIN)
      .setCharset(StandardCharsets.UTF_8)
      .build();
  }

  private List<IssueResolution> issueResolutions(DefaultInputFile inputFile) {
    return tester.getIssueResolutions().getOrDefault(inputFile.key(), Collections.emptyList());
  }

  private static class RecordingAnalysisWarnings implements AnalysisWarnings {
    private final List<String> warnings = new ArrayList<>();

    @Override
    public void addUnique(String text) {
      warnings.add(text);
    }

    private List<String> warnings() {
      return warnings;
    }
  }
}
