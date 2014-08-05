/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
 * dev@sonar.codehaus.org
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
package org.sonar.plugins.web.rules;

import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.plugins.web.api.WebConstants;
import org.sonar.plugins.web.checks.WebRule;

/**
 * Sonar way profile for the Web language
 */
public final class SonarWayProfile extends BaseProfileDefinition {

  public SonarWayProfile(RuleFinder ruleFinder) {
    super(ruleFinder);
  }

  @Override
  protected boolean isActive(Class ruleClass) {
    WebRule ruleAnnotation = AnnotationUtils.getAnnotation(ruleClass, WebRule.class);
    return ruleAnnotation != null && ruleAnnotation.activeByDefault();
  }

  @Override
  protected String getLanguageKey() {
    return WebConstants.LANGUAGE_KEY;
  }
}
