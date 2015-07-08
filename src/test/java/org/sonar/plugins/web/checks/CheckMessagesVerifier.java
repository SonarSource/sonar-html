/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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
package org.sonar.plugins.web.checks;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import javax.annotation.Nullable;

import org.sonar.squid.api.CheckMessage;

import com.google.common.base.Objects;
import com.google.common.collect.Ordering;

/**
 * Helper class for testing checks without having to deploy them on a Sonar instance.
 * It can be used as following:
 * <pre>{@code
 * CheckMessagesVerifier.verify(messages)
 *   .next().atLine(1).withMessage("foo")
 *   .next().atLine(2).withMessage("bar")
 *   .noMore();
 * }</pre>
 * Strictly speaking this is just a wrapper over collection of {@link CheckMessage},
 * which guarantees order of traversal.
 *
 * @see CheckMessagesVerifierRule
 * @since sslr-squid-bridge 2.1
 */
public final class CheckMessagesVerifier {

  public static CheckMessagesVerifier verify(Collection<WebIssue> messages) {
    return new CheckMessagesVerifier(messages);
  }

  private final Iterator<WebIssue> iterator;
  private WebIssue current;

  private static final Comparator<WebIssue> ORDERING = new Comparator<WebIssue>() {
    @Override
    public int compare(WebIssue left, WebIssue right) {
      if (Objects.equal(left.line(), right.line())) {
        return left.message().compareTo(right.message());
      } else if (left.line() == null) {
        return -1;
      } else if (right.line() == null) {
        return 1;
      } else {
        return left.line().compareTo(right.line());
      }
    }
  };

  private CheckMessagesVerifier(Collection<WebIssue> messages) {
    iterator = Ordering.from(ORDERING).sortedCopy(messages).iterator();
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
      WebIssue next = iterator.next();
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
    if (!Objects.equal(expectedLine, current.line())) {
      throw new AssertionError("\nExpected: " + expectedLine + "\ngot: " + current.line());
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
    if (!Objects.equal(expectedCost, current.cost())) {
      throw new AssertionError("\nExpected: " + expectedCost + "\ngot: " + current.cost());
    }
    return this;
  }

}
