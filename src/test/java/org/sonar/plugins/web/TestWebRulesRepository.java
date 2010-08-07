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

package org.sonar.plugins.web;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.rules.ProfileXmlParser;

/**
 * @author Matthijs Galesloot
 */
public class TestWebRulesRepository {

  @Test
  public void testWebRulesRepository() {
    WebRulesRepository rulesRepository = new WebRulesRepository(Web.INSTANCE);

    assertTrue(rulesRepository.getInitialReferential().size() > 1);

    RulesProfile rulesProfile = rulesRepository.getProvidedProfiles().get(0);

    assertTrue(rulesProfile.getActiveRules().size() > 3);

    int params = 0;
    for (ActiveRule activeRule : rulesProfile.getActiveRules()) {
      params += activeRule.getActiveRuleParams().size();
    }

    assertTrue(params > 2);
  }

  @Test
  public void testExportImport() {
    String myProfileName = "My Profile";
    WebRulesRepository rulesRepository = new WebRulesRepository(Web.INSTANCE);

    RulesProfile rulesProfile = rulesRepository.getProvidedProfiles().get(0);
    rulesProfile.setName(myProfileName);

    // export the rules to xml
    String xmlProfile = new ProfileXmlParser().exportConfiguration(rulesProfile);
    assertNotNull(xmlProfile);

    // parse back the xml to rules
    List<ActiveRule> activeRules = rulesRepository.importConfiguration(xmlProfile, rulesRepository.parseReferential(null));

    assertNotSame(activeRules, rulesProfile.getActiveRules());
    assertEquals(activeRules.size(), rulesProfile.getActiveRules().size());
  }

}
