/*
 * Copyright (C) 2010
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

  private String filename;
  private String longName;
  private String packageKey;
  private WebPackage parent = null;

  public WebFile(String key) {
    super();

    String realKey = StringUtils.trim(key);

    if (realKey.contains(".")) {
      this.filename = StringUtils.substringAfterLast(realKey, ".");
      this.packageKey = StringUtils.substringBeforeLast(realKey, ".");
      this.longName = realKey;

    } else {
      this.filename = realKey;
      this.longName = realKey;
      this.packageKey = WebPackage.DEFAULT_PACKAGE_NAME;
      realKey = new StringBuilder().append(WebPackage.DEFAULT_PACKAGE_NAME).append(".").append(realKey).toString();
    }
    setKey(realKey);
  }

  public WebFile(String packageKey, String className) {
    super();

    this.filename = className.trim();
    final String key;
    if (StringUtils.isBlank(packageKey)) {
      this.packageKey = WebPackage.DEFAULT_PACKAGE_NAME;
      this.longName = this.filename;
      key = new StringBuilder().append(this.packageKey).append(".").append(this.filename).toString();
    } else {
      this.packageKey = packageKey.trim();
      key = new StringBuilder().append(this.packageKey).append(".").append(this.filename).toString();
      this.longName = key;
    }
    setKey(key);
  }

  @Override
  public WebPackage getParent() {
    if (parent == null) {
      parent = new WebPackage(packageKey);
    }
    return parent;
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
  public String getName() {
    return filename;
  }

  @Override
  public String getLongName() {
    return longName;
  }

  @Override
  public String getScope() {
    return Resource.SCOPE_ENTITY;
  }

  @Override
  public String getQualifier() {
    return Resource.QUALIFIER_FILE;
  }

  @Override
  public boolean matchFilePattern(String antPattern) {
    String patternWithoutFileSuffix = StringUtils.substringBeforeLast(antPattern, ".");
    WildcardPattern matcher = WildcardPattern.create(patternWithoutFileSuffix, ".");
    return matcher.match(getKey());
  }

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
        pacname = StringUtils.replace(pacname, "/", ".");
        classname = StringUtils.substringAfterLast(relativePath, "/");
      }
      // classname = StringUtils.substringBeforeLast(classname, ".");
      return new WebFile(pacname, classname);
    }
    return null;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("key", getKey()).append("package", packageKey).append("longName", longName).toString();
  }
}
