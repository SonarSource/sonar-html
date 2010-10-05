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

package org.sonar.plugins.web.jmeter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.web.jmeter.xml.HttpSample;
import org.sonar.plugins.web.jmeter.xml.JMeterReport;
import org.sonar.plugins.web.toetstool.ValidationReport;
import org.sonar.plugins.web.toetstool.xml.ToetstoolReport;

public class JMeter {

  private static final Logger LOG = Logger.getLogger(JMeter.class);

  public void extractResponses(File folder) {
    // first clear output html folder
    File htmlFolder = new File(folder.getPath() + "/html/");
    resetFolder(htmlFolder);

    // find all JMeter reports
    Collection<File> files = FileUtils.listFiles(folder, new String[] { "xml" }, true);

    // get the responses from the JMeter reports
    for (File file : files) {
      try {
        JMeterReport report = JMeterReport.fromXml(new FileInputStream(file));
        writeHttpSamples(htmlFolder, report.getHttpSamples(), false);
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void writeHttpSamples(File htmlFolder, List<HttpSample> httpSamples, Boolean nested) {
    for (HttpSample sample : httpSamples) {

      File folder = new File(htmlFolder.getPath() + "/" + sample.getTn());

      if ( !folder.exists()) {
        folder.mkdir();
      }
      if ( !StringUtils.isEmpty(sample.getResponseData())) {

        File file;

        try {
          URL url = new URL(sample.getLb());
          file = new File(folder.getPath() + "/" + url.getPath());
          writeFile(sample, file);
        } catch (MalformedURLException e) {
          file = new File(folder.getPath() + "/" + sample.getLb() + ".html");
          writeFile(sample, file);
        }

        // save URL in report
        ToetstoolReport report = new ToetstoolReport();
        report.setUrl(sample.getLb());
        report.toXml(ValidationReport.reportFile(file));
      }

      if (sample.getHttpSamples() != null) {

        StringBuilder sx = new StringBuilder();
        for (HttpSample sample2 : sample.getHttpSamples()) {
          try {
            if (sx.length() > 0) {
              sx.append(" --> ");
            }
            URL url = new URL(sample2.getLb());
            sx.append(url.getPath());

          } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        LOG.info("Nesting " + sx.toString());

        writeHttpSamples(folder, sample.getHttpSamples(), true);
      }
    }
  }

  private File resetFolder(File folder) {

    try {
      FileUtils.deleteDirectory(folder);
    } catch (IOException e1) {
      LOG.error("Could not delete folder " + folder.getPath());
      throw new RuntimeException();
    }
    folder.mkdir();
    return folder;
  }

  private void writeFile(HttpSample sample, File file) {
    try {
      file.getParentFile().mkdirs();
      FileWriter writer = new FileWriter(file);
      writer.append(sample.getResponseData());
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
