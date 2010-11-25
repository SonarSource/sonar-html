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

import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.sonar.api.platform.ServerFileSystem;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.rules.WebRulesRepository;

/**
 *
 * @author Matthijs Galesloot
 *
 */
public class AbstractWebPluginTester {

  private class WebRuleFinder implements RuleFinder {

    private final WebRulesRepository repository;
    private final List<Rule> rules;

    public WebRuleFinder() {
      repository = new WebRulesRepository(newServerFileSystem());
      rules = repository.createRules();
    }

    public Rule find(RuleQuery query) {
      return null;
    }

    public Collection<Rule> findAll(RuleQuery query) {
      return null;
    }

    public Rule findByKey(String repositoryKey, String key) {
      for (Rule rule : rules) {
        if (rule.getKey().equals(key)) {
          return rule;
        }
      }
      return null;
    }
  }

  private static MavenProject loadPom(File pomFile) throws URISyntaxException {

    FileReader fileReader = null;
    try {
      fileReader = new FileReader(pomFile);
      Model model = new MavenXpp3Reader().read(fileReader);
      MavenProject project = new MavenProject(model);
      project.setFile(pomFile);
      project.addCompileSourceRoot(project.getBuild().getSourceDirectory());

      return project;
    } catch (Exception e) {
      throw new SonarException("Failed to read Maven project file : " + pomFile.getPath(), e);
    } finally {
      IOUtils.closeQuietly(fileReader);
    }
  }

  protected static Project loadProjectFromPom(File pomFile) throws Exception {
    MavenProject pom = loadPom(pomFile);
    Project project = new Project(pom.getGroupId() + ":" + pom.getArtifactId()).setPom(pom).setConfiguration(
        new MapConfiguration(pom.getProperties()));
    project.setFileSystem(new DefaultProjectFileSystem(project));
    project.setPom(pom);
    project.setLanguageKey(Web.INSTANCE.getKey());
    project.setLanguage(Web.INSTANCE);

    return project;
  }

  protected RuleFinder newRuleFinder() {
    return new WebRuleFinder();
  }

  protected ServerFileSystem newServerFileSystem() {

    return new ServerFileSystem() {

      public List<File> getExtensions(String dirName, String... suffixes) {
        return null;
      }

      public File getHomeDir() {
        return null;
      }
    };
  }
}
