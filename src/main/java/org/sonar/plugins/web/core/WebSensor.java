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

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.checks.AnnotationCheckFactory;
import org.sonar.api.checks.NoSonarFilter;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.RangeDistributionBuilder;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.File;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Violation;
import org.sonar.api.scan.filesystem.FileQuery;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.plugins.web.analyzers.ComplexityVisitor;
import org.sonar.plugins.web.analyzers.PageCountLines;
import org.sonar.plugins.web.api.WebConstants;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.rules.CheckClasses;
import org.sonar.plugins.web.rules.WebRulesRepository;
import org.sonar.plugins.web.visitor.HtmlAstScanner;
import org.sonar.plugins.web.visitor.NoSonarScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.FileReader;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class WebSensor implements Sensor {

  private static final Number[] FILES_DISTRIB_BOTTOM_LIMITS = {0, 5, 10, 20, 30, 60, 90};
  private static final Logger LOG = LoggerFactory.getLogger(WebSensor.class);

  private final Web web;
  private final NoSonarFilter noSonarFilter;
  private final AnnotationCheckFactory annotationCheckFactory;
  private final ModuleFileSystem fileSystem;
  private final FileLinesContextFactory fileLinesContextFactory;

  public WebSensor(Web web, RulesProfile profile, NoSonarFilter noSonarFilter, ModuleFileSystem fileSystem, FileLinesContextFactory fileLinesContextFactory) {
    this.web = web;
    this.noSonarFilter = noSonarFilter;
    this.annotationCheckFactory = AnnotationCheckFactory.create(profile, WebRulesRepository.REPOSITORY_KEY, CheckClasses.getCheckClasses());
    this.fileSystem = fileSystem;
    this.fileLinesContextFactory = fileLinesContextFactory;

  }

  @Override
  public void analyse(Project project, SensorContext sensorContext) {
    // configure the lexer
    final PageLexer lexer = new PageLexer();

    // configure page scanner and the visitors
    final HtmlAstScanner scanner = setupScanner();

    for (java.io.File file : fileSystem.files(FileQuery.onSource().onLanguage(WebConstants.LANGUAGE_KEY))) {
      File resource = File.fromIOFile(file, project);
      WebSourceCode sourceCode = new WebSourceCode(file, resource);
      FileReader reader = null;
      try {
        reader = new FileReader(file);
        List<Node> nodeList = lexer.parse(reader);
        scanner.scan(nodeList, sourceCode, fileSystem.sourceCharset());
        saveMetrics(sensorContext, sourceCode);
        saveLineLevelMeasures(resource, sourceCode);
      } catch (Exception e) {
        LOG.error("Can not analyze file " + file.getAbsolutePath(), e);
      } finally {
        IOUtils.closeQuietly(reader);
      }
    }
  }

  private static void saveMetrics(SensorContext sensorContext, WebSourceCode sourceCode) {
    saveComplexityDistribution(sensorContext, sourceCode);
    List<Measure> measures = sourceCode.getMeasures();
    for (Measure measure : measures) {
      sensorContext.saveMeasure(sourceCode.getResource(), measure);
    }

    List<Violation> violations = sourceCode.getViolations();
    for (Violation violation : violations) {
      sensorContext.saveViolation(violation);
    }
  }

  private static void saveComplexityDistribution(SensorContext sensorContext, WebSourceCode sourceCode) {
    if (sourceCode.getMeasure(CoreMetrics.COMPLEXITY) != null) {
      RangeDistributionBuilder complexityFileDistribution = new RangeDistributionBuilder(CoreMetrics.FILE_COMPLEXITY_DISTRIBUTION,
        FILES_DISTRIB_BOTTOM_LIMITS);
      complexityFileDistribution.add(sourceCode.getMeasure(CoreMetrics.COMPLEXITY).getValue());
      sensorContext.saveMeasure(sourceCode.getResource(), complexityFileDistribution.build().setPersistenceMode(PersistenceMode.MEMORY));
    }
  }

  private void saveLineLevelMeasures(File sonarFile, WebSourceCode webSourceCode) {
    FileLinesContext fileLinesContext = fileLinesContextFactory.createFor(sonarFile);
    Set<Integer> linesOfCode = webSourceCode.getDetailedLinesOfCode();
    Set<Integer> linesOfComments = webSourceCode.getDetailedLinesOfComments();

    for (int line = 1; line <= webSourceCode.getMeasure(CoreMetrics.LINES).getIntValue(); line++) {
      fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, line, linesOfCode.contains(line) ? 1 : 0);
      fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, line, linesOfComments.contains(line) ? 1 : 0);
    }
    fileLinesContext.save();
  }

  /**
   * Create PageScanner with Visitors.
   */
  private HtmlAstScanner setupScanner() {
    HtmlAstScanner scanner = new HtmlAstScanner(ImmutableList.of(new PageCountLines(), new ComplexityVisitor(), new NoSonarScanner(noSonarFilter)));
    for (AbstractPageCheck check : (Collection<AbstractPageCheck>) annotationCheckFactory.getChecks()) {
      scanner.addVisitor(check);
      check.setRule(annotationCheckFactory.getActiveRule(check).getRule());
    }
    return scanner;
  }

  /**
   * This sensor only executes on Web projects.
   */
  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return WebConstants.LANGUAGE_KEY.equals(project.getLanguageKey()) ||
      (StringUtils.isBlank(project.getLanguageKey()) &&
      !fileSystem.files(FileQuery.onSource().onLanguage(WebConstants.LANGUAGE_KEY)).isEmpty());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
