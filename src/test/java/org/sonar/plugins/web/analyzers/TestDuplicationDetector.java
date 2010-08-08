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

package org.sonar.plugins.web.analyzers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.web.MockSensorContext;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class TestDuplicationDetector {

  @Test
  public void testDuplicationDetector() throws FileNotFoundException {

    String fileName = "src/test/resources/src/main/webapp/user-properties.jsp";
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(fileName));
    assertTrue(nodeList.size() > 100);

    WebFile webFile = new WebFile("test", "user-properties.jsp");
    WebSourceCode webSourceCode = new WebSourceCode(webFile);

    DuplicationDetector detector = new DuplicationDetector();
    detector.setMinimumTokens(3);
    detector.addTokens(nodeList, webSourceCode);

    SensorContext context = new MockSensorContext();
    detector.analyse(context);

    assertEquals(8, context.getMeasure(webFile, CoreMetrics.DUPLICATED_LINES).getValue().intValue());
    assertEquals(2, context.getMeasure(webFile, CoreMetrics.DUPLICATED_BLOCKS).getValue().intValue());
  }
}
