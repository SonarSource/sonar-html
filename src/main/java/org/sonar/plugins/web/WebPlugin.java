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
import org.sonar.plugins.web.dashboard.WebDashboardWidget;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.language.WebDirectoryDecorator;
import org.sonar.plugins.web.language.WebFilesDecorator;
import org.sonar.plugins.web.language.WebMetricsDecorator;
import org.sonar.plugins.web.rules.WebRulesRepository;

/**
 * @author Matthijs Galesloot
 */
public class WebPlugin implements Plugin {

  public static String KEY = "sonar-web-plugin";

  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "Web plugin";
  }

  public String getDescription() {
    return getName() + " collects metrics on web code, such as lines of code, violations, documentation level...";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    list.add(Web.class);
    list.add(WebRulesRepository.class);
    list.add(WebSourceImporter.class);
    list.add(WebMetrics.class);
    // sensor
    list.add(WebSensor.class);
    // decorators
    list.add(WebFilesDecorator.class);
    list.add(WebDirectoryDecorator.class);
    list.add(WebMetricsDecorator.class);
    // portal
    list.add(WebDashboardWidget.class);

    return list;
  }

  @Override
  public String toString() {
    return getKey();
  }
}
