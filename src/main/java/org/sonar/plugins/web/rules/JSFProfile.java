/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharEncoding;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.utils.ValidationMessages;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Profile with checks applicable for JSF.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class JSFProfile extends ProfileDefinition {

  private final String[] notSupportedRules = new String[] {
    "AvoidHtmlCommentCheck",
    "DynamicJspIncludeCheck",
    "IllegalTagLibsCheck",
    "MultiplePageDirectivesCheck"
  };

  private final XMLProfileParser profileParser;

  public JSFProfile(XMLProfileParser profileParser) {
    this.profileParser = profileParser;
  }

  @Override
  public RulesProfile createProfile(ValidationMessages validationMessages) {
    Reader reader = new InputStreamReader(DefaultWebProfile.class.getClassLoader().getResourceAsStream(DefaultWebProfile.ALL_RULES),
        Charset.forName(CharEncoding.UTF_8));
    RulesProfile profile = profileParser.parse(reader, validationMessages);
    profile.setName("JSF Profile");

    // find rules not applicable for JSF
    List<ActiveRule> removeRules = new ArrayList<ActiveRule>();
    for (ActiveRule activeRule : profile.getActiveRules()) {
      if (ArrayUtils.contains(notSupportedRules, activeRule.getConfigKey())) {
        removeRules.add(activeRule);
      }
    }

    // remove not applicable rules
    profile.getActiveRules().removeAll(removeRules);
    return profile;
  }
}
