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
package org.sonar.plugins.web.rules;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.rules.xml.Profile;
import org.sonar.api.rules.xml.Property;
import org.sonar.api.rules.xml.Rule;

import com.thoughtworks.xstream.XStream;

/**
 * Export the rules configuration as an XML file.
 * 
 * @author Matthijs Galesloot
 * since 1.0
 */
public final class ProfileXmlParser {

  private XStream getXStream() {
    XStream xstream = new XStream();
    xstream.processAnnotations(Profile.class);
    xstream.processAnnotations(Rule.class);
    xstream.processAnnotations(Property.class);
    return xstream;
  }

  public String exportConfiguration(RulesProfile activeProfile) {

    XStream xstream = getXStream();
    xstream.setClassLoader(getClass().getClassLoader());
    Profile profile = new Profile();
    profile.setLanguage(activeProfile.getLanguage());
    profile.setName(activeProfile.getName());
    List<Rule> rules =  new ArrayList<Rule>();
    for (ActiveRule activeRule : activeProfile.getActiveRules()) {
      Rule rule = new Rule(activeRule.getRule().getKey());
      rule.setPriority(activeRule.getPriority().name());
      rules.add(rule);
      if (activeRule.getActiveRuleParams() != null) {
        for (ActiveRuleParam param : activeRule.getActiveRuleParams()) {
          Property property = new Property(param.getRuleParam().getKey(), param.getValue());
          rule.addProperty(property);
        }
      }
    }
    profile.setRules(rules);
    return xstream.toXML(profile);
  }
}