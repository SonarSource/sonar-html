/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot and SonarSource
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

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.AbstractLanguage;
import org.sonar.plugins.web.api.WebConstants;

import java.util.ArrayList;

@Properties({
  @Property(key = WebConstants.FILE_EXTENSIONS_PROP_KEY,
    name = "File extensions",
    description = "List of file extensions that will be scanned.",
    defaultValue = WebConstants.FILE_EXTENSIONS_DEF_VALUE,
    global = true,
    project = true)
})
public class Web extends AbstractLanguage {

  private final String[] fileSuffixes;

  /**
   * Default constructor.
   */
  public Web(Settings settings) {
    super(WebConstants.LANGUAGE_KEY, WebConstants.LANGUAGE_NAME);
    String extensions = settings.getString(WebConstants.FILE_EXTENSIONS_PROP_KEY);
    if (StringUtils.isBlank(extensions)) {
      extensions = WebConstants.FILE_EXTENSIONS_DEF_VALUE;
    }
    ArrayList<String> extensionsList = Lists.newArrayList();
    for (String extension : StringUtils.split(extensions, ",")) {
      extensionsList.add(extension.trim());
    }
    fileSuffixes = extensionsList.toArray(new String[extensionsList.size()]);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] getFileSuffixes() {
    return fileSuffixes;
  }
}
