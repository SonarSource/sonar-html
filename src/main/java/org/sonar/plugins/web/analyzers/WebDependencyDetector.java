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

package org.sonar.plugins.web.analyzers;

import java.io.File;

import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.web.WebUtils;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.visitor.AbstractNodeVisitor;

/**
 * Experimental: generate web dependencies.
 * 
 * @author Matthijs Galesloot
 */
public class WebDependencyDetector extends AbstractNodeVisitor {

  private final ProjectFileSystem projectFileSystem;

  public WebDependencyDetector(ProjectFileSystem projectFileSystem) {
    this.projectFileSystem = projectFileSystem;
  }

  private void calculateDependencies(TagNode element) {

    String attributeValue = element.getAttribute("src");
    if (attributeValue != null) {

      String fileName = getWebSourceCode().createFullPath(projectFileSystem, attributeValue);

      File dependencyFile = new File(fileName);
      if (dependencyFile.exists()) {
        WebFile dependencyResource = WebFile.fromIOFile(dependencyFile, projectFileSystem.getSourceDirs());

        WebUtils.LOG.debug("dependency: " + dependencyResource.getLongName());

        getWebSourceCode().addDependency(dependencyResource);
      } else {
        WebUtils.LOG.warn("dependency to non-existing file: " + fileName);
      }
    }
  }

  @Override
  public void startElement(TagNode element) {
    calculateDependencies(element);
  }
}
