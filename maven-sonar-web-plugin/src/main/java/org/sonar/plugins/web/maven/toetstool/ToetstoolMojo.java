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

package org.sonar.plugins.web.maven.toetstool;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.sonar.plugins.web.Configuration;
import org.sonar.plugins.web.maven.AbstractValidationMojo;
import org.sonar.plugins.web.maven.HtmlValidator;

/**
 * Goal to execute the HTML verification with Toetstool.
 *
 * @goal validate-html-toetstool
 *
 * @author Matthijs Galesloot
 * @since 0.2
 */
public class ToetstoolMojo extends AbstractValidationMojo {

  /**
   * Toetstool URL.
   *
   * @parameter
   * @required
   */
  private String toetsToolUrl;

  @Override
  protected void configureSettings() {
    super.configureSettings();

    getLog().info("toetsToolUrl = " + toetsToolUrl);
    Configuration.setToetstoolURL(toetsToolUrl);
  }

  public void execute() throws MojoExecutionException {

    configureSettings();

    // prepare HTML
    prepareHtml();

    // execute validation
    File htmlFolder = new File(Configuration.getHtmlDir());
    HtmlValidator toetstool = new ToetsToolValidator();
    toetstool.validateFiles(htmlFolder);

    // build report
    ToetsToolReportBuilder reportBuilder = new ToetsToolReportBuilder();
    reportBuilder.buildReports(htmlFolder);
  }

  public void setToetsToolUrl(String toetsToolUrl) {
    this.toetsToolUrl = toetsToolUrl;
  }
}