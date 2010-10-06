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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("result")
public class ToetstoolReport {

  public static ToetstoolReport fromXml(File file) {
    try {
      FileInputStream input = new FileInputStream(file);
      ToetstoolReport report = (ToetstoolReport) getXstream().fromXML(input);
      report.file = file;
      return report;
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static ToetstoolReport fromXml(InputStream input) {
    return (ToetstoolReport) getXstream().fromXML(input);
  }

  private static XStream getXstream() {
    XStream xstream = new XStream();
    xstream.processAnnotations(new Class[] { ToetstoolReport.class });
    return xstream;
  }

  private File file;

  private Report report;

  @XStreamAsAttribute
  private String reportNumber;
  @XStreamAsAttribute
  private String status;

  @XStreamAsAttribute
  private String url;

  public Report getReport() {
    return report;
  }

  public String getReportNumber() {
    return reportNumber;
  }

  public String getStatus() {
    return status;
  }

  public String getUrl() {
    return url;
  }

  public void save() {
    try {
      FileOutputStream out = new FileOutputStream(file);
      toXml(out);
      out.close();
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void setReportNumber(String reportNumber) {
    this.reportNumber = reportNumber;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String toXml() {
    return getXstream().toXML(this);
  }

  public void toXml(File reportFile) {
    try {
      getXstream().toXML(this, new FileOutputStream(reportFile));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private void toXml(FileOutputStream out) {
    getXstream().toXML(this, out);
  }
}
