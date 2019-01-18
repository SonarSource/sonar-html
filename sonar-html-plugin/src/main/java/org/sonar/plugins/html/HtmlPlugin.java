/*
 * SonarHTML :: SonarQube Plugin
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
package org.sonar.plugins.html;

import com.google.common.collect.ImmutableList;
import org.sonar.api.Plugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.core.Html;
import org.sonar.plugins.html.core.HtmlSensor;
import org.sonar.plugins.html.core.Jsp;
import org.sonar.plugins.html.rules.JspQualityProfile;
import org.sonar.plugins.html.rules.SonarWayProfile;
import org.sonar.plugins.html.rules.HtmlRulesDefinition;

/**
 * HTML Plugin publishes extensions to sonar engine.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class HtmlPlugin implements Plugin {

  private static final String CATEGORY = "HTML";

  @Override
  public void define(Context context) {
    context.addExtensions(
      // web language
      Html.class,
      Jsp.class,

      // web rules repository
      HtmlRulesDefinition.class,

      // profiles
      SonarWayProfile.class,
      JspQualityProfile.class,

      // web sensor
      HtmlSensor.class
    );

    context.addExtensions(pluginProperties());
  }

  private static ImmutableList<PropertyDefinition> pluginProperties() {
    return ImmutableList.of(

      PropertyDefinition.builder(HtmlConstants.FILE_EXTENSIONS_PROP_KEY)
        .name("HTML File suffixes")
        .description("List of file suffixes that will be scanned.")
        .category(CATEGORY)
        .defaultValue(HtmlConstants.FILE_EXTENSIONS_DEF_VALUE)
        .deprecatedKey("sonar.web.file.suffixes")
        .onQualifiers(Qualifiers.PROJECT)
        .multiValues(true)
        .build(),
      PropertyDefinition.builder(HtmlConstants.JSP_FILE_EXTENSIONS_PROP_KEY)
        .name("JSP File suffixes")
        .description("List of JSP file suffixes that will be scanned.")
        .category(CATEGORY)
        .defaultValue(HtmlConstants.JSP_FILE_EXTENSIONS_DEF_VALUE)
        .onQualifiers(Qualifiers.PROJECT)
        .multiValues(true)
        .build()
    );
  }
}
