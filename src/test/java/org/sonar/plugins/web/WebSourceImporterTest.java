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
import org.sonar.api.resources.Project;

/**
 * @author Matthijs Galesloot
 */
public class WebSourceImporterTest extends AbstractWebPluginTester {

  @Test
  public void testImporter() throws Exception {

    final Project project = loadProjectFromPom();

    WebSourceImporter importer = new WebSourceImporter(project);

    assertTrue("Importer only supports web projects", importer.shouldExecuteOnProject(project));
    MockSensorContext sensorContext = new MockSensorContext();
    importer.analyse(project, sensorContext);
    assertTrue(sensorContext.getNumResources() > 0);
  }
}
