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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class PageCountLinesTest {

  private static final Logger LOG = LoggerFactory.getLogger(PageCountLinesTest.class);

  @Test
  public void testCountLines() throws FileNotFoundException {

    String fileName = "src/test/resources/src/main/webapp/user-properties.jsp";
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(fileName));
    assertTrue(nodeList.size() > 100);

    WebFile webFile = new WebFile("test", "user-properties.jsp");
    PageCountLines countLines = new PageCountLines();

    WebSourceCode webSourceCode = new WebSourceCode(webFile);

    countLines.count(nodeList, webSourceCode);

    LOG.warn("Lines:" + webSourceCode.getMeasure(CoreMetrics.LINES).getIntValue());

    int numLines = 287;
    assertTrue("Expected " + numLines + " lines, but was: " + webSourceCode.getMeasure(CoreMetrics.LINES).getIntValue(), webSourceCode
        .getMeasure(CoreMetrics.LINES).getIntValue() == numLines);
  }
}
