/*
 * Sonar Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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
package org.sonar.plugins.web.checks.sonar;

import com.google.common.base.Throwables;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.DefaultNodeVisitor;
import org.sonar.plugins.web.visitor.HtmlAstScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public class TestHelper {

  private TestHelper() {
  }

  public static WebSourceCode scan(File file, DefaultNodeVisitor visitor) {
    try {
      PageLexer lexer = new PageLexer();
      List<Node> nodes = lexer.parse(new FileReader(file));
      WebSourceCode result = new WebSourceCode(new org.sonar.api.resources.File("test"));

      HtmlAstScanner walker = new HtmlAstScanner();
      walker.addVisitor(visitor);
      walker.scan(nodes, result);

      return result;
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

}
