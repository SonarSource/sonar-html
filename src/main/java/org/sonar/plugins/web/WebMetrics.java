/*
 * Copyright (C) 2010
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

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

public class WebMetrics implements Metrics {

  public static final Metric JAVASCRIPT_LINES = new Metric("javascript_loc", "Javascipt LOC", "Javascript Lines of code",
      Metric.ValueType.INT, Metric.DIRECTION_NONE, false, CoreMetrics.DOMAIN_SIZE);

  public static final Metric HTML_LINES = new Metric("html_loc", "HTML LOC", "HTML Lines of code", Metric.ValueType.INT,
      Metric.DIRECTION_NONE, false, CoreMetrics.DOMAIN_SIZE);
  
  public static final Metric CSS_LINES = new Metric("css_loc", "CSS LOC", "CSS Lines of code", Metric.ValueType.INT,
      Metric.DIRECTION_NONE, false, CoreMetrics.DOMAIN_SIZE);

  public List<Metric> getMetrics() {
    return Arrays.asList(HTML_LINES, CSS_LINES, JAVASCRIPT_LINES);
  }
}
