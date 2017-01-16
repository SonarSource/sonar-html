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
package org.sonar.plugins.web.node;

import org.apache.commons.lang.StringUtils;

/**
 * Defines a text node.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public class TextNode extends Node {

  public TextNode() {
    super(NodeType.TEXT);
  }

  public boolean isBlank() {
    return StringUtils.isBlank(getCode());
  }
}
