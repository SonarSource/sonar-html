/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.plugins.html.analyzers.ComplexityVisitor;
import org.sonar.plugins.html.analyzers.PageCountLines;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.lex.VueLexer;
import org.sonar.plugins.html.visitor.DefaultNodeVisitor;
import org.sonar.plugins.html.visitor.HtmlAstScanner;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class TestHelper {

  private TestHelper() {
  }

  public static HtmlSourceCode scan(File file, DefaultNodeVisitor visitor) {
    FileReader fileReader;
    try {
      fileReader = new FileReader(file);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException(e);
    }

    HtmlSourceCode result = new HtmlSourceCode(
      new TestInputFileBuilder("key", file.getPath())
        .setLanguage(HtmlConstants.LANGUAGE_KEY)
        .setType(InputFile.Type.MAIN)
        .setModuleBaseDir(new File(".").toPath())
        .setCharset(StandardCharsets.UTF_8)
        .build()
    );

    HtmlAstScanner walker = new HtmlAstScanner(List.of(new PageCountLines(), new ComplexityVisitor()));
    PageLexer lexer = file.getName().endsWith(".vue") ? new VueLexer() : new PageLexer();
    walker.addVisitor(visitor);
    walker.scan(
      lexer.parse(fileReader),
      result
    );

    return result;
  }

}
