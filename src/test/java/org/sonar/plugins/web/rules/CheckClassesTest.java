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
package org.sonar.plugins.web.rules;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.sonar.api.rules.AnnotationRuleParser;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleParam;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.plugins.web.checks.WebRule;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.fest.assertions.Assertions.assertThat;

public class CheckClassesTest {

  @Test(expected=IllegalAccessException.class)
  public void create() throws Exception {
    CheckClasses.class.newInstance();
  }

  /**
   * Enforces that each check declared in list.
   */
  @Test
  public void count() {
    final List<Class> checkClasses = CheckClasses.getCheckClasses();
    int count = 0;
    List<File> files = (List<File>) FileUtils.listFiles(new File("src/main/java/org/sonar/plugins/web/checks/"), new String[]{"java"}, true);
    for (File file : files) {
      if (file.getName().endsWith("Check.java") && (!file.getName().endsWith("AbstractPageCheck.java"))) {
        count++;
      }
    }
    assertThat(CheckClasses.getCheckClasses().size()).isEqualTo(count);
  }

  /**
   * Enforces that each check has test, name and description.
   */
  @Test
  public void test() {
    List<Class> checks = CheckClasses.getCheckClasses();

    for (Class cls : checks) {
      String testName = '/' + cls.getName().replace('.', '/') + "Test.class";
      assertThat(getClass().getResource(testName))
        .overridingErrorMessage("No test for " + cls.getSimpleName())
        .isNotNull();
      assertThat(AnnotationUtils.getClassAnnotation(cls, WebRule.class) != null)
        .overridingErrorMessage("Add @WebRule to " + cls.getSimpleName())
        .isTrue();
    }

    ResourceBundle resourceBundle = ResourceBundle.getBundle("org.sonar.l10n.Web", Locale.ENGLISH);

    List<Rule> rules = new AnnotationRuleParser().parse("repositoryKey", checks);
    for (Rule rule : rules) {
      resourceBundle.getString("rule.Web." + rule.getKey() + ".name");
      assertThat(getClass().getResource("/org/sonar/l10n/web/rules/Web/" + rule.getKey() + ".html"))
        .overridingErrorMessage("No description for " + rule.getKey())
        .isNotNull();

      assertThat(rule.getDescription())
        .overridingErrorMessage("Description of " + rule.getKey() + " should be in separate file")
        .isNullOrEmpty();

      for (RuleParam param : rule.getParams()) {
        resourceBundle.getString("rule.Web." + rule.getKey() + ".param." + param.getKey());

        assertThat(param.getDescription())
          .overridingErrorMessage("Description for param " + param.getKey() + " of " + rule.getKey() + " should be in separate file")
          .isNullOrEmpty();
      }
    }
  }

}