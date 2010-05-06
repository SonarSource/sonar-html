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

package org.sonar.plugins.web.rules.xml;

import org.sonar.api.rules.Iso9126RulesCategories;
import org.sonar.api.rules.RulesCategory;

import com.thoughtworks.xstream.XStream;

public class RulesUtils {

  public static Ruleset buildRuleSetFromXml(String configuration) {
    XStream xstream = new XStream();
    xstream.processAnnotations(Ruleset.class);
    xstream.processAnnotations(RuleDefinition.class);
    xstream.processAnnotations(Property.class);
    xstream.aliasSystemAttribute("ref", "class");

    return (Ruleset) xstream.fromXML(configuration);
  }

  public static RulesCategory matchRuleCategory(String category) {
    for (RulesCategory ruleCategory : Iso9126RulesCategories.ALL) {
      if (ruleCategory.getName().equalsIgnoreCase(category)) {
        return ruleCategory;
      }
    }
    throw new IllegalArgumentException("Unexpected category name " + category);
  }

  public static String buildXmlFromRuleset(Ruleset tree) {
    XStream xstream = new XStream();
    xstream.processAnnotations(Ruleset.class);
    xstream.processAnnotations(RuleDefinition.class);
    xstream.processAnnotations(Property.class);
    return addHeaderToXml(xstream.toXML(tree));
  }

  private static String addHeaderToXml(String xmlModules) {
    String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    return header + xmlModules;
  }

}
