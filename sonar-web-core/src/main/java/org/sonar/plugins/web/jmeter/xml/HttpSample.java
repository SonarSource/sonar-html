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

package org.sonar.plugins.web.jmeter.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class HttpSample {

  @XStreamImplicit(itemFieldName = "assertionResult")
  private List<AssertionResult> assertionResult;

  @XStreamAsAttribute
  private String cookies;

  @XStreamImplicit(itemFieldName = "httpSample")
  private List<HttpSample> httpSamples;

  @XStreamAsAttribute
  private String lb;

  @XStreamAsAttribute
  private String method;

  @XStreamAsAttribute
  private String queryString;

  @XStreamAsAttribute
  private String redirectLocation;

  @XStreamAsAttribute
  private String responseData;

  @XStreamAsAttribute
  private String tn;

  public List<AssertionResult> getAssertionResult() {
    return assertionResult;
  }

  public String getCookies() {
    return cookies;
  }

  public List<HttpSample> getHttpSamples() {
    return httpSamples;
  }

  public String getLb() {
    return lb;
  }

  public String getMethod() {
    return method;
  }

  public String getQueryString() {
    return queryString;
  }

  public String getRedirectLocation() {
    return redirectLocation;
  }

  public String getResponseData() {
    return responseData;
  }

  public String getTn() {
    return tn;
  }

  public void setResponseData(String responseData) {
    this.responseData = responseData;
  }

}
