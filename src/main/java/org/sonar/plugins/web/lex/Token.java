/*
 * Copyright (C) 2010
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

package org.sonar.plugins.web.lex;

import org.apache.commons.lang.StringUtils;
import org.sonar.channel.CodeReader;

/**
 * @author Matthijs Galesloot
 */
public class Token {

  private int startLinePosition;
  private int startColumnPosition;
  private int endLinePosition;
  private int endColumnPosition;
  private String code;

  public String getCode() {
    return code;
  }

  public int getEndColumnPosition() {
    return endColumnPosition;
  }

  public int getEndLinePosition() {
    return endLinePosition;
  }

  public int getStartColumnPosition() {
    return startColumnPosition;
  }

  public int getStartLinePosition() {
    return startLinePosition;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setEndPosition(CodeReader code) {
    endLinePosition = code.getLinePosition();
    endColumnPosition = code.getColumnPosition();
  }

  public void setStartPosition(CodeReader code) {
    startLinePosition = code.getLinePosition();
    startColumnPosition = code.getColumnPosition();
  }

  public boolean isBlank() {
    return StringUtils.isBlank(code);
  }

  public int getLinesOfCode() {
    return StringUtils.countMatches(code, "\n");
  }

  public String getAttribute(String attribute) {

    // TODO implement a tag parser here
    return null;
  }

  @Override
  public String toString() {
    return code;
  }

}