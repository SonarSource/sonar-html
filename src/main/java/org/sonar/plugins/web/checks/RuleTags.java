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

  public static final String ACCESSIBILITY = "accessibility";
  public static final String BRAIN_OVERLOADED = "brain-overloaded";
  public static final String BUG = "bug";
  public static final String CONVENTION = "convention";
  public static final String CROSS_BROWSER = "cross-browser";
  public static final String HTML5 = "html5";
  public static final String JSP_JSF = "jsp-jsf";
  public static final String OBSOLETE = "obsolete";
  public static final String PSR2 = "psr2";
  public static final String SECURITY = "security";
  public static final String UNUSED = "unused";
  public static final String USER_EXPERIENCE = "user-experience";

  String[] value();

}
