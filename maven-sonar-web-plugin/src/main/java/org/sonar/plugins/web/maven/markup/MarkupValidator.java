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

package org.sonar.plugins.web.maven.markup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
import org.sonar.plugins.web.maven.HtmlValidator;

/**
 * Validator for the W3C Markup Validation Service.
 *
 * @see http://validator.w3.org/docs/api.html
 *
 * @author Matthijs Galesloot
 * @since 0.2
 *
 */
final class MarkupValidator extends HtmlValidator {

  private static final String ERROR_XML = ".mur.error";

  private static final Logger LOG = Logger.getLogger(MarkupValidator.class);

  private static final String OUTPUT = "output";

  public static final String REPORT_SUFFIX = ".mur";

  private static final String SOAP12 = "soap12";

  private static final String TEXT_HTML_CONTENT_TYPE = "text/html";
  private static final String UPLOADED_FILE = "uploaded_file";

  /** the URL for the online validation service */
  private static final String validatorUrl = "http://validator.w3.org/check";

  /**
   * Get all report files
   */
  public static Collection<File> getReportFiles(File folder) {
    return getReportFiles(folder, REPORT_SUFFIX);
  }

  private File errorFile(File file) {
    return new File(file.getParentFile().getPath() + "/" + file.getName() + ERROR_XML);
  }

  /**
   * Post content of HTML file and CSS files to the W3C validation service. In return, receive a Soap response message.
   *
   * Documentation of interface: http://validator.w3.org/docs/api.html
   */
  private void postHtmlContents(File file, String url) {
    PostMethod post = new PostMethod(validatorUrl);

    try {

      LOG.info("W3C Validate: " + file.getName());

      // prepare content
      List<PartBase> parts = new ArrayList<PartBase>();

      try {
        FilePart filePart = new FilePart(UPLOADED_FILE, file.getName(), file);
        filePart.setContentType(TEXT_HTML_CONTENT_TYPE);
        parts.add(filePart);
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }

      StringPart outputFormat = new StringPart(OUTPUT, SOAP12);
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

  /**
   * Create the path to the report file.
   */
  @Override
  public File reportFile(File file) {
    return new File(file.getParentFile().getPath() + "/" + file.getName() + REPORT_SUFFIX);
  }

  /**
   * Validate a file with the W3C Markup service.
   */
  @Override
  public void validateFile(File file, String url) {

    postHtmlContents(file, url);
  }

  @Override
  protected void waitBetweenValidationRequests() {
    sleep(1000L);
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
      Writer writer = new FileWriter(reportFile);
      IOUtils.copy(post.getResponseBodyAsStream(), writer);
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
