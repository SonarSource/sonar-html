/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2023 SonarSource SA and Matthijs Galesloot
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
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * This JUnit Extension allows to automatically execute {@link CheckMessagesVerifier#noMore()}.
 * <pre>
 * &#064;org.junit.jupiter.api.extension.RegisterExtension
 * public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();
 *
 * &#064;org.junit.jupiter.api.Test;
 * public void test() {
 *   checkMessagesVerifier.verify(messages)
 *     .next().atLine(1)
 *     .next().atLine(2);
 * }
 * </pre>
 */
public class CheckMessagesVerifierRule implements AfterEachCallback {

  private final List<CheckMessagesVerifier> verifiers = new ArrayList<>();

  public CheckMessagesVerifier verify(Collection<HtmlIssue> messages) {
    CheckMessagesVerifier verifier = CheckMessagesVerifier.verify(messages);
    verifiers.add(verifier);
    return verifier;
  }

  protected void verify() {
    for (CheckMessagesVerifier verifier : verifiers) {
      verifier.noMore();
    }
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception {
    verify();
  }
}
