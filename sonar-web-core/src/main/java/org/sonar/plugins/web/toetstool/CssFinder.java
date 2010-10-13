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

package org.sonar.plugins.web.toetstool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.web.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/**
 * @author Matthijs Galesloot
 * @since 0.2
 */
final class CssFinder {

  private static final Logger LOG = Logger.getLogger(CssFinder.class);

  private final List<String> importedStyleSheets = new ArrayList<String>();

  private final List<String> styleSheets = new ArrayList<String>();

  public File[] findCssFiles() {
    List<File> cssFiles = new ArrayList<File>();
    File folder = new File(Configuration.getCssPath());
    for (File file : folder.listFiles()) {
      for (String styleSheet : styleSheets) {
        if (file.getName().contains(styleSheet)) {
          cssFiles.add(file);
        }
      }
    }

    LOG.debug(cssFiles.size() + " css files (" + cssFiles + ")");
    return cssFiles.toArray(new File[cssFiles.size()]);
  }

  public File[] findCssImports() {
    List<File> imports = new ArrayList<File>();
    for (File file : findCssFiles()) {

      try {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
          // @import url(type.css);
          if (line.contains("@import")) {
            String fileName = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(line, "("), ")");
            File importFile = new File(Configuration.getCssPath() + "/" + fileName);
            if (importFile.exists()) {
              LOG.debug("Import file " + fileName);
              imports.add(importFile);
            } else {
              LOG.error("Import file " + fileName + " does not exist");
              break;
            }
          }
        }
      } catch (FileNotFoundException e) {
        LOG.error("Could not find imported css file " + file.getName());
      } catch (IOException e) {
        LOG.error("Could not find read from css file " + file.getName());
      }
    }
    return imports.toArray(new File[imports.size()]);
  }

  public List<String> getImportedStyleSheets() {
    return importedStyleSheets;
  }

  public List<String> getStyleSheets() {
    return styleSheets;
  }

  /**
   * Parse an XML file to find linked stylesheets.
   */
  private void parseFile(File file) {
    try {
      Tidy tidy = new Tidy();
      tidy.setShowWarnings(false);
      tidy.setQuiet(true);

      Document document = tidy.parseDOM(new FileInputStream(file), null);
      NodeList nodeList = document.getElementsByTagName("link");
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        Node type = node.getAttributes().getNamedItem("type");
        if (type != null && "text/css".equals(type.getNodeValue())) {
          Node href = node.getAttributes().getNamedItem("href");
          if (href != null) {
            String styleSheetRef = href.getNodeValue();
            styleSheets.add(StringUtils.substringAfterLast(styleSheetRef, "/"));
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<String> parseWebFile(File file) {

    parseFile(file);
    return styleSheets;
  }
}
