/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.sonar;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.api.Helpers;

@Rule(key = "S8687")
public class AllowedLangAttributeCheck extends LangAttributeCheck {

  @RuleProperty(
    key = "languages",
    description = "Comma-separated list of allowed ISO 639-1 language codes (e.g. \"en,fr,es,ca\")")
  public String languages = "";

  private Set<String> allowedLanguagesCache = null;

  @Override
  protected boolean isValidLangAttributeValue(String langAttributeValue) {
    if (!super.isValidLangAttributeValue(langAttributeValue)) {
      return false;
    }
    if (Helpers.isDynamicValue(langAttributeValue, getHtmlSourceCode())) {
      return true;
    }
    var parts = langAttributeValue.split("-");
    if (parts[0].length() != 2) {
      // template placeholder (e.g. %lang%, #{locale}) — parent already validated
      return true;
    }
    return allowedLanguages().contains(parts[0].toLowerCase(Locale.ENGLISH));
  }

  private Set<String> allowedLanguages() {
    if (allowedLanguagesCache == null) {
      Set<String> isoLanguages = Arrays.stream(Locale.getISOLanguages()).collect(Collectors.toSet());
      allowedLanguagesCache = Arrays.stream(languages.split(","))
        .map(s -> s.trim().toLowerCase(Locale.ENGLISH))
        .filter(isoLanguages::contains)
        .collect(Collectors.toSet());
    }
    return allowedLanguagesCache;
  }
}