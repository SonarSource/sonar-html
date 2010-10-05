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
import org.sonar.plugins.web.toetstool.AggregateReport;
import org.sonar.plugins.web.toetstool.ToetsTool;


/**
 * Goal to execute the verification with Toetstool.
 *
 * @goal toetstool
 */
public class ToetstoolMojo extends AbstractMojo {

  /**
   * JMeter directory with output files.
   *
   * @parameter
   * @required
   */
  private String jMeterDir;

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

    getLog().info("jMeterDir = " + jMeterDir);
    getLog().info("toetsToolUrl = " + toetsToolUrl);
    getLog().info("cssDir = " + cssDir);

    Settings.setToetstoolURL(toetsToolUrl);
    Settings.setCssPath(cssDir);

    File jMeterFolder = new File(jMeterDir);
    JMeter jmeter = new JMeter();
    jmeter.extractResponses(jMeterFolder);

    File htmlFolder = new File(jMeterFolder.getAbsolutePath() + "/html");
    if (htmlFolder.exists()) {

      ToetsTool toetstool = new ToetsTool();
      toetstool.validateFiles(htmlFolder);

      AggregateReport aggregateReport = new AggregateReport();
      aggregateReport.buildReports(htmlFolder);

    }
  }
}