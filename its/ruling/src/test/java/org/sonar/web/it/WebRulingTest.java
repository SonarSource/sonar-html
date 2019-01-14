/*
 * SonarSource :: HTML :: ITs :: Ruling
 * Copyright (c) 2013-2018 SonarSource SA and Matthijs Galesloot
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
package org.sonar.web.it;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;
import com.sonar.orchestrator.locator.FileLocation;
import com.sonar.orchestrator.locator.MavenLocation;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.SonarClient;

import static org.assertj.core.api.Assertions.assertThat;

public class WebRulingTest {

  private static final Gson GSON = new Gson();

  @ClassRule
  public static Orchestrator orchestrator = Orchestrator.builderEnv()
    .setSonarVersion(Optional.ofNullable(System.getProperty("sonar.runtimeVersion")).orElse("LATEST_RELEASE[6.7]"))
    .addPlugin(FileLocation.byWildcardMavenFilename(new File("../../sonar-html-plugin/target"), "sonar-html-plugin-*.jar"))
    .addPlugin(MavenLocation.of("org.sonarsource.sonar-lits-plugin", "sonar-lits-plugin", "0.6"))
    .build();

  @BeforeClass
  public static void prepare_quality_profiles() {
    ImmutableMap<String, ImmutableMap<String, String>> rulesParameters = ImmutableMap.<String, ImmutableMap<String, String>>builder().build();
    ProfileGenerator.generate(orchestrator, rulesParameters, ImmutableSet.<String>of());

    instantiateTemplateRule("IllegalAttributeCheck", "Template_DoNotUseNameProperty", "attributes=\"name\"");
  }

  @Test
  public void ruling() throws Exception {
    File litsDifferencesFile = FileLocation.of("target/differences").getFile();
    String projectKey = "project";
    orchestrator.getServer().provisionProject(projectKey, projectKey);
    orchestrator.getServer().associateProjectToQualityProfile(projectKey, "web", "rules");
    SonarScanner build = SonarScanner.create()
      .setProjectDir(FileLocation.of("../sources").getFile())
      .setProjectKey(projectKey)
      .setProjectName(projectKey)
      .setProjectVersion("1")
      .setSourceDirs(".")
      .setSourceEncoding("UTF-8")
      .setProperty("sonar.html.file.suffixes", "xhtml,html,php,erb")
      .setProperty("sonar.jsp.file.suffixes", "jspf,jsp")
      .setProperty("sonar.analysis.mode", "preview")
      .setProperty("sonar.issuesReport.html.enable", "true")
      .setProperty("dump.old", FileLocation.of("src/test/resources/expected").getFile().getAbsolutePath())
      .setProperty("dump.new", FileLocation.of("target/actual").getFile().getAbsolutePath())
      .setProperty("lits.differences", litsDifferencesFile.getAbsolutePath())
      .setProperty("sonar.exclusions", "external_webkit-jb-mr1/LayoutTests/fast/encoding/*utf*")
      .setProperty("sonar.cpd.skip", "true")
      .setEnvironmentVariable("SONAR_RUNNER_OPTS", "-Xmx1024m");
    orchestrator.executeBuild(build);

    assertThat(Files.toString(litsDifferencesFile, StandardCharsets.UTF_8)).isEmpty();
  }

  private static void instantiateTemplateRule(String ruleTemplateKey, String instantiationKey, String params) {
    SonarClient sonarClient = orchestrator.getServer().adminWsClient();

    // create the template rule
    sonarClient.post("/api/rules/create", ImmutableMap.<String, Object>builder()
      .put("name", instantiationKey)
      .put("markdown_description", instantiationKey)
      .put("severity", "INFO")
      .put("status", "READY")
      .put("template_key", ProfileGenerator.REPOSITORY_KEY + ":" + ruleTemplateKey)
      .put("custom_key", instantiationKey)
      .put("prevent_reactivation", "true")
      .put("params", "name=\"" + instantiationKey + "\";key=\"" + instantiationKey + "\";markdown_description=\"" + instantiationKey + "\";" + params)
      .build());

    // check that the rule has been created
    String post = sonarClient.get("api/qualityprofiles/search");
    String profileKey = null;
    Map map = GSON.fromJson(post, Map.class);
    for (Map qp : ((List<Map>) map.get("profiles"))) {
      if ("rules".equals(qp.get("name"))) {
        profileKey = (String) qp.get("key");
        break;
      }
    }
    if (StringUtils.isEmpty(profileKey)) {
      throw new IllegalStateException("Could not retrieve profile key : Template rule " + ruleTemplateKey + " has not been activated");
    }

    // activate the rule
    sonarClient.post("api/qualityprofiles/activate_rule", ImmutableMap.of(
      "profile_key", profileKey,
      "rule_key", ProfileGenerator.REPOSITORY_KEY + ":" + instantiationKey,
      "severity", "INFO",
      "params", ""));
  }

}
