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

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.web.HtmlValidator;
import org.sonar.plugins.web.Settings;
import org.sonar.plugins.web.toetstool.ValidationReport;
import org.sonar.plugins.web.toetstool.xml.ToetstoolReport;

/**
 * Work in progress...
 *
 * @author Matthijs Galesloot
 * @since 0.2
 *
 */
public class MarkupValidator extends HtmlValidator {

  /** the URL for the online validation service */
  private static final String validatorUrl = "http://validator.w3.org/check";

  private static final Logger LOG = Logger.getLogger(MarkupValidator.class);

  /**
   * Validate a set of files using the Toetstool service.
   */
  public void validateFiles(File folder) {
    Collection<File> files = FileUtils.listFiles(folder, new String[] { "html", "htm", "xhtml" }, true);

    if (Settings.getNrOfSamples() != null) {
      files = randomSubset(files, Settings.getNrOfSamples());
    }
    for (File file : files) {
      validateFile(file);
    }
  }

  /**
   * Validate a file with the Toetstool service.
   */
  void validateFile(File file) {

    ToetstoolReport report = ToetstoolReport.fromXml(ValidationReport.reportFile(file));

    try {
      // post html contents, in return we get a redirect location
      postHtmlContents(file, report.getUrl());

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Post content of HTML file and CSS files to the W3C validation service. In return, receive a Soap response message.
   *
   * Documentation of interface:
   * http://validator.w3.org/docs/api.html
   */
  public String postHtmlContents(File file, String url) throws IOException {
    PostMethod post = new PostMethod(validatorUrl);

    try {

      LOG.info("W3C Validate " + file.getName());

      // prepare content
      List<PartBase> parts = new ArrayList<PartBase>();

      LOG.info("Sending url: " + url);
      FilePart filePart = new FilePart("uploaded_file", file.getName(), file);
      filePart.setContentType("text/html");
      parts.add(filePart);
      StringPart outputFormat = new StringPart("output", "soap12");
      parts.add(outputFormat);

      MultipartRequestEntity multiPartRequestEntity = new MultipartRequestEntity(parts.toArray(new PartBase[parts.size()]),
          post.getParams());
      post.setRequestEntity(multiPartRequestEntity);

      getClient().executeMethod(post);
      LOG.info("Post: " + parts.size() + " parts, " + post.getStatusLine().toString());

      LOG.info(post.getResponseBodyAsString());

      // TODO read the soap response

      return "";
    } finally {
      // release any connection resources used by the method
      post.releaseConnection();
    }
  }
}
