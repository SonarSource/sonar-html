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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.GeneratesViolations;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.design.Dependency;
import org.sonar.api.measures.Measure;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.web.analyzers.PageCountLines;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.language.ConfigurableWeb;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.language.WebProperties;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.PageScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public final class WebSensor implements Sensor, GeneratesViolations {

  private static final Logger LOG = LoggerFactory.getLogger(WebSensor.class);

  private final RulesProfile profile;

  public WebSensor(RulesProfile profile) {
    this.profile = profile;
  }

  public static void addSourceDir(Project project) {
    if (project.getProperty(WebProperties.SOURCE_DIRECTORY) != null) {
      File file = new File(project.getFileSystem().getBasedir() + "/" + project.getProperty(WebProperties.SOURCE_DIRECTORY).toString());
      for (File sourceDir : project.getFileSystem().getSourceDirs()) {
        if (sourceDir.equals(file)) {
          return;
        }
      }
      project.getFileSystem().addSourceDir(file);
    }
  }

  public void analyse(Project project, SensorContext sensorContext) {

    addSourceDir(project);

    final PageCountLines pageLineCounter = new PageCountLines();

    // configure the lexer
    final PageLexer lexer = new PageLexer();

    // configure scanner
    final PageScanner scanner = new PageScanner();
    for (AbstractPageCheck check : WebRulesRepository.createChecks(profile)) {
      scanner.addVisitor(check);
    }
    /*
     * WebDependencyDetector does not work in current version of sonar (2.2)
     * scanner.addVisitor(new WebDependencyDetector(project));
     */

    for (File webFile : project.getFileSystem().getSourceFiles(new ConfigurableWeb(project))) {

      try {
        WebFile resource = WebFile.fromIOFile(webFile, project.getFileSystem().getSourceDirs());

        WebSourceCode sourceCode = new WebSourceCode(resource);
        List<Node> nodeList = lexer.parse(new FileReader(webFile));
        scanner.scan(nodeList, sourceCode);
        pageLineCounter.count(nodeList, sourceCode);
        saveMetrics(sensorContext, sourceCode);

      } catch (Exception e) {
        LOG.error("Could not analyze the file " + webFile.getAbsolutePath(), e);
      }
    }
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
    return isEnabled(project) && Web.INSTANCE.equals(project.getLanguage());
  }

  private boolean isEnabled(Project project) {
    return project.getConfiguration().getBoolean(CoreProperties.CORE_IMPORT_SOURCES_PROPERTY,
        CoreProperties.CORE_IMPORT_SOURCES_DEFAULT_VALUE);
  }


  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
