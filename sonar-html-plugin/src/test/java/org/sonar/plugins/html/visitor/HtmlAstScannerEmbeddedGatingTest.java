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
package org.sonar.plugins.html.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.plugins.html.checks.EmbeddedHtmlCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlAstScannerEmbeddedGatingTest {

  @Test
  void embeddedNodesReachOnlyOptInVisitors() {
    TagNode plain = newTag("plain", false);
    TagNode embedded = newTag("embedded", true);

    RecordingVisitor optedOut = new RecordingVisitor();
    RecordingEmbeddedVisitor optedIn = new RecordingEmbeddedVisitor();

    HtmlAstScanner scanner = new HtmlAstScanner(Collections.emptyList());
    scanner.addVisitor(optedOut);
    scanner.addVisitor(optedIn);
    scanner.scan(List.of(plain, embedded), newSourceCode());

    assertThat(optedOut.starts).containsExactly("plain");
    assertThat(optedIn.starts).containsExactly("plain", "embedded");
  }

  @Test
  void nonOptInCheckSeesEmbeddedNodesViaStartDocumentButNotViaCallbacks() {
    TagNode plain = newTag("plain", false);
    TagNode embedded = newTag("embedded", true);

    List<Node> capturedByStartDocument = new ArrayList<>();
    RecordingVisitor nonOptIn = new RecordingVisitor() {
      @Override
      public void startDocument(List<Node> nodes) {
        capturedByStartDocument.addAll(nodes);
      }
    };

    HtmlAstScanner scanner = new HtmlAstScanner(Collections.emptyList());
    scanner.addVisitor(nonOptIn);
    scanner.scan(List.of(plain, embedded), newSourceCode());

    // Full node list — including embedded — reaches every check via startDocument.
    assertThat(capturedByStartDocument).containsExactly(plain, embedded);
    // Visitor callbacks (startElement) are gated: only the plain node fires.
    assertThat(nonOptIn.starts).containsExactly("plain");
  }

  private static TagNode newTag(String name, boolean embedded) {
    TagNode tag = new TagNode();
    tag.setNodeName(name);
    tag.setCode("<" + name + ">");
    tag.setEmbedded(embedded);
    return tag;
  }

  private static HtmlSourceCode newSourceCode() {
    return new HtmlSourceCode(
      new TestInputFileBuilder("fv", "probe.html")
        .setLanguage("web")
        .build());
  }

  private static class RecordingVisitor extends DefaultNodeVisitor {
    final List<String> starts = new ArrayList<>();

    @Override
    public void startElement(TagNode element) {
      starts.add(element.getNodeName());
    }
  }

  private static class RecordingEmbeddedVisitor extends RecordingVisitor implements EmbeddedHtmlCheck {
  }
}
