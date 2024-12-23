/*
 * SonarQube HTML
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
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
