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

package org.sonar.plugins.web.checks.comments;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.squid.measures.Metric;
import org.sonar.squid.recognizer.CodeRecognizer;
import org.sonar.squid.recognizer.ContainsDetector;
import org.sonar.squid.recognizer.Detector;
import org.sonar.squid.recognizer.EndWithDetector;
import org.sonar.squid.recognizer.LanguageFootprint;
import org.sonar.squid.text.Source;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Finds commented-out code.
 *
 */
@Rule(key = "AvoidCommentedOutCodeCheck", priority = Priority.MAJOR)
public class AvoidCommentedOutCodeCheck extends AbstractPageCheck {

  private static final Logger LOG = LoggerFactory.getLogger(AvoidCommentedOutCodeCheck.class);

  private static final double CODE_RECOGNIZER_SENSITIVITY = 0.9;

  @Override
  public void comment(CommentNode node) {
    if (node.isHtml()) {
      Source source = analyseSourceCode(node.getCode());
      int commentedOutLocs = source.getMeasure(Metric.COMMENTED_OUT_CODE_LINES);
      if (commentedOutLocs > 0) {
        createViolation(node.getStartLinePosition(), "Some commented-out code was detected.");
      }
    }
  }

  private Source analyseSourceCode(String commentText) {
    Source result = null;
    StringReader reader = null;
    try {
      reader = new StringReader(commentText);
      // the last string ("") is necessary because the Source class needs to consider that every line is a comment, not only the standard
      // "/*", "//", ...etc.
      result = new Source(reader, new CodeRecognizer(CODE_RECOGNIZER_SENSITIVITY, new WebFootprint()), "");
    } catch (Exception e) {
      LOG.error("Error while parsing comment: " + commentText, e);
    } finally {
      IOUtils.closeQuietly(reader);
    }

    return result;
  }

  class WebFootprint implements LanguageFootprint {

    public Set<Detector> getDetectors() {
      Set<Detector> detectors = new HashSet<Detector>();
      detectors.add(new ContainsDetector(0.7, "=\"", "='"));
      detectors.add(new ContainsDetector(0.8, "/>", "</", "<%", "%>"));
      detectors.add(new EndWithDetector(0.9, '>'));
      return detectors;
    }
  }

}
