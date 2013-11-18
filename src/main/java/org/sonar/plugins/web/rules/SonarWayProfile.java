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
package org.sonar.plugins.web.rules;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.utils.ValidationMessages;

/**
 * Sonar way profile for the Web language
 */
public final class SonarWayProfile extends ProfileDefinition {

  private static final String SONAR_WAY_PROFILE_FILE = "org/sonar/plugins/web/rules/web/sonar-way-profile.xml";

  private final XMLProfileParser profileParser;

  /**
   * Creates the {@link SonarWayProfile}
   */
  public SonarWayProfile(XMLProfileParser profileParser) {
    this.profileParser = profileParser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RulesProfile createProfile(ValidationMessages validation) {
    RulesProfile profile = profileParser.parseResource(getClass().getClassLoader(), SONAR_WAY_PROFILE_FILE, validation);
    profile.setDefaultProfile(true);
    return profile;
  }
}
