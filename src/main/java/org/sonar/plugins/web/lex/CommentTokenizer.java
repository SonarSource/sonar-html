/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.web.lex;

import java.util.List;

import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.Node;

class CommentTokenizer extends AbstractTokenizer<List<Node>> {

  private final Boolean html;

  public CommentTokenizer(String startToken, String endToken, Boolean html) {
    super(startToken, endToken);

    this.html = html;
  }

  @Override
  Node createNode() {

    CommentNode node = new CommentNode();
    node.setHtml(html);
    return node;
  }
}
