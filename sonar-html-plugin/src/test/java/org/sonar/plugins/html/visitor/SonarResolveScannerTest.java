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
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.batch.fs.InputFile;
import com.sonarsource.scanner.engine.sensor.test.fixtures.SensorContextTester;
import com.sonarsource.scanner.engine.sensor.test.fixtures.TestInputFileBuilder;
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
import static org.junit.jupiter.params.provider.Arguments.arguments;

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
  void scan_case_insensitive_accept_status_with_multiple_rule_keys() throws IOException {
    String content = String.join("\n",
      "<table>",
      "<!--",
      "SoNaR-ReSoLvE [AcCePt] Web:S5256, Web:S1827 \"accepted for both rules\"",
      "-->",
      "</table>");

    SensorContextTester tester = newSensorContext();
    InputFile inputFile = createInputFile("accept-multiple-rules.html", content);
    scan(tester, inputFile, content);

    assertThat(issueResolutions(tester, inputFile)).singleElement().satisfies(issueResolution -> {
      assertThat(issueResolution.status()).isEqualTo(IssueResolution.Status.DEFAULT);
      assertThat(issueResolution.ruleKeys()).containsExactlyInAnyOrder(
        RuleKey.of(HtmlRulesDefinition.REPOSITORY_KEY, "S5256"),
        RuleKey.of(HtmlRulesDefinition.REPOSITORY_KEY, "S1827"));
      assertThat(issueResolution.comment()).isEqualTo("accepted for both rules");
      assertThat(issueResolution.textRange().start().line()).isEqualTo(3);
    });
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
  void scan_multiline_html_directive_preserves_justification_indentation() throws IOException {
    String content = String.join("\n",
      "<table>",
      "<!--",
      "sonar-resolve Web:S5256 \"first",
      "    second",
      "  third\"",
      "-->",
      "</table>");

    SensorContextTester tester = newSensorContext();
    InputFile inputFile = createInputFile("multiline-indented.html", content);
    scan(tester, inputFile, content);

    assertResolution(issueResolutions(tester, inputFile).get(0), "S5256", "first\n    second\n  third", 3);
  }

  @Test
  void scan_multiline_jsp_directive_preserves_justification_indentation() throws IOException {
    String content = String.join("\n",
      "<%--",
      "sonar-resolve Web:S5256 \"first",
      "    second",
      "  third\"",
      "--%>");

    SensorContextTester tester = newSensorContext();
    InputFile inputFile = createInputFile("multiline-indented.jsp", content);
    scan(tester, inputFile, content);

    assertResolution(issueResolutions(tester, inputFile).get(0), "S5256", "first\n    second\n  third", 2);
  }

  @Test
  void scan_multiline_twig_directive() throws IOException {
    String content = String.join("\n",
      "{#",
      "sonar-resolve Web:S5256 \"first",
      "second\"",
      "#}");

    SensorContextTester tester = newSensorContext();
    InputFile inputFile = createInputFile("multiline.twig", content);
    scan(tester, inputFile, content);

    assertThat(issueResolutions(tester, inputFile)).singleElement().satisfies(issueResolution -> {
      assertThat(issueResolution.status()).isEqualTo(IssueResolution.Status.DEFAULT);
      assertThat(issueResolution.ruleKeys()).containsExactly(RuleKey.of(HtmlRulesDefinition.REPOSITORY_KEY, "S5256"));
      assertThat(issueResolution.comment()).isEqualTo("first\nsecond");
      assertThat(issueResolution.textRange().start().line()).isEqualTo(2);
    });
  }

  @Test
  void scan_directives_with_multiple_justification_delimiters() throws IOException {
    String content = String.join("\n",
      "<table>",
      "<!--",
      "sonar-resolve Web:S110 `backtick`",
      "sonar-resolve Web:S111 'single quote'",
      "sonar-resolve Web:S112 (parentheses)",
      "sonar-resolve Web:S113 [square bracket]",
      "sonar-resolve Web:S114 {brace}",
      "-->",
      "</table>");

    SensorContextTester tester = newSensorContext();
    InputFile inputFile = createInputFile("delimiters.html", content);
    scan(tester, inputFile, content);

    List<IssueResolution> issueResolutions = issueResolutions(tester, inputFile);
    assertThat(issueResolutions).hasSize(5);
    assertResolution(issueResolutions.get(0), "S110", "backtick", 3);
    assertResolution(issueResolutions.get(1), "S111", "single quote", 4);
    assertResolution(issueResolutions.get(2), "S112", "parentheses", 5);
    assertResolution(issueResolutions.get(3), "S113", "square bracket", 6);
    assertResolution(issueResolutions.get(4), "S114", "brace", 7);
  }

  @ParameterizedTest
  @MethodSource("singleLineInvalidDirectiveCases")
  void invalid_single_line_directives_log_warning(String directive, String expectedErrorMessage) throws IOException {
    assertInvalidDirectiveLogsWarning(expectedErrorMessage, directive);
  }

  @ParameterizedTest
  @MethodSource("multiLineInvalidDirectiveCases")
  void invalid_multi_line_directives_log_warning(String[] directiveLines, String expectedErrorMessage) throws IOException {
    assertInvalidDirectiveLogsWarning(expectedErrorMessage, directiveLines);
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

  private static void assertResolution(IssueResolution issueResolution, String ruleKey, String comment, int line) {
    assertThat(issueResolution.status()).isEqualTo(IssueResolution.Status.DEFAULT);
    assertThat(issueResolution.ruleKeys()).containsExactly(RuleKey.of(HtmlRulesDefinition.REPOSITORY_KEY, ruleKey));
    assertThat(issueResolution.comment()).isEqualTo(comment);
    assertThat(issueResolution.textRange().start().line()).isEqualTo(line);
  }

  private void assertInvalidDirectiveLogsWarning(String expectedErrorMessage, String... directiveLines) throws IOException {
    SensorContextTester tester = newSensorContext();
    String content = htmlCommentContent(directiveLines);
    InputFile inputFile = createInputFile("invalid.html", content);
    scan(tester, inputFile, content);

    assertThat(issueResolutions(tester, inputFile)).isEmpty();
    assertThat(logTester.logs()).singleElement().satisfies(log -> assertThat(log)
      .contains(expectedErrorMessage)
      .contains("line 2"));
  }

  private static String htmlCommentContent(String... directiveLines) {
    StringBuilder content = new StringBuilder("<table>\n<!-- ").append(directiveLines[0]);
    for (int i = 1; i < directiveLines.length; i++) {
      content.append('\n').append(directiveLines[i]);
    }
    return content.append(" -->\n</table>").toString();
  }

  private static Stream<Arguments> singleLineInvalidDirectiveCases() {
    return Stream.of(
      arguments("sonar-resolve[fp] Web:S5256 \"reason\"", "Invalid sonar-resolve directive: expected whitespace after 'sonar-resolve'"),
      arguments("sonar-resolve \"reason\"", "Invalid sonar-resolve directive: missing rule key"),
      arguments("sonar-resolve WebS5256 \"reason\"", "Invalid sonar-resolve directive: invalid rule key 'WebS5256'"),
      arguments("sonar-resolve [accepted] Web:S5256 \"reason\"", "Invalid sonar-resolve directive: invalid status '[accepted]'"),
      arguments("sonar-resolve [fp Web:S5256 \"reason\"", "Invalid sonar-resolve directive: unterminated status"),
      arguments("sonar-resolve Web:S5256, Web:S5256 \"reason\"", "Invalid sonar-resolve directive: duplicate rule key 'Web:S5256'"),
      arguments("sonar-resolve Web:S5256, \"reason\"", "Invalid sonar-resolve directive: invalid rule key list"),
      arguments("sonar-resolve Web:S5256", "Invalid sonar-resolve directive: missing justification"),
      arguments("sonar-resolve Web:S5256 <reason>", "Invalid sonar-resolve directive: missing justification"),
      arguments("sonar-resolve Web:S5256 \"reason", "Invalid sonar-resolve directive: unterminated justification"),
      arguments("sonar-resolve Web:S5256 [reason", "Invalid sonar-resolve directive: unterminated justification"));
  }

  private static Stream<Arguments> multiLineInvalidDirectiveCases() {
    return Stream.of(
      arguments(new String[] {"sonar-resolve", "\"reason\""}, "Invalid sonar-resolve directive: missing rule key"),
      arguments(new String[] {"sonar-resolve [f", "p] Web:S5256 \"reason\""}, "Invalid sonar-resolve directive: invalid status '[f\np]'"),
      arguments(new String[] {"sonar-resolve [fp", "Web:S5256 \"reason\""}, "Invalid sonar-resolve directive: unterminated status"),
      arguments(new String[] {"sonar-resolve Web:S5256,", "\"reason\""}, "Invalid sonar-resolve directive: invalid rule key list"),
      arguments(new String[] {"sonar-resolve Web:S5256 \"reason", "still reason"}, "Invalid sonar-resolve directive: unterminated justification"),
      arguments(new String[] {"sonar-resolve Web:S5256 [reason", "still reason"}, "Invalid sonar-resolve directive: unterminated justification"));
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
