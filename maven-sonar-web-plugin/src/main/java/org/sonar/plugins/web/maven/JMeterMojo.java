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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.sonar.plugins.web.Settings;
import org.sonar.plugins.web.jmeter.JMeter;

/**
 * Extract HTML responses from JMeter report files
 *
 * @goal extract-html
 */
public class JMeterMojo extends AbstractMojo {

  /**
   * JMeter directory with script files.
   *
   * @required
   * @parameter
   */
  private String jMeterScriptDir;

  /**
   * JMeter directory with report files.
   *
   * @required
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

  public void execute() throws MojoExecutionException {

    configureSettings();

    JMeter jmeter = new JMeter();
    jmeter.extractResponses();
  }

  private void configureSettings() {
    for (Object key : getPluginContext().keySet()){
      getLog().info((String) getPluginContext().get(key));
    }
    getLog().info("HTMLDir = " + htmlDir);
    getLog().info("jMeterScriptDir = " + jMeterScriptDir);
    getLog().info("jMeterReportDir = " + jMeterReportDir);

    Settings.setHTMLDir(htmlDir);
    Settings.setJMeterScriptDir(jMeterScriptDir);
    Settings.setJMeterReportDir (jMeterReportDir);
  }
}