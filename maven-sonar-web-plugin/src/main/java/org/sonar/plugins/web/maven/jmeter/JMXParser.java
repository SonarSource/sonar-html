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

package org.sonar.plugins.web.maven.jmeter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parse JMeter jmx file.
 *
 * TODO rewrite with staxmate.
 *
 * @author Matthijs Galesloot
 * @since 0.2
 */
final class JMXParser {

  /**
   * Match HTTPSampler.stringProp[testname].path to HTTPSampler.path.testName.
   */
  private class HttpSamplerHandler extends DefaultHandler {

    private boolean inSamplerNode; // parser inside <HTTPSampler2>
    private boolean inStringPropPathNode; // parser inside <stringProp>
    private StringBuilder sb;
    private String testname;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      if (inStringPropPathNode) {
        if (sb == null) {
          sb = new StringBuilder();
        }
        sb.append(ch, start, length);
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      if (sb != null) {
        httpSamples.put(testname, sb.toString());
        sb = null;
      }
      if ("HTTPSampler2".equals(qName)) {
        inSamplerNode = false;
      }
      inStringPropPathNode = false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if ("HTTPSampler2".equals(qName)) {
        inSamplerNode = true;
        testname = attributes.getValue("testname");
      } else if (inSamplerNode && "stringProp".equals(qName) && "HTTPSampler.path".equals(attributes.getValue("name"))) {
        inStringPropPathNode = true;
      }
    }
  }

  private static final Logger LOG = Logger.getLogger(JMXParser.class);

  private static final SAXParserFactory SAX_FACTORY;

  /**
   * Build the SAXParserFactory without validation features.
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

  private final Map<String, String> httpSamples = new HashMap<String, String>();

  public Map<String, String> findHttpSampleTestNames(File file) {

    parseFile(file, new HttpSamplerHandler());
    return httpSamples;
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

}
