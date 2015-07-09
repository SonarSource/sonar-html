/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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
package org.sonar.plugins.web.checks.sonar;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Rule(
  key = "S1827",
  priority = Priority.MAJOR,
  name = "Attributes deprecated in HTML5 should not be used",
  tags = {RuleTags.HTML5, RuleTags.OBSOLETE})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.LANGUAGE_RELATED_PORTABILITY)
@SqaleConstantRemediation("5min")
public class DeprecatedAttributesInHtml5Check extends AbstractPageCheck {

  private static final Map<String, Set<String>> DEPRECATED;

  static {
    final String align = "align";
    final String background = "background";
    final String bgcolor = "bgcolor";
    final String vspace = "vspace";
    final String width = "width";
    final String charoff = "charoff";
    final String hspace = "hspace";
    final String dataformatas = "dataformatas";
    final String datafld = "datafld";
    final String datasrc = "datasrc";
    final String compact = "compact";
    final String border = "border";
    final String valign = "valign";
    ImmutableMap.Builder<String, Set<String>> builder = ImmutableMap.builder();
    builder.put("a", ImmutableSet.of("charset", "coords", datafld, datasrc, "methods", "name", "shape", "urn"));
    builder.put("applet", ImmutableSet.of(datafld, datasrc));
    builder.put("area", ImmutableSet.of("nohref"));
    builder.put(
      "body",
      ImmutableSet.of("alink", background, bgcolor, "link", "marginbottom", "marginheight", "marginleft", "marginright", "margintop", "marginwidth", "text", "vlink"));
    builder.put("br", ImmutableSet.of("clear"));
    builder.put("button", ImmutableSet.of(datafld, dataformatas, datasrc));
    builder.put("caption", ImmutableSet.of(align));
    builder.put("col", ImmutableSet.of(align, "char", charoff, valign, width));
    builder.put("div", ImmutableSet.of(align, datafld, dataformatas, datasrc));
    builder.put("dl", ImmutableSet.of(compact));
    builder.put("embed", ImmutableSet.of(align, hspace, "name", vspace));
    builder.put("fieldset", ImmutableSet.of(datafld));
    builder.put("form", ImmutableSet.of("accept"));
    builder.put("frame", ImmutableSet.of(datafld, datasrc));
    builder.put("h1", ImmutableSet.of(align));
    builder.put("h2", ImmutableSet.of(align));
    builder.put("h3", ImmutableSet.of(align));
    builder.put("h4", ImmutableSet.of(align));
    builder.put("h5", ImmutableSet.of(align));
    builder.put("h6", ImmutableSet.of(align));
    builder.put("head", ImmutableSet.of("profile"));
    builder.put("hr", ImmutableSet.of(align, "color", "noshade", "size", width));
    builder.put("html", ImmutableSet.of("version"));
    builder.put("iframe", ImmutableSet.of(align, "allowtransparency", datafld, datasrc, "frameborder", hspace, "marginheight", "marginwidth", "scrolling", vspace));
    builder.put("img", ImmutableSet.of(align, border, datafld, datasrc, hspace, "lowsrc", "name", vspace));
    builder.put("input", ImmutableSet.of(align, datafld, dataformatas, datasrc, hspace, "ismap", "usemap", vspace));
    builder.put("label", ImmutableSet.of(datafld, dataformatas, datasrc));
    builder.put("legend", ImmutableSet.of(align, datafld, dataformatas, datasrc));
    builder.put("li", ImmutableSet.of("type"));
    builder.put("link", ImmutableSet.of("charset", "methods", "target", "urn"));
    builder.put("marquee", ImmutableSet.of(datafld, dataformatas, datasrc));
    builder.put("meta", ImmutableSet.of("scheme"));
    builder.put(
      "object",
      ImmutableSet.of(align, "archive", border, "classid", "code", "codebase", "codetype", datafld, dataformatas, datasrc, "declare", hspace, "standby", vspace));
    builder.put("ol", ImmutableSet.of(compact));
    builder.put("option", ImmutableSet.of(datafld, dataformatas, datasrc, "name"));
    builder.put("p", ImmutableSet.of(align));
    builder.put("param", ImmutableSet.of(datafld, "type", "valuetype"));
    builder.put("params", ImmutableSet.of("type"));
    builder.put("pre", ImmutableSet.of(width));
    builder.put("script", ImmutableSet.of("event", "for", "langauge"));
    builder.put("select", ImmutableSet.of(datafld, dataformatas, datasrc));
    builder.put("span", ImmutableSet.of(datafld, dataformatas, datasrc));
    builder.put(
      "table",
      ImmutableSet.of(align, background, bgcolor, "bordercolor", "cellpadding", "cellspacing",
        dataformatas, "datapagesize", datasrc, "frame", "rules", "summary", width));
    builder.put("text", ImmutableSet.of("body"));
    builder.put("textarea", ImmutableSet.of(datafld, datasrc));
    builder.put("tbody", ImmutableSet.of(align, background, "char", charoff, valign));
    builder.put("thead", ImmutableSet.of(align, background, bgcolor, "char", charoff, valign));
    builder.put("tfoot", ImmutableSet.of(align, background, "char", charoff, valign));
    builder.put("td", ImmutableSet.of(align, "axis", background, bgcolor, "char", charoff, "height", "nowrap", "scope", valign, width));
    builder.put("th", ImmutableSet.of(align, "axis", background, bgcolor, "char", charoff, "height", "nowrap", valign, width));
    builder.put("tr", ImmutableSet.of(align, background, bgcolor, "char", charoff, valign));
    builder.put("ul", ImmutableSet.of(compact, "type"));
    DEPRECATED = builder.build();
  }

  @Override
  public void startElement(TagNode element) {
    String elementName = element.getNodeName().toLowerCase();
    Set<String> deprecatedAttributes = DEPRECATED.get(elementName);
    if (deprecatedAttributes != null) {
      List<Attribute> attributes = element.getAttributes();
      for (Attribute attribute : attributes) {
        if (isDeprecated(element, deprecatedAttributes, attribute.getName().toLowerCase(), attribute.getValue().toLowerCase())) {
          createViolation(element.getStartLinePosition(), "Remove this deprecated \"" + attribute.getName() + "\" attribute.");
        }
      }
    }
  }

  private static boolean isDeprecated(TagNode element, Set<String> deprecatedAttributes, String attributeName, String attributeValue) {
    String elementName = element.getNodeName().toLowerCase();
    if ("img".equals(elementName) && "border".equals(attributeName)) {
      return !"0".equals(attributeValue);
    } else if ("script".equals(elementName) && "language".equals(attributeName)) {
      return !"javascript".equals(attributeValue);
    } else if ("a".equals(elementName) && "name".equals(attributeName)) {
      String id = element.getAttribute("id");
      return Strings.isNullOrEmpty(id) || !id.equals(attributeValue);
    } else {
      return deprecatedAttributes.contains(attributeName.toLowerCase());
    }
  }

}
