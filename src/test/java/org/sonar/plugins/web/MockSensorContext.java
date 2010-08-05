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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
class MockSensorContext implements SensorContext {

  private int numResources;
  private Measure measure;

  public int getNumResources() {
    return numResources;
  }

  private final List violations = new ArrayList<Violation>();

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
    // TODO Auto-generated method stub
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

  public Collection<Dependency> getOutgoingDependencies(Resource from) {
    // TODO Auto-generated method stub
    return null;
  }

  public Resource getResource(Resource resource) {
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
    // TODO Auto-generated method stub
    return null;
  }

  public Measure saveMeasure(Resource resource, Metric metric, Double value) {
    // TODO Auto-generated method stub
    return null;
  }

  public String saveResource(Resource resource) {
    numResources ++;
    return null;
  }

  public void saveSource(Resource resource, String source) {
    numResources ++;
  }

  public void saveViolation(Violation violation) {
    violations.add(violation);
  }

  public void saveViolations(Collection<Violation> violations) {
    // TODO Auto-generated method stub

  }
}