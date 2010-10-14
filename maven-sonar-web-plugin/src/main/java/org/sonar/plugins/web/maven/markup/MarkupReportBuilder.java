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

package org.sonar.plugins.web.maven.markup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.plugins.web.html.AbstractReportBuilder;
import org.sonar.plugins.web.markupvalidation.MarkupError;
import org.sonar.plugins.web.markupvalidation.MarkupErrorCatalog;
import org.sonar.plugins.web.markupvalidation.MarkupErrorCatalog.ErrorDefinition;
import org.sonar.plugins.web.markupvalidation.MarkupReport;
import org.sonar.plugins.web.markupvalidation.MarkupValidator;

/**
 * Builds HTML report from a list of W3C responses.
 *
 * @author Matthijs Galesloot
 * @since 0.2
 */
class MarkupReportBuilder extends AbstractReportBuilder {

  private static final class Violation {

    public int count;
    public Integer messageId;
  }

  public void buildReports(File folder) {
    List<MarkupReport> reports = new ArrayList<MarkupReport>();
    for (File reportFile : MarkupValidator.getReportFiles(folder)) {
      MarkupReport report = MarkupReport.fromXml(reportFile);
      reports.add(report);
    }

    createHtmlReport(new File("target/markup-report.html"), reports, false);
    createHtmlReport(new File("target/markup-report-details.html"), reports, true);
  }

  private Violation findViolation(List<Violation> violations, Integer messageId) {
    for (Violation violation : violations) {
      if (violation.messageId.equals(messageId)) {
        return violation;
      }
    }
    Violation violation = new Violation();
    violation.messageId = messageId;
    violations.add(violation);
    return violation;
  }

  private List<Violation> collectViolations(List<MarkupReport> reports) {
    List<Violation> violations = new ArrayList<Violation>();

    for (MarkupReport report : reports) {

      for (MarkupError error : report.getErrors()) {
        Violation violation = findViolation(violations, error.getMessageId());
        violation.count++;
      }
    }
    return violations;
  }

  private void createViolationsReport(List<MarkupReport> reports, boolean showDetails) {

    List<Violation> violations = collectViolations(reports);
    sb.append("<h2>Guidelines Summary</h2>");
    sb.append("<table cellspacing='0' >");
    startRow();
    addHeaderCell("id");
    addHeaderCell("remark");
    addHeaderCell("count");
    endRow();

    MarkupErrorCatalog errorCatalog = new MarkupErrorCatalog();

    for (Violation violation : violations) {
      ErrorDefinition errorDefinition = errorCatalog.findErrorDefinition(violation.messageId);

      startRow();
      addCells(violation.messageId, errorDefinition.getRemark(), violation.count);
      endRow();

      if (showDetails) {
        startRow();
        sb.append("<td colspan='4'>");
        for (MarkupReport report : collectViolatedReports(reports, violation)) {
          sb.append(StringUtils.substringBeforeLast(report.getReportFile().getName(), "."));
          sb.append("<br/>");
        }
        sb.append("</td>");
        endRow();
      }
    }
    sb.append("</table>");
  }

  private List<MarkupReport> collectViolatedReports(List<MarkupReport> reports, Violation violation) {
    List<MarkupReport> violatedReports = new ArrayList<MarkupReport>();
    for (MarkupReport report : reports) {

      if (hasViolation(report, violation)) {
        violatedReports.add(report);
      }
    }
    return violatedReports;
  }

  private boolean hasViolation(MarkupReport report, Violation violation) {

    for (MarkupError error : report.getErrors()) {
      if (error.getMessageId().equals(violation.messageId)) {
        return true;
      }
    }
    return false;
  }

  private void createHtmlReport(File file, List<MarkupReport> reports, boolean showDetails) {
    sb = new StringBuilder();
    sb.append("<style type='text/css'>");
    sb.append("table{ border-color: gray; border-collapse: collapse; border: 1px solid}");
    sb.append("th { text-align: left; border: 1px solid; padding: 2px; } td{ border: 1px solid; padding: 2px; }</style>");
    sb.append("<h1>Markup Validation Report</h1>");

    createViolationsReport(reports, showDetails);

    // createUrlsReport(reports, showDetails);

    try {
      FileUtils.writeStringToFile(file, sb.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
