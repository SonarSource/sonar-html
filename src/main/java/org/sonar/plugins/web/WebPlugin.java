/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.plugins.web.duplications.WebCpdMapping;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.rules.DefaultWebProfile;
import org.sonar.plugins.web.rules.JSFProfile;
import org.sonar.plugins.web.rules.WebProfileExporter;
import org.sonar.plugins.web.rules.WebProfileImporter;
import org.sonar.plugins.web.rules.WebRulesRepository;

/**
 * Web Plugin publishes extensions to sonar engine.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Properties({
@Property(key = ProjectConfiguration.CPD_MINIMUM_TOKENS, defaultValue = "70",
    name = "Minimum tokens",
    description = "The number of duplicate tokens above which a HTML block is considered as a duplicated.",
    global = true, project = true),
@Property(key = ProjectConfiguration.FILE_EXTENSIONS,
    name = "File extensions",
    description = "List of file extensions that will be scanned.",
    defaultValue="xhtml,jspf,jsp",
    global = true, project = true),
@Property(key = ProjectConfiguration.SOURCE_DIRECTORY,
        name = "Source directory",
        description = "Source directory that will be scanned.",
        defaultValue="src/main/webapp",
        global = false, project = true)})
public final class WebPlugin implements Plugin {

  private static final String KEY = "sonar-web-plugin";

  public static String getKEY() {
    return KEY;
  }

  public String getDescription() {
    return getName() + " collects metrics on web code, such as lines of code, violations, documentation level...";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    // web language
    list.add(Web.class);
    // web files importer
    list.add(WebSourceImporter.class);

    // web rules repository
    list.add(WebRulesRepository.class);
    list.add(WebProfileImporter.class);
    list.add(WebProfileExporter.class);

    // profiles
    list.add(DefaultWebProfile.class);
    list.add(JSFProfile.class);

    // web sensor
    list.add(WebSensor.class);

    // Code Colorizer
    list.add(WebCodeColorizerFormat.class);
    // Copy/Paste detection mechanism
    list.add(WebCpdMapping.class);

    return list;
  }

  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "Web plugin";
  }

  @Override
  public String toString() {
    return getKey();
  }
}
