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

package org.sonar.plugins.web.maven.jmeter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.web.Configuration;
import org.sonar.plugins.web.html.FileSet;
import org.sonar.plugins.web.html.FileSet.HtmlFile;
import org.sonar.plugins.web.maven.jmeter.xml.HttpSample;
import org.sonar.plugins.web.maven.jmeter.xml.JMeterReport;

/**
 * Prepare JMeter report files for HTML validation.
 *
 * @author Matthijs Galesloot
 * @since 0.2
 */
class JMeter {

  private static final Logger LOG = Logger.getLogger(JMeter.class);

  /**
   * Find the reportFile for a JMeter script file.
   */
  private static File findReportFile(File scriptFile) {
    File reportFolder = new File(Configuration.getJMeterReportDir());

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
    File htmlFolder = new File(Configuration.getHtmlDir());
    resetFolder(htmlFolder);

    FileSet fileSet = new FileSet();

    // find JMeter scripts
    Collection<File> scriptFiles = FileUtils.listFiles(new File(Configuration.getJMeterScriptDir()), new String[] { "jmx" }, true);

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

          writeHttpSamples(fileSet, testNames, report.getHttpSamples(), false);
        } else {
          LOG.error("Could not find report file for JMeter script " + scriptFile.getName());
        }
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

    fileSet.toXml(FileSet.getPath(htmlFolder));
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

  private void writeFile(org.sonar.plugins.web.maven.jmeter.xml.HttpSample sample, File file) {
    try {
      file.getParentFile().mkdirs();
      FileUtils.writeStringToFile(file, sample.getResponseData());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void writeHttpSamples(FileSet fileSet, Map<String, String> testNames, List<HttpSample> httpSamples, Boolean nested) {
    for (HttpSample sample : httpSamples) {

      if ( !StringUtils.isEmpty(sample.getResponseData())) {

        try {
          URL url = new URL(sample.getLb());
          File file = new File(Configuration.getHtmlDir() + "/" + url.getPath());
          if ( !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
          }
          writeFile(sample, file);
          HtmlFile htmlFile = fileSet.addReplaceFile(file, new File(Configuration.getHtmlDir()));
          htmlFile.url = sample.getLb();
        } catch (MalformedURLException e) {
          File file = new File(Configuration.getHtmlDir() + "/" + sample.getLb() + ".html");
          if ( !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
          }
          writeFile(sample, file);
          String url = testNames.get(sample.getLb());
          HtmlFile htmlFile = fileSet.addReplaceFile(file, new File(Configuration.getHtmlDir()));
          htmlFile.url = url != null ? url : "http://localhost/";
        }
      }

      if (sample.getHttpSamples() != null) {
        writeHttpSamples(fileSet, testNames, sample.getHttpSamples(), true);
      }
    }
  }
}
