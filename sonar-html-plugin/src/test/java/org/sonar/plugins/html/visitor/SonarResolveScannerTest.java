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
package org.sonar.plugins.html.visitor;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.IssueResolution;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.testfixtures.log.LogTesterJUnit5;
import org.sonar.api.utils.Version;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.rules.HtmlRulesDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class SonarResolveScannerTest {

  @RegisterExtension
  public LogTesterJUnit5 logTester = new LogTesterJUnit5();

  @Test
  void scan_multiple_html_directives() throws IOException {
    String content = String.join("\n",
      "<table>",
      "<!--",
      "sonar-resolve Web:S5256 \"accept reason\"",
      "sonar-resolve [fp] Web:S1827 \"false positive reason\"",
      "-->",
      "</table>");

    SensorContextTester tester = newSensorContext();
    InputFile inputFile = createInputFile("multiple.html", content);
    scan(tester, inputFile, content);

    List<IssueResolution> issueResolutions = issueResolutions(tester, inputFile);
    assertThat(issueResolutions).hasSize(2);
    assertThat(issueResolutions.get(0).status()).isEqualTo(IssueResolution.Status.DEFAULT);
    assertThat(issueResolutions.get(0).ruleKeys()).containsExactly(RuleKey.of(HtmlRulesDefinition.REPOSITORY_KEY, "S5256"));
    assertThat(issueResolutions.get(0).comment()).isEqualTo("accept reason");
    assertThat(issueResolutions.get(0).textRange().start().line()).isEqualTo(3);
    assertThat(issueResolutions.get(1).status()).isEqualTo(IssueResolution.Status.FALSE_POSITIVE);
    assertThat(issueResolutions.get(1).ruleKeys()).containsExactly(RuleKey.of(HtmlRulesDefinition.REPOSITORY_KEY, "S1827"));
    assertThat(issueResolutions.get(1).comment()).isEqualTo("false positive reason");
    assertThat(issueResolutions.get(1).textRange().start().line()).isEqualTo(4);
  }

  @Test
  void scan_multiline_jsp_directive() throws IOException {
    String content = String.join("\n",
      "<%--",
      "SONAR-RESOLVE",
      "[FP] Web:S5256 \"first",
      "second\"",
      "--%>");

    SensorContextTester tester = newSensorContext();
    InputFile inputFile = createInputFile("multiline.jsp", content);
    scan(tester, inputFile, content);

    assertThat(issueResolutions(tester, inputFile)).singleElement().satisfies(issueResolution -> {
      assertThat(issueResolution.status()).isEqualTo(IssueResolution.Status.FALSE_POSITIVE);
      assertThat(issueResolution.ruleKeys()).containsExactly(RuleKey.of(HtmlRulesDefinition.REPOSITORY_KEY, "S5256"));
      assertThat(issueResolution.comment()).isEqualTo("first\nsecond");
      assertThat(issueResolution.textRange().start().line()).isEqualTo(2);
    });
  }

  @Test
  void invalid_directive_logs_warning_and_skips_resolution() throws IOException {
    String content = String.join("\n",
      "<table>",
      "<!-- sonar-resolve Web:S5256 \"reason -->",
      "</table>");

    SensorContextTester tester = newSensorContext();
    InputFile inputFile = createInputFile("invalid.html", content);
    scan(tester, inputFile, content);

    assertThat(issueResolutions(tester, inputFile)).isEmpty();
    assertThat(logTester.logs()).anySatisfy(log -> assertThat(log)
      .contains("Invalid sonar-resolve directive: unterminated justification")
      .contains("line 2"));
  }

  @Test
  void immediately_invalid_directive_logs_warning_and_skips_resolution() throws IOException {
    String content = String.join("\n",
      "<table>",
      "<!-- sonar-resolve [accepted] Web:S5256 \"reason\" -->",
      "</table>");

    SensorContextTester tester = newSensorContext();
    InputFile inputFile = createInputFile("invalid-status.html", content);
    scan(tester, inputFile, content);

    assertThat(issueResolutions(tester, inputFile)).isEmpty();
    assertThat(logTester.logs()).anySatisfy(log -> assertThat(log)
      .contains("Invalid sonar-resolve directive: invalid status '[accepted]'")
      .contains("line 2"));
  }

  @Test
  void regular_comments_are_ignored() throws IOException {
    String content = String.join("\n",
      "<table>",
      "<!-- regular comment -->",
      "</table>");

    SensorContextTester tester = newSensorContext();
    InputFile inputFile = createInputFile("regular-comment.html", content);
    scan(tester, inputFile, content);

    assertThat(issueResolutions(tester, inputFile)).isEmpty();
    assertThat(logTester.logs()).noneMatch(log -> log.contains("Invalid sonar-resolve directive"));
  }

  @Test
  void comments_with_embedded_keyword_are_ignored() throws IOException {
    String content = String.join("\n",
      "<table>",
      "<!-- keep sonar-resolve Web:S5256 \"reason\" as documentation -->",
      "</table>");

    SensorContextTester tester = newSensorContext();
    InputFile inputFile = createInputFile("embedded-keyword.html", content);
    scan(tester, inputFile, content);

    assertThat(issueResolutions(tester, inputFile)).isEmpty();
    assertThat(logTester.logs()).noneMatch(log -> log.contains("Invalid sonar-resolve directive"));
  }

  @Test
  void malformed_directive_start_still_logs_warning() throws IOException {
    String content = String.join("\n",
      "<table>",
      "<!-- sonar-resolve[fp] Web:S5256 \"reason\" -->",
      "</table>");

    SensorContextTester tester = newSensorContext();
    InputFile inputFile = createInputFile("missing-space.html", content);
    scan(tester, inputFile, content);

    assertThat(issueResolutions(tester, inputFile)).isEmpty();
    assertThat(logTester.logs()).anySatisfy(log -> assertThat(log)
      .contains("Invalid sonar-resolve directive: expected whitespace after 'sonar-resolve'")
      .contains("line 2"));
  }

  private static void scan(SensorContextTester tester, InputFile inputFile, String content) throws IOException {
    List<Node> nodeList;
    try (Reader reader = new StringReader(content)) {
      nodeList = new PageLexer().parse(reader);
    }

    HtmlAstScanner scanner = new HtmlAstScanner(Collections.emptyList());
    scanner.addVisitor(new SonarResolveScanner(tester));
    scanner.scan(nodeList, new HtmlSourceCode(inputFile));
  }

  private static SensorContextTester newSensorContext() {
    return SensorContextTester.create(Paths.get("."))
      .setRuntime(SonarRuntimeImpl.forSonarQube(Version.create(13, 6), SonarQubeSide.SCANNER, SonarEdition.COMMUNITY));
  }

  private static InputFile createInputFile(String fileName, String content) {
    return new TestInputFileBuilder("key", fileName)
      .setModuleBaseDir(Paths.get("."))
      .setContents(content)
      .setLanguage(HtmlConstants.LANGUAGE_KEY)
      .setType(InputFile.Type.MAIN)
      .setCharset(StandardCharsets.UTF_8)
      .build();
  }

  private static List<IssueResolution> issueResolutions(SensorContextTester tester, InputFile inputFile) {
    return tester.getIssueResolutions().getOrDefault(inputFile.key(), Collections.emptyList());
  }
}
