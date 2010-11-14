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

package org.sonar.plugins.web.language;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.WildcardPattern;

/**
 * @author Matthijs Galesloot
 */
public class WebFile extends Resource<WebPackage> {

  public static WebFile fromIOFile(File file, List<File> sourceDirs) {
    if (file == null) {
      return null;
    }
    String relativePath = DefaultProjectFileSystem.getRelativePath(file, sourceDirs);
    if (relativePath != null) {
      String pacname = null;
      String classname = relativePath;

      if (relativePath.indexOf('/') >= 0) {
        pacname = StringUtils.substringBeforeLast(relativePath, "/");
        classname = StringUtils.substringAfterLast(relativePath, "/");
      }
      return new WebFile(pacname, classname);
    }
    return null;
  }

  private final String filename;
  private final String longName;
  private final String packageKey;
  private WebPackage parent;

  public WebFile(String packageKey, String fileName) {
    super();

    this.filename = fileName.trim();
    final String key;
    if (StringUtils.isBlank(packageKey)) {
      this.packageKey = WebPackage.DEFAULT_PACKAGE_NAME;
      this.longName = this.filename;
      key = new StringBuilder().append(this.packageKey).append("/").append(this.filename).toString();
    } else {
      this.packageKey = packageKey.trim();
      key = new StringBuilder().append(this.packageKey).append("/").append(this.filename).toString();
      this.longName = key;
    }

    setKey(key);
  }

  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public Language getLanguage() {
    return Web.INSTANCE;
  }

  @Override
  public String getLongName() {
    return longName;
  }

  @Override
  public String getName() {
    return filename;
  }

  @Override
  public WebPackage getParent() {
    if (parent == null) {
      parent = new WebPackage(packageKey);
    }
    return parent;
  }

  @Override
  public String getQualifier() {
    return Resource.QUALIFIER_FILE;
  }

  @Override
  public String getScope() {
    return Resource.SCOPE_ENTITY;
  }

  @Override
  public boolean matchFilePattern(String antPattern) {
    String patternWithoutFileSuffix = StringUtils.substringBeforeLast(antPattern, ".");
    WildcardPattern matcher = WildcardPattern.create(patternWithoutFileSuffix, ".");
    return matcher.match(getKey());
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("key", getKey()).append("package", packageKey).append("longName", longName).toString();
  }
}
