/*
 * Copyright (C) 2011-2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.it.web;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.OrchestratorBuilder;
import com.sonar.orchestrator.build.SonarRunner;
import com.sonar.orchestrator.locator.FileLocation;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
  FileSuffixesTest.class
})
public class WebTestSuite {

  public static final String PLUGIN_KEY = "web";

  @ClassRule
  public static final Orchestrator ORCHESTRATOR;

  static {
    OrchestratorBuilder orchestratorBuilder = Orchestrator.builderEnv()
      .addPlugin(PLUGIN_KEY)
      .setMainPluginKey(PLUGIN_KEY)
      .restoreProfileAtStartup(FileLocation.of("profiles/no_rule.xml"));
    ORCHESTRATOR = orchestratorBuilder.build();
  }

  public static boolean is_after_sonar_4_2() {
    return ORCHESTRATOR.getConfiguration().getSonarVersion().isGreaterThanOrEquals("4.2");
  }

  public static boolean is_after_plugin_2_2() {
    return ORCHESTRATOR.getConfiguration().getPluginVersion(PLUGIN_KEY).isGreaterThanOrEquals("2.2");
  }

  public static SonarRunner createSonarRunner() {
    SonarRunner build = SonarRunner.create();
    if (!is_multi_language()) {
      build.setProperty("sonar.language", PLUGIN_KEY);
    }
    return build;
  }

  private static boolean is_multi_language() {
    return is_after_plugin_2_2() && is_after_sonar_4_2();
  }
}
