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

import java.io.File;

import org.sonar.api.resources.Project;

/**
 * Utilities and constants for the project configuration.
 *
 * @author Matthijs Galesloot
 *
 */
public final class ProjectConfiguration {

  public static final String FILE_EXTENSIONS = "sonar.web.fileExtensions";
  public static final String SOURCE_DIRECTORY = "sonar.web.sourceDirectory";
  public static final String CPD_MINIMUM_TOKENS = "sonar.cpd.web.minimumTokens";

  private ProjectConfiguration() {
    // cannot instantiate
  }

  public static void configureSourceDir(Project project) {
    String sourceDir = getSourceDir(project);
    if (sourceDir != null) {
      File file = new File(project.getFileSystem().getBasedir() + "/" + sourceDir);

      project.getPom().getCompileSourceRoots().clear();
      project.getFileSystem().addSourceDir(file);
    }
  }

  private static String getSourceDir(Project project) {
    return (String) project.getProperty(ProjectConfiguration.SOURCE_DIRECTORY);
  }
}
