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

package org.sonar.plugins.web.checks;

import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sonar.plugins.web.checks.jsp.AttributeClassCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.visitor.PageScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class TestAttributeClassCheck {

  @Test
  public void testAttributeCheck() {

    List<Node> nodeList = new ArrayList<Node>();
    TagNode node = new TagNode();
    node.setCode("<h:xx class=\"redflag\">");
    node.setNodeName("h:xx");
    node.getAttributes().add(new Attribute("class", "redflag"));
    nodeList.add(node);
    
    WebSourceCode webSourceCode = new WebSourceCode(null);
    AttributeClassCheck attributeClassCheck = new AttributeClassCheck();

    PageScanner pageScanner = new PageScanner();
    pageScanner.addVisitor(attributeClassCheck);
    pageScanner.scan(nodeList, webSourceCode);

    assertTrue("Should have found 1 violation", webSourceCode.getViolations().size() == 1);
  }
}
