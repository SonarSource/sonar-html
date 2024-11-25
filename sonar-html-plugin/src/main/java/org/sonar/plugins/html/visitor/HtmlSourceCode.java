/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.Metric;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.checks.HtmlIssue;

public class HtmlSourceCode {

  private final InputFile inputFile;
  private final Map<Metric<Integer>, Integer> measures = new HashMap<>();
  private final List<HtmlIssue> issues = new ArrayList<>();
  private Set<Integer> detailedLinesOfCode = new HashSet<>();

  public HtmlSourceCode(InputFile inputFile) {
    this.inputFile = inputFile;
  }

  public InputFile inputFile() {
    return inputFile;
  }

  public void addMeasure(Metric<Integer> metric, int value) {
    if (shouldComputeMetric()) {
      measures.put(metric, value);
    }
  }

  public void addIssue(HtmlIssue issue) {
    this.issues.add(issue);
  }

  public Integer getMeasure(Metric metric) {
    return measures.get(metric);
  }

  public Map<Metric<Integer>, Integer> getMeasures() {
    return measures;
  }

  public List<HtmlIssue> getIssues() {
    return issues;
  }

  @Override
  public String toString() {
    return inputFile().toString();
  }

  public Set<Integer> getDetailedLinesOfCode() {
    return detailedLinesOfCode;
  }

  public void setDetailedLinesOfCode(Set<Integer> detailedLinesOfCode) {
    this.detailedLinesOfCode = detailedLinesOfCode;
  }

  public boolean shouldComputeMetric() {
    // if input file has a language other than web, then we should not compute metrics for this file as we assume they will be computed by another plugin
    String language = inputFile.language();
    return language == null || HtmlConstants.LANGUAGE_KEY.equals(language) || HtmlConstants.JSP_LANGUAGE_KEY.equals(language);
  }

}
