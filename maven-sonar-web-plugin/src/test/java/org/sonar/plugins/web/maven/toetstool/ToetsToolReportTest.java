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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.sonar.plugins.web.Configuration;
import org.sonar.plugins.web.toetstool.xml.ToetstoolReport;


public class ToetsToolReportTest {

  private static final String packagePath = "src/test/resources/org/sonar/plugins/web/maven/toetstool/";

  @Before
  public void setToetstoolUrl() {
    Configuration.setToetstoolURL("http://xyz");
  }

  @Test
  public void parseReport() {
    ToetstoolReport report = ToetstoolReport.fromXml(new File(packagePath + "report.ttr"));
    assertNotNull(report);
  }

  @Test
  public void buildReport() {
    File report = new File("target/toetstool-report.html");
    if (report.exists()) {
      report.delete();
    }
    ToetsToolReportBuilder reportBuilder = new ToetsToolReportBuilder();
    reportBuilder.buildReports(new File(packagePath));

    assertTrue(report.exists());
  }
}
