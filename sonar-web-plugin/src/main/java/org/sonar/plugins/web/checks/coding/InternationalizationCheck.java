/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2017 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.checks.coding;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

@Rule(key = "InternationalizationCheck")
public class InternationalizationCheck extends AbstractPageCheck {

	private static final String PUNCTUATIONS_AND_SPACE = " \t\n\r|-%:,.?!/,'\"";
	private static final String DEFAULT_ATTRIBUTES = "outputLabel.value, outputText.value";

	@RuleProperty(key = "attributes", description = "Attributes", defaultValue = DEFAULT_ATTRIBUTES)
	public String attributes = DEFAULT_ATTRIBUTES;

	@RuleProperty(key = "ignoredContentRegex", description = "Text content matching this expression will be ignored", defaultValue = StringUtils.EMPTY)
	public String ignoredContentRegex;

	private QualifiedAttribute[] attributesArray;
	private Pattern ignoredContentPattern = null;

	@Override
	public void startDocument(List<Node> nodes) {
		this.attributesArray = this.parseAttributes(this.attributes);

		if (!StringUtils.isEmpty(this.ignoredContentRegex)) {
			this.ignoredContentPattern = Pattern.compile(this.ignoredContentRegex);
		}
	}

	@Override
	public void characters(TextNode textNode) {
		if (!this.textIsValid(textNode.getCode())) {
			this.createViolation(textNode.getStartLinePosition(), "Define this label in the resource bundle.");
		}
	}

	@Override
	public void startElement(TagNode element) {
		if (this.attributesArray.length > 0) {
			for (QualifiedAttribute attribute : this.attributesArray) {
				if (this.notValid(element, attribute)) {
					return;
				}
			}
		}
	}

	private boolean notValid(TagNode element, QualifiedAttribute attribute) {
		if (element.equalsElementName(attribute.getNodeName())) {
			String value = element.getAttribute(attribute.getAttributeName());
			if (!this.textIsValid(value)) {
				this.createViolation(element.getStartLinePosition(), "Define this label in the resource bundle.");
				return true;
			}
		}
		return false;
	}

	private boolean textIsValid(String text) {
		String trimmed = StringUtils.trim(text);
		if (!StringUtils.isEmpty(trimmed) && !this.isUnifiedExpression(trimmed) && !isPunctuationOrSpace(trimmed)
				&& !this.isIgnoredByRegex(trimmed)) {
			return false;
		} else {
			// empty text, ok
			return true;
		}
	}

	private static boolean isPunctuationOrSpace(String value) {
		return StringUtils.containsAny(value, PUNCTUATIONS_AND_SPACE);
	}

	private boolean isIgnoredByRegex(String value) {
		if (this.ignoredContentPattern != null && this.ignoredContentPattern.matcher(value).matches()) {
			return true;
		}
		return false;
	}

}
