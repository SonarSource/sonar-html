/*
 * SonarQube HTML
 * Copyright (C) 2011-2025 SonarSource SA
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
package com.sonar.it.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.sonar.orchestrator.locator.FileLocation;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidOpenFileParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidUpdateFileSystemParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.junit.jupiter.api.io.TempDir;

import org.sonarsource.sonarlint.core.test.utils.SonarLintBackendFixture;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import org.sonarsource.sonarlint.core.test.utils.plugins.Plugin;

class SonarLintIntegrationTest {

  private static final String CONFIG_SCOPE_ID = "CONFIG_SCOPE_ID";
  private SonarLintBackendFixture.FakeSonarLintRpcClient client;
  private SonarLintTestRpcServer backend;

  @SonarLintTest
  void should_report_issues(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var fileDTO = createFile(baseDir, "foo.html", "<html>\n<body>\n<a href=\"foo.png\">a</a>\n</body>\n</html>\n");
    initWithFiles(harness, baseDir, fileDTO);

    triggerAnalysisByFileOpened(fileDTO);

    assertResults(results -> {
      assertThat(results).hasSize(1);
      assertThat(results.get(0).getRuleKey()).isEqualTo("Web:DoctypePresenceCheck");
    });

    triggerAnalysisByFileChanged(fileDTO, "x = true ? 42 : 43");

    assertResults(results -> {
      assertThat(results).isEmpty();
    });
  }


  private static ClientFileDto createFile(Path folderPath, String fileName, String content) {
    var filePath = folderPath.resolve(fileName);
    try {
      Files.writeString(filePath, content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new ClientFileDto(
      filePath.toUri(),
      folderPath.relativize(filePath),
      CONFIG_SCOPE_ID,
      false,
      null,
      filePath,
      null,
      null,
      true
    );
  }

  private void triggerAnalysisByFileOpened(ClientFileDto fileDTO) {
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileDTO.getUri()));
  }

  private void triggerAnalysisByFileChanged(ClientFileDto fileDTO, String content) {
    try {
      Files.writeString(fileDTO.getFsPath(), content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    backend
      .getFileService()
      .didUpdateFileSystem(new DidUpdateFileSystemParams(List.of(), List.of(fileDTO), List.of()));
  }

  private void initWithFiles(
    SonarLintTestHarness harness,
    Path baseDir,
    ClientFileDto... fileDTOs
  ) {
    client = harness
      .newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, Arrays.asList(fileDTOs))
      .build();

    backend = harness
      .newBackend()
      .withStandaloneEmbeddedPluginAndEnabledLanguage(
        new Plugin(
          Set.of(org.sonarsource.sonarlint.core.rpc.protocol.common.Language.HTML),
          FileLocation.byWildcardMavenFilename(new File("../../sonar-html-plugin/target"), "sonar-html-plugin-*.jar").getFile().toPath(),
          "",
          ""
        )
      )
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .start(client);
  }

  private void assertResults(Consumer<List<RaisedIssueDto>> assertionLambda) {
    await()
      .atMost(15, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        var results = client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID);
        assertionLambda.accept(results);
      });
  }
}

