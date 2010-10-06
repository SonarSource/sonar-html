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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sonar.plugins.web.Settings;


public class ReportTest {

  private static final String folder = "src/test/resources/org/sonar/plugins/web/toetstool";

  @Before
  public void setToetstoolUrl() {
    Settings.setToetstoolURL("http://xyz");
  }

  @Test
  public void testAggregateReport() {

    new File("target/report.html").delete();

    Report report = new Report();
    report.buildReports(new File(folder));

    assertTrue(new File("target/report.html").exists());
  }

  @Test
  @Ignore
  public void testAggregateJmeterReport() throws FileNotFoundException {
    File file = new File("C:/workspaces/tenderned/src/tenderned-performance/target/jmeter-reports");
    new Report().buildReports(file);
  }
}
