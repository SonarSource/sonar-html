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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public final class Settings {

    private static final String TOETS_TOOL_URL = "toetstool.url";

    private static final String PROXY_USE = "proxy.use";

    private static final String PROXY_PORT = "proxy.port";

    private static final String PROXY_HOST = "proxy.host";

    private static final String SETTINGS_FILE_NAME = "settings.properties";

    private static final String CSS_PATH = "css.path";

    private static final String JMETER_PATH = "jmeter.path";

    private static Properties properties;

    private static Properties getProperties() {
        // logger.debug("getProperties()");
        if (properties == null) {
            properties = loadProperties(SETTINGS_FILE_NAME);
        }
        return properties;
    }

    /**
     * Read properties from file.
     */
    public static Properties loadProperties(final String fileName) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot find the settings file '" + fileName + "'.",
                    e);
        } catch (IOException e) {
            throw new RuntimeException("Problem loading the settings file '" + fileName
                    + "'.", e);
        }
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

    public static String getToetstoolURL() {
      String url = getProperties().getProperty(TOETS_TOOL_URL);
      if (url.endsWith("/")) {
        return url;
      } else {
        return url + '/';
      }
    }

    public static void setToetstoolURL(String url) {
      getProperties().setProperty(TOETS_TOOL_URL, url);
    }

    public static String getCssPath() {
      return getProperties().getProperty(CSS_PATH);
    }

    public static void setCssPath(String path) {
      getProperties().setProperty(CSS_PATH, path);
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
