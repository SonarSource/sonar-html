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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * FileSet contains a list of files prepared for HTML validation.
 *
 * @author Matthijs Galesloot
 * @since 0.2
 */
@XStreamAlias("fileset")
public class FileSet {

  @XStreamAlias("file")
  public static class HtmlFile {

    public String path;
    public String url;
    public long checksum;
    public String duplicateFile;
  }

  public final List<HtmlFile> files = new ArrayList<HtmlFile>();

  public static FileSet fromXml(File file) {
    try {
      FileInputStream input = new FileInputStream(file);
      FileSet fileSet = (FileSet) getXstream().fromXML(input);
      return fileSet;
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static FileSet fromXml(InputStream input) {
    return (FileSet) getXstream().fromXML(input);
  }

  private static XStream getXstream() {
    XStream xstream = new XStream();
    xstream.processAnnotations(new Class[] { FileSet.class });
    return xstream;
  }

  public void toXml(File file) {
    try {
      getXstream().toXML(this, new FileOutputStream(file));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static File getPath(File htmlFolder) {
    return new File(htmlFolder.getPath() + "/fileset.xml");
  }

  /**
   * getRelativePath("c:/foo/src/my/package/Hello.java", ["c:/bar", "c:/foo/src"]) is "my/package/Hello.java".
   * <p/>
   * <p>
   * Relative path is composed of slashes. Windows backslaches are replaced by /
   * </p>
   *
   * @return null if file is not in dir (including recursive subdirectories)
   */
  public String getRelativePath(File file, File dir) {
    List<String> stack = new ArrayList<String>();
    String path = FilenameUtils.normalize(file.getAbsolutePath());
    File cursor = new File(path);
    while (cursor != null) {
      if (FilenameUtils.equalsNormalizedOnSystem(dir.getAbsolutePath(), cursor.getAbsolutePath())) {
        return StringUtils.join(stack, "/");
      }
      stack.add(0, cursor.getName());
      cursor = cursor.getParentFile();
    }
    return null;
  }

  /**
   * Add/Replace a file to the fileset.
   *
   * @param file File to add
   * @param htmlFolder Folder containing Html files
   * @return
   */
  public HtmlFile addReplaceFile(File file, File htmlFolder) {
    String path = getRelativePath(file, htmlFolder);
    for (HtmlFile htmlFile : files) {
      if (htmlFile.path.equals(path)) {
        return htmlFile;
      }
    }

    HtmlFile htmlFile = new HtmlFile();
    htmlFile.path = path;
    try {
      htmlFile.checksum = FileUtils.checksumCRC32(file);
    } catch (IOException e) {
      htmlFile.checksum = -1;
    }
    files.add(htmlFile);
    return htmlFile;
  }
}

