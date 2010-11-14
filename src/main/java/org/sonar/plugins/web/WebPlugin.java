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

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.plugins.web.duplications.WebCpdMapping;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.rules.DefaultWebProfile;
import org.sonar.plugins.web.rules.JSFProfile;
import org.sonar.plugins.web.rules.JSPProfile;
import org.sonar.plugins.web.rules.WebProfileExporter;
import org.sonar.plugins.web.rules.WebProfileImporter;
import org.sonar.plugins.web.rules.WebRulesRepository;

/**
 * @author Matthijs Galesloot
 */
@Properties({
@Property(key = ProjectConfiguration.CPD_MINIMUM_TOKENS, defaultValue = "70",
    name = "Minimum tokens",
    description = "The number of duplicate tokens above which a HTML block is considered as a duplicated.",
    global = true, project = true),
@Property(key = ProjectConfiguration.FILE_EXTENSIONS,
    name = "File extensions",
    description = "List of file extensions that will be scanned.",
    defaultValue="xhtml,jspf,jsp",
    global = true, project = true),
@Property(key = ProjectConfiguration.SOURCE_DIRECTORY,
        name = "Source directory",
        description = "Source directory that will be scanned.",
        defaultValue="src/main/webapp",
        global = false, project = true)})
public final class WebPlugin implements Plugin {

  private static final String KEY = "sonar-web-plugin";

  public static String getKEY() {
    return KEY;
  }

  public String getDescription() {
    return getName() + " collects metrics on web code, such as lines of code, violations, documentation level...";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    // web language
    list.add(Web.class);
    // web files importer
    list.add(WebSourceImporter.class);

    // web rules repository
    list.add(WebRulesRepository.class);
    list.add(WebProfileImporter.class);
    list.add(WebProfileExporter.class);

    // profiles
    list.add(DefaultWebProfile.class);
    list.add(JSFProfile.class);
    list.add(JSPProfile.class);

    // web sensor
    list.add(WebSensor.class);

    // Code Colorizer
    list.add(WebCodeColorizerFormat.class);
    // Copy/Paste detection mechanism
    list.add(WebCpdMapping.class);

    return list;
  }

  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "Web plugin";
  }

  @Override
  public String toString() {
    return getKey();
  }
}
