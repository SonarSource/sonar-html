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

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.plugins.web.duplications.WebCpdMapping;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.rules.markup.DefaultMarkupProfile;
import org.sonar.plugins.web.rules.markup.MarkupProfileExporter;
import org.sonar.plugins.web.rules.markup.MarkupProfileImporter;
import org.sonar.plugins.web.rules.markup.MarkupRuleRepository;
import org.sonar.plugins.web.rules.web.DefaultWebProfile;
import org.sonar.plugins.web.rules.web.JSFProfile;
import org.sonar.plugins.web.rules.web.JSPProfile;
import org.sonar.plugins.web.rules.web.WebProfileExporter;
import org.sonar.plugins.web.rules.web.WebProfileImporter;
import org.sonar.plugins.web.rules.web.WebRulesRepository;

/**
 * @author Matthijs Galesloot
 */
@Properties({
  @Property(key = "sonar.cpd.web.minimumTokens", defaultValue = "70",
    name = "Minimum tokens",
    description = "The number of duplicate tokens above which a HTML block is considered as a duplicated.",
    global = true, project = true)})
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

    // web rules
    list.add(WebRulesRepository.class);
    list.add(WebProfileImporter.class);
    list.add(WebProfileExporter.class);
    list.add(DefaultWebProfile.class);
    list.add(JSFProfile.class);
    list.add(JSPProfile.class);

    // markup rules
    list.add(MarkupRuleRepository.class);
    list.add(MarkupProfileExporter.class);
    list.add(MarkupProfileImporter.class);
    list.add(DefaultMarkupProfile.class);

    // sensor
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
