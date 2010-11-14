/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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
