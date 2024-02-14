/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

import java.util.List;

@Rule(key = "S6850")
public class HeadingHasAccessibleContentCheck extends AbstractPageCheck {
    private final List<String> invalidAttributes = List.of(
            "aria-hidden"
    );

    private TagNode processedHeadingNode = null;

    private boolean hasTextContent = false;

    @Override
    public void startElement(TagNode node) {
        if (Helpers.isHeadingTag(node)) {
            processedHeadingNode = node;
            hasTextContent = false;

            if (hasAnInvalidAttribute(node)) {
                createViolation(node);
            }
        }

        super.startElement(node);
    }

    @Override
    public void endElement(TagNode node) {
        if (processedHeadingNode != null) {
            if (!hasTextContent) {
                createViolation(processedHeadingNode);
            }

            processedHeadingNode = null;
        }

        super.endElement(node);
    }

    @Override
    public void characters(TextNode textNode) {
        if (processedHeadingNode != null) {
            hasTextContent = !textNode.isBlank();
        }

        super.characters(textNode);
    }

    private boolean hasAnInvalidAttribute(TagNode node) {
        return node.getAttributes().stream()
                .map(Attribute::getName)
                .anyMatch(invalidAttributes::contains);
    }



    private void createViolation(TagNode node) {
        super.createViolation(node.getStartLinePosition(), "Headings must have content and the content must be accessible by a screen reader.");
    }
}
