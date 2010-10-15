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

package org.sonar.plugins.web.maven.toetstool;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.web.html.AbstractReportBuilder;
import org.sonar.plugins.web.toetstool.xml.Guideline;
import org.sonar.plugins.web.toetstool.xml.Guideline.ValidationType;
import org.sonar.plugins.web.toetstool.xml.ToetstoolReport;

/**
 * Builds HTML report from the list of Toetstool reports.
 *
 * @author Matthijs Galesloot
 * @since 0.2
 */
class ToetsToolReportBuilder extends AbstractReportBuilder {

  private static final class Violation {

    public int error;
    public Guideline guideline;
    public String remark;
    public int warning;
  }

  private static final Logger LOG = Logger.getLogger(ToetsToolReportBuilder.class);

  public void buildReports(File folder) {
    List<ToetstoolReport> reports = new ArrayList<ToetstoolReport>();
    for (File reportFile : ToetsToolValidator.getReportFiles(folder)) {
      ToetstoolReport report = ToetstoolReport.fromXml(reportFile);
      reports.add(report);
    }
    Collections.sort(reports, new Comparator<ToetstoolReport>() {

      public int compare(ToetstoolReport t1, ToetstoolReport t2) {
        if (t1.getReport() == null || t2.getReport() == null) {
          return 0;
        }
        return t1.getReport().getUrl().compareTo(t2.getReport().getUrl());
      }
    });

    createHtmlReport(new File("target/toetstool-report.html"), reports, false);
    createHtmlReport(new File("target/toetstool-report-details.html"), reports, true);
  }

  private List<ToetstoolReport> collectViolatedReports(List<ToetstoolReport> reports, Violation violation) {
    List<ToetstoolReport> violatedReports = new ArrayList<ToetstoolReport>();
    for (ToetstoolReport report : reports) {

      Guideline guideline = findGuideline(report, violation);
      if (guideline != null && (guideline.getType() == ValidationType.error || guideline.getType() == ValidationType.warning)) {
        violatedReports.add(report);
      }
    }
    return violatedReports;
  }

  private List<Violation> collectViolations(List<ToetstoolReport> reports) {
    List<Violation> violations = new ArrayList<Violation>();

    for (ToetstoolReport report : reports) {
      if (report.getReport() == null) {
        continue;
      }
      for (Guideline guideline : report.getReport().getGuidelines()) {
        Violation violation = findViolation(violations, guideline);
        switch (guideline.getType()) {
          case error:
            violation.error++;
            break;
          case warning:
            violation.warning++;
            break;
          default:
            break;
        }
      }
    }
    return violations;
  }

