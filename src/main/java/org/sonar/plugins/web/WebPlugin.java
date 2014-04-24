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
package org.sonar.plugins.web;

import com.google.common.collect.ImmutableList;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.plugins.web.api.WebConstants;
import org.sonar.plugins.web.core.Web;
import org.sonar.plugins.web.core.WebCodeColorizerFormat;
import org.sonar.plugins.web.core.WebSensor;
import org.sonar.plugins.web.core.WebSourceImporter;
import org.sonar.plugins.web.duplications.WebCpdMapping;
import org.sonar.plugins.web.rules.SonarWayProfile;
import org.sonar.plugins.web.rules.WebRulesRepository;

import java.util.List;

/**
 * Web Plugin publishes extensions to sonar engine.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class WebPlugin extends SonarPlugin {

  public List getExtensions() {
    ImmutableList.Builder builder = ImmutableList.builder();

    // web language
    builder.add(Web.class);
    // web files importer
    builder.add(WebSourceImporter.class);

    // web rules repository
    builder.add(WebRulesRepository.class);

    // profiles
    builder.add(SonarWayProfile.class);

    // web sensor
    builder.add(WebSensor.class);

    // Code Colorizer
    builder.add(WebCodeColorizerFormat.class);
    // Copy/Paste detection mechanism
    builder.add(WebCpdMapping.class);

    builder.addAll(pluginProperties());

    return builder.build();
  }

  private static ImmutableList<PropertyDefinition> pluginProperties() {
    return ImmutableList.of(

      PropertyDefinition.builder(WebConstants.FILE_EXTENSIONS_PROP_KEY)
        .name("File extensions")
        .description("List of file extensions that will be scanned.")
        .deprecatedKey(WebConstants.OLD_FILE_EXTENSIONS_PROP_KEY)
        .defaultValue(WebConstants.FILE_EXTENSIONS_DEF_VALUE)
        .onQualifiers(Qualifiers.PROJECT)
        .build()
    );
  }
}
