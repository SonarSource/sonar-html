/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2021 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Rule(key = "S1829")
public class AbsoluteURICheck extends AbstractPageCheck {

  private static final String DEFAULT_ATTRIBUTES = "a.href,applet.codebase,area.href,base.href,blockquote.cite,body.background,del.cite,form.action,frame.longdesc,frame.src," +
    "head.profile,iframe.longdesc,iframe.src,img.longdesc,img.src,img.usemap,input.src,input.usemap,ins.cite,link.href," +
    "object.classid,object.codebase,object.data,object.usemap,q.cite,script.src,audio.src,button.formaction,command.icon,embed.src," +
    "html.manifest,input.formaction,source.src,video.poster,video.src";

  private Matcher matcher = Pattern.compile("[A-Za-z0-9]*://.*").matcher("");

  @RuleProperty(
    key = "attributes",
    description = "Comma-separated list of tag.attributes to be checked for absolute URI.",
    defaultValue = DEFAULT_ATTRIBUTES,
    type = "TEXT")
  public String attributes = DEFAULT_ATTRIBUTES;

  private QualifiedAttribute[] attributesArray;

  @Override
  public void startDocument(List<Node> nodes) {
    this.attributesArray = parseAttributes(attributes);
  }

  @Override
  public void startElement(TagNode element) {
    for (Attribute a : getMatchingAttributes(element, attributesArray)) {
      if (matcher.reset(a.getValue()).matches()) {
        createViolation(
          element, "Replace this absolute URI \"" + a.getName() + "\" with a relative one, or move this absolute URI to a configuration file.");
      }
    }
  }

}
