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
package org.sonar.plugins.web.core.helpers;

import java.util.List;

import org.sonar.api.component.Component;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;

public class IssuableMock implements Issuable {

  @Override
  public IssueBuilder newIssueBuilder() {
    return new IssueBuilderMock();
  }

  @Override
  public boolean addIssue(Issue issue) {
    // Do nothing
    return true;
  }

  @Override
  public List<Issue> issues() {
    return null;
  }

  @Override
  public List<Issue> resolvedIssues() {
    return null;
  }

  @Override
  public Component component() {
    return null;
  }
}
