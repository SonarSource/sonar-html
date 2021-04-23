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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S1827")
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
    DEPRECATED = new HashMap<>();
    put("a", "charset", "coords", datafld, datasrc, "methods", "name", "shape", "urn");
    put("applet", datafld, datasrc);
    put("area", "nohref");
    put(
      "body",
      "alink", background, bgcolor, "link", "marginbottom", "marginheight", "marginleft", "marginright", "margintop", "marginwidth", "text", "vlink");
    put("br", "clear");
    put("button", datafld, dataformatas, datasrc);
    put("caption", align);
    put("col", align, "char", charoff, valign, width);
    put("div", align, datafld, dataformatas, datasrc);
    put("dl", compact);
    put("embed", align, hspace, "name", vspace);
    put("fieldset", datafld);
    put("form", "accept");
    put("frame", datafld, datasrc);
    put("h1", align);
    put("h2", align);
    put("h3", align);
    put("h4", align);
    put("h5", align);
    put("h6", align);
    put("head", "profile");
    put("hr", align, "color", "noshade", "size", width);
    put("html", "version");
    put("iframe", align, "allowtransparency", datafld, datasrc, "frameborder", hspace, "marginheight", "marginwidth", "scrolling", vspace);
    put("img", align, border, datafld, datasrc, hspace, "lowsrc", "name", vspace);
    put("input", align, datafld, dataformatas, datasrc, hspace, "ismap", "usemap", vspace);
    put("label", datafld, dataformatas, datasrc);
    put("legend", align, datafld, dataformatas, datasrc);
    put("li", "type");
    put("link", "charset", "methods", "target", "urn");
    put("marquee", datafld, dataformatas, datasrc);
    put("meta", "scheme");
    put(
      "object",
      align, "archive", border, "classid", "code", "codebase", "codetype", datafld, dataformatas, datasrc, "declare", hspace, "standby", vspace);
    put("ol", compact);
    put("option", datafld, dataformatas, datasrc, "name");
    put("p", align);
    put("param", datafld, "type", "valuetype");
    put("params", "type");
    put("pre", width);
    put("script", "event", "for", "language");
    put("select", datafld, dataformatas, datasrc);
    put("span", datafld, dataformatas, datasrc);
    put(
      "table",
      align, background, bgcolor, "bordercolor", "cellpadding", "cellspacing",
        dataformatas, "datapagesize", datasrc, "frame", "rules", "summary", width);
    put("text", "body");
    put("textarea", datafld, datasrc);
    put("tbody", align, background, "char", charoff, valign);
    put("thead", align, background, bgcolor, "char", charoff, valign);
    put("tfoot", align, background, "char", charoff, valign);
    put("td", align, "axis", background, bgcolor, "char", charoff, "height", "nowrap", "scope", valign, width);
    put("th", align, "axis", background, bgcolor, "char", charoff, "height", "nowrap", valign, width);
    put("tr", align, background, bgcolor, "char", charoff, valign);
    put("ul", compact, "type");
  }

  private static void put(String key, String... values) {
    DEPRECATED.put(key, new HashSet<>(Arrays.asList(values)));
  }

  @Override
  public void startElement(TagNode element) {
    String nodeName = element.getNodeName();
    String elementName = nodeName.toLowerCase(Locale.ROOT);
    Set<String> deprecatedAttributes = DEPRECATED.get(elementName);
    if (deprecatedAttributes != null) {
      List<Attribute> attributes = element.getAttributes();
      for (Attribute attribute : attributes) {
        if (isDeprecated(element, deprecatedAttributes, getOriginalAttributeName(attribute.getName()), attribute.getValue().toLowerCase(Locale.ROOT))) {
          createViolation(element, "Remove this deprecated \"" + attribute.getName() + "\" attribute.");
        }
      }
    }
  }

  private static boolean isDeprecated(TagNode element, Set<String> deprecatedAttributes, String attributeName, String attributeValue) {
    String elementName = element.getNodeName().toLowerCase(Locale.ROOT);
    if ("img".equals(elementName) && "border".equals(attributeName)) {
      return !"0".equals(attributeValue);
    } else if ("script".equals(elementName) && "language".equals(attributeName)) {
      return !"javascript".equals(attributeValue);
    } else if ("a".equals(elementName) && "name".equals(attributeName)) {
      String id = element.getPropertyValue("id");
      return id == null || id.isEmpty() || !id.equals(attributeValue);
    } else {
      return deprecatedAttributes.contains(attributeName);
    }
  }

  /**
   * Returns the original name of the attribute. So if a framework such as angular is used to set the property, such as '[src]=blalba' or '[attr.src]=blabla', the
   * method will strip the property name of '[' or '[attr.' and ']'.
   */
  private static String getOriginalAttributeName(String attributeName) {
    String attributeNameLower = attributeName.toLowerCase(Locale.ENGLISH);
    if (attributeNameLower.startsWith("[attr.") && attributeNameLower.endsWith("]")) {
      return attributeNameLower.substring("[attr.".length(), attributeName.length() - 1);
    }
    if (attributeNameLower.startsWith("[") && attributeNameLower.endsWith("]")) {
      return attributeNameLower.substring(1, attributeName.length() - 1);
    }
    return attributeNameLower;
  }

}
