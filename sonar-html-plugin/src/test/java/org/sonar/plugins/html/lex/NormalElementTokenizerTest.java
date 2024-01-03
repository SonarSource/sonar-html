/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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

import org.junit.jupiter.api.Test;
import org.sonar.sslr.channel.CodeReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.plugins.html.lex.NormalElementTokenizer.isValidSingleCharCodeNameStartChar;
import static org.sonar.plugins.html.lex.NormalElementTokenizer.isValidSurrogatePairNameStartChar;
import static org.sonar.plugins.html.lex.NormalElementTokenizer.isValidTagNameStartChar;

public class NormalElementTokenizerTest {

  @Test
  public void is_valid_tag_name_start_char() {
    String invalidSingleCharCode = "\u0011";
    String validSingleCharCode = "\u0070";
    String invalidSurrogatePair = "\uDBBF\uDF00";
    String validSurrogatePair = "\uD800\uDC01";
    assertThat(isValidTagNameStartChar(new CodeReader(invalidSingleCharCode), 0)).isFalse();
    assertThat(isValidTagNameStartChar(new CodeReader(validSingleCharCode), 0)).isTrue();
    assertThat(isValidTagNameStartChar(new CodeReader(invalidSurrogatePair), 0)).isFalse();
    assertThat(isValidTagNameStartChar(new CodeReader(validSurrogatePair), 0)).isTrue();
  }

  @Test
  public void is_valid_single_char_code_name_start_char() {
    // Valid ranges for first character name for a tag (encoded on a single char). See https://www.w3.org/TR/REC-xml/#NT-NameStartChar.
    assertThat(isValidRangeForFirstCharacter('\u003A', '\u003A')).isTrue(); // ':' char
    assertThat(isValidRangeForFirstCharacter('\u0041', '\u005A')).isTrue(); // 'A' to 'Z'
    assertThat(isValidRangeForFirstCharacter('\u005F', '\u005F')).isTrue(); // '_' char
    assertThat(isValidRangeForFirstCharacter('\u0061', '\u007A')).isTrue(); // 'a' to 'z'
    assertThat(isValidRangeForFirstCharacter('\u00C0', '\u00D6')).isTrue();
    assertThat(isValidRangeForFirstCharacter('\u00D8', '\u00F6')).isTrue();
    assertThat(isValidRangeForFirstCharacter('\u00F8', '\u02FF')).isTrue();
    assertThat(isValidRangeForFirstCharacter('\u0370', '\u037D')).isTrue();
    assertThat(isValidRangeForFirstCharacter('\u037F', '\u1FFF')).isTrue();
    assertThat(isValidRangeForFirstCharacter('\u200C', '\u200D')).isTrue();
    assertThat(isValidRangeForFirstCharacter('\u2070', '\u218F')).isTrue();
    assertThat(isValidRangeForFirstCharacter('\u2C00', '\u2FEF')).isTrue();
    assertThat(isValidRangeForFirstCharacter('\u3001', '\uD7FF')).isTrue();
    assertThat(isValidRangeForFirstCharacter('\uF900', '\uFDCF')).isTrue();
    assertThat(isValidRangeForFirstCharacter('\uFDF0', '\uFFFD')).isTrue();
  }

  @Test
  public void is_invalid_single_char_code_name_start_char() {
    // Invalid character codepoints (encoded on a single char): all ranges in between the above valid ranges.
    assertThat(isInvalidRangeForFirstCharacter('\u0000', '\u0039')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\u003B', '\u0040')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\u005B', '\u005E')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\u0060', '\u0060')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\u007B', '\u00BF')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\u00D7', '\u00D7')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\u00F7', '\u00F7')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\u0300', '\u036F')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\u037E', '\u037E')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\u2000', '\u200B')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\u200E', '\u206F')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\u2190', '\u2BFF')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\u2FF0', '\u3000')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\uD800', '\uF8FF')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\uFDD0', '\uFDEF')).isTrue();
    assertThat(isInvalidRangeForFirstCharacter('\uFFFE', '\uFFFF')).isTrue();
  }

  @Test
  public void is_valid_surrogate_pair_name_start_char() {
    for (int codePoint = 0x10000; codePoint <= 0xEFFFF; codePoint++) {
      char[] chars = Character.toChars(codePoint);
      assertThat(chars).hasSize(2);
      assertThat(isValidSurrogatePairNameStartChar(chars[0], chars[1])).isTrue();
    }

    for (int codePoint = 0xF0000; codePoint <= 0xFFFFF; codePoint++) {
      char[] chars = Character.toChars(codePoint);
      assertThat(chars).hasSize(2);
      assertThat(isValidSurrogatePairNameStartChar(chars[0], chars[1])).isFalse();
    }
  }

  /**
   * Helper test method: returns true if all invocations to {@link NormalElementTokenizer#isValidSingleCharCodeNameStartChar} returns true for the given character range.
   */
  private static boolean isValidRangeForFirstCharacter(char startInclusive, char endInclusive) {
    for (int i = startInclusive; i <= endInclusive; i++) {
      if (!isValidSingleCharCodeNameStartChar((char) i)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Helper test method: returns true if all invocations to {@link NormalElementTokenizer#isValidSingleCharCodeNameStartChar} returns false for the given character range.
   */
  private static boolean isInvalidRangeForFirstCharacter(char startInclusive, char endInclusive) {
    for (int i = startInclusive; i <= endInclusive; i++) {
      if (isValidSingleCharCodeNameStartChar((char) i)) {
        return false;
      }
    }
    return true;
  }

}
