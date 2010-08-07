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

package org.sonar.plugins.web.visitor;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.design.Dependency;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Violation;
import org.sonar.squid.api.SourceCodeEdgeUsage;

/**
 * Checks and analyzers report measurements, violations and other findings in WebSourceCode.
 * 
 * @author Matthijs Galesloot
 */
public class WebSourceCode {

  private final List<Dependency> dependencies = new ArrayList<Dependency>();
  private final List<Measure> measures = new ArrayList<Measure>();
  private final Resource<?> resource;
  private final List<Violation> violations = new ArrayList<Violation>();

  public WebSourceCode(Resource<?> resource) {
    this.resource = resource;
  }

  public void addDependency(Resource<?> dependencyResource) {
    Dependency dependency = new Dependency(resource, dependencyResource);
    dependency.setUsage(SourceCodeEdgeUsage.USES.name());
    dependency.setWeight(1);

    dependencies.add(dependency);
  }

  public void addMeasure(Metric metric, double value) {
    Measure measure = new Measure(metric, value);
    this.measures.add(measure);
  }

  public void addViolation(Violation violation) {
    violation.setResource(resource);
    this.violations.add(violation);
  }

  public List<Dependency> getDependencies() {
    return dependencies;
  }

  public List<Measure> getMeasures() {
    return measures;
  }

  public Measure getMeasure(Metric metric) {
    for (Measure measure : measures) {
      if (measure.getMetric().equals(metric)) {
        return measure;
      }
    }
    return null;
  }

  public Resource<?> getResource() {
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
