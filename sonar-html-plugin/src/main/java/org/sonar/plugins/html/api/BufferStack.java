/*
 * SonarQube HTML Plugin :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.api;

import java.util.ArrayDeque;
import java.util.Deque;

public class BufferStack {
  Deque<StringBuffer> buffers = new ArrayDeque<>();

  public int getLevel() {
    return buffers.size();
  }

  public void start() {
    buffers.push(new StringBuffer());
  }

  public void write(String data) {
    if (!buffers.isEmpty()) {
      StringBuffer active = buffers.getFirst();

      active.append(data);
    }
  }

  public String getAndFlush() {
    String content = getContents();

    endAndFlush();

    return content;
  }

  public String getContents() {
    StringBuffer active = buffers.getFirst();

    return active != null ? active.toString() : "";
  }

  public void endAndFlush() {
    flush();

    buffers.pop();
  }

  public void flush() {
    StringBuffer active = buffers.pop();

    write(active.toString());

    buffers.push(new StringBuffer());
  }
}
