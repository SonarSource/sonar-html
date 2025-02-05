/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html;

import java.util.List;
import org.sonar.api.Plugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.core.Html;
import org.sonar.plugins.html.core.HtmlSensor;
import org.sonar.plugins.html.core.Jsp;
import org.sonar.plugins.html.rules.HtmlRulesDefinition;
import org.sonar.plugins.html.rules.JspQualityProfile;
import org.sonar.plugins.html.rules.SonarWayProfile;

/**
 * HTML Plugin publishes extensions to sonar engine.
 *

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

  private static List<PropertyDefinition> pluginProperties() {
    return List.of(
      PropertyDefinition.builder(HtmlConstants.FILE_EXTENSIONS_PROP_KEY)
        .name("HTML File suffixes")
        .description("List of file suffixes that will be scanned.")
        .category(CATEGORY)
        .defaultValue(HtmlConstants.FILE_EXTENSIONS_DEF_VALUE)
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
