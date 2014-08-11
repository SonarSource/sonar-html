/*
 * SonarQube Web Plugin
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
package org.sonar.plugins.web.checks;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import org.sonar.plugins.web.analyzers.ComplexityVisitor;
import org.sonar.plugins.web.analyzers.PageCountLines;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.DefaultNodeVisitor;
import org.sonar.plugins.web.visitor.HtmlAstScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.mockito.Mockito.mock;

public class TestHelper {

  private TestHelper() {
  }

  public static WebSourceCode scan(String code, DefaultNodeVisitor visitor) {
    return scan(mock(File.class), new StringReader(code), visitor);
  }

  public static WebSourceCode scan(File file, DefaultNodeVisitor visitor) {
    try {
      return scan(file, new FileReader(file), visitor);
    } catch (FileNotFoundException e) {
      throw Throwables.propagate(e);
    }
  }

  public static WebSourceCode scan(File file, Reader reader, DefaultNodeVisitor visitor) {
    try {
      PageLexer lexer = new PageLexer();
      List<Node> nodes = lexer.parse(reader);
      WebSourceCode result = new WebSourceCode(file, new org.sonar.api.resources.File("test"));

      HtmlAstScanner walker = new HtmlAstScanner(ImmutableList.of(new PageCountLines(), new ComplexityVisitor()));
      walker.addVisitor(visitor);
      walker.scan(nodes, result, Charsets.UTF_8);

      return result;
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

}
