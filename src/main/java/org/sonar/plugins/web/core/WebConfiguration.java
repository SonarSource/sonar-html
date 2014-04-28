/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
 * dev@sonar.codehaus.org
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
package org.sonar.plugins.web.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.plugins.web.api.WebConstants;

public class WebConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(WebConfiguration.class);

  private final Settings settings;

  public WebConfiguration(Settings settings) {
    this.settings = settings;
  }

  public String[] fileSuffixes() {
    String[] result;

    String oldFileExtensions = settings.getString(WebConstants.OLD_FILE_EXTENSIONS_PROP_KEY);
    if (oldFileExtensions != null) {
      logDeprecatedPropertyUsage(WebConstants.FILE_EXTENSIONS_PROP_KEY, WebConstants.OLD_FILE_EXTENSIONS_PROP_KEY);
      result = settings.getStringArray(WebConstants.OLD_FILE_EXTENSIONS_PROP_KEY);
    } else {
      result = settings.getStringArray(WebConstants.FILE_EXTENSIONS_PROP_KEY);
    }
    return result;
  }

  private static void logDeprecatedPropertyUsage(String newPropertyKey, String oldProperty) {
    LOG.warn("Use the new property \"" + newPropertyKey + "\" instead of the deprecated \"" + oldProperty + "\"");
  }

}
