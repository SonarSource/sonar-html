/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
 * dev@sonar.codehaus.org
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

import java.io.FileReader;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.RangeDistributionBuilder;
import org.sonar.api.resources.Project;
import org.sonar.plugins.web.analyzers.ComplexityVisitor;
import org.sonar.plugins.web.analyzers.PageCountLines;
import org.sonar.plugins.web.api.WebConstants;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.WebIssue;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.rules.CheckClasses;
import org.sonar.plugins.web.rules.WebRulesDefinition;
import org.sonar.plugins.web.visitor.HtmlAstScanner;
import org.sonar.plugins.web.visitor.NoSonarScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

import com.google.common.collect.ImmutableList;

public final class WebSensor implements Sensor {

  private static final Number[] FILES_DISTRIB_BOTTOM_LIMITS = {0, 5, 10, 20, 30, 60, 90};
  private static final Logger LOG = LoggerFactory.getLogger(WebSensor.class);

  private final NoSonarFilter noSonarFilter;
  private final ResourcePerspectives resourcePerspectives;
  private final Checks<Object> checks;
  private final FileSystem fileSystem;
  private final FileLinesContextFactory fileLinesContextFactory;
  private final FilePredicate mainFilesPredicate;

  public WebSensor(NoSonarFilter noSonarFilter, FileSystem fileSystem, FileLinesContextFactory fileLinesContextFactory,
    CheckFactory checkFactory, ResourcePerspectives resourcePerspectives) {

    this.noSonarFilter = noSonarFilter;
    this.resourcePerspectives = resourcePerspectives;
    this.checks = checkFactory.create(WebRulesDefinition.REPOSITORY_KEY).addAnnotatedChecks(CheckClasses.getCheckClasses());
    this.fileSystem = fileSystem;
    this.fileLinesContextFactory = fileLinesContextFactory;
    this.mainFilesPredicate = fileSystem.predicates().and(
      fileSystem.predicates().hasType(InputFile.Type.MAIN),
      fileSystem.predicates().hasLanguage(WebConstants.LANGUAGE_KEY));
  }

  @Override
  public void analyse(Project project, SensorContext sensorContext) {
    // configure the lexer
    final PageLexer lexer = new PageLexer();

    // configure page scanner and the visitors
    final HtmlAstScanner scanner = setupScanner();

    for (InputFile inputFile : fileSystem.inputFiles(mainFilesPredicate)) {
      WebSourceCode sourceCode = new WebSourceCode(inputFile, sensorContext.getResource(inputFile));
      FileReader reader = null;

      try {
        reader = new FileReader(inputFile.file());
        scanner.scan(lexer.parse(reader), sourceCode, fileSystem.encoding());
        saveMetrics(sensorContext, sourceCode);
        saveLineLevelMeasures(inputFile, sourceCode);

      } catch (Exception e) {
        LOG.error("Cannot analyze file " + inputFile.file().getAbsolutePath(), e);
        e.printStackTrace();

      } finally {
        IOUtils.closeQuietly(reader);
      }
    }
  }

  private void saveMetrics(SensorContext sensorContext, WebSourceCode sourceCode) {
    saveComplexityDistribution(sensorContext, sourceCode);

    for (Measure measure : sourceCode.getMeasures()) {
      sensorContext.saveMeasure(sourceCode.inputFile(), measure);
    }

    for (WebIssue issue : sourceCode.getIssues()) {
      Issuable issuable = resourcePerspectives.as(Issuable.class, sourceCode.inputFile());

      issuable.addIssue(
        issuable.newIssueBuilder()
          .ruleKey(issue.ruleKey())
          .line(issue.line())
          .message(issue.message())
          .build());
    }
  }

  private static void saveComplexityDistribution(SensorContext sensorContext, WebSourceCode sourceCode) {
    if (sourceCode.getMeasure(CoreMetrics.COMPLEXITY) != null) {
      RangeDistributionBuilder complexityFileDistribution = new RangeDistributionBuilder(CoreMetrics.FILE_COMPLEXITY_DISTRIBUTION,
        FILES_DISTRIB_BOTTOM_LIMITS);
      complexityFileDistribution.add(sourceCode.getMeasure(CoreMetrics.COMPLEXITY).getValue());
      sensorContext.saveMeasure(sourceCode.inputFile(), complexityFileDistribution.build().setPersistenceMode(PersistenceMode.MEMORY));
    }
  }

  private void saveLineLevelMeasures(InputFile inputFile, WebSourceCode webSourceCode) {
    FileLinesContext fileLinesContext = fileLinesContextFactory.createFor(inputFile);

    for (int line = 1; line <= webSourceCode.getMeasure(CoreMetrics.LINES).getIntValue(); line++) {
      fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, line, webSourceCode.isLineOfCode(line) ? 1 : 0);
      fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, line, webSourceCode.isLineOfComment(line) ? 1 : 0);
    }

    fileLinesContext.save();
  }

  /**
   * Create PageScanner with Visitors.
   */
  private HtmlAstScanner setupScanner() {
    HtmlAstScanner scanner = new HtmlAstScanner(ImmutableList.of(
      new PageCountLines(),
      new ComplexityVisitor(),
      new NoSonarScanner(noSonarFilter)));

    for (Object check : checks.all()) {
      ((AbstractPageCheck) check).setRuleKey(checks.ruleKey(check));
      scanner.addVisitor((AbstractPageCheck) check);
    }
    return scanner;
  }

  /**
   * This sensor only executes on Web projects.
   */
  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return fileSystem.hasFiles(mainFilesPredicate);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
