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
