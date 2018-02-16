/*
 * SonarWeb :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.rules;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.debt.DebtRemediationFunction;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.web.api.WebConstants;
import org.sonar.squidbridge.annotations.AnnotationBasedRulesDefinition;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public final class WebRulesDefinition implements RulesDefinition {

  public static final String REPOSITORY_KEY = WebConstants.LANGUAGE_NAME;
  public static final String REPOSITORY_NAME = "SonarAnalyzer";

  private final Gson gson = new Gson();

  @Override
  public void define(Context context) {
    NewRepository repository = context
      .createRepository(REPOSITORY_KEY, WebConstants.LANGUAGE_KEY)
      .setName(REPOSITORY_NAME);

    new AnnotationBasedRulesDefinition(repository, WebConstants.LANGUAGE_KEY).addRuleClasses(false, CheckClasses.getCheckClasses());
    for (NewRule rule : repository.rules()) {
      String metadataKey = rule.key();
      rule.setInternalKey(metadataKey);
      addMetadata(rule, metadataKey);
    }
    repository.done();
  }

  private void addMetadata(NewRule rule, String metadataKey) {
    String json = readRuleDefinitionResource(metadataKey + ".json");
    RuleMetadata metadata = gson.fromJson(json, RuleMetadata.class);
    rule.setSeverity(metadata.defaultSeverity.toUpperCase(Locale.US));
    rule.setName(metadata.title);
    rule.setTags(metadata.tags);
    rule.setType(Preconditions.checkNotNull(metadata.type, metadataKey));
    rule.setStatus(RuleStatus.valueOf(metadata.status.toUpperCase(Locale.US)));

    if (metadata.remediation != null) {
      rule.setDebtRemediationFunction(metadata.remediation.remediationFunction(rule.debtRemediationFunctions()));
      rule.setGapDescription(metadata.remediation.linearDesc);
    }
  }

  private static String readRuleDefinitionResource(String fileName) {
    URL resource = WebRulesDefinition.class.getResource("/org/sonar/l10n/web/rules/Web/" + fileName);
    if (resource == null) {
      throw new IllegalStateException();
    }
    try {
      return Resources.toString(resource, Charsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read: " + resource, e);
    }
  }

  private static class RuleMetadata {
    String title;
    String status;
    RuleType type;
    @Nullable
    Remediation remediation;

    String[] tags;
    String defaultSeverity;
  }

  private static class Remediation {
    String func;
    String constantCost;
    String linearDesc;
    String linearOffset;
    String linearFactor;

    private DebtRemediationFunction remediationFunction(DebtRemediationFunctions drf) {
      if (func.startsWith("Constant")) {
        return drf.constantPerIssue(constantCost.replace("mn", "min"));
      }
      if ("Linear".equals(func)) {
        return drf.linear(linearFactor.replace("mn", "min"));
      }
      return drf.linearWithOffset(linearFactor.replace("mn", "min"), linearOffset.replace("mn", "min"));
    }
  }

}
