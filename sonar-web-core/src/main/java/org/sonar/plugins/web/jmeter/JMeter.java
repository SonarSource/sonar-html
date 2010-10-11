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
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.web.Settings;
import org.sonar.plugins.web.jmeter.xml.HttpSample;
import org.sonar.plugins.web.jmeter.xml.JMeterReport;
import org.sonar.plugins.web.toetstool.ValidationReport;
import org.sonar.plugins.web.toetstool.xml.ToetstoolReport;

/**
 * Prepare JMeter report files for HTML validation.
 *
 * @author Matthijs Galesloot
 * @since 0.2
 */
public class JMeter {

  private static final Logger LOG = Logger.getLogger(JMeter.class);

  /**
   * Find the reportFile for a JMeter script file.
   */
  private static File findReportFile(File scriptFile) {
    File reportFolder = new File(Settings.getJMeterReportDir());

    // find JMeter reports
    Collection<File> reportFiles = FileUtils.listFiles(reportFolder, new String[] { "xml" }, true);
    String scriptName = StringUtils.substringBeforeLast(scriptFile.getName(), ".");

    for (File file : reportFiles) {
      if (file.getName().contains(scriptName)) {
        return file;
      }
    }
    return null;
  }

  /**
   * Extract HTTP responses from the JMeter report file.
   */
  public void extractResponses() {
    // first clear output html folder
    File htmlFolder = new File(Settings.getHtmlDir());
    resetFolder(htmlFolder);

    // find JMeter scripts
    Collection<File> scriptFiles = FileUtils.listFiles(new File(Settings.getJMeterScriptDir()), new String[] { "jmx" }, true);

    // get the responses from the JMeter reports
    for (File scriptFile : scriptFiles) {
      try {
        // parse JMeter script to find test names
        JMXParser jmxParser = new JMXParser();
        Map<String, String> testNames = jmxParser.findHttpSampleTestNames(scriptFile);

        // parse JMeter report
        File reportFile = findReportFile(scriptFile);
        if (reportFile != null) {
          JMeterReport report = JMeterReport.fromXml(new FileInputStream(reportFile));

          writeHttpSamples(testNames, htmlFolder, report.getHttpSamples(), false);
        } else {
          LOG.error("Could not find report file for JMeter script " + scriptFile.getName());
        }
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
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

  private void writeHttpSamples(Map<String, String> testNames, File htmlFolder, List<HttpSample> httpSamples, Boolean nested) {
    for (HttpSample sample : httpSamples) {

      File folder = new File(htmlFolder.getPath() + "/" + sample.getTn());

      if ( !folder.exists()) {
        folder.mkdir();
      }
      if ( !StringUtils.isEmpty(sample.getResponseData())) {

        File file;

        ToetstoolReport report = new ToetstoolReport();

        try {
          URL url = new URL(sample.getLb());
          file = new File(folder.getPath() + "/" + url.getPath());
          writeFile(sample, file);
          report.setUrl(sample.getLb());
        } catch (MalformedURLException e) {
          file = new File(folder.getPath() + "/" + sample.getLb() + ".html");
          writeFile(sample, file);
          String url = testNames.get(sample.getLb());
          report.setUrl(url != null ? url : "http://localhost/");
        }

        // save report with URL
        report.toXml(ValidationReport.reportFile(file));
      }

      if (sample.getHttpSamples() != null) {
        writeHttpSamples(testNames, folder, sample.getHttpSamples(), true);
      }
    }
  }
}
