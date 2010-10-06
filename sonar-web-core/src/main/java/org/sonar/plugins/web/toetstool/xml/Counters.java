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

package org.sonar.plugins.web.toetstool.xml;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Counters {

  @XStreamAsAttribute
  private Integer csserrors;

  @XStreamAsAttribute
  private Integer error;

  @XStreamAsAttribute
  private Integer htmlerrors;

  @XStreamAsAttribute
  private Integer info;

  @XStreamAsAttribute
  private Integer ok;

  @XStreamAsAttribute
  private Integer unknown;

  @XStreamAsAttribute
  private Integer warning;

  public Integer getCsserrors() {
    return csserrors;
  }

  public Integer getError() {
    return error;
  }

  public Integer getHtmlerrors() {
    return htmlerrors;
  }

  public Integer getInfo() {
    return info;
  }

  public Integer getOk() {
    return ok;
  }

  public Integer getUnknown() {
    return unknown;
  }

  public Integer getWarning() {
    return warning;
  }
}
