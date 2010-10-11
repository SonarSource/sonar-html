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

package org.sonar.plugins.web.markupvalidation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.web.html.HtmlValidator;

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

  private static final String REPORT_XML = "-mu.xml";
  private static final String ERROR_XML = "-mu.error";

  private static final Logger LOG = Logger.getLogger(MarkupValidator.class);

  /**
   * Validate a file with the Toetstool service.
   */
  @Override
  public void validateFile(File file, String url) {

    // post html contents to service
    postHtmlContents(file, url);
  }

  /**
   * Post content of HTML file and CSS files to the W3C validation service. In return, receive a Soap response message.
   *
   * Documentation of interface: http://validator.w3.org/docs/api.html
   */
  public void postHtmlContents(File file, String url) {
    PostMethod post = new PostMethod(validatorUrl);

    try {

      LOG.info("W3C Validate: " + file.getName());

      // prepare content
      List<PartBase> parts = new ArrayList<PartBase>();

      LOG.info("Sending url: " + url);
      FilePart filePart;
      try {
        filePart = new FilePart("uploaded_file", file.getName(), file);
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
      filePart.setContentType("text/html");
      parts.add(filePart);
      StringPart outputFormat = new StringPart("output", "soap12");
      parts.add(outputFormat);

      MultipartRequestEntity multiPartRequestEntity = new MultipartRequestEntity(parts.toArray(new PartBase[parts.size()]),
          post.getParams());
      post.setRequestEntity(multiPartRequestEntity);

      executePostMethod(post);

      LOG.info("Post: " + parts.size() + " parts, " + post.getStatusLine().toString());

      writeResponse(post, file);
    } finally {
      // release any connection resources used by the method
      post.releaseConnection();
    }
  }

  private void writeResponse(PostMethod post, File file) {
    final File reportFile;
    if (post.getStatusCode() != 200) {
      LOG.error("failed to validate file " + file.getPath());
      reportFile = errorFile(file);
    } else {
      reportFile = reportFile(file);
    }

    try {
      IOUtils.copy(post.getResponseBodyAsStream(), new FileWriter(reportFile));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public File reportFile(File file) {
    return new File(file.getParentFile().getPath() + "/" + file.getName() + REPORT_XML);
  }

  private File errorFile(File file) {
    return new File(file.getParentFile().getPath() + "/" + file.getName() + ERROR_XML);
  }

  public static Collection<File> getReportFiles(File folder) {
    return getReportFiles(folder, REPORT_XML);
  }

  @Override
  protected void waitBetweenValidationRequests() {
    sleep(1000L);
  }
}
