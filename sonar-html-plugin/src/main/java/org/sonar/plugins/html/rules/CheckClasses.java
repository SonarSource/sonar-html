/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.rules;

import java.util.List;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;

public final class CheckClasses {
  private CheckClasses() {
  }

  public static List<Class<?>> getCheckClasses() {
    Reflections reflections = new Reflections("org.sonar.plugins.html");

    return reflections
            .getSubTypesOf(AbstractPageCheck.class)
            .stream()
            .filter(clazz -> clazz.getAnnotation(Rule.class) != null)
            .map(clazz -> (Class<?>) clazz)
            .collect(Collectors.toList());
  }
}
