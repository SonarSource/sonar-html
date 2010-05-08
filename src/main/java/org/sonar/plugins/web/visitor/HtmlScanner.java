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

package org.sonar.plugins.web.visitor;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.batch.SensorContext;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.lex.Token;

public class HtmlScanner {

  private List<HtmlVisitor> visitors = new ArrayList<HtmlVisitor>();

  public void scan(List<Token> tokenList, SensorContext sensorContext, WebFile resource) {
    
    // notify visitors for a new document
    for (HtmlVisitor visitor : visitors) {
      visitor.startDocument(sensorContext, resource);
    }

    // notify the visitors for start of element
    // TODO notify end of element - e.g. element starts with </) 
    for (Token token : tokenList) {
      for (HtmlVisitor visitor : visitors) {
        visitor.startElement(token);
      }
    }

    // notify visitors for end of document
    for (HtmlVisitor visitor : visitors) {
      visitor.endDocument();
    }
  }

  public void addVisitor(HtmlVisitor visitor) {
    visitors.add(visitor);
  }

}
