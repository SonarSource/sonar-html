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

package org.sonar.plugins.web.analyzers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.File;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.visitor.DefaultNodeVisitor;

/**
 * Experimental: generate web dependencies.
 *
 * @author Matthijs Galesloot
 */
public class WebDependencyDetector extends DefaultNodeVisitor {

  private static final Logger LOG = LoggerFactory.getLogger(PageCountLines.class);
  private final Web web;

  public WebDependencyDetector(Web web) {
    this.web = web;
  }

  private void calculateDependencies(TagNode element) {

    String attributeValue = element.getAttribute("src");
    if (attributeValue != null) {
      addDependency(attributeValue);
    }
  }

  private void addDependency(String path) {

    if (supportedExtension(path)) {
      File dependencyFile = new File(path);

      LOG.debug(String.format("dependency from %s -> %s",
          getWebSourceCode().getResource().getName(), dependencyFile.getLongName()));

      getWebSourceCode().addDependency(dependencyFile);
    }
  }

  private boolean supportedExtension(String path) {
    for (String suffix : web.getFileSuffixes()) {
      if (path.endsWith(suffix)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void startElement(TagNode element) {
    calculateDependencies(element);
  }

  @Override
  public void directive(DirectiveNode node) {
    if (node.isJsp() && "include".equals(node.getNodeName())) {
      String file = node.getAttribute("file");
      if (file != null) {
        addDependency(file);
      }
    }
  }
}
