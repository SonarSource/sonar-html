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

import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.plugins.web.language.Web;

/**
 * @author Matthijs Galesloot
 */
public class WebSensorTest extends AbstractWebPluginTester {

  @Test
  public void testSensor() throws Exception {
    WebRulesRepository webRulesRepository = new WebRulesRepository(Web.INSTANCE);

    RulesProfile rulesProfile = webRulesRepository.getProvidedProfiles().get(0);

    WebSensor sensor = new WebSensor(rulesProfile);

    final Project project = loadProjectFromPom();
    MockSensorContext sensorContext = new MockSensorContext();
    sensor.analyse(project, sensorContext);

    assertTrue("Should have found 1 violation", sensorContext.getViolations().size() > 0);
  }
}
