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

package org.sonar.plugins.web.maven;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.sonar.plugins.web.Settings;
import org.sonar.plugins.web.html.HtmlScanner;
import org.sonar.plugins.web.html.MarkupValidator;
import org.sonar.plugins.web.jmeter.JMeter;
import org.sonar.plugins.web.toetstool.ReportBuilder;
import org.sonar.plugins.web.toetstool.ToetsTool;

/**
 * Goal to execute the verification with Toetstool.
 *
 * @goal toetstool
 */
public class ToetstoolMojo extends AbstractMojo {

  /**
   * The Maven Settings.
   *
   * @parameter default-value="${settings}"
   * @required
   * @readonly
   */
  private org.apache.maven.settings.Settings settings;

  /**
   * JMeter directory with script files.
   *
   * @parameter
   */
  private String jMeterScriptDir;

  /**
   * JMeter directory with report files.
   *
   * @parameter
   */
  private String jMeterReportDir;

  /**
   * HTML directory with location of HTML files.
   *
   * @parameter
   * @required
   */
  private String htmlDir;

  /**
   * Toetstool URL.
   *
   * @parameter
   * @required
   */
  private String toetsToolUrl;

  /**
   * Directory containing css files.
   *
   * @parameter
   * @required
   */
  private String cssDir;

  /**
   * Number of samples.
   *
   * @parameter
   */
  private Integer nrOfSamples;

  /**
   * @parameter
   * @required
   */
  private List<String> validationServices;

  public void execute() throws MojoExecutionException {

    configureSettings();

    if (jMeterScriptDir != null && jMeterScriptDir != null) {
      JMeter jmeter = new JMeter();
      jmeter.extractResponses();
    }

    File htmlFolder = new File(htmlDir);
    if (htmlFolder.exists()) {

      HtmlScanner htmlScanner = new HtmlScanner();
      htmlScanner.prepare(Settings.getHtmlDir());

      if (validationServices.contains("HtmlMarkup")) {
        MarkupValidator markupValidator = new MarkupValidator();
        markupValidator.validateFiles(htmlFolder);
      }

      if (validationServices.contains("Toetstool")) {
        ToetsTool toetstool = new ToetsTool();
        toetstool.validateFiles(htmlFolder);
      }

      ReportBuilder aggregateReport = new ReportBuilder();
      aggregateReport.buildReports(htmlFolder);
    }
  }

  private void configureSettings() {
    for (Object key : getPluginContext().keySet()){
      getLog().info((String) getPluginContext().get(key));
    }
    getLog().info("HTMLDir = " + htmlDir);
    getLog().info("jMeterScriptDir = " + jMeterScriptDir);
    getLog().info("jMeterReportDir = " + jMeterReportDir);
    getLog().info("toetsToolUrl = " + toetsToolUrl);
    getLog().info("cssDir = " + cssDir);
    getLog().info("validationServices = " + StringUtils.join(validationServices, ", "));
    getLog().info("nrOfSamples = " + nrOfSamples);

    Settings.setHTMLDir(htmlDir);
    Settings.setJMeterScriptDir(jMeterScriptDir);
    Settings.setJMeterReportDir (jMeterReportDir);
    Settings.setToetstoolURL(toetsToolUrl);
    Settings.setCssPath(cssDir);
    if (nrOfSamples != null && nrOfSamples > 0) {
      Settings.setNrOfSamples(nrOfSamples);
    }

    // configure proxy
    if (settings.getActiveProxy() != null) {
      getLog().info("proxy = " + settings.getActiveProxy().getHost() + ":" + settings.getActiveProxy().getPort() );
      Settings.setProxyHost(settings.getActiveProxy().getHost());
      Settings.setProxyPort(settings.getActiveProxy().getPort());
    }
  }
}