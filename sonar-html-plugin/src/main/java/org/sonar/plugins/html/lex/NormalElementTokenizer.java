/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
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
package org.sonar.plugins.html.lex;

import java.util.List;
import org.sonar.channel.CodeReader;
import org.sonar.plugins.html.node.Node;

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
      || currentChar == '_'
      || isInCharRange(currentChar, 'A', 'Z')
      || isInCharRange(currentChar, 'a', 'z')
      || isInCharRange(currentChar, '\u00C0', '\u00D6')
      || isInCharRange(currentChar, '\u00D8', '\u00F6')
      || isInCharRange(currentChar, '\u00F8', '\u02FF')
      || isInCharRange(currentChar, '\u0370', '\u037D')
      || isInCharRange(currentChar, '\u037F', '\u1FFF')
      || isInCharRange(currentChar, '\u200C', '\u200D')
      || isInCharRange(currentChar, '\u2070', '\u218F')
      || isInCharRange(currentChar, '\u2C00', '\u2FEF')
      || isInCharRange(currentChar, '\u3001', '\uD7FF')
      || isInCharRange(currentChar, '\uF900', '\uFDCF')
      || isInCharRange(currentChar, '\uFDF0', '\uFFFD');
  }

  // Visible for testing
  static boolean isValidSurrogatePairNameStartChar(char currentChar, char nextChar) {
    if (!Character.isSurrogatePair(currentChar, nextChar)) {
      return false;
    }

    int codePoint = Character.toCodePoint(currentChar, nextChar);
    return codePoint >= 0x10000 && codePoint <= 0xEFFFF;
  }

  private static boolean isInCharRange(char c, char lowerInclusive, char higherInclusive) {
    return c >= lowerInclusive && c <= higherInclusive;
  }

}
