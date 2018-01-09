/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
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
import org.sonar.api.Plugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.plugins.web.api.WebConstants;
import org.sonar.plugins.web.core.Web;
import org.sonar.plugins.web.core.WebSensor;
import org.sonar.plugins.web.rules.SonarWayProfile;
import org.sonar.plugins.web.rules.WebRulesDefinition;

/**
 * Web Plugin publishes extensions to sonar engine.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class WebPlugin implements Plugin {

  private static final String CATEGORY = "Web";

  @Override
  public void define(Context context) {
    context.addExtensions(
      // web language
      Web.class,

      // web rules repository
      WebRulesDefinition.class,

      // profiles
      SonarWayProfile.class,

      // web sensor
      WebSensor.class
    );

    context.addExtensions(pluginProperties());
  }

  private static ImmutableList<PropertyDefinition> pluginProperties() {
    return ImmutableList.of(

      PropertyDefinition.builder(WebConstants.FILE_EXTENSIONS_PROP_KEY)
        .name("File suffixes")
        .description("List of file suffixes that will be scanned.")
        .category(CATEGORY)
        .defaultValue(WebConstants.FILE_EXTENSIONS_DEF_VALUE)
        .onQualifiers(Qualifiers.PROJECT)
        .build(),

      deprecatedPropertyDefinition(WebConstants.OLD_FILE_EXTENSIONS_PROP_KEY)
    );
  }

  private static PropertyDefinition deprecatedPropertyDefinition(String oldKey) {
    return PropertyDefinition
      .builder(oldKey)
      .name(oldKey)
      .description("This property is deprecated and will be removed in a future version.<br />"
        + "You should stop using it as soon as possible.<br />"
        + "Consult the migration guide for guidance.")
      .category(CATEGORY)
      .subCategory("Deprecated")
      .onQualifiers(Qualifiers.PROJECT)
      .build();
  }
}
