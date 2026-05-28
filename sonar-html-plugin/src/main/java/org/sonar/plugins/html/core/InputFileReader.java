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
import java.io.InputStreamReader;
import java.io.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;

/**
 * I/O helpers for {@link InputFile}. Callers that only need a prefix or a slice of a file
 * (e.g. content-type sniffing, magic-byte detection) should reach for {@link #readPartial}
 * rather than open their own reader.
 */
public final class InputFileReader {

  private static final Logger LOG = LoggerFactory.getLogger(InputFileReader.class);

  private InputFileReader() {
  }

  /**
   * Reads characters from {@code inputFile} in the {@code [start, end)} range (exclusive
   * end), using the file's configured charset.
   *
   * <p>Returns the shorter chunk when the file ends before {@code end}, an empty string
   * when the file is shorter than {@code start}, and {@code null} when the file cannot be
   * read at all. The failure is logged at debug level — callers decide how to surface it.
   *
   * @param inputFile the file to read
   * @param start     starting character offset, inclusive ({@code >= 0})
   * @param end       ending character offset, exclusive ({@code > start})
   * @return the read slice, or {@code null} on I/O failure
   * @throws IllegalArgumentException if {@code start < 0} or {@code end <= start}
   */
  public static String readPartial(InputFile inputFile, int start, int end) {
    if (start < 0 || end <= start) {
      throw new IllegalArgumentException("readPartial requires 0 <= start < end, got start=" + start + " end=" + end);
    }
    int length = end - start;
    char[] buf = new char[length];
    try (Reader reader = new InputStreamReader(inputFile.inputStream(), inputFile.charset())) {
      skipFully(reader, start);
      int total = 0;
      while (total < length) {
        int read = reader.read(buf, total, length - total);
        if (read < 0) {
          break;
        }
        total += read;
      }
      return new String(buf, 0, total);
    } catch (IOException e) {
      LOG.debug("Could not read {} chars from {} at offset {}: {}", length, inputFile, start, e.getMessage());
      return null;
    }
  }

  private static void skipFully(Reader reader, int n) throws IOException {
    long remaining = n;
    while (remaining > 0) {
      long skipped = reader.skip(remaining);
      if (skipped <= 0) {
        // Reader exhausted before we reached `start` — leave remaining; next read returns -1.
        return;
      }
      remaining -= skipped;
    }
  }
}
