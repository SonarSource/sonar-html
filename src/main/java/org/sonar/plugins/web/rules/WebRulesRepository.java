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

import org.sonar.api.rules.AnnotationRuleParser;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.plugins.web.api.WebConstants;

import java.util.List;

public final class WebRulesRepository extends RuleRepository {

  public static final String REPOSITORY_NAME = "SonarQube";
  public static final String REPOSITORY_KEY = "Web";
  private final AnnotationRuleParser annotationRuleParser;

  public WebRulesRepository(AnnotationRuleParser annotationRuleParser) {
    super(REPOSITORY_KEY, WebConstants.LANGUAGE_KEY);
    setName(REPOSITORY_NAME);

    this.annotationRuleParser = annotationRuleParser;
  }

  @Override
  public List<Rule> createRules() {
    return annotationRuleParser.parse(REPOSITORY_KEY, CheckClasses.getCheckClasses());
  }

}
