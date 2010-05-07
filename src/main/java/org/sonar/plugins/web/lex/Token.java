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

package org.sonar.plugins.web.lex;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang.StringUtils;
import org.apache.html.dom.HTMLDocumentImpl;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.sonar.channel.ChannelDispatcher;
import org.sonar.channel.CodeReader;
import org.sonar.plugins.web.WebUtils;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Matthijs Galesloot
 */
public class Token {

  private int startLinePosition;
  private int startColumnPosition;
  private int endLinePosition;
  private int endColumnPosition;
  private String code;

  public String getCode() {
    return code;
  }

  public int getEndColumnPosition() {
    return endColumnPosition;
  }

  public int getEndLinePosition() {
    return endLinePosition;
  }

  public int getStartColumnPosition() {
    return startColumnPosition;
  }

  public int getStartLinePosition() {
    return startLinePosition;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setEndPosition(CodeReader code) {
    endLinePosition = code.getLinePosition();
    endColumnPosition = code.getColumnPosition();
  }

  public void setStartPosition(CodeReader code) {
    startLinePosition = code.getLinePosition();
    startColumnPosition = code.getColumnPosition();
  }

  public boolean isBlank() {
    return StringUtils.isBlank(code);
  }

  public int getLinesOfCode() {
    return StringUtils.countMatches(code, "\n");
  }

  private Element element;
  private boolean parsed; 
  
  public String getAttribute(String attributeName) {

    if (!parsed) {
      parseElement(); 
    }
    
    if (element != null && element.hasAttribute(attributeName)) {
      return element.getAttribute(attributeName);
    }
    
    return null;
  }
  
  public String getNodeName() {
    if (!parsed) {
      parseElement(); 
    }
    
    if (element != null) {
      return element.getNodeName();
    }
    
    return null;
  }

  private void parseElement() {
    try {
      parsed = true; 
      
      DOMFragmentParser parser = new DOMFragmentParser();
      HTMLDocument document = new HTMLDocumentImpl();
      DocumentFragment fragment = document.createDocumentFragment();
        
      InputSource source = new InputSource(new StringReader(code));
      parser.parse(source, fragment);
      if (fragment.getFirstChild() != null && fragment.getFirstChild() instanceof Element) {
        element = (Element) fragment.getFirstChild();
      }
    } catch (SAXException e) {
      WebUtils.LOG.warn(null, e);
    } catch (IOException e) {
      WebUtils.LOG.warn(null, e);
    }
  }

  @Override
  public String toString() {
    return code;
  }

}