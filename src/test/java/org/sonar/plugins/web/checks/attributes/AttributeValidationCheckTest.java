/*
 * Sonar Web Plugin
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
package org.sonar.plugins.web.checks.attributes;

import org.sonar.plugins.web.checks.sonar.TestHelper;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.web.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class AttributeValidationCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() {
    AttributeValidationCheck check = new AttributeValidationCheck();

    assertThat(check.attributes).isEmpty();
    assertThat(check.values).isEmpty();
  }

  @Test
  public void custom() {
    AttributeValidationCheck check = new AttributeValidationCheck();
    check.attributes = "a.foo,bar";
    check.values = "a|b";

    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/AttributeValidationCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getViolations())
        .next().atLine(2).withMessage("The attribute 'foo' does not respect the value constraint: a|b")
        .next().atLine(5).withMessage("The attribute 'bar' does not respect the value constraint: a|b")
        .next().atLine(7);
  }

}
