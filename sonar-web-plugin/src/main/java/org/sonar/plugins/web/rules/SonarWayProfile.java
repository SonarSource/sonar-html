/*
 * SonarWeb :: SonarQube Plugin
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
package org.sonar.plugins.web.rules;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleAnnotationUtils;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.web.api.WebConstants;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

/**
 * Sonar way profile for the Web language
 */
public final class SonarWayProfile extends ProfileDefinition {

  private static final String NAME = "Sonar way";

  private final RuleFinder ruleFinder;

  public SonarWayProfile(RuleFinder ruleFinder) {
    this.ruleFinder = ruleFinder;
  }

  @Override
  public RulesProfile createProfile(ValidationMessages messages) {
    RulesProfile profile = RulesProfile.create(NAME, WebConstants.LANGUAGE_KEY);
    Set<String> activeKeys = loadActiveKeysFromJsonProfile();
    for (Class ruleClass : CheckClasses.getCheckClasses()) {
      String ruleKey = RuleAnnotationUtils.getRuleKey(ruleClass);
      if (activeKeys.contains(ruleKey)) {
        Rule rule = ruleFinder.findByKey(WebRulesDefinition.REPOSITORY_KEY, ruleKey);
        profile.activateRule(rule, null);
      }
    }
    return profile;
  }

  public static Set<String> loadActiveKeysFromJsonProfile() {
    URL profileUrl = SonarWayProfile.class.getResource("/org/sonar/l10n/web/rules/Web/Sonar_way_profile.json");
    try {
      Gson gson = new Gson();
      Profile profile = gson.fromJson(Resources.toString(profileUrl, Charsets.UTF_8), Profile.class);
      return profile.ruleKeys;
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read: " + profileUrl, e);
    }
  }

  private static class Profile {
    String name;
    Set<String> ruleKeys;
  }

}
