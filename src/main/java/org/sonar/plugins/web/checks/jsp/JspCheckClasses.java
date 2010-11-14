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

package org.sonar.plugins.web.checks.jsp;

import java.util.Arrays;
import java.util.List;

import org.sonar.plugins.web.checks.AbstractPageCheck;

/**
 * Provides a list of all JSP checks.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class JspCheckClasses {

  private static final Class<AbstractPageCheck>[] CLASSES = new Class[] { AvoidHtmlCommentCheck.class, DynamicJspIncludeCheck.class,
    HeaderCheck.class, InlineStyleCheck.class, InternationalizationCheck.class, JspScriptletCheck.class, LongJavaScriptCheck.class,
    MultiplePageDirectivesCheck.class, IllegalTagLibsCheck.class, UnifiedExpressionCheck.class, WhiteSpaceAroundCheck.class };

  public static List<Class<AbstractPageCheck>> getCheckClasses() {
    return Arrays.asList(CLASSES);
  }

  private JspCheckClasses() {
    // utility class
  }
}
