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

package org.sonar.plugins.web.html;


public abstract class AbstractReportBuilder {

  protected StringBuilder sb;

  protected void addCells(Object... values) {
    for (Object value : values) {
      sb.append("<td>");
      sb.append(value == null ? "" : value);
      sb.append("</td>\n");
    }
  }

  protected void addHeaderCell(Object value) {
    sb.append("<th>");
    sb.append(value);
    sb.append("</th>\n");
  }

  protected void addRow(Object... values) {
    startRow();
    addCells(values);
    endRow();
  }

  protected void startRow() {
    sb.append("<tr>");
  }

  protected void endRow() {
    sb.append("</tr>");
  }
}
