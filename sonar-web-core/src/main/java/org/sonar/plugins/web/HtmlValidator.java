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

package org.sonar.plugins.web;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.sonar.plugins.web.ssl.EasySSLProtocolSocketFactory;


public class HtmlValidator {

  static {
    Protocol.registerProtocol("https", new Protocol("https", (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), 443));
  }

  protected HttpClient getClient() {
    return client;
  }

  public static Collection<File> getReportFiles(File htmlFolder) {
    @SuppressWarnings("unchecked")
    Collection<File> reportFiles = FileUtils.listFiles(htmlFolder, new IOFileFilter() {

      @Override
      public boolean accept(File file) {
        return file.getName().endsWith("-report.xml");
      }

      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith("-report.xml");
      }
    }, new IOFileFilter() {

      @Override
      public boolean accept(File file) {
        return true;
      }

      @Override
      public boolean accept(File dir, String name) {
        return true;
      }
    });

    return reportFiles;
  }

  private final HttpClient client;

  public HtmlValidator() {
    client = new HttpClient();
    if (Settings.useProxy()) {
      client.getHostConfiguration().setProxy(Settings.getProxyHost(), Settings.getProxyPort());
    }
  }

  protected List<File> randomSubset(Collection<File> collection, Integer amount) {
    List<File> newCollection = new ArrayList<File>();
    File[] files = collection.toArray(new File[collection.size()]);
    for (int i = 0; i < amount && i < files.length; i++) {
      newCollection.add(files[i]);
    }
    return newCollection;
  }

}
