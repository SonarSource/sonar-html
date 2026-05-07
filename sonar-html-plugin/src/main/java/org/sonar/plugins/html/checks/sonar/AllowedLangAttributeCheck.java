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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S8687")
public class AllowedLangAttributeCheck extends AbstractPageCheck {

  private static final Logger LOG = LoggerFactory.getLogger(AllowedLangAttributeCheck.class);

  static final String ALLOWED_LANG_MESSAGE = "Update the \"lang\" attribute to one of the configured languages.";

  @RuleProperty(
    key = "languages",
    description = "Comma-separated list of allowed language codes (e.g. \"en,fr,es,ca\"). "
      + "Values are matched case-insensitively against the primary language subtag of the \"lang\" attribute (the part before any hyphen).")
  public String languages = "";

  private Set<String> allowedLanguagesCache = null;

  @Override
  public void startElement(TagNode node) {
    if (allowedLanguages().isEmpty()) {
      return;
    }
    String langValue = node.getAttribute("lang");
    if (langValue == null || langValue.isBlank()) {
      return;
    }
    langValue = langValue.trim();
    if (Helpers.isDynamicValue(langValue, getHtmlSourceCode())) {
      return;
    }
    if (!LangAttributeCheck.LANG_CODE_PATTERN.matcher(langValue).matches()) {
      return;
    }
    String primaryLang = langValue.split("-")[0].toLowerCase(Locale.ENGLISH);
    if (!allowedLanguages().contains(primaryLang)) {
      createViolation(node, ALLOWED_LANG_MESSAGE);
    }
  }

  private Set<String> allowedLanguages() {
    if (allowedLanguagesCache == null) {
      allowedLanguagesCache = Arrays.stream(languages.split(","))
        .map(s -> s.trim().toLowerCase(Locale.ENGLISH))
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toSet());
      if (allowedLanguagesCache.isEmpty()) {
        LOG.warn("Rule S8687 is enabled but no languages are configured — the rule will have no effect.");
      }
    }
    return allowedLanguagesCache;
  }
}
