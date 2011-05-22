/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sonar.api.batch.Event;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.design.Dependency;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasuresFilter;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.ProjectLink;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Violation;

/**
 * @author Matthijs Galesloot
 */
public class MockSensorContext implements SensorContext {

  private Measure measure;
  private final Map<Resource, List<Measure>> measures = new HashMap<Resource, List<Measure>>();
  private final List<Resource> resources = new ArrayList<Resource>();

  private final List<Violation> violations = new ArrayList<Violation>();

  public Event createEvent(Resource resource, String name, String description, String category, Date date) {
    // TODO Auto-generated method stub
    return null;
  }

  public void deleteEvent(Event event) {
    // TODO Auto-generated method stub
  }

  public void deleteLink(String key) {
    // TODO Auto-generated method stub
  }

  public Set<Dependency> getDependencies() {
    // TODO Auto-generated method stub
    return null;
  }

  public List<Event> getEvents(Resource resource) {
    // TODO Auto-generated method stub
    return null;
  }

  public Collection<Dependency> getIncomingDependencies(Resource to) {
    // TODO Auto-generated method stub
    return null;
  }

  public Measure getMeasure(Metric metric) {
    return measure;
  }

  public Measure getMeasure(Resource resource, Metric metric) {
    for (Resource r : measures.keySet()) {
      if (r.equals(resource)) {
        for (Measure m : measures.get(r)) {
          if (m.getMetric().equals(metric)) {
            return m;
          }
        }
      }
    }
    return null;
  }

  public <M> M getMeasures(MeasuresFilter<M> filter) {
    // TODO Auto-generated method stub
    return null;
  }

  public <M> M getMeasures(Resource resource, MeasuresFilter<M> filter) {
    // TODO Auto-generated method stub
    return null;
  }

  public int getNumResources() {
    return resources.size();
  }

  public Collection<Dependency> getOutgoingDependencies(Resource from) {
    // TODO Auto-generated method stub
    return null;
  }

  public List getViolations() {
    return violations;
  }

  public Dependency saveDependency(Dependency dependency) {
    // TODO Auto-generated method stub
    return null;
  }

  public void saveLink(ProjectLink link) {
    // TODO Auto-generated method stub

  }

  public Measure saveMeasure(Measure measure) {
    // TODO Auto-generated method stub
    return null;
  }

  public Measure saveMeasure(Metric metric, Double value) {
    this.measure = new Measure(metric, value);
    return measure;
  }

  public Measure saveMeasure(Resource resource, Measure measure) {
    if (measures.get(resource) == null) {
      measures.put(resource, new ArrayList<Measure>());
    }
    measures.get(resource).add(measure);
    return measure;
  }

  public Measure saveMeasure(Resource resource, Metric metric, Double value) {
    Measure m = new Measure(metric, value);
    return saveMeasure(resource, m);
  }

  public String saveResource(Resource resource) {
    resources.add(resource);
    return null;
  }

  public void saveSource(Resource resource, String source) {
    resources.add(resource);
  }

  public void saveViolation(Violation violation) {
    violations.add(violation);
  }

  public void saveViolations(Collection<Violation> violations) {
    // TODO Auto-generated method stub

  }

  public List<Resource> getResources() {
    return resources;
  }

  public boolean index(Resource resource) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean index(Resource resource, Resource parentReference) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isExcluded(Resource reference) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isIndexed(Resource reference, boolean acceptExcluded) {
    // TODO Auto-generated method stub
    return false;
  }

  public <R extends Resource> R getResource(R reference) {
    // TODO Auto-generated method stub
    return null;
  }

  public Resource getParent(Resource reference) {
    // TODO Auto-generated method stub
    return null;
  }

  public Collection<Resource> getChildren(Resource reference) {
    // TODO Auto-generated method stub
    return null;
  }

  public void saveViolation(Violation violation, boolean force) {
    // TODO Auto-generated method stub

  }
}