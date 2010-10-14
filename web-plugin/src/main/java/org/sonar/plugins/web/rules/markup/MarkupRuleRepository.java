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

package org.sonar.plugins.web.rules.markup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.RulesCategory;
import org.sonar.check.IsoCategory;
import org.sonar.plugins.web.language.Web;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public final class MarkupRuleRepository extends RuleRepository {

  private static final int RULENAME_MAX_LENGTH = 192;

  private static final String ALL_RULES = "org/sonar/plugins/web/rules/markup/rules.xml";

  public static final String REPOSITORY_NAME = "W3C Markup Validation";
  public static final String REPOSITORY_KEY = "W3CMarkupValidation";

  public MarkupRuleRepository() {
    super(MarkupRuleRepository.REPOSITORY_KEY, Web.KEY);
    setName(MarkupRuleRepository.REPOSITORY_NAME);
  }

  @XStreamAlias("rules")
  public class HtmlMarkupRules {

    @XStreamImplicit(itemFieldName = "rule")
    public List<HtmlMarkupRule> rules;
  }

  @XStreamAlias("rule")
  public class HtmlMarkupRule {

    private String key;
    private String remark;
    private String explanation;

    public void setKey(String key) {
      this.key = key;
    }

    public String getKey() {
      return key;
    }

    public void setRemark(String remark) {
      this.remark = remark;
    }

    public String getRemark() {
      return remark;
    }

    public void setExplanation(String explanation) {
      this.explanation = explanation;
    }

    public String getExplanation() {
      return explanation;
    }
  }

  @Override
  public List<Rule> createRules() {
    List<Rule> rules = new ArrayList<Rule>();

    XStream xstream = new XStream();
    xstream.setClassLoader(getClass().getClassLoader());
    xstream.processAnnotations(HtmlMarkupRules.class);
    HtmlMarkupRules markupRules = (HtmlMarkupRules) xstream.fromXML(getClass().getClassLoader().getResourceAsStream(ALL_RULES));
    for (HtmlMarkupRule htmlMarkupRule : markupRules.rules) {
      Rule rule = Rule.create(REPOSITORY_KEY, htmlMarkupRule.getKey(),
          StringUtils.abbreviate(htmlMarkupRule.getRemark(), RULENAME_MAX_LENGTH));
      if (htmlMarkupRule.getExplanation() != null) {
        rule.setDescription(StringEscapeUtils.escapeHtml(htmlMarkupRule.getExplanation()));
      }
      rule.setRulesCategory(RulesCategory.fromIsoCategory(IsoCategory.Usability));
      rules.add(rule);
    }
    return rules;
  }
}
