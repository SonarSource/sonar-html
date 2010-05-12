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

package org.sonar.plugins.web.checks.jsp;

import org.sonar.plugins.web.checks.xml.UnclosedTagCheck;
import org.sonar.plugins.web.rules.AbstractPageCheck;

public class JspCheckClasses {

  private static final Class<AbstractPageCheck>[] checkClasses = new Class[] { 
    JspScriptletCheck.class, 
    AttributeClassCheck.class,
    HeaderCheck.class, 
    MultiplePageDirectivesCheck.class,
    TabCheck.class,
    WhiteSpaceAroundCheck.class};

  public static Class<AbstractPageCheck>[] getCheckClasses() {
    return checkClasses; 
  }
}
