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

import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.sonar.api.CoreProperties;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.rules.WebRulesRepository;
import org.sonar.plugins.web.visitor.PageCountLines;
import org.sonar.plugins.web.visitor.PageScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class TestWeb {

  private static final File testPom;

  static {
    try {
      testPom = new File(TestWeb.class.getResource("/pom.xml").toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private static MavenProject loadPom(File pomFile) {
    FileReader fileReader = null;
    try {
      MavenXpp3Reader pomReader = new MavenXpp3Reader();
      fileReader = new FileReader(pomFile);
      Model model = pomReader.read(fileReader);
      MavenProject project = new MavenProject(model);
      project.setFile(pomFile);
      project.getBuild().setDirectory(pomFile.getParentFile().getPath());
      return project;
    } catch (Exception e) {
      throw new SonarException("Failed to read Maven project file : " + pomFile.getPath(), e);
    } finally {
      IOUtils.closeQuietly(fileReader);
    }
  }

  public static Project loadProjectFromPom(File pomFile) {
    MavenProject pom = loadPom(pomFile);
    Project project = new Project(pom.getGroupId() + ":" + pom.getArtifactId()).setPom(pom).setConfiguration(
        new MapConfiguration(pom.getProperties()));
    project.setFileSystem(new DefaultProjectFileSystem(project));
    project.setPom(pom);

    String languageKey = pom.getProperties().getProperty(CoreProperties.PROJECT_LANGUAGE_PROPERTY);

    project.setLanguageKey(languageKey);
    if (Web.INSTANCE.getKey().equals(languageKey)) {
      project.setLanguage(Web.INSTANCE);
    }

    pom.addCompileSourceRoot(pom.getBuild().getSourceDirectory());

    return project;
  }

  @Test
  public void testImporter() throws URISyntaxException {

    final Project project = loadProjectFromPom(testPom);

    WebSourceImporter importer = new WebSourceImporter(Web.INSTANCE);

    assertTrue("Importer only supports web projects", importer.shouldExecuteOnProject(project));
    MockSensorContext sensorContext = new MockSensorContext();
    importer.analyse(project, sensorContext);
  }

  @Test
  public void testJspRules() throws FileNotFoundException {

    String fileName = "src/test/resources/src/main/webapp/user-properties.jsp";
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(fileName));
    assertTrue(nodeList.size() > 100);

    WebFile webFile = new WebFile("test", "user-properties.jsp");
    PageCountLines countLines = new PageCountLines();
    PageScanner scanner = new PageScanner();
    scanner.addVisitor(countLines);

    WebSourceCode webSourceCode = new WebSourceCode(webFile);

    scanner.scan(nodeList, webSourceCode);

  }

  @Test
  public void testSensor() {
    WebRulesRepository webRulesRepository = new WebRulesRepository(Web.INSTANCE);

    RulesProfile rulesProfile = webRulesRepository.getProvidedProfiles().get(0);

    WebSensor sensor = new WebSensor(rulesProfile);

    final Project project = loadProjectFromPom(testPom);
    MockSensorContext sensorContext = new MockSensorContext();
    sensor.analyse(project, sensorContext);

    assertTrue("Should have found 1 violation", sensorContext.getViolations().size() > 0);
  }
}
