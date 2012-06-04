/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * Default Struts profile with OGNL Expression Check.
 *
 * @author Matthijs Galesloot
 * @since 1.1
 */
public final class StrutsProfile extends ProfileDefinition {

  public static final String ALL_RULES = "org/sonar/plugins/web/rules/web/rules.xml";

  private final XMLProfileParser profileParser;

  private final RuleFinder rulefinder;

  public StrutsProfile(XMLProfileParser profileParser, RuleFinder rulefinder) {
    this.profileParser = profileParser;
    this.rulefinder = rulefinder;
  }

  /**
   * Create a profile as a copy of the default web profile but add the OGNL Expression check.
   */
  @Override
  public RulesProfile createProfile(ValidationMessages validation) {
    Reader reader = new InputStreamReader(StrutsProfile.class.getClassLoader().getResourceAsStream(ALL_RULES),
        Charset.forName(CharEncoding.UTF_8));
    try {
      RulesProfile profile = profileParser.parse(reader, validation);
      profile.setName("Default Struts Profile");

      // add OGNLExpressionCheck
      profile.activateRule(rulefinder.findByKey(WebRulesRepository.REPOSITORY_KEY, "OGNLExpressionCheck"), null);

      // remove UnifiedExpressionCheck
      for (ActiveRule activeRule : profile.getActiveRules()) {
        if (StringUtils.equalsIgnoreCase("UnifiedExpressionCheck", activeRule.getConfigKey())) {
          profile.removeActiveRule(activeRule);
          break;
        }
      }
      profile.setDefaultProfile(false);
      return profile;
    } finally {
      IOUtils.closeQuietly(reader);
    }
  }
}
