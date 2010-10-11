/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.rules.markup;

import java.io.Writer;

import org.apache.commons.configuration.Configuration;
import org.sonar.api.profiles.ProfileExporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileExporter;
import org.sonar.plugins.web.language.Web;

public class MarkupProfileExporter extends ProfileExporter {

  public MarkupProfileExporter(Configuration conf) {
    super(MarkupRuleRepository.REPOSITORY_KEY, MarkupRuleRepository.REPOSITORY_NAME);
    setSupportedLanguages(Web.KEY);
    setMimeType("application/xml");
  }

  @Override
  public void exportProfile(RulesProfile profile, Writer writer) {

    new XMLProfileExporter().exportProfile(profile, writer);
  }
}
