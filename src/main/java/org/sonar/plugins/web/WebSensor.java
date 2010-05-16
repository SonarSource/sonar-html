/*
 * Copyright (C) 2010 Matthijs Galesloot
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

import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.sonar.api.batch.GeneratesViolations;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.design.Dependency;
import org.sonar.api.measures.Measure;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.web.analyzers.ComplexityDetector;
import org.sonar.plugins.web.analyzers.DuplicationDetector;
import org.sonar.plugins.web.analyzers.WebDependencyDetector;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.rules.AbstractPageCheck;
import org.sonar.plugins.web.rules.PageChecks;
import org.sonar.plugins.web.visitor.PageCountLines;
import org.sonar.plugins.web.visitor.PageScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public final class WebSensor implements Sensor, GeneratesViolations {

  private final RulesProfile profile;

  public WebSensor(RulesProfile profile) {
    this.profile = profile;
  }

  public void analyse(Project project, SensorContext sensorContext) {

    final DuplicationDetector duplicationDetector = new DuplicationDetector();

    // configure the lexer
    final PageLexer lexer = new PageLexer();

    // configure scanner
    final PageScanner scanner = new PageScanner();
    for (AbstractPageCheck check : PageChecks.getChecks(profile)) {
      scanner.addVisitor(check);
    }
    scanner.addVisitor(new PageCountLines());
    scanner.addVisitor(new WebDependencyDetector(project.getFileSystem()));
    scanner.addVisitor(new ComplexityDetector());

    for (File webFile : project.getFileSystem().getSourceFiles(Web.INSTANCE)) {

      try {
        WebFile resource = WebFile.fromIOFile(webFile, project.getFileSystem().getSourceDirs());

        WebSourceCode sourceCode = new WebSourceCode(resource);
        List<Node> nodeList = lexer.parse(new FileReader(webFile));
        scanner.scan(nodeList, sourceCode);
        duplicationDetector.addTokens(nodeList, sourceCode);
        saveMetrics(sensorContext, sourceCode);

      } catch (Exception e) {
        WebUtils.LOG.error("Could not analyze the file " + webFile.getAbsolutePath(), e);
      }
    }

    duplicationDetector.analyse(sensorContext);
  }

  private void saveMetrics(SensorContext sensorContext, WebSourceCode sourceCode) {
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
   * This sensor executes on Web projects.
   */
  public boolean shouldExecuteOnProject(Project project) {
    return Web.INSTANCE.equals(project.getLanguage());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
