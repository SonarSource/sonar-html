/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks;

/**
 * Marker interface for checks that should also run against HTML extracted from
 * PHP string literals by {@code PhpEmbeddedHtmlExtractor}. Only standalone
 * single-element / single-attribute checks should opt in — checks that rely on
 * surrounding DOM, sibling relationships, or text content cannot trust the
 * fragmented re-lex output and must stay off the embedded path.
 */
public interface EmbeddedHtmlCheck {
}
