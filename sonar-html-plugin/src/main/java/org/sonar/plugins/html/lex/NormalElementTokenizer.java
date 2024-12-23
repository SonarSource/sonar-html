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
package org.sonar.plugins.html.lex;

import java.util.List;
import org.sonar.plugins.html.node.Node;
import org.sonar.sslr.channel.CodeReader;

public class NormalElementTokenizer extends ElementTokenizer {

  public NormalElementTokenizer() {
    super("<", ">");
  }

  @Override
  public boolean consume(CodeReader codeReader, List<Node> nodeList) {
    if (codeReader.charAt(0) != '<') {
      return false;
    }
    int i = 1;
    if (codeReader.charAt(i) == '/' || codeReader.charAt(i) == '!') {
      i++;
    }
    return isValidTagNameStartChar(codeReader, i) && super.consume(codeReader, nodeList);
  }

  /**
   * To avoid wrongly considering a '<' character as being the start of a tag, the first character of the expected tag name is checked for validity.
   * The ranges of valid starting characters for a tag name are defined here: https://www.w3.org/TR/REC-xml/#NT-NameStartChar.
   * The method returns true in the following two cases:
   *  - Current character code value is a valid single UTF-16 character codepoint in the expected range
   *  - Current character and next character are a valid UTF-16 surrogate pair in the expected range
   */
  public static boolean isValidTagNameStartChar(CodeReader codeReader, int index) {
    char nameStartChar = codeReader.charAt(index);
    return isValidSingleCharCodeNameStartChar(nameStartChar) || isValidSurrogatePairNameStartChar(nameStartChar, codeReader.charAt(index + 1));
  }

  // Visible for testing
  static boolean isValidSingleCharCodeNameStartChar(char currentChar) {
    return currentChar == ':'
      || (currentChar >= 'A' && currentChar <= 'Z')
      || currentChar == '_'
      || (currentChar >= 'a' && currentChar <= 'z')
      || (currentChar >= '\u00C0' && currentChar <= '\u00D6')
      || (currentChar >= '\u00D8' && currentChar <= '\u00F6')
      || (currentChar >= '\u00F8' && currentChar <= '\u02FF')
      || (currentChar >= '\u0370' && currentChar <= '\u037D')
      || (currentChar >= '\u037F' && currentChar <= '\u1FFF')
      || (currentChar >= '\u200C' && currentChar <= '\u200D')
      || (currentChar >= '\u2070' && currentChar <= '\u218F')
      || (currentChar >= '\u2C00' && currentChar <= '\u2FEF')
      || (currentChar >= '\u3001' && currentChar <= '\uD7FF')
      || (currentChar >= '\uF900' && currentChar <= '\uFDCF')
      || (currentChar >= '\uFDF0' && currentChar <= '\uFFFD');
  }

  // Visible for testing
  static boolean isValidSurrogatePairNameStartChar(char currentChar, char nextChar) {
    if (!Character.isSurrogatePair(currentChar, nextChar)) {
      return false;
    }

    int codePoint = Character.toCodePoint(currentChar, nextChar);
    return codePoint >= 0x10000 && codePoint <= 0xEFFFF;
  }

}