  private void createHtmlReport(File file, List<ToetstoolReport> reports, boolean showDetails) {
    sb = new StringBuilder();
    sb.append("<style type='text/css'>");
    sb.append("table{ border-color: gray; border-collapse: collapse; border: 1px solid}");
    sb.append("th { text-align: left; border: 1px solid; padding: 2px; } td{ border: 1px solid; padding: 2px; }</style>");
    sb.append("<h1>Toetstool Validation Report</h1>");

    if ( !showDetails) {
      createSummaryReport(reports);
    }

    createViolationsReport(reports, showDetails);
    createUrlsReport(reports, showDetails);

    try {
      FileUtils.writeStringToFile(file, sb.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void createSummaryReport(List<ToetstoolReport> reports) {
    sb.append("<h2>Summary</h2>");
    sb.append("<table cellspacing='0'>");

    int minscore = Integer.MAX_VALUE;
    int maxscore = 0;
    int totalscore = 0;
    for (ToetstoolReport report : reports) {
      if (report.getReport() != null) {
        totalscore += report.getReport().getScore();
        minscore = Math.min(minscore, report.getReport().getScore());
        maxscore = Math.max(maxscore, report.getReport().getScore());
      } else {
        LOG.error("Could not find toetstool report for " + report.getReportNumber());
      }
    }
    addRow("Report Date", new SimpleDateFormat().format(new Date()));
    addRow("Validator", "<a href=\"http://www.toetstool.nl\">Toetstool</a>");
    addRow("Html files", reports.size());
    addRow("Min score", minscore);
    addRow("Max score", maxscore);
    addRow("AVG score", reports.size() > 0 ? totalscore / reports.size() : 0);

    sb.append("</table>");
  }

  private void createUrlsReport(List<ToetstoolReport> reports, boolean showDetails) {
    sb.append("<h2>URL Summary</h2>");
    sb.append("<table cellspacing='0'>");
    startRow();
    addHeaderCell("url");
    addHeaderCell("path");
    addHeaderCell("query");
    addHeaderCell("score");
    startRow();
    for (ToetstoolReport report : reports) {
      if (report.getReport() == null) {
        continue; // message
      }
      sb.append("<tr>");

      // URL to toetstool report
      String reportUrl = ToetsToolValidator.getHtmlReportUrl(report.getReportNumber());
      String anchor = String.format("<a href=\"%s\">%s</a>", reportUrl, report.getReportNumber());
      addCells(anchor);

      // page URL and query string
      try {
        URL pageUrl = new URL(report.getReport().getUrl());
        addCells(pageUrl.getPath());
        addCells(pageUrl.getQuery());
      } catch (MalformedURLException e) {
        addCells(report.getReport().getUrl());
        addCells("");
      }

      // score
      addCells(report.getReport().getScore());
      endRow();

      // errors and warnings
      if (showDetails) {
        startRow();
        sb.append("<td colspan='5'>");
        for (Guideline guideline : report.getReport().getGuidelines()) {
          if (guideline.getType() == ValidationType.error || guideline.getType() == ValidationType.warning) {
            sb.append(guideline.getRef());
            sb.append(": ");
            sb.append(StringEscapeUtils.unescapeHtml(guideline.getRemark()));
            sb.append("<br/>");
          }
        }
        sb.append("</td>");
        endRow();
      }
    }
    sb.append("</table>");
  }

  private void createViolationsReport(List<ToetstoolReport> reports, boolean showDetails) {

    List<Violation> violations = collectViolations(reports);
    sb.append("<h2>Guidelines Summary</h2>");
    sb.append("<table cellspacing='0' >");
    startRow();
    addHeaderCell("guideline");
    addHeaderCell("remark");
    addHeaderCell("error");
    addHeaderCell("warning");
    endRow();

    for (Violation violation : violations) {
      if (violation.guideline.getRef() != null) {
        startRow();
        String anchor = String.format("<a href=\"%s\">%s</a>", violation.guideline.getReflink(), violation.guideline.getRef());
        addCells(anchor);
        addCells(StringEscapeUtils.unescapeHtml(violation.remark));
        addCells(violation.error);
        addCells(violation.warning);
        endRow();

        if (showDetails) {
          startRow();
          sb.append("<td colspan='4'>");
          for (ToetstoolReport report : collectViolatedReports(reports, violation)) {

            sb.append(report.getReport().getUrl());
            sb.append("<br/>");
          }
          sb.append("</td>");
          endRow();
        }
      }
    }
    sb.append("</table>");
  }

  private Guideline findGuideline(ToetstoolReport report, Violation violation) {
    if (report.getReport() != null) {
      for (Guideline guideline : report.getReport().getGuidelines()) {
        if (guideline.getRef() != null && guideline.getRef().equals(violation.guideline.getRef())) {
          return guideline;
        }
      }
    }
    return null;
  }

  private Violation findViolation(List<Violation> violations, Guideline guideline) {
    for (Violation violation : violations) {
      if (violation.guideline.getReflink().equals(guideline.getReflink())) {
        return violation;
      }
    }
    Violation violation = new Violation();
    violation.guideline = guideline;
    violation.remark = guideline.getRemark();
    violations.add(violation);
    return violation;
  }
}
