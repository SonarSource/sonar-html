/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.rules;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonarsource.analyzer.commons.BuiltInQualityProfileJsonLoader;

import static org.sonar.plugins.html.rules.HtmlRulesDefinition.REPOSITORY_KEY;
import static org.sonar.plugins.html.rules.HtmlRulesDefinition.RESOURCE_BASE_PATH;

/**
 * Sonar way profile for the HTML language
 */
public final class SonarWayProfile implements BuiltInQualityProfilesDefinition {

  private static final String NAME = "Sonar way";
  public static final String JSON_PROFILE_PATH = RESOURCE_BASE_PATH + "/Sonar_way_profile.json";

  @Override
  public void define(Context context) {
    NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(NAME, HtmlConstants.LANGUAGE_KEY);
    BuiltInQualityProfileJsonLoader.load(profile, REPOSITORY_KEY, JSON_PROFILE_PATH);
    profile.done();
  }

}
