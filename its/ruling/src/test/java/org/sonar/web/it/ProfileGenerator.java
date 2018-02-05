/*
 * SonarSource :: Web :: ITs :: Ruling
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

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.locator.FileLocation;
import org.sonar.wsclient.internal.HttpRequestFactory;
import org.sonar.wsclient.jsonsimple.JSONValue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileGenerator {

  static void generate(Orchestrator orchestrator, String language, String repositoryKey, ImmutableMap<String, ImmutableMap<String, String>> rulesParameters, Set<String> excluded) {
    try {
      StringBuilder sb = new StringBuilder()
        .append("<profile>")
        .append("<name>rules</name>")
        .append("<language>").append(language).append("</language>")
        .append("<alerts>")
        .append("<alert>")
        .append("<metric>blocker_violations</metric>")
        .append("<operator>&gt;</operator>")
        .append("<warning></warning>")
        .append("<error>0</error>")
        .append("</alert>")
        .append("<alert>")
        .append("<metric>info_violations</metric>")
        .append("<operator>&gt;</operator>")
        .append("<warning></warning>")
        .append("<error>0</error>")
        .append("</alert>")
        .append("</alerts>")
        .append("<rules>");

      List<String> ruleKeys = Lists.newArrayList();
      String json = new HttpRequestFactory(orchestrator.getServer().getUrl())
        .get("/api/rules/search", ImmutableMap.<String, Object>of("languages", language, "repositories", repositoryKey, "ps", "500"));
      @SuppressWarnings("unchecked")
      List<Map> jsonRules = (List<Map>) ((Map) JSONValue.parse(json)).get("rules");
      Preconditions.checkState(jsonRules.size() < 500);
      for (Map jsonRule : jsonRules) {
        String key = (String) jsonRule.get("key");
        ruleKeys.add(key.split(":")[1]);
      }

      for (String key : ruleKeys) {
        if (excluded.contains(key)) {
          continue;
        }
        sb.append("<rule>")
          .append("<repositoryKey>").append(repositoryKey).append("</repositoryKey>")
          .append("<key>").append(key).append("</key>")
          .append("<priority>INFO</priority>");
        if (rulesParameters.containsKey(key)) {
          sb.append("<parameters>");
          for (Map.Entry<String, String> parameter : rulesParameters.get(key).entrySet()) {
            sb.append("<parameter>")
              .append("<key>").append(parameter.getKey()).append("</key>")
              .append("<value>").append(parameter.getValue()).append("</value>")
              .append("</parameter>");
          }
          sb.append("</parameters>");
        }
        sb.append("</rule>");
      }

      sb.append("</rules>")
        .append("</profile>");

      File file = File.createTempFile("profile", ".xml");
      Files.write(sb, file, Charsets.UTF_8);
      orchestrator.getServer().restoreProfile(FileLocation.of(file));
      file.delete();
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }
}
