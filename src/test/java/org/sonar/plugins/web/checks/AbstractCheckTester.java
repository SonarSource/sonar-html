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

import java.io.Reader;
import java.util.List;

import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.rules.AbstractPageCheck;
import org.sonar.plugins.web.visitor.PageScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

public abstract class AbstractCheckTester {

  public WebSourceCode parseAndCheck(Reader reader, AbstractPageCheck pageCheck) {
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);
    WebSourceCode webSourceCode = new WebSourceCode(null);

    PageScanner pageScanner = new PageScanner();
    pageScanner.addVisitor(pageCheck);
    pageScanner.scan(nodeList, webSourceCode);
    return webSourceCode;
  }
}
