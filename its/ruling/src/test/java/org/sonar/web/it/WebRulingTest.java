/*
 * SonarSource :: HTML :: ITs :: Ruling
 * Copyright (c) 2013-2019 SonarSource SA and Matthijs Galesloot
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

import com.google.gson.Gson;
import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;
import com.sonar.orchestrator.http.HttpMethod;
import com.sonar.orchestrator.locator.FileLocation;
import com.sonar.orchestrator.locator.MavenLocation;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonarsource.analyzer.commons.ProfileGenerator;

import static org.assertj.core.api.Assertions.assertThat;

public class WebRulingTest {

  private static final String LANGUAGE = "web";
  private static final String REPOSITORY_KEY = "Web";
  private static final Gson GSON = new Gson();

  @ClassRule
  public static Orchestrator orchestrator = Orchestrator.builderEnv()
    .setSonarVersion(Optional.ofNullable(System.getProperty("sonar.runtimeVersion")).orElse("LATEST_RELEASE"))
    .addPlugin(FileLocation.byWildcardMavenFilename(new File("../../sonar-html-plugin/target"), "sonar-html-plugin-*.jar"))
    .addPlugin(MavenLocation.of("org.sonarsource.sonar-lits-plugin", "sonar-lits-plugin", "0.8.0.1209"))
    .build();

  @BeforeClass
  public static void prepare_quality_profiles() {
    File profile = ProfileGenerator.generateProfile(orchestrator.getServer().getUrl(), LANGUAGE, REPOSITORY_KEY,
      new ProfileGenerator.RulesConfiguration(), Collections.emptySet());
    orchestrator.getServer().restoreProfile(FileLocation.of(profile));
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
      .setProperty("dump.old", FileLocation.of("src/test/resources/expected").getFile().getAbsolutePath())
      .setProperty("dump.new", FileLocation.of("target/actual").getFile().getAbsolutePath())
      .setProperty("lits.differences", litsDifferencesFile.getAbsolutePath())
      .setProperty("sonar.exclusions", "external_webkit-jb-mr1/LayoutTests/fast/encoding/*utf*")
      .setProperty("sonar.cpd.exclusions", "**/*")
      .setEnvironmentVariable("SONAR_RUNNER_OPTS", "-Xmx1024m");
    orchestrator.executeBuild(build);

    String differences = new String(Files.readAllBytes(litsDifferencesFile.toPath()), StandardCharsets.UTF_8);
    assertThat(differences).isEmpty();
  }

  private static void instantiateTemplateRule(String ruleTemplateKey, String instantiationKey, String params) {
    orchestrator.getServer()
      .newHttpCall("/api/rules/create")
      .setAdminCredentials()
      .setMethod(HttpMethod.POST)
      .setParams(
        "name", instantiationKey,
        "markdown_description", instantiationKey,
        "severity", "INFO",
        "status", "READY",
        "template_key", REPOSITORY_KEY + ":" + ruleTemplateKey,
        "custom_key", instantiationKey,
        "prevent_reactivation", "true",
        "params", "name=\"" + instantiationKey + "\";key=\"" + instantiationKey + "\";markdown_description=\"" + instantiationKey + "\";" + params)
      .execute();

    // check that the rule has been created
    String get = orchestrator.getServer()
      .newHttpCall("api/qualityprofiles/search")
      .execute()
      .getBodyAsString();

    String profileKey = null;
    Map map = GSON.fromJson(get, Map.class);
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
    orchestrator.getServer()
      .newHttpCall("api/qualityprofiles/activate_rule")
      .setAdminCredentials()
      .setMethod(HttpMethod.POST)
      .setParams(
        "key", profileKey,
        "rule", REPOSITORY_KEY + ":" + instantiationKey,
        "severity", "INFO",
        "params", "")
      .execute();
  }

}
