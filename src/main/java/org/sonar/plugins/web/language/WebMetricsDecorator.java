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

package org.sonar.plugins.web.language;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.plugins.web.WebMetrics;
import org.sonar.plugins.web.WebUtils;

/**
 * The WebMetricsDecorator calculates metrics for web files.
 * 
 * @author Matthijs Galesloot
 */
public class WebMetricsDecorator implements Decorator {

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    return Web.INSTANCE.equals(project.getLanguage());
  }

  @DependedUpon
  public List<Metric> generatesLinesMetrics() {
    return Arrays.asList(WebMetrics.JAVASCRIPT_LINES, WebMetrics.HTML_LINES, WebMetrics.CSS_LINES);
  }

  public void decorate(Resource resource, DecoratorContext context) {

    if (ResourceUtils.isSpace(resource) || ResourceUtils.isProject(resource)) {

      Integer javaScriptLinesOfCode = 0;
      Integer htmlLinesOfCode = 0;
      Integer cssLinesOfCode = 0;

      for (DecoratorContext child : context.getChildren()) {

        if (child.getResource() instanceof WebFile) {
          if (child.getMeasure(CoreMetrics.LINES) == null) {
            WebUtils.LOG.warn(child.getResource().getName() + ": no LOC" );
          } else {
            WebFile webFile = (WebFile) child.getResource();
            switch (webFile.getFileType()) {
              case Css:
                cssLinesOfCode += child.getMeasure(CoreMetrics.LINES).getIntValue();
                break;
              case Html:
                htmlLinesOfCode += child.getMeasure(CoreMetrics.LINES).getIntValue();
                break;
              case JavaScript:
                javaScriptLinesOfCode += child.getMeasure(CoreMetrics.LINES).getIntValue();
                break;
            }
          } 
        } else {

          javaScriptLinesOfCode += child.getMeasure(WebMetrics.JAVASCRIPT_LINES).getIntValue();
          htmlLinesOfCode += child.getMeasure(WebMetrics.HTML_LINES).getIntValue();
          cssLinesOfCode += child.getMeasure(WebMetrics.CSS_LINES).getIntValue();
        }
      }
      context.saveMeasure(WebMetrics.JAVASCRIPT_LINES, javaScriptLinesOfCode.doubleValue());
      context.saveMeasure(WebMetrics.HTML_LINES, htmlLinesOfCode.doubleValue());
      context.saveMeasure(WebMetrics.CSS_LINES, htmlLinesOfCode.doubleValue());
    }
  }
}
