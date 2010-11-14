/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.web;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.CoreProperties;
import org.sonar.api.platform.ServerFileSystem;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.web.language.Web;

/**
 *
 * @author Matthijs Galesloot
 *
 */
public class AbstractWebPluginTester {

  private static MavenProject loadPom() throws URISyntaxException {
    File pomFile = new File(WebSensorTest.class.getResource("/pom.xml").toURI());

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

  protected static Project loadProjectFromPom() throws Exception {
    MavenProject pom = loadPom();
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

  protected RuleFinder newRuleFinder() {
    RuleFinder ruleFinder = mock(RuleFinder.class);
    when(ruleFinder.findByKey(anyString(), anyString())).thenAnswer(new Answer<Rule>(){
      public Rule answer(InvocationOnMock iom) throws Throwable {
        return Rule.create((String) iom.getArguments()[0], (String) iom.getArguments()[1], (String) iom.getArguments()[1]);
      }
    });
    return ruleFinder;
  }

  protected ServerFileSystem newServerFileSystem() {

    return new ServerFileSystem() {

      public File getHomeDir() {
        // TODO Auto-generated method stub
        return null;
      }

      public List<File> getExtensions(String dirName, String... suffixes) {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }
}
