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
package org.sonar.plugins.web.checks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RuleTags {

  String ACCESSIBILITY = "accessibility";
  String BRAIN_OVERLOADED = "brain-overloaded";
  String BUG = "bug";
  String CONVENTION = "convention";
  String CROSS_BROWSER = "cross-browser";
  String HTML5 = "html5";
  String JSP_JSF = "jsp-jsf";
  String OBSOLETE = "obsolete";
  String PITFALL = "pitfall";
  String PSR2 = "psr2";
  String SECURITY = "security";
  String UNUSED = "unused";
  String USER_EXPERIENCE = "user-experience";

  String[] value();

}
