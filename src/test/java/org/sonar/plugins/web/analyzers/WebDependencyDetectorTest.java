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

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.junit.Test;
import org.sonar.api.resources.File;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.PageScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class WebDependencyDetectorTest {

  @Test
  public void testDependencyDetector() throws FileNotFoundException {

    String fileName = "src/test/resources/src/main/webapp/user-properties.jsp";
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(fileName));
    assertTrue(nodeList.size() > 100);

    File webFile = new File("test", "user-properties.jsp");

    final PageScanner scanner = new PageScanner();
    scanner.addVisitor(new WebDependencyDetector(new Web()));
    WebSourceCode webSourceCode = new WebSourceCode(webFile);
    scanner.scan(nodeList, webSourceCode);

    assertEquals(3, webSourceCode.getDependencies().size());
  }
}
