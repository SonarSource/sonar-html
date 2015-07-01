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

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.web.api.WebConstants;
import org.sonar.squidbridge.annotations.AnnotationBasedRulesDefinition;

public final class WebRulesDefinition implements RulesDefinition {

  public static final String REPOSITORY_KEY = WebConstants.LANGUAGE_NAME;
  public static final String REPOSITORY_NAME = "SonarQube";

  @Override
  public void define(Context context) {
    NewRepository repository = context
      .createRepository(REPOSITORY_KEY, WebConstants.LANGUAGE_KEY)
      .setName(REPOSITORY_NAME);

    new AnnotationBasedRulesDefinition(repository, WebConstants.LANGUAGE_KEY).addRuleClasses(false, CheckClasses.getCheckClasses());

    repository.done();
  }

}
