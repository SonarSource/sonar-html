/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2019 SonarSource SA and Matthijs Galesloot
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

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.sonar.api.SonarProduct;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.measures.Metric;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.html.analyzers.ComplexityVisitor;
import org.sonar.plugins.html.analyzers.PageCountLines;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.checks.HtmlIssue;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.rules.CheckClasses;
import org.sonar.plugins.html.rules.HtmlRulesDefinition;
import org.sonar.plugins.html.visitor.DefaultNodeVisitor;
import org.sonar.plugins.html.visitor.HtmlAstScanner;
import org.sonar.plugins.html.visitor.HtmlSourceCode;
import org.sonar.plugins.html.visitor.NoSonarScanner;

public final class HtmlSensor implements Sensor {
  private static final Logger LOG = Loggers.get(HtmlSensor.class);
  private static final String[] OTHER_FILE_SUFFIXES = {"php", "php3", "php4", "php5", "phtml", "inc"};

  private final NoSonarFilter noSonarFilter;
  private final Checks<Object> checks;
  private final FileLinesContextFactory fileLinesContextFactory;

  public HtmlSensor(NoSonarFilter noSonarFilter, FileLinesContextFactory fileLinesContextFactory, CheckFactory checkFactory) {
    this.noSonarFilter = noSonarFilter;
    this.checks = checkFactory.create(HtmlRulesDefinition.REPOSITORY_KEY).addAnnotatedChecks((Iterable) CheckClasses.getCheckClasses());
    this.fileLinesContextFactory = fileLinesContextFactory;
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor
      .name(HtmlConstants.LANGUAGE_NAME)
      .onlyOnFileType(InputFile.Type.MAIN);
  }

  @Override
  public void execute(SensorContext sensorContext) {
    // configure the lexer
    final PageLexer lexer = new PageLexer();

    FileSystem fileSystem = sensorContext.fileSystem();

    // configure page scanner and the visitors
    final HtmlAstScanner scanner = setupScanner(sensorContext);

    FilePredicates predicates = fileSystem.predicates();
    Iterable<InputFile> inputFiles = fileSystem.inputFiles(
      predicates.and(
        predicates.hasType(InputFile.Type.MAIN),
        predicates.or(
          predicates.hasLanguages(HtmlConstants.LANGUAGE_KEY, HtmlConstants.JSP_LANGUAGE_KEY),
          predicates.or(Stream.of(OTHER_FILE_SUFFIXES).map(predicates::hasExtension).toArray(FilePredicate[]::new))
          )
    ));

    for (InputFile inputFile : inputFiles) {
      if (sensorContext.isCancelled()) {
        return;
      }

      HtmlSourceCode sourceCode = new HtmlSourceCode(inputFile);

      try (Reader reader = new StringReader(inputFile.contents())) {
        scanner.scan(lexer.parse(reader), sourceCode, fileSystem.encoding());
        saveMetrics(sensorContext, sourceCode);
        saveLineLevelMeasures(inputFile, sourceCode);

      } catch (Exception e) {
        LOG.error("Cannot analyze file " + inputFile, e);
        sensorContext.newAnalysisError()
          .onFile(inputFile)
          .message(e.getMessage())
          .save();
      }
    }
  }

  private static void saveMetrics(SensorContext context, HtmlSourceCode sourceCode) {
    InputFile inputFile = sourceCode.inputFile();

    for (Map.Entry<Metric<Integer>, Integer> entry : sourceCode.getMeasures().entrySet()) {
      context.<Integer>newMeasure()
        .on(inputFile)
        .forMetric(entry.getKey())
        .withValue(entry.getValue())
        .save();
    }

    for (HtmlIssue issue : sourceCode.getIssues()) {
      NewIssue newIssue = context.newIssue()
        .forRule(issue.ruleKey())
        .gap(issue.cost());
      Integer line = issue.line();
      NewIssueLocation location = newIssue.newLocation()
        .on(inputFile)
        .message(issue.message());
      if (line != null) {
        location.at(inputFile.selectLine(line));
      }
      newIssue.at(location);
      newIssue.save();
    }
  }

  private void saveLineLevelMeasures(InputFile inputFile, HtmlSourceCode htmlSourceCode) {
    FileLinesContext fileLinesContext = fileLinesContextFactory.createFor(inputFile);

    for (Integer line : htmlSourceCode.getDetailedLinesOfCode()) {
      fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, line, 1);
    }

    fileLinesContext.save();
  }

  /**
   * Create PageScanner with Visitors.
   */
  private HtmlAstScanner setupScanner(SensorContext context) {
    List<DefaultNodeVisitor> visitors = new ArrayList<>();
    if (context.runtime().getProduct() != SonarProduct.SONARLINT) {
      visitors.add(new HtmlTokensVisitor(context));
    }
    visitors.add(new PageCountLines());
    visitors.add(new ComplexityVisitor());
    visitors.add(new NoSonarScanner(noSonarFilter));
    HtmlAstScanner scanner = new HtmlAstScanner(visitors);

    for (Object check : checks.all()) {
      ((AbstractPageCheck) check).setRuleKey(checks.ruleKey(check));
      scanner.addVisitor((AbstractPageCheck) check);
    }
    return scanner;
  }

}
