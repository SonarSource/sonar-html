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

import java.io.InputStream;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("testResults")
public class JMeterReport {

  public static JMeterReport fromXml(InputStream input) {
    XStream xstream = new XStream();
    xstream.processAnnotations(new Class[] { JMeterReport.class });
    return (JMeterReport) xstream.fromXML(input);
  }

  @XStreamImplicit(itemFieldName = "httpSample")
  private List<HttpSample> httpSamples;

  public List<HttpSample> getHttpSamples() {
    return httpSamples;
  }

  public String toXml() {
    XStream xstream = new XStream();
    xstream.processAnnotations(new Class[] { JMeterReport.class });
    return xstream.toXML(this);
  }
}
