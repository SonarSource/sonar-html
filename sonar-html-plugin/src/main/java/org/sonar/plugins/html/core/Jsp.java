/*
 * SonarQube HTML
 * Copyright (C) 2010-2024 SonarSource SA
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
package org.sonar.plugins.html.core;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;
import org.sonar.plugins.html.api.HtmlConstants;

public class Jsp extends AbstractLanguage {

  private Configuration configuration;

  public Jsp(Configuration configuration) {
    super(HtmlConstants.JSP_LANGUAGE_KEY, HtmlConstants.JSP_LANGUAGE_NAME);
    this.configuration = configuration;
  }

  @Override
  public String[] getFileSuffixes() {
    return configuration.getStringArray(HtmlConstants.JSP_FILE_EXTENSIONS_PROP_KEY);
  }

}
