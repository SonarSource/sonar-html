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
 * Callback opt-in for checks that want to receive visitor events
 * ({@code startElement}, {@code endElement}, {@code characters}, …) for HTML
 * nodes extracted from PHP string literals by {@code PhpEmbeddedHtmlExtractor}.
 *
 * <p><b>What this controls:</b> visitor callbacks only. A check that does
 * <em>not</em> implement this interface will not receive {@code startElement}
 * etc. for nodes whose {@link org.sonar.plugins.html.node.Node#isEmbedded()}
 * flag is {@code true}.
 *
 * <p><b>What this does not control:</b> AST visibility. The full node list —
 * including embedded nodes — is still passed to every check's
 * {@link org.sonar.plugins.html.visitor.DefaultNodeVisitor#startDocument(java.util.List)},
 * and the parent/child hierarchy remains intact. Non-opted-in checks can
 * therefore still observe embedded nodes by iterating the {@code startDocument}
 * list or calling {@link org.sonar.plugins.html.node.Node#getChildren()}, and
 * can call {@link org.sonar.plugins.html.node.Node#isEmbedded()} to filter when
 * needed.
 *
 * <p>Only standalone single-element or single-attribute checks should opt in.
 * Checks that reconstruct a DOM from the full node list or that rely on sibling
 * or ancestor relationships should <em>not</em> opt in; they already reach
 * embedded nodes through the tree.
 */
public interface EmbeddedHtmlCheck {
}
