/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.markupvalidation.MarkupError;
import org.sonar.plugins.web.markupvalidation.MarkupReport;
import org.sonar.plugins.web.markupvalidation.MarkupValidator;
import org.sonar.plugins.web.rules.markup.MarkupRuleRepository;

/**
 * @author Matthijs Galesloot
 * @since 0.2
 */
public final class HtmlSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(HtmlSensor.class);

  private final RulesProfile profile;
  private final RuleFinder ruleFinder;

  public HtmlSensor(RulesProfile profile, RuleFinder ruleFinder) {
    LOG.info("Profile: " + profile.getName());
    this.profile = profile;
    this.ruleFinder = ruleFinder;
  }

  public void analyse(Project project, SensorContext sensorContext) {

    ProjectConfiguration projectConfiguration = new ProjectConfiguration(project);
    projectConfiguration.addSourceDir();
    File htmlDir = new File(project.getFileSystem().getBasedir() + "/" + projectConfiguration.getSourceDir());
    LOG.info("HTML Dir:" + projectConfiguration.getSourceDir());

    for (File reportFile : MarkupValidator.getReportFiles(htmlDir)) {
      MarkupReport report = MarkupReport.fromXml(reportFile);
      File file = new File(StringUtils.substringBefore(report.getPath(), MarkupValidator.REPORT_SUFFIX));
      WebFile resource = WebFile.fromIOFile(file, project.getFileSystem().getSourceDirs());

      for (MarkupError error : report.getErrors()) {
        addError(sensorContext, resource, error);
      }
    }
  }

  private void addError(SensorContext sensorContext, WebFile resource, MarkupError error) {
    String ruleKey = Integer.toString(error.getMessageId());
    Rule rule = ruleFinder.findByKey(MarkupRuleRepository.REPOSITORY_KEY, ruleKey);
    if (rule != null) {
      LOG.info("Added error " + error);
      Violation violation = Violation.create(rule, resource).setLineId(error.getLine());
      violation.setMessage(error.getMessage());
      sensorContext.saveViolation(violation);
      LOG.info("Added error " + error.getMessageId() + " for " + resource);
    } else {
      LOG.warn("Could not find Markup Rule " + ruleKey);
    }
  }

  /**
   * This sensor executes on Html profiles.
   */
  public boolean shouldExecuteOnProject(Project project) {
    return isEnabled(project) && Web.INSTANCE.equals(project.getLanguage()) && hasMarkupRules();
  }

  private boolean hasMarkupRules() {
    for (ActiveRule activeRule : profile.getActiveRules()) {
      if (MarkupRuleRepository.REPOSITORY_KEY.equals(activeRule.getRepositoryKey())) {
        return true;
      }
    }
    return false;
  }

  private boolean isEnabled(Project project) {
    return project.getConfiguration().getBoolean(CoreProperties.CORE_IMPORT_SOURCES_PROPERTY,
        CoreProperties.CORE_IMPORT_SOURCES_DEFAULT_VALUE);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
