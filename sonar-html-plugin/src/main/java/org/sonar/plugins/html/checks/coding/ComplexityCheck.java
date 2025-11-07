/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.coding;

import java.util.Optional;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;

import java.util.List;

@Rule(key = "ComplexityCheck")
public final class ComplexityCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_COMPLEXITY = 10;

  @RuleProperty(
    key = "max",
    description = "Maximum allowed complexity",
    defaultValue = "" + DEFAULT_MAX_COMPLEXITY)
  public int max = DEFAULT_MAX_COMPLEXITY;

  @Override
  public void startDocument(List<Node> nodes) {
    int complexity = Optional.ofNullable(getHtmlSourceCode().getMeasure(CoreMetrics.COMPLEXITY)).orElse(0);

    if (complexity > max) {
      String msg = String.format("Split this file to reduce complexity per file from %d to no more than the %d authorized.", complexity, max);
      createViolation(0, msg, (double) (complexity - max));
    }
  }

}
