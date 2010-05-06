/*
 * Copyright (C) 2010
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
import java.util.List;

import org.sonar.api.batch.AbstractSourceImporter;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.language.WebFile;

/**
 * @author Matthijs Galesloot
 */
public class WebSourceImporter extends AbstractSourceImporter {

  public WebSourceImporter(Web web) {
    super(web);
  }

  @Override
  protected Resource<?> createResource(File file, List<File> sourceDirs, boolean unitTest) {
    WebUtils.LOG.debug("WebSourceImporter:" + file.getPath());
    return file != null ? WebFile.fromIOFile(file, sourceDirs) : null;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}