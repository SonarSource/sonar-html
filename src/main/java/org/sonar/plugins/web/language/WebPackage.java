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

package org.sonar.plugins.web.language;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.WildcardPattern;

/**
 * @author Matthijs Galesloot
 */
public class WebPackage extends Resource {

  public static final String DEFAULT_PACKAGE_NAME = "[default]";

  public WebPackage(String key) {
    super();
    setKey(StringUtils.defaultIfEmpty(StringUtils.trim(key), DEFAULT_PACKAGE_NAME));
  }

  public boolean isDefault() {
    return StringUtils.equals(getKey(), DEFAULT_PACKAGE_NAME);
  }

  @Override
  public boolean matchFilePattern(String antPattern) {
    String patternWithoutFileSuffix = StringUtils.substringBeforeLast(antPattern, ".");
    WildcardPattern matcher = WildcardPattern.create(patternWithoutFileSuffix, ".");
    return matcher.match(getKey());
  }

  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public String getScope() {
    return Resource.SCOPE_SPACE;
  }

  @Override
  public String getQualifier() {
    return Resource.QUALIFIER_PACKAGE;
  }

  @Override
  public String getName() {
    return getKey();
  }

  @Override
  public Resource<?> getParent() {
    return null;
  }

  @Override
  public String getLongName() {
    return null;
  }

  @Override
  public Language getLanguage() {
    return Web.INSTANCE;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("key", getKey()).toString();
  }
}
