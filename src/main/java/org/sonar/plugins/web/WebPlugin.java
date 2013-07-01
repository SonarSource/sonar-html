/*
 * Sonar Web Plugin
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
package org.sonar.plugins.web;

import org.sonar.api.Extension;
import org.sonar.api.SonarPlugin;
import org.sonar.plugins.web.core.Web;
import org.sonar.plugins.web.core.WebCodeColorizerFormat;
import org.sonar.plugins.web.core.WebSensor;
import org.sonar.plugins.web.core.WebSourceImporter;
import org.sonar.plugins.web.duplications.WebCpdMapping;
import org.sonar.plugins.web.rules.SonarWayProfile;
import org.sonar.plugins.web.rules.WebRulesRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Web Plugin publishes extensions to sonar engine.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class WebPlugin extends SonarPlugin {

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    // web language
    list.add(Web.class);
    // web files importer
    list.add(WebSourceImporter.class);

    // web rules repository
    list.add(WebRulesRepository.class);

    // profiles
    list.add(SonarWayProfile.class);

    // web sensor
    list.add(WebSensor.class);

    // Code Colorizer
    list.add(WebCodeColorizerFormat.class);
    // Copy/Paste detection mechanism
    list.add(WebCpdMapping.class);

    return list;
  }

}
