/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot and SonarSource
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
package org.sonar.plugins.web.checks;

import com.google.common.base.Objects;
import com.google.common.collect.Ordering;
import org.hamcrest.Matcher;
import org.sonar.api.rules.Violation;
import org.sonar.squid.api.CheckMessage;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import static org.junit.Assert.assertThat;

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

  public static CheckMessagesVerifier verify(Collection<Violation> messages) {
    return new CheckMessagesVerifier(messages);
  }

  private final Iterator<Violation> iterator;
  private Violation current;

  private static final Comparator<Violation> ORDERING = new Comparator<Violation>() {
    @Override
    public int compare(Violation left, Violation right) {
      if (Objects.equal(left.getLineId(), right.getLineId())) {
        return left.getMessage().compareTo(right.getMessage());
      } else if (left.getLineId() == null) {
        return -1;
      } else if (right.getLineId() == null) {
        return 1;
      } else {
        return left.getLineId().compareTo(right.getLineId());
      }
    }
  };

  private CheckMessagesVerifier(Collection<Violation> messages) {
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
      Violation next = iterator.next();
      throw new AssertionError("\nNo more violations expected\ngot: at line " + next.getLineId());
    }
  }

  private void checkStateOfCurrent() {
    if (current == null) {
      throw new IllegalStateException("Prior to this method you should call next()");
    }
  }

  public CheckMessagesVerifier atLine(@Nullable Integer expectedLine) {
    checkStateOfCurrent();
    if (!Objects.equal(expectedLine, current.getLineId())) {
      throw new AssertionError("\nExpected: " + expectedLine + "\ngot: " + current.getLineId());
    }
    return this;
  }

  public CheckMessagesVerifier withMessage(String expectedMessage) {
    checkStateOfCurrent();
    String actual = current.getMessage();
    if (!actual.equals(expectedMessage)) {
      throw new AssertionError("\nExpected: \"" + expectedMessage + "\"\ngot: \"" + actual + "\"");
    }
    return this;
  }

  /**
   * Note that this method requires JUnit and Hamcrest.
   */
  public CheckMessagesVerifier withMessageThat(Matcher<String> matcher) {
    checkStateOfCurrent();
    String actual = current.getMessage();
    assertThat(actual, matcher);
    return this;
  }

  /**
   * @since sslr-squid-bridge 2.3
   */
  public CheckMessagesVerifier withCost(Double expectedCost) {
    checkStateOfCurrent();
    if (!Objects.equal(expectedCost, current.getCost())) {
      throw new AssertionError("\nExpected: " + expectedCost + "\ngot: " + current.getCost());
    }
    return this;
  }

}
