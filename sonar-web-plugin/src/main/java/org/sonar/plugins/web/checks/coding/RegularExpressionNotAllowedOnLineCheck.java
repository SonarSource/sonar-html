/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2016 SonarSource SA and Matthijs Galesloot
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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.sonar.api.utils.SonarException;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.CharsetAwareVisitor;
import org.sonar.squidbridge.annotations.NoSqale;
import org.sonar.squidbridge.annotations.RuleTemplate;

import com.google.common.io.Files;

@Rule(
  key = "RegularExpressionNotAllowedOnLineCheck", 
  name = "Regular expression \"regex\" not allowed on web page",
  priority = Priority.MAJOR, tags = {
  RuleTags.CONVENTION })
@RuleTemplate
@NoSqale
public final class RegularExpressionNotAllowedOnLineCheck extends AbstractPageCheck implements CharsetAwareVisitor {

	@RuleProperty(
      key = "regex", 
      description = "Single line regular expression to prohibit on web pages")
	public String regex = "";
	
	private Charset charset;

	@Override
	public void setCharset(Charset charset) {
	   this.charset = charset;
	}

	@Override
	public void startDocument(List<Node> nodes) {
		// if user forgot to enter a regular expression, don't throw a violation on every line
		if (! regex.isEmpty()) {
			List<String> lines;
			try {
				lines = Files.readLines(getWebSourceCode().inputFile().file(), charset);
			} catch (IOException e) {
				throw new SonarException(e);
			}
			for (int i = 0; i < lines.size(); i++) {
				// only support matching text within a single line to facilitate identifying line number
				if (lines.get(i).matches("^.*" + regex + ".*$")) {
					createViolation(i + 1, "Replace all instances of regular expression: " + regex);
				}
			}
		}
	}

}
