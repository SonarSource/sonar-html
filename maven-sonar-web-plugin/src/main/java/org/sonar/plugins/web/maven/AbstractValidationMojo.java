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
import org.sonar.plugins.web.Settings;
import org.sonar.plugins.web.html.HtmlScanner;

public abstract class AbstractValidationMojo extends AbstractMojo {

  /**
   * The Maven Settings.
   *
   * @parameter default-value="${settings}"
   * @required
   * @readonly
   */
  private org.apache.maven.settings.Settings settings;

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

    File htmlFolder = new File(Settings.getHtmlDir());
    if (htmlFolder.exists()) {

      HtmlScanner htmlScanner = new HtmlScanner();
      htmlScanner.prepare(Settings.getHtmlDir());
    }
  }

  protected void configureSettings() {
    for (Object key : getPluginContext().keySet()){
      getLog().info((String) getPluginContext().get(key));
    }
    getLog().info("HTMLDir = " + htmlDir);
    getLog().info("cssDir = " + cssDir);
    getLog().info("nrOfSamples = " + nrOfSamples);

    Settings.setHTMLDir(htmlDir);
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