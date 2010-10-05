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
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.Project;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.visitor.DefaultNodeVisitor;

/**
 * Experimental: generate web dependencies.
 * 
 * @author Matthijs Galesloot
 */
public class WebDependencyDetector extends DefaultNodeVisitor {

  private static final Logger LOG = LoggerFactory.getLogger(PageCountLines.class);

  private final Project project;

  private final File sourcePath;

  public WebDependencyDetector(Project project) {
    this.project = project;

    String path = (String) project.getProperty("sonar.web.sourceDirectory");
    if (path == null) {
      this.sourcePath = project.getFileSystem().getSourceDirs().get(0);
    } else {
      this.sourcePath = new File(project.getFileSystem().getBasedir() + "/" + path);
    }
  }

  private void calculateDependencies(TagNode element) {

    String attributeValue = element.getAttribute("src");
    if (attributeValue != null) {

      String fileName = createFullPath(project, attributeValue);

      File dependencyFile = new File(fileName);
      if (dependencyFile.exists()) {
        WebFile dependencyResource = WebFile.fromIOFile(dependencyFile, Arrays.asList(sourcePath));

        LOG.debug(String.format("dependency from %s -> %s", getWebSourceCode().getResource().getName(), dependencyResource.getLongName()));

        getWebSourceCode().addDependency(dependencyResource);
      } else {
        LOG.warn("dependency to non-existing file: " + fileName);
      }
    }
  }

  private String createFullPath(Project project, String fileName) {
    String absoluteSourceDir = sourcePath.getAbsolutePath();

    if (fileName.startsWith("/")) {
      return absoluteSourceDir + fileName;
    } else {
      return absoluteSourceDir + "/" + getWebSourceCode().getResource().getParent().getName() + "/" + fileName;
    }
  }

  @Override
  public void startElement(TagNode element) {
    calculateDependencies(element);
  }
}
