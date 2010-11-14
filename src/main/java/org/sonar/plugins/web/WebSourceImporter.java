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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.AbstractSourceImporter;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.language.WebFile;

/**
 * @author Matthijs Galesloot
 */
public final class WebSourceImporter extends AbstractSourceImporter {

  private static final Logger LOG = LoggerFactory.getLogger(WebSourceImporter.class);

  public WebSourceImporter(Project project) {
    super(new Web(project));
  }

  @Override
  public void analyse(Project project, SensorContext context) {
    ProjectConfiguration.configureSourceDir(project);

    super.analyse(project, context);
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return isEnabled(project) && Web.KEY.equals(project.getLanguage().getKey());
  }

  @Override
  protected Resource<?> createResource(File file, List<File> sourceDirs, boolean unitTest) {
    LOG.debug("WebSourceImporter:" + file.getPath());
    return WebFile.fromIOFile(file, sourceDirs);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}