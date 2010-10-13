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

package org.sonar.plugins.web.html;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.web.html.FileSet.HtmlFile;

/**
 * Scans HTML files and provides a list of files in FileSet.xml. Checks for duplicate content.
 *
 * @author Matthijs Galesloot
 * @since 0.2
 */
public class HtmlScanner {

  private static final Logger LOG = Logger.getLogger(HtmlScanner.class);

  /**
   * Prepare Html files for analysis.
   *
   * @param path
   *          Path to folder with HTML files.
   */
  public void prepare(String path) {

    // the provided path must exist
    File htmlFolder = new File(path);
    if ( !htmlFolder.exists()) {
      throw new RuntimeException(path + " does not exist");
    }

    // open existing fileset or create a new fileset
    final FileSet fileSet;
    if (FileSet.getPath(htmlFolder).exists()) {
      fileSet = FileSet.fromXml(FileSet.getPath(htmlFolder));
    } else {
      fileSet = new FileSet();

      // collect the files
      collectFiles(fileSet, htmlFolder);
    }

    markDuplicates(fileSet);
    fileSet.toXml(FileSet.getPath(htmlFolder));
  }

  private void collectFiles(FileSet fileSet, File htmlFolder) {
    Collection<File> htmlFiles = FileUtils.listFiles(htmlFolder, new String[] { "html", "htm", "xhtml" }, true);

    for (File file : htmlFiles) {
      fileSet.addReplaceFile(file, htmlFolder);
    }
  }

  private void markDuplicates(FileSet fileSet) {
    int duplicates = 0;
    for (int i = 0; i < fileSet.files.size(); i++) {

      HtmlFile htmlFile = fileSet.files.get(i);
      if (htmlFile.duplicateFile == null) {
        for (int j = i + 1; j < fileSet.files.size(); j++) {
          if (fileSet.files.get(j).checksum == htmlFile.checksum) {
            fileSet.files.get(j).duplicateFile = htmlFile.path;
            duplicates++;
          }
        }
      }
    }

    LOG.info(fileSet.files.size() + " files, " + duplicates + " duplicates");
  }
}
