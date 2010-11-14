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
