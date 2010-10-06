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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.sonar.plugins.web.Settings;
import org.sonar.plugins.web.jmeter.JMeter;
import org.sonar.plugins.web.toetstool.Report;
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
   * @required
   */
  private String jMeterScriptDir;

  /**
   * JMeter directory with report files.
   *
   * @parameter
   * @required
   */
  private String jMeterReportDir;

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

  public void execute() throws MojoExecutionException {

    configureSettings();

    JMeter jmeter = new JMeter();
    jmeter.extractResponses();

    File htmlFolder = new File(jMeterReportDir + "/html");
    if (htmlFolder.exists()) {

      ToetsTool toetstool = new ToetsTool();
      toetstool.validateFiles(htmlFolder);

      Report aggregateReport = new Report();
      aggregateReport.buildReports(htmlFolder);
    }
  }

  private void configureSettings() {
    for (Object key : getPluginContext().keySet()){
      getLog().info((String) getPluginContext().get(key));
    }
    getLog().info("jMeterScriptDir = " + jMeterScriptDir);
    getLog().info("jMeterReportDir = " + jMeterReportDir);
    getLog().info("toetsToolUrl = " + toetsToolUrl);
    getLog().info("cssDir = " + cssDir);

    Settings.setJMeterScriptDir(jMeterScriptDir);
    Settings.setJMeterReportDir (jMeterReportDir);
    Settings.setToetstoolURL(toetsToolUrl);
    Settings.setCssPath(cssDir);

    // configure proxy
    if (settings.getActiveProxy() != null) {
      getLog().info("proxy = " + settings.getActiveProxy().getHost() + ":" + settings.getActiveProxy().getPort() );
      Settings.setProxyHost(settings.getActiveProxy().getHost());
      Settings.setProxyPort(settings.getActiveProxy().getPort());
    }
  }
}