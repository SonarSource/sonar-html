/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2020 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
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
package org.sonar.plugins.html.checks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.rules.Verifier;

/**
 * This JUnit Rule allows to automatically execute {@link CheckMessagesVerifier#noMore()}.
 * <pre>
 * &#064;org.junit.Rule
 * public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();
 *
 * &#064;org.junit.Test
 * public void test() {
 *   checkMessagesVerifier.verify(messages)
 *     .next().atLine(1)
 *     .next().atLine(2);
 * }
 * </pre>
 */
public class CheckMessagesVerifierRule extends Verifier {

  private final List<CheckMessagesVerifier> verifiers = new ArrayList<>();

  public CheckMessagesVerifier verify(Collection<HtmlIssue> messages) {
    CheckMessagesVerifier verifier = CheckMessagesVerifier.verify(messages);
    verifiers.add(verifier);
    return verifier;
  }

  @Override
  protected void verify() {
    for (CheckMessagesVerifier verifier : verifiers) {
      verifier.noMore();
    }
  }

}
