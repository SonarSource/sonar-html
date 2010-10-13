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
import org.apache.maven.settings.Settings;
import org.sonar.plugins.web.Configuration;
import org.sonar.plugins.web.html.HtmlScanner;

/**
 * Abstract superclass for Validation Mojo's
 *
 * @author Matthijs Galesloot
 * @since 0.2
 *
 */
public abstract class AbstractValidationMojo extends AbstractMojo {

  /**
   * The Maven Settings.
   *
   * @parameter default-value="${settings}"
   * @required
   * @readonly
   */
  private Settings settings;

  /**
   * HTML directory with location of HTML files.
   *
   * @parameter
   * @required
   */
  private String htmlDir;

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

  protected void prepareHtml() {

    File htmlFolder = new File(Configuration.getHtmlDir());
    if (htmlFolder.exists()) {

      HtmlScanner htmlScanner = new HtmlScanner();
      htmlScanner.prepare(Configuration.getHtmlDir());
    }
  }

  protected void configureSettings() {
    for (Object key : getPluginContext().keySet()){
      getLog().info((String) getPluginContext().get(key));
    }
    getLog().info("HTMLDir = " + htmlDir);
    getLog().info("cssDir = " + cssDir);
    getLog().info("nrOfSamples = " + nrOfSamples);

    Configuration.setHTMLDir(htmlDir);
    Configuration.setCssPath(cssDir);
    if (nrOfSamples != null && nrOfSamples > 0) {
      Configuration.setNrOfSamples(nrOfSamples);
    }

    // configure proxy
    if (settings.getActiveProxy() != null) {
      getLog().info("proxy = " + settings.getActiveProxy().getHost() + ":" + settings.getActiveProxy().getPort() );
      Configuration.setProxyHost(settings.getActiveProxy().getHost());
      Configuration.setProxyPort(settings.getActiveProxy().getPort());
    }
  }
}