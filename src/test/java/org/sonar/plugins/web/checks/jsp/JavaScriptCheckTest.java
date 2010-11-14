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

import static junit.framework.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.StringReader;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class JavaScriptCheckTest extends AbstractCheckTester {

  @Test
  public void testJavaScriptCheck() throws FileNotFoundException {

    String fragment = "<h:someNode/><script language=\"JavaScript\">var a;\nvar b;\nvar c;\nvar d;\nvar e;\nvar f;\n</script>";

    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), LongJavaScriptCheck.class);

    assertTrue("Should have found 1 violation", sourceCode.getViolations().size() == 1);
  }
}
