/*
 * Copyright (C) 2010
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

package org.sonar.plugins.web.rules.checks;

import java.io.File;

import org.sonar.api.design.Dependency;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.web.WebUtils;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.lex.HtmlElement;
import org.sonar.plugins.web.lex.HtmlVisitor;
import org.sonar.plugins.web.lex.Token;
import org.sonar.squid.api.SourceCodeEdgeUsage;

/**
 * Experimental: generate web dependencies.
 * 
 * @author Matthijs Galesloot
 */
public class WebDependency extends HtmlVisitor {

  private final ProjectFileSystem projectFileSystem;

  public WebDependency(ProjectFileSystem projectFileSystem) {
    this.projectFileSystem = projectFileSystem;
  }

  @Override
  public void startElement(Token token) {
    calculateDependencies(token);
  }

  private void calculateDependencies(Token token) {

    if (token instanceof HtmlElement) {
      String attributeValue = token.getAttribute("src");
      if (attributeValue != null) {

        String fileName = attributeValue;
        if (fileName.startsWith("/")) {
          fileName = projectFileSystem.getSourceDirs().get(0).getAbsolutePath() + fileName;
        } else {
          fileName = projectFileSystem.getSourceDirs().get(0).getAbsolutePath() + "/"
              + getResource().getParent().getName().replace(".", "/") + "/" + fileName;
        }

        Resource resource2 = WebFile.fromIOFile(new File(fileName), projectFileSystem.getSourceDirs());

        WebUtils.LOG.debug("dependency: " + resource2.getLongName());
        Dependency dependency = new Dependency(getResource(), resource2);
        dependency.setUsage(SourceCodeEdgeUsage.USES.name());
        dependency.setWeight(1);
        getSensorContext().saveDependency(dependency);
      }
    }
  }
}
