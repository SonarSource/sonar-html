/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.web.rules;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;

public final class DefaultWebProfile extends ProfileDefinition {

  private static final String ALL_RULES = "org/sonar/plugins/web/rules/web/rules.xml";

  private final RuleFinder ruleFinder;

  public DefaultWebProfile(RuleFinder ruleFinder) {
    this.ruleFinder = ruleFinder;
  }

  @Override
  public RulesProfile createProfile(ValidationMessages validation) {
    Reader reader = new InputStreamReader(DefaultWebProfile.class.getClassLoader().getResourceAsStream(ALL_RULES),
        Charset.forName(CharEncoding.UTF_8));
    try {
      XMLProfileParser parser = new XMLProfileParser(ruleFinder);
      RulesProfile profile = parser.parse(reader, validation);
      profile.setDefaultProfile(true);
      return profile;
    } finally {
      IOUtils.closeQuietly(reader);
    }
  }
}
