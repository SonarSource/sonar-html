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

package org.sonar.plugins.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Matthijs Galesloot
 * @since 0.2
 */
public final class Settings {

  private static final String CSS_PATH = "css.path";

  private static final String HTML_PATH = "html.path";

  private static final String JMETER_REPORT_PATH = "jmeter.report.path";

  private static final String JMETER_SCRIPT_PATH = "jmeter.script.path";

  private static final String NR_OF_SAMPLES = "nrOfSamples";

  private static final Properties properties = new Properties();

  private static final String PROXY_HOST = "proxy.host";

  private static final String PROXY_PORT = "proxy.port";

  private static final String PROXY_USE = "proxy.use";

  private static final String SETTINGS_FILE_NAME = "settings.properties";

  private static final String TOETS_TOOL_URL = "toetstool.url";

  static {
    loadProperties(SETTINGS_FILE_NAME);
  }

  public static String getCssPath() {
    return getProperties().getProperty(CSS_PATH);
  }

  public static String getHtmlDir() {
    return getProperties().getProperty(HTML_PATH);
  }

  public static String getJMeterReportDir() {
    return getProperties().getProperty(JMETER_REPORT_PATH);
  }

  public static String getJMeterScriptDir() {
    return getProperties().getProperty(JMETER_SCRIPT_PATH);
  }

  public static Integer getNrOfSamples() {
    if (getProperties().getProperty(NR_OF_SAMPLES) != null) {
      return Integer.parseInt(getProperties().getProperty(NR_OF_SAMPLES));
    } else {
      return null;
    }
  }

  private static Properties getProperties() {
    return properties;
  }

  /**
   * Returns the proxy host.
   * 
   * @return proxy host; null if proxy.use == false
   */
  public static String getProxyHost() {
    if (useProxy()) {
      return getProperties().getProperty(PROXY_HOST);
    } else {
      return null;
    }
  }

  /**
   * Returns the proxy port.
   * 
   * @return proxy port; null if proxy.use == false
   */
  public static int getProxyPort() {
    if (useProxy()) {
      return Integer.parseInt(getProperties().getProperty(PROXY_PORT));
    } else {
      return 0;
    }
  }

  public static String getToetstoolURL() {
    String url = getProperties().getProperty(TOETS_TOOL_URL);
    if (url.endsWith("/")) {
      return url;
    } else {
      return url + '/';
    }
  }

  /**
   * Read properties from file.
   */
  private static void loadProperties(final String fileName) {
    try {
      if (new File(fileName).exists()) {
        properties.load(new FileInputStream(fileName));
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Cannot find the settings file '" + fileName + "'.", e);
    } catch (IOException e) {
      throw new RuntimeException("Problem loading the settings file '" + fileName + "'.", e);
    }
  }

  public static void setCssPath(String path) {
    getProperties().setProperty(CSS_PATH, path);
  }

  public static void setHTMLDir(String htmlDir) {
    getProperties().setProperty(HTML_PATH, htmlDir);
  }

  public static void setJMeterReportDir(String dir) {
    getProperties().setProperty(JMETER_REPORT_PATH, dir);
  }

  public static void setJMeterScriptDir(String dir) {
    getProperties().setProperty(JMETER_SCRIPT_PATH, dir);
  }

  public static void setNrOfSamples(Integer nrOfSamples) {
    getProperties().setProperty(NR_OF_SAMPLES, Integer.toString(nrOfSamples));
  }

  public static void setProxyHost(String proxyHost) {
    getProperties().setProperty(PROXY_USE, "true");
    getProperties().setProperty(PROXY_HOST, proxyHost);
  }

  public static void setProxyPort(int proxyPort) {
    getProperties().setProperty(PROXY_PORT, Integer.toString(proxyPort));
  }

  public static void setToetstoolURL(String url) {
    getProperties().setProperty(TOETS_TOOL_URL, url);
  }

  /**
   * Returns whether or not to use a proxy.
   * 
   * @return true/false
   */
  public static boolean useProxy() {
    return Boolean.parseBoolean(getProperties().getProperty(PROXY_USE));
  }

  /**
   * Private Constructor.
   */
  private Settings() {

  }
}
