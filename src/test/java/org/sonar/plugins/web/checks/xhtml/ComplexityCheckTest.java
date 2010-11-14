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

import static junit.framework.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Test;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class ComplexityCheckTest extends AbstractCheckTester {

  @Test
  public void testComplexityCheck() throws FileNotFoundException {

    FileReader reader = new FileReader("src/test/resources/src/main/webapp/create-salesorder.xhtml");
    WebSourceCode sourceCode = parseAndCheck(reader, ComplexityCheck.class, "max", "15" );

    assertEquals("Incorrect number of complexity", 17, sourceCode.getMeasure(CoreMetrics.COMPLEXITY).getIntValue().intValue());
    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
  }
}
