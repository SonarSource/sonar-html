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

package org.sonar.plugins.web.markupvalidation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parser for the W3C Validation Soap Report
 *
 * @author Matthijs Galesloot
 * @since 0.2
 */
public final class MarkupReport {

  private static final Logger LOG = Logger.getLogger(MarkupReport.class);
  /**
   * Create a report from soap message that was saved in a file.
   */
  public static MarkupReport fromXml(File reportFile) {
    MarkupReport markupReport = new MarkupReport();
    markupReport.parse(reportFile);
    return markupReport;
  }

  private final List<MarkupError> errors = new ArrayList<MarkupError>();

  private File reportFile;

  /**
   * Get all the errors from the report.
   */
  public List<MarkupError> getErrors() {
    return errors;
  }

  private Integer getInteger(Element element, String elementName) {
    NodeList nodeList = element.getElementsByTagName(elementName);
    if (nodeList.getLength() > 0) {
      return Integer.parseInt(nodeList.item(0).getTextContent());
    } else {
      return -1;
    }
  }

  public String getPath() {
    return reportFile.getPath();
  }

  public File getReportFile() {
    return reportFile;
  }

  private String getString(Element element, String elementName) {
    NodeList nodeList = element.getElementsByTagName(elementName);
    if (nodeList.getLength() > 0) {
      return nodeList.item(0).getTextContent();
    } else {
      return null;
    }
  }

  private void parse(File reportFile) {
    this.reportFile = reportFile;
    if ( !reportFile.exists()) {
      throw new RuntimeException("File does not exist: " + reportFile.getPath());
    }
    Document document = parseSoapMessage(reportFile);
    if (document != null) {
      NodeList nodeList = document.getElementsByTagName("m:error");
      for (int i = 0; i < nodeList.getLength(); i++) {
        Element element = (Element) nodeList.item(i);
        MarkupError error = new MarkupError();
        error.setLine(getInteger(element, "m:line"));
        error.setMessageId(getInteger(element, "m:messageid"));
        error.setMessage(getString(element, "m:message"));
        errors.add(error);
      }
    }
  }

  private Document parseSoapMessage(File file) {
    DOMParser parser = new DOMParser();

    try {
      parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      parser.parse(new InputSource(new FileReader(file)));
    } catch (SAXException se) {
      LOG.error("Failed to parse " + file.getPath());
      return null;
    } catch (IOException ioe) {
      LOG.error("Failed to parse " + file.getPath());
      return null;
    }
    return parser.getDocument();
  }
}