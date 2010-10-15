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

package org.sonar.plugins.web.markupvalidation;


/**
 * MarkupError contains error information retrieved from the W3C error messages.
 *
 * @author Matthijs Galesloot
 * @since 0.2
 */
public class MarkupMessage {

  private Integer line;
  private String message;

  private Integer messageId;

  public Integer getLine() {
    return line;
  }

  public String getMessage() {
    return message;
  }

  public Integer getMessageId() {
    return messageId;
  }

  public void setLine(Integer line) {
    this.line = line;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setMessageId(Integer messageId) {
    this.messageId = messageId;
  }

}
