/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.plugins.html.core;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.sonar.api.SonarProduct;
import org.sonar.api.SonarRuntime;
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
import org.sonar.api.utils.Version;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.html.analyzers.ComplexityVisitor;
import org.sonar.plugins.html.analyzers.PageCountLines;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.checks.HtmlIssue;
import org.sonar.plugins.html.checks.PreciseHtmlIssue;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.lex.VueLexer;
import org.sonar.plugins.html.rules.CheckClasses;
import org.sonar.plugins.html.rules.HtmlRulesDefinition;
import org.sonar.plugins.html.visitor.DefaultNodeVisitor;
import org.sonar.plugins.html.visitor.HtmlAstScanner;
import org.sonar.plugins.html.visitor.HtmlSourceCode;
import org.sonar.plugins.html.visitor.NoSonarScanner;

public final class HtmlSensor implements Sensor {
  private static final Logger LOG = Loggers.get(HtmlSensor.class);
  private static final String[] OTHER_FILE_SUFFIXES = {"php", "php3", "php4", "php5", "phtml", "inc", "vue"};

  private final SonarRuntime sonarRuntime;
  private final NoSonarFilter noSonarFilter;
  private final Checks<Object> checks;
  private final FileLinesContextFactory fileLinesContextFactory;

  public HtmlSensor(SonarRuntime sonarRuntime, NoSonarFilter noSonarFilter, FileLinesContextFactory fileLinesContextFactory, CheckFactory checkFactory) {
    this.sonarRuntime = sonarRuntime;
    this.noSonarFilter = noSonarFilter;
    this.checks = checkFactory.create(HtmlRulesDefinition.REPOSITORY_KEY).addAnnotatedChecks((Iterable) CheckClasses.getCheckClasses());
    this.fileLinesContextFactory = fileLinesContextFactory;
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor
      .name(HtmlConstants.LANGUAGE_NAME)
      .onlyOnFileType(InputFile.Type.MAIN);
    processesFilesIndependently(descriptor);
  }

  private void processesFilesIndependently(SensorDescriptor descriptor) {
    if ((sonarRuntime.getProduct() == SonarProduct.SONARLINT)
      || !sonarRuntime.getApiVersion().isGreaterThanOrEqual(Version.create(9, 3))) {
      return;
    }
    descriptor.processesFilesIndependently();
  }

  @Override
  public void execute(SensorContext sensorContext) {

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

      try (Reader reader = new InputStreamReader(inputFile.inputStream(), inputFile.charset())) {
        PageLexer lexer = inputFile.filename().endsWith(".vue") ? new VueLexer() : new PageLexer();
        scanner.scan(lexer.parse(reader), sourceCode);
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
      NewIssueLocation location = locationForIssue(inputFile, issue, newIssue);
      newIssue.at(location);
      newIssue.save();
    }
  }

  private static NewIssueLocation locationForIssue(InputFile inputFile, HtmlIssue issue, NewIssue newIssue) {
    NewIssueLocation location = newIssue.newLocation()
      .on(inputFile)
      .message(issue.message());
    Integer line = issue.line();
    if (issue instanceof PreciseHtmlIssue preciseHtmlIssue) {
      location.at(inputFile.newRange(issue.line(),
        preciseHtmlIssue.startColumn(),
        preciseHtmlIssue.endLine(),
        preciseHtmlIssue.endColumn()));
    } else if (line != null) {
      location.at(inputFile.selectLine(line));
    }
    return location;
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
