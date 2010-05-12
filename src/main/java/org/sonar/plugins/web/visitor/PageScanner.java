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

package org.sonar.plugins.web.visitor;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.batch.SensorContext;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

public class PageScanner {

  private List<AbstractTokenVisitor> visitors = new ArrayList<AbstractTokenVisitor>();

  public void scan(List<Node> nodeList, SensorContext sensorContext, WebFile resource) {
    
    // notify visitors for a new document
    for (AbstractTokenVisitor visitor : visitors) {
      visitor.startDocument(sensorContext, resource);
    }

    // notify the visitors for start and end of element 
    for (Node node : nodeList) {
      for (AbstractTokenVisitor visitor : visitors) {
        switch (node.getNodeType()) {
          case Tag:
            TagNode element = (TagNode) node;  
            if (element.hasStart()) {
              visitor.startElement(element);
            } else {
              visitor.endElement(element);
            }
            break;
          case Text:
            visitor.characters((TextNode) node); 
            break;
          case Comment:
            visitor.comment((CommentNode) node); 
            break; 
          case Directive:
            visitor.directive((DirectiveNode) node); 
            break; 
        }
      }
    }

    // notify visitors for end of document
    for (AbstractTokenVisitor visitor : visitors) {
      visitor.endDocument();
    }
  }

  public void addVisitor(AbstractTokenVisitor visitor) {
    visitors.add(visitor);
  }

}
