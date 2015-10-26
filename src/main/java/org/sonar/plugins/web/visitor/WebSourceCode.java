/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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
package org.sonar.plugins.web.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.web.checks.WebIssue;

public class WebSourceCode {

  private final InputFile inputFile;
  private final Resource resource;
  private final List<Measure> measures = new ArrayList<>();
  private final List<WebIssue> issues = new ArrayList<>();
  private Set<Integer> detailedLinesOfCode;
  private Set<Integer> detailedLinesOfComments;

  public WebSourceCode(InputFile inputFile, Resource resource) {
    this.inputFile = inputFile;
    this.resource = resource;
  }

  public InputFile inputFile() {
    return inputFile;
  }

  public void addMeasure(Metric metric, double value) {
    Measure measure = new Measure(metric, value);
    this.measures.add(measure);
  }

  public void addIssue(WebIssue issue) {
    this.issues.add(issue);
  }

  public Measure getMeasure(Metric metric) {
    for (Measure measure : measures) {
      if (measure.getMetric().equals(metric)) {
        return measure;
      }
    }
    return null;
  }

  public List<Measure> getMeasures() {
    return measures;
  }

  /**
   * <p> /!\ Should not be used /!\ </p>
   * Only {@link org.sonar.api.batch.fs.InputFile InputFile} should be used. It is necessary only for
   * {@link org.sonar.api.issue.NoSonarFilter#addComponent(String fileKey, java.util.Set noSonarLines)}
   * in {@link org.sonar.plugins.web.visitor.NoSonarScanner NoSonarScanner} because SonarQube API provides
   * the corresponding method taking an inputFile as a parameter only from version 5.0.
   *
   */
  public Resource getResource() {
    return resource;
  }

  public List<WebIssue> getIssues() {
    return issues;
  }

  @Override
  public String toString() {
    return resource.getLongName();
  }

  public Set<Integer> getDetailedLinesOfCode() {
    return detailedLinesOfCode;
  }

  public void setDetailedLinesOfCode(Set<Integer> detailedLinesOfCode) {
    this.detailedLinesOfCode = detailedLinesOfCode;
  }

  public boolean isLineOfCode(int line) {
    return detailedLinesOfCode.contains(line);
  }

  public boolean isLineOfComment(int line) {
    return detailedLinesOfComments.contains(line);
  }

  public Set<Integer> getDetailedLinesOfComments() {
    return detailedLinesOfComments;
  }

  public void setDetailedLinesOfComments(Set<Integer> detailedLinesOfComments) {
    this.detailedLinesOfComments = detailedLinesOfComments;
  }
}
