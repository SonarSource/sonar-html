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

package org.sonar.plugins.web.toetstool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.web.toetstool.xml.Guideline;
import org.sonar.plugins.web.toetstool.xml.Guideline.ValidationType;
import org.sonar.plugins.web.toetstool.xml.ToetstoolReport;

public class Report {

  private class Violation {

    int error;
    Guideline guideline;
    String remark;
    int warning;
  }

  private static final Logger LOG = Logger.getLogger(Report.class);

  /**
   * Decodes html entities.
   *
   * @param s
   *          the <code>String</code> to decode
   * @return the newly decoded <code>String</code>
   */
  public static String htmlEntityDecode(String s) {

    return StringUtils.replaceEachRepeatedly(s, new String[] { "&amp;", "&lt;", "&gt;" }, new String[] { "&", "<", ">" });
  }

  private final boolean details = true;

  private final StringBuilder sb = new StringBuilder();

  private void addCells(Object... values) {
    for (Object value : values) {
      sb.append("<td>");
      sb.append(value);
      sb.append("</td>\n");
    }
  }

  private void addHeaderCell(Object value) {
    sb.append("<th>");
    sb.append(value);
    sb.append("</th>\n");
  }

  private void addRow(Object... values) {
    startRow();
    addCells(values);
    endRow();
  }

  public void buildReports(File folder) {
    List<ToetstoolReport> reports = new ArrayList<ToetstoolReport>();
    for (File reportFile : ToetsTool.getReportFiles(folder)) {
      ToetstoolReport report = ToetstoolReport.fromXml(reportFile);
      reports.add(report);
    }

    createHtmlReport(reports);
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

  private void createHtmlReport(List<ToetstoolReport> reports) {
    sb.append("<style type='text/css'>");
    sb.append("table{ border-color: gray; border-collapse: collapse; border: 1px solid}");
    sb.append("th { text-align: left; border: 1px solid; padding: 2px; } td{ border: 1px solid; padding: 2px; }</style>");
    sb.append("<h1>Toetstool Validation Report</h1>");

    createSummaryReport(reports);
    createUrlsReport(reports);

    List<Violation> violations = collectViolations(reports);
    createViolationsReport(violations);

    try {
      FileWriter writer = new FileWriter("target/report.html");
      writer.append(sb.toString());
      writer.close();
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
    addRow("AVG score", totalscore / reports.size());

    sb.append("</table>");
  }

  private void createUrlsReport(List<ToetstoolReport> reports) {
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
      String reportUrl = ToetsTool.getHtmlReportUrl(report.getReportNumber());
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

      // errors and warnings
      if (details) {
        endRow();
        startRow();
        sb.append("<td colspan='5'>");
        for (Guideline guideline : report.getReport().getGuidelines()) {
          if (guideline.getType() == ValidationType.error || guideline.getType() == ValidationType.warning) {
            sb.append(guideline.getRef());
            sb.append(": ");
            sb.append(htmlEntityDecode(guideline.getRemark()));
            sb.append("<br/>");
          }
        }
        sb.append("</td>");
      }
      endRow();
    }
    sb.append("</table>");
  }

  private void createViolationsReport(List<Violation> violations) {
    sb.append("<h2>Guidelines Summary</h2>");
    sb.append("<table cellspacing='0' >");
    sb.append("<tr>");
    addHeaderCell("guideline");
    addHeaderCell("remark");
    addHeaderCell("error");
    addHeaderCell("warning");
    sb.append("</tr>");
    for (Violation violation : violations) {
      if (violation.error > 0 || violation.warning > 0) {
        sb.append("<tr>");
        String anchor = String.format("<a href=\"%s\">%s</a>", violation.guideline.getReflink(), violation.guideline.getRef());
        addCells(anchor);
        addCells(htmlEntityDecode(violation.remark));
        addCells(violation.error);
        addCells(violation.warning);
        sb.append("</tr>");
      }
    }
    sb.append("</table>");
  }

  private void endRow() {
    sb.append("</tr>");
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

  private void startRow() {
    sb.append("<tr>");
  }

}
