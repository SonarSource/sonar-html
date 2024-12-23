/*
 * SonarQube HTML
 * Copyright (C) 2010-2024 SonarSource SA
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
package org.sonar.plugins.html.core;

import com.sonar.sslr.api.Token;

class TokenLocation {

  private final int startLine;
  private final int startCharacter;
  private final int endLine;
  private final int endCharacter;

  TokenLocation(Token token) {
    this.startLine = token.getLine();
    this.startCharacter = token.getColumn();
    final String[] lines = token.getOriginalValue().split("\r\n|\n|\r", -1);
    if (lines.length > 1) {
      this.endLine = token.getLine() + lines.length - 1;
      this.endCharacter = lines[lines.length - 1].length();
    } else {
      this.endLine = startLine;
      this.endCharacter = startCharacter + token.getOriginalValue().length();
    }
  }

  int startLine() {
    return startLine;
  }

  int startCharacter() {
    return startCharacter;
  }

  int endLine() {
    return endLine;
  }

  int endCharacter() {
    return endCharacter;
  }

}
