/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.toetstool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class CssFinderTest {

  private static final String testfile = "src/test/resources/org/sonar/plugins/web/toetstool/a.html";

  @Test
  public void testCssFinder() {

    File file = new File(testfile);
    assertTrue(file.exists());
    List<String> styleSheets = new CssFinder().parseWebFile(file);
    assertEquals(3, styleSheets.size());
  }
}