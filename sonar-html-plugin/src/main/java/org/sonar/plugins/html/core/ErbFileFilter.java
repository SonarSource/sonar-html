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
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

// ERB is Ruby's general-purpose template engine and is used for many non-HTML
// outputs (Dockerfile, YAML, plain text, ...). We accept an .erb file only when
// we have evidence it produces HTML: either a recognized double extension
// (foo.html.erb, foo.vue.erb, ...) or a content sniff of the first 2 KB that
// finds HTML markers once ERB code blocks have been stripped.
public final class ErbFileFilter {

  private static final Logger LOG = Loggers.get(ErbFileFilter.class);

  private static final String ERB_SUFFIX = ".erb";
  private static final int READ_CHARACTERS_LIMIT = 2048;
  private static final int WEAK_HTML_MIN_MATCHES = 2;

  private static final Pattern ERB_BLOCK = Pattern.compile("<%[\\s\\S]*?%>");

  private static final Pattern STRONG_HTML_TAG = Pattern.compile(
    "<!DOCTYPE\\s+html\\b|<(?:html|head|body)\\b",
    Pattern.CASE_INSENSITIVE);

  private static final Pattern WEAK_HTML_TAG = Pattern.compile(
    "<(?:div|span|p|a|ul|ol|li|table|tr|td|th|form|input|textarea|select|button|" +
      "script|style|section|article|header|footer|nav|main)\\b",
    Pattern.CASE_INSENSITIVE);

  private ErbFileFilter() {
  }

  /**
   * Tells whether {@code inputFile} should be analyzed by sonar-html.
   * Non-.erb files are always accepted. For .erb files, we keep them when the
   * filename carries a recognized double extension; otherwise we sniff the first
   * {@value #READ_CHARACTERS_LIMIT} characters of content for HTML markers.
   *
   * @param inputFile the file to inspect
   * @param recognizedExtensions extensions sonar-html knows about, lowercase and without leading dot
   * @return true if the file should be passed through to the HTML lexer
   */
  public static boolean shouldAnalyze(InputFile inputFile, Set<String> recognizedExtensions) {
    String filename = inputFile.filename().toLowerCase(Locale.ROOT);
    if (!filename.endsWith(ERB_SUFFIX)) {
      return true;
    }
    if (hasRecognizedDoubleExtension(filename, recognizedExtensions)) {
      return true;
    }
    return looksLikeHtml(readHead(inputFile));
  }

  static boolean hasRecognizedDoubleExtension(String lowerFilename, Set<String> recognizedExtensions) {
    String beforeErb = lowerFilename.substring(0, lowerFilename.length() - ERB_SUFFIX.length());
    int dotIdx = beforeErb.lastIndexOf('.');
    if (dotIdx < 0) {
      return false;
    }
    return recognizedExtensions.contains(beforeErb.substring(dotIdx + 1));
  }

  static boolean looksLikeHtml(String content) {
    if (content.isEmpty()) {
      return false;
    }
    String cleaned = ERB_BLOCK.matcher(content).replaceAll(" ");
    if (STRONG_HTML_TAG.matcher(cleaned).find()) {
      return true;
    }
    Matcher m = WEAK_HTML_TAG.matcher(cleaned);
    int count = 0;
    while (m.find()) {
      if (++count >= WEAK_HTML_MIN_MATCHES) {
        return true;
      }
    }
    return false;
  }

  private static String readHead(InputFile inputFile) {
    char[] buf = new char[READ_CHARACTERS_LIMIT];
    try (Reader reader = new InputStreamReader(inputFile.inputStream(), inputFile.charset())) {
      int total = 0;
      while (total < buf.length) {
        int read = reader.read(buf, total, buf.length - total);
        if (read < 0) {
          break;
        }
        total += read;
      }
      return total > 0 ? new String(buf, 0, total) : "";
    } catch (IOException e) {
      LOG.debug("Could not read ERB head for {}: {}", inputFile, e.getMessage());
      return "";
    }
  }
}
