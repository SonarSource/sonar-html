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

package org.sonar.plugins.web.maven.toetstool;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sonar.plugins.web.maven.toetstool.ToetsToolValidator;

public class ToetstoolTest {

  private static final String testfile = "src/test/resources/org/sonar/plugins/web/toetstool/a.html";

  @Test
  @Ignore
  // TODO put in mvn verify
  public void validateTestFile() throws InterruptedException, IOException {
    File file = new File(testfile);
    assertTrue(file.exists());

    new ToetsToolValidator().validateFile(file, "http://localhost");

//    assertNotNull(toetstool);
//    assertNotNull(toetstool.getReport().getUrl());
//    assertNotNull(toetstool.getReport().getCounters().getError());
//
//    FileWriter writer = new FileWriter("report.xml");
//    writer.write(toetstool.toXml());
//    writer.close();
  }
}
