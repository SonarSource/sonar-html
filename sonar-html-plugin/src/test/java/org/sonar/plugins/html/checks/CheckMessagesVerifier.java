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
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import javax.annotation.Nullable;

/**
 * Helper class for testing checks without having to deploy them on a Sonar instance.
 * It can be used as following:
 * <pre>{@code
 * CheckMessagesVerifier.verify(messages)
 *   .next().atLine(1).withMessage("foo")
 *   .next().atLine(2).withMessage("bar")
 *   .noMore();
 * }</pre>
 * Strictly speaking this is just a wrapper over collection of {@link HtmlIssue}
 * which guarantees order of traversal.
 *
 * @see CheckMessagesVerifierRule
 */
public final class CheckMessagesVerifier {

  public static CheckMessagesVerifier verify(Collection<HtmlIssue> messages) {
    return new CheckMessagesVerifier(messages);
  }

  private final Iterator<HtmlIssue> iterator;
  private HtmlIssue current;

  private static final Comparator<HtmlIssue> ORDERING = (left, right) -> {
    if (Objects.equals(left.line(), right.line())) {
      return left.message().compareTo(right.message());
    } else if (left.line() == null) {
      return -1;
    } else if (right.line() == null) {
      return 1;
    } else {
      return left.line().compareTo(right.line());
    }
  };

  private CheckMessagesVerifier(Collection<HtmlIssue> messages) {
    ArrayList<HtmlIssue> messagesList = new ArrayList<>(messages);
    messagesList.sort(ORDERING);
    iterator = messagesList.iterator();
  }

  public CheckMessagesVerifier next() {
    if (!iterator.hasNext()) {
      throw new AssertionError("\nExpected violation");
    }
    current = iterator.next();
    return this;
  }

  public void noMore() {
    if (iterator.hasNext()) {
      HtmlIssue next = iterator.next();
      throw new AssertionError("\nNo more violations expected\ngot: at line " + next.line());
    }
  }

  private void checkStateOfCurrent() {
    if (current == null) {
      throw new IllegalStateException("Prior to this method you should call next()");
    }
  }

  public CheckMessagesVerifier atLine(@Nullable Integer expectedLine) {
    checkStateOfCurrent();
    if (!Objects.equals(expectedLine, current.line())) {
      throw new AssertionError("\nExpected: " + expectedLine + "\ngot: " + current.line());
    }
    return this;
  }

  public CheckMessagesVerifier atLocation(int startLine, int startColumn, int endLine, int endColumn) {
    checkStateOfCurrent();
    PreciseHtmlIssue preciseHtmlIssue = (PreciseHtmlIssue) current;
    if (!Objects.equals(startLine, current.line())) {
      throw new AssertionError("\nExpected: " + startLine + "\ngot: " + current.line());
    }
    if (!Objects.equals(startColumn, preciseHtmlIssue.startColumn())) {
      throw new AssertionError("\nExpected: " + startColumn + "\ngot: " + preciseHtmlIssue.startColumn());
    }
    if (!Objects.equals(endLine, preciseHtmlIssue.endLine())) {
      throw new AssertionError("\nExpected: " + endLine + "\ngot: " + preciseHtmlIssue.endLine());
    }
    if (!Objects.equals(endColumn, preciseHtmlIssue.endColumn())) {
      throw new AssertionError("\nExpected: " + endColumn + "\ngot: " + preciseHtmlIssue.endColumn());
    }
    return this;
  }

  public CheckMessagesVerifier withMessage(String expectedMessage) {
    checkStateOfCurrent();
    String actual = current.message();
    if (!actual.equals(expectedMessage)) {
      throw new AssertionError("\nExpected: \"" + expectedMessage + "\"\ngot: \"" + actual + "\"");
    }
    return this;
  }

  public CheckMessagesVerifier withCost(@Nullable Double expectedCost) {
    checkStateOfCurrent();
    if (!Objects.equals(expectedCost, current.cost())) {
      throw new AssertionError("\nExpected: " + expectedCost + "\ngot: " + current.cost());
    }
    return this;
  }

}
