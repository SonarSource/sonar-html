/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
 * dev@sonar.codehaus.org
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

import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Violation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WebSourceCode<E extends Resource> {

  private final File file;
  private final Resource<E> resource;
  private final List<Measure> measures = new ArrayList<Measure>();
  private final List<Violation> violations = new ArrayList<Violation>();

  public WebSourceCode(File file, Resource<E> resource) {
    this.file = file;
    this.resource = resource;
  }

  public File getFile() {
    return file;
  }

  public void addMeasure(Metric metric, double value) {
    Measure measure = new Measure(metric, value);
    this.measures.add(measure);
  }

  public void addViolation(Violation violation) {
    this.violations.add(violation);
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

  public Resource<E> getResource() {
    return resource;
  }

  public List<Violation> getViolations() {
    return violations;
  }

  @Override
  public String toString() {
    return resource.getLongName();
  }
}
