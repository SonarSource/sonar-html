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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.web.Settings;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public final class CssFinder {

    /**
     * Find propagation attributes
     */
    private class LinkTagHandler extends DefaultHandler {

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("link".equals(qName) && "text/css".equals(attributes.getValue("type"))) {
                styleSheets.add(new File(attributes.getValue("href")).getName());
            }
        }
    }

    private static final Logger LOG = Logger.getLogger(CssFinder.class);

    private static final SAXParserFactory SAX_FACTORY;


    /**
     * Build the SAXParserFactory.
     */
    static {

        SAX_FACTORY = SAXParserFactory.newInstance();

        try {
            SAX_FACTORY.setValidating(false);
            SAX_FACTORY.setFeature("http://xml.org/sax/features/validation", false);
            SAX_FACTORY.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            SAX_FACTORY.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            SAX_FACTORY.setFeature("http://xml.org/sax/features/external-general-entities", false);
            SAX_FACTORY.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private final List<String> importedStyleSheets = new ArrayList<String>();

    private final List<String> styleSheets = new ArrayList<String>();

    public File[] findCssFiles() {
      List<File> cssFiles = new ArrayList<File>();
      File folder = new File(Settings.getCssPath());
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

    public File[] findCssImports() throws IOException {
      List<File> imports = new ArrayList<File>();
      for (File file : findCssFiles()) {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
          // @import url(type.css);
          if (line.contains("@import")) {
            String fileName = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(line, "("), ")");
            File importFile = new File(Settings.getCssPath() + "/" + fileName);
            if (importFile.exists()) {
              LOG.info("Import file " + fileName);
              imports.add(importFile);
            } else {
              LOG.error("Import file " + fileName + " does not exist");
              break;
            }
          }
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
     * Parse an XML file with the specified handler.
     */
    private void parseFile(File file, DefaultHandler handler) {
        try {
            SAX_FACTORY.newSAXParser().parse(file, handler);
        } catch (SAXException e) {
            LOG.error(e);
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> parseWebFile(File file) {

        parseFile(file, new LinkTagHandler());
        return styleSheets;
    }

}
