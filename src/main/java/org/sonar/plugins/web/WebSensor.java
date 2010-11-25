/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web;

import java.io.FileReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.checks.NoSonarFilter;
import org.sonar.api.design.Dependency;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.RangeDistributionBuilder;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.File;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.web.analyzers.PageCountLines;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.rules.WebRulesRepository;
import org.sonar.plugins.web.visitor.NoSonarScanner;
import org.sonar.plugins.web.visitor.PageScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * WebSensor provides analysis of web files.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class WebSensor implements Sensor {

  private final static Number[] FILES_DISTRIB_BOTTOM_LIMITS = {0, 5, 10, 20, 30, 60, 90};

  private static final Logger LOG = LoggerFactory.getLogger(WebSensor.class);

  private final NoSonarFilter noSonarFilter;

  private final RulesProfile profile;

  private final Web web;

  public WebSensor(Web web, RulesProfile profile, NoSonarFilter noSonarFilter) {
    this.web = web;
    this.profile = profile;
    this.noSonarFilter = noSonarFilter;
  }

  public void analyse(Project project, SensorContext sensorContext) {

    ProjectConfiguration.configureSourceDir(project);

    // configure the lexer
    final PageLexer lexer = new PageLexer();

    // configure page scanner and the visitors
    final PageScanner scanner = setupScanner();

    for (java.io.File webFile : project.getFileSystem().getSourceFiles(new Web(project))) {

      try {
        File resource = File.fromIOFile(webFile, project.getFileSystem().getSourceDirs());

        WebSourceCode sourceCode = new WebSourceCode(resource);
        List<Node> nodeList = lexer.parse(new FileReader(webFile));
        scanner.scan(nodeList, sourceCode);
        saveMetrics(sensorContext, sourceCode);

      } catch (Exception e) {
        LOG.error("Could not analyze the file " + webFile.getAbsolutePath(), e);
      }
    }
  }

  private void saveMetrics(SensorContext sensorContext, WebSourceCode sourceCode) {
    RangeDistributionBuilder complexityFileDistribution = new RangeDistributionBuilder(CoreMetrics.FILE_COMPLEXITY_DISTRIBUTION,
      FILES_DISTRIB_BOTTOM_LIMITS);
    complexityFileDistribution.add(sourceCode.getMeasure(CoreMetrics.COMPLEXITY).getValue());
    sensorContext.saveMeasure(sourceCode.getResource(), complexityFileDistribution.build().setPersistenceMode(PersistenceMode.MEMORY));

    for (Measure measure : sourceCode.getMeasures()) {
      sensorContext.saveMeasure(sourceCode.getResource(), measure);
    }

    for (Violation violation : sourceCode.getViolations()) {
      sensorContext.saveViolation(violation);
    }
    for (Dependency dependency : sourceCode.getDependencies()) {
      sensorContext.saveDependency(dependency);
    }
  }

  /**
   * Create PageScanner with Visitors.
   */
  private PageScanner setupScanner() {
    PageScanner scanner = new PageScanner();
    for (AbstractPageCheck check : WebRulesRepository.createChecks(profile)) {
      scanner.addVisitor(check);
    }
    scanner.addVisitor(new PageCountLines());
   // dependencies not yet supported in v 1.0
   // scanner.addVisitor(new WebDependencyDetector(web));
    scanner.addVisitor(new NoSonarScanner(noSonarFilter));
    return scanner;
  }

  /**
   * This sensor only executes on Web projects.
   */
  public boolean shouldExecuteOnProject(Project project) {
    return Web.KEY.equals(project.getLanguageKey());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
