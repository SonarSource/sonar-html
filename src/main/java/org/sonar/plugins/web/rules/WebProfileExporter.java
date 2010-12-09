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

import java.io.Writer;

import org.sonar.api.profiles.ProfileExporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileSerializer;
import org.sonar.plugins.web.language.Web;

/**
 * Export a web profile.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public class WebProfileExporter extends ProfileExporter {

  private final XMLProfileSerializer profileSerializer;

  public WebProfileExporter(XMLProfileSerializer profileSerializer) {
    super(WebRulesRepository.REPOSITORY_KEY, WebRulesRepository.REPOSITORY_NAME);
    setSupportedLanguages(Web.KEY);
    setMimeType("application/xml");
    this.profileSerializer = profileSerializer;
  }

  @Override
  public void exportProfile(RulesProfile profile, Writer writer) {
    profileSerializer.write(profile, writer);
  }
}
