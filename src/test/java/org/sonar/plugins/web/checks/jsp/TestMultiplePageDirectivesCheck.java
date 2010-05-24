package org.sonar.plugins.web.checks.jsp;

import static junit.framework.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.StringReader;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;


public class TestMultiplePageDirectivesCheck extends AbstractCheckTester {

  @Test
  public void onlyOnePageDirectiveIsAllowed() throws FileNotFoundException {

    StringBuilder sb = new StringBuilder();
    sb.append("<h:someNode/>");
    sb.append("<%@ page session=\"false\" %>");
    sb.append("<%@ page errorPage=\"/common/errorPage.jsp\" %>");

    WebSourceCode sourceCode = parseAndCheck(new StringReader(sb.toString()), new MultiplePageDirectivesCheck());

    assertTrue("Should have found 1 violation", sourceCode.getViolations().size() == 1);

    // add an import attribute to the page directive: show that the page directive will still be counted
    sb = new StringBuilder();
    sb.append("<h:someNode/>");
    sb.append("<%@ page session=\"false\" %>");
    sb.append("<%@ page errorPage=\"/common/errorPage.jsp\" import=\"java.util.*,java.text.*\" %>");

    sourceCode = parseAndCheck(new StringReader(sb.toString()), new MultiplePageDirectivesCheck());

    assertTrue("Should have found 1 violation", sourceCode.getViolations().size() == 1);
  }

  @Test
  public void pageImportsAreOK() throws FileNotFoundException {

    // page import directives are not counted.
    StringBuilder sb = new StringBuilder();
    sb.append("<h:someNode/>");
    sb.append("<%@ page session=\"false\" %>");
    sb.append("<%@ page import=\"java.util.*,java.text.*\" %>");

    WebSourceCode sourceCode = parseAndCheck(new StringReader(sb.toString()), new MultiplePageDirectivesCheck());

    assertTrue("Should have found 1 violation", sourceCode.getViolations().size() == 0);
  }
}
