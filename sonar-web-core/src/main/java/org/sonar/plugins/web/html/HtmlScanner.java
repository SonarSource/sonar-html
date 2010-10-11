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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Scans HTML files and checks for duplicate content.
 *
 * @author Matthijs Galesloot
 * @since 0.2
 */
public class HtmlScanner {

  private static final class FileContent {

    public Long checksum;
    public boolean duplicate;
    public File file;
  }

  private static final Logger LOG = Logger.getLogger(HtmlScanner.class);

  /**
   *
   * @param path
   */
  public void prepare(String path) {
    if ( !new File(path).exists()) {
      throw new RuntimeException(path + " does not exist");
    }
    Collection<File> htmlFiles = FileUtils.listFiles(new File(path), new String[] { "html", "htm", "xhtml" }, true);

    List<FileContent> files = new ArrayList<FileContent>();

    for (File htmlFile : htmlFiles) {
      try {
        FileContent fileContent = new FileContent();
        fileContent.file = htmlFile;
        fileContent.checksum = FileUtils.checksumCRC32(htmlFile);
        files.add(fileContent);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    int duplicates = 0;
    for (int i = 0; i < files.size(); i++) {

      FileContent fileContent = files.get(i);
      if ( !fileContent.duplicate) {
        for (int j = i + 1; j < files.size(); j++) {
          if (files.get(j).checksum.equals(fileContent.checksum)) {
            files.get(j).duplicate = true;
            duplicates++;
          }
        }
      }
    }

    System.out.println(files.size() + " files, " + duplicates + " duplicates");
  }
}
