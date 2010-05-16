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

package org.sonar.plugins.web.analyzers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.web.WebUtils;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TextNode;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 * 
 *         Based on Running Kaper-Rabin algorithm.
 * 
 * @see http://www.ncbi.nlm.nih.gov/pubmed/7584464
 */
public final class DuplicationDetector {

  private static final class DuplicationsData {

    protected double duplicatedBlocks;
    protected final Set<Integer> duplicatedLines = new HashSet<Integer>();
    private final List<StringBuilder> duplicationXMLEntries = new ArrayList<StringBuilder>();
    private final Resource<?> resource;

    private DuplicationsData(Resource<?> resource) {
      this.resource = resource;
    }

    protected void addMatch(Match match) {
      Resource<?> targetResource = match.targetElement.resource;
      int targetDuplicationStartLine = match.targetDuplicationStartLine;
      int duplicationStartLine = match.duplicationStartLine;
      int duplicatedLines = match.duplicatedLines;

      StringBuilder xml = new StringBuilder();
      xml.append("<duplication lines=\"").append(duplicatedLines).append("\" start=\"").append(duplicationStartLine).append(
          "\" target-start=\"").append(targetDuplicationStartLine).append("\" target-resource=\"").append(targetResource.getKey()).append(
          "\"/>");

      duplicationXMLEntries.add(xml);

      int duplicatedLinesBefore = this.duplicatedLines.size();
      for (int duplicatedLine = duplicationStartLine; duplicatedLine < duplicationStartLine + duplicatedLines; duplicatedLine++) {
        this.duplicatedLines.add(duplicatedLine);
      }
      if (duplicatedLinesBefore != this.duplicatedLines.size()) {
        this.duplicatedBlocks++;
      }
    }

    private String getDuplicationXMLData() {
      StringBuilder duplicationXML = new StringBuilder("<duplications>");
      for (StringBuilder xmlEntry : duplicationXMLEntries) {
        duplicationXML.append(xmlEntry);
      }
      duplicationXML.append("</duplications>");
      return duplicationXML.toString();
    }

    protected void save(SensorContext context) {

      WebUtils.LOG.debug(resource.getName() + " duplicates: " + duplicatedBlocks + ", " + duplicatedLines.size());
      context.saveMeasure(resource, CoreMetrics.DUPLICATED_FILES, 1d);
      context.saveMeasure(resource, CoreMetrics.DUPLICATED_LINES, (double) duplicatedLines.size());
      context.saveMeasure(resource, CoreMetrics.DUPLICATED_BLOCKS, duplicatedBlocks);
      context.saveMeasure(resource, new Measure(CoreMetrics.DUPLICATIONS_DATA, getDuplicationXMLData()));
    }
  }

  private static class Element {

    private int groupHashCode;
    private int hashCode;
    private int linePosition;
    private int linesOfCode;
    private boolean marked;
    private Element next;
    private Resource<?> resource;
  }

  private static class Match {

    private final Element duplicatedElement;

    private int duplicatedLines;
    private final int duplicationStartLine;
    private int numberOfMatchingElements;
    private final int targetDuplicationStartLine;
    private final Element targetElement;

    public Match(Element duplicatedElement, Element targetElement) {
      this.duplicatedElement = duplicatedElement;
      this.targetElement = targetElement;
      duplicationStartLine = duplicatedElement.linePosition;
      targetDuplicationStartLine = targetElement.linePosition;

      calculateMatchingElements();
    }

    private void calculateMatchingElements() {
      Element f1 = duplicatedElement;
      Element f2 = targetElement;
      Element lastMatch = null;

      while (f1 != null && f2 != null && f1.hashCode == f2.hashCode) {
        numberOfMatchingElements++;
        lastMatch = f1;
        f1 = f1.next;
        f2 = f2.next;
      }
      if (lastMatch != null) {
        duplicatedLines = lastMatch.linePosition + lastMatch.linesOfCode - duplicatedElement.linePosition;
      }
    }

    public void markTargetElements() {
      Element f2 = targetElement;
      for (int i = 0; i < numberOfMatchingElements; i++) {
        f2.marked = true;
        f2 = f2.next;
      }
    }
  }

  private final static int minimumTokens = 5;

  private final List<Element> elements = new ArrayList<Element>();

  public void addTokens(List<Node> nodeList, WebSourceCode sourceCode) {

    int startElementIndex = elements.size();
    Element lastElement = null;

    for (int i = 0; i < nodeList.size(); i++) {
      Node node = nodeList.get(i);

      // skip white space
      switch (node.getNodeType()) {
        case Text:
          if (((TextNode) node).isBlank()) {
            continue;
          }
          break;
        case Comment:
          continue;
      }

      Element element = new Element();
      element.hashCode = node.getCode().hashCode();
      element.resource = sourceCode.getResource();
      element.linePosition = node.getStartLinePosition();
      element.linesOfCode = node.getLinesOfCode();

      if (lastElement != null) {
        lastElement.next = element;
      }
      if (i < nodeList.size() - minimumTokens) {
        elements.add(element);
      }

      // calculate sum hashCode for groups with minimumTokens size
      int last = elements.size() - 1;
      for (int j = 0; j < minimumTokens && last - j >= startElementIndex; j++) {
        Element groupElement = elements.get(last - j);
        groupElement.groupHashCode += element.hashCode;
      }

      lastElement = element;
    }
  }

  public void analyse(SensorContext context) {

    WebUtils.LOG.debug("Analyse " + elements.size() + " elements for duplication");

    if (elements.size() > 0) {
      // sort elements on groupHashValue so we can easily find the potentential matches
      Collections.sort(elements, new Comparator<Element>() {

        public int compare(Element o1, Element o2) {
          return o1.groupHashCode - o2.groupHashCode;
        }
      });

      Map<Resource, DuplicationsData> duplicationsData = new HashMap<Resource, DuplicationsData>();
      findMatches(elements, duplicationsData);

      for (DuplicationsData data : duplicationsData.values()) {
        data.save(context);
      }
    }
  }

  /**
   * @param elements
   * @param duplicationsData
   * @return
   */
  private void findMatches(List<Element> elements, Map<Resource, DuplicationsData> duplicationsData) {
    final List<Match> matches = new ArrayList<Match>();

    Element previousElement = elements.get(0);

    for (int i = 1; i < elements.size(); i++) {
      Element current = elements.get(i);
      if (current.groupHashCode == previousElement.groupHashCode) {

        Match match = new Match(previousElement, current);
        if (match.numberOfMatchingElements > minimumTokens) {
          matches.add(match);
        }

      } else {
        previousElement = current;
      }
    }

    // sort ASC on numberOfMatchingElements, so we get the longest match on top
    Collections.sort(matches, new Comparator<Match>() {

      public int compare(Match m1, Match m2) {
        return m2.numberOfMatchingElements - m1.numberOfMatchingElements;
      }
    });

    for (Match match : matches) {

      if ( !match.targetElement.marked) {
        WebUtils.LOG.debug(match.numberOfMatchingElements + "");

        processDuplication(duplicationsData, match);
      }
      match.markTargetElements();
    }
    WebUtils.LOG.debug("Found " + matches.size() + " matches");

  }

  private void processDuplication(Map<Resource, DuplicationsData> fileContainer, Match match) {

    DuplicationsData data = fileContainer.get(match.duplicatedElement.resource);
    if (data == null) {
      data = new DuplicationsData(match.duplicatedElement.resource);
      fileContainer.put(match.duplicatedElement.resource, data);
    }
    data.addMatch(match);
  }
}