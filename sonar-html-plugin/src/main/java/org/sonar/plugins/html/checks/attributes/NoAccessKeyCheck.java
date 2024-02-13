package org.sonar.plugins.html.checks.attributes;

import org.sonar.check.Rule;

@Rule(key = "S6846")
public class NoAccessKeyCheck extends IllegalAttributeCheck {

  public NoAccessKeyCheck() {
    super();
    attributes = "accessKey";
  }
}
