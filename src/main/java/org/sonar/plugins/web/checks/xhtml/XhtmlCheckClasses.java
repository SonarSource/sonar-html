/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.checks.xhtml;

import java.util.Arrays;
import java.util.List;

import org.sonar.plugins.web.checks.AbstractPageCheck;

/**
 * Provides a list of available XHTML checks.
 *
 */
public final class XhtmlCheckClasses {

  private static final Class<AbstractPageCheck>[] CLASSES = new Class[] { ComplexityCheck.class, DocTypeCheck.class,
    IllegalAttributeCheck.class, DoubleQuotesCheck.class, IllegalElementCheck.class, IllegalTabCheck.class, MaxLineLengthCheck.class,
    IllegalNamespaceCheck.class, RegularExpressionCheck.class, RequiredAttributeCheck.class,
    RequiredElementCheck.class, UnclosedTagCheck.class };

  /**
   * Gets the list of XML checks.
   */
  public static List<Class<AbstractPageCheck>> getCheckClasses() {
    return Arrays.asList(CLASSES);
  }

  private XhtmlCheckClasses() {

  }
}
