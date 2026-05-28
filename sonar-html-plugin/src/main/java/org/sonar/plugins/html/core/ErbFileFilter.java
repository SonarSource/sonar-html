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

import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Configuration;
import org.sonar.plugins.html.api.HtmlConstants;

// ERB is Ruby's general-purpose template engine and is used for many non-HTML
// outputs (Dockerfile, YAML, plain text, ...). We accept an .erb file only when
// we have evidence it produces HTML: either a recognized double extension
// (foo.html.erb, foo.vue.erb, ...) or a content sniff of the first 2 KB that
// finds HTML markers once ERB code blocks have been stripped.
public final class ErbFileFilter {

  private static final Logger LOG = LoggerFactory.getLogger(ErbFileFilter.class);

  private static final String ERB_SUFFIX = ".erb";
  private static final String ERB_EXTENSION = "erb";
  private static final int SNIFF_CHARACTERS = 2048;
  private static final int WEAK_HTML_MIN_MATCHES = 2;

  private static final Pattern ERB_BLOCK = Pattern.compile("<%[\\s\\S]*?%>");

  // A single occurrence of any of these is enough — they are page-root markers
  // and only show up in genuine HTML content.
  private static final Pattern STRONG_HTML_TAG = Pattern.compile(
    "<!DOCTYPE\\s+html\\b|<(?:html|head|body)\\b",
    Pattern.CASE_INSENSITIVE);

  // Built from HtmlConstants.KNOWN_HTML_TAGS so the sniff vocabulary stays in
  // lockstep with the analyzer's tag list. Requires WEAK_HTML_MIN_MATCHES hits
  // to keep the false-positive rate low on plain text that happens to mention
  // a single tag name.
  private static final Pattern WEAK_HTML_TAG = Pattern.compile(
    "<(?:" + HtmlConstants.KNOWN_HTML_TAGS.stream().collect(Collectors.joining("|")) + ")\\b",
    Pattern.CASE_INSENSITIVE);

  private ErbFileFilter() {
  }

  /**
   * Builds a {@link FilePredicate} that drops bare {@code .erb} files lacking HTML evidence,
   * while letting every non-{@code .erb} file pass through unchanged at the predicate-composition
   * layer (no per-file lambda invocation).
   */
  public static FilePredicate filePredicate(FilePredicates predicates, Configuration config) {
    Set<String> recognized = HtmlFileExtensions.recognized(config);
    return predicates.or(
      predicates.not(predicates.hasExtension(ERB_EXTENSION)),
      inputFile -> {
        boolean keep = shouldAnalyze(inputFile, recognized);
        if (!keep) {
          LOG.debug("Skipping ERB file without recognized double extension or HTML content sniff: {}", inputFile);
        }
        return keep;
      });
  }

  /**
   * Tells whether {@code inputFile} should be analyzed by sonar-html.
   * Non-.erb files are always accepted. For .erb files, we keep them when the
   * filename carries a recognized double extension; otherwise we sniff the first
   * {@value #SNIFF_CHARACTERS} characters of content for HTML markers.
   * When the content cannot be read, the file is kept so the sensor's normal
   * analysis-error path reports the failure instead of silently dropping it.
   */
  public static boolean shouldAnalyze(InputFile inputFile, Set<String> recognizedExtensions) {
    String filename = inputFile.filename().toLowerCase(Locale.ROOT);
    if (!filename.endsWith(ERB_SUFFIX)) {
      return true;
    }
    if (hasRecognizedDoubleExtension(filename, recognizedExtensions)) {
      return true;
    }
    String head = InputFileReader.readPartial(inputFile, 0, SNIFF_CHARACTERS);
    if (head == null) {
      // Let it through; the sensor will catch the IOException and create an analysis error.
      return true;
    }
    return looksLikeHtml(head);
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
}
