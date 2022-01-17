/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
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
