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

package org.sonar.plugins.web;

import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.web.rules.DefaultWebProfile;

/**
 * @author Matthijs Galesloot
 */
public class WebSensorTest extends AbstractWebPluginTester {

  @Test
  public void testSensor() throws Exception {
    ProfileDefinition profileDefinition = new DefaultWebProfile(newRuleFinder());
    RulesProfile profile = profileDefinition.createProfile(ValidationMessages.create());
    WebSensor sensor = new WebSensor(profile);

    final Project project = loadProjectFromPom();
    MockSensorContext sensorContext = new MockSensorContext();
    sensor.analyse(project, sensorContext);

    assertTrue("Should have found 1 violation", sensorContext.getViolations().size() > 0);
  }
}
