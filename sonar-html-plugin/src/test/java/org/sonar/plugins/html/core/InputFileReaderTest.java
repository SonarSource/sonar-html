/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.InputFile;
import com.sonarsource.scanner.engine.sensor.test.fixtures.TestInputFileBuilder;
import org.sonar.scanner.plugin.api.impl.fs.DefaultInputFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InputFileReaderTest {

  @Test
  void reads_first_n_chars_when_start_is_zero() {
    DefaultInputFile file = inputFile("0123456789ABCDEF");

    assertThat(InputFileReader.readPartial(file, 0, 4)).isEqualTo("0123");
    assertThat(InputFileReader.readPartial(file, 0, 16)).isEqualTo("0123456789ABCDEF");
  }

  @Test
  void reads_slice_at_arbitrary_offset() {
    DefaultInputFile file = inputFile("0123456789ABCDEF");

    assertThat(InputFileReader.readPartial(file, 4, 8)).isEqualTo("4567");
    assertThat(InputFileReader.readPartial(file, 10, 16)).isEqualTo("ABCDEF");
  }

  @Test
  void returns_short_chunk_when_file_ends_before_end() {
    DefaultInputFile file = inputFile("short");

    assertThat(InputFileReader.readPartial(file, 0, 100)).isEqualTo("short");
    assertThat(InputFileReader.readPartial(file, 3, 100)).isEqualTo("rt");
  }

  @Test
  void returns_empty_when_file_is_shorter_than_start() {
    DefaultInputFile file = inputFile("abc");

    assertThat(InputFileReader.readPartial(file, 10, 20)).isEmpty();
  }

  @Test
  void returns_null_when_input_stream_throws() throws IOException {
    InputFile file = mock(InputFile.class);
    when(file.charset()).thenReturn(StandardCharsets.UTF_8);
    when(file.inputStream()).thenThrow(new IOException("boom"));

    assertThat(InputFileReader.readPartial(file, 0, 32)).isNull();
  }

  @Test
  void rejects_invalid_range() {
    DefaultInputFile file = inputFile("anything");

    assertThatThrownBy(() -> InputFileReader.readPartial(file, -1, 10))
      .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> InputFileReader.readPartial(file, 5, 5))
      .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> InputFileReader.readPartial(file, 10, 5))
      .isInstanceOf(IllegalArgumentException.class);
  }

  private static DefaultInputFile inputFile(String contents) {
    return new TestInputFileBuilder("key", "sample.txt")
      .setModuleBaseDir(Paths.get("."))
      .setContents(contents)
      .setType(InputFile.Type.MAIN)
      .setCharset(StandardCharsets.UTF_8)
      .build();
  }
}
