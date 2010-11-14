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

import org.apache.commons.lang.CharEncoding;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;

public final class JSPProfile extends ProfileDefinition {

  private static final String JSP_RULES = "org/sonar/plugins/web/rules/web/jsp-rules.xml";

  private final RuleFinder ruleFinder;

  public JSPProfile(RuleFinder ruleFinder) {
    this.ruleFinder = ruleFinder;
  }

  @Override
  public RulesProfile createProfile(ValidationMessages validationMessages) {
    XMLProfileParser parser = new XMLProfileParser(ruleFinder);
    Reader reader = new InputStreamReader(DefaultWebProfile.class.getClassLoader().getResourceAsStream(JSP_RULES),
        Charset.forName(CharEncoding.UTF_8));
    return parser.parse(reader, validationMessages);
  }
}
