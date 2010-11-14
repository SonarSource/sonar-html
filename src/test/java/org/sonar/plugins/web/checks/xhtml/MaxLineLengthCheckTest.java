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

import static junit.framework.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.StringReader;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.checks.xhtml.MaxLineLengthCheck;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class MaxLineLengthCheckTest extends AbstractCheckTester {

  @Test
  public void testMaxLengthViolated() throws FileNotFoundException {

    String fragment = "<td><br><tr>";
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < 12; i++) {
      sb.append(fragment);
    }
    WebSourceCode sourceCode = parseAndCheck(new StringReader(sb.toString()), MaxLineLengthCheck.class);
    int numViolations = 1;
    assertTrue("Should have found " + numViolations + " violations", sourceCode.getViolations().size() == numViolations);
  }

  @Test
  public void testMaxLengthOK() throws FileNotFoundException {

    String fragment = "<td><br><tr>";
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < 12; i++) {
      sb.append(fragment);
      sb.append("\n");
    }
    WebSourceCode sourceCode = parseAndCheck(new StringReader(sb.toString()), MaxLineLengthCheck.class);
    int numViolations = 0;
    assertTrue("Should have found " + numViolations + " violations", sourceCode.getViolations().size() == numViolations);
  }

  //  @Test
  //  public void testMaxLengthFromFile() throws FileNotFoundException {
  //
  //    FileReader reader = new FileReader("src/test/resources/src/main/webapp/detail-publicatie.xhtml");
  //    WebSourceCode sourceCode = parseAndCheck(reader, new MaxLineLengthCheck());
  //    int numViolations = 0;
  //    assertTrue("Should have found " + numViolations + " violations", sourceCode.getViolations().size() == numViolations);
  //  }

}
