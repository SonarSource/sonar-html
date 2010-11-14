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

import java.io.Reader;

import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.web.language.Web;

public class WebProfileImporter extends ProfileImporter {

  private final RuleFinder ruleFinder;

  public WebProfileImporter(RuleFinder ruleFinder) {
    super(WebRulesRepository.REPOSITORY_KEY, WebRulesRepository.REPOSITORY_NAME);
    setSupportedLanguages(Web.KEY);
    this.ruleFinder = ruleFinder;
  }

  @Override
  public RulesProfile importProfile(Reader reader, ValidationMessages messages) {

    XMLProfileParser profileParser = new XMLProfileParser(ruleFinder);
    return profileParser.parse(reader, messages);
  }
}
