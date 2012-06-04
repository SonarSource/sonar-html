/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package org.sonar.plugins.web.core;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.bootstrap.ProjectBuilder;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonar.api.batch.bootstrap.ProjectReactor;
import org.sonar.api.config.Settings;
import org.sonar.plugins.web.api.WebConstants;

import java.io.File;

/**
 * Class used only to allow backward compatibility for the "sonar.web.sourceDirectory" property that has been deprecated
 * in version 1.2. This class should be removed when the support for this property is dropped. 
 * 
 * @since 1.2
 */
public class WebProjectBuilder extends ProjectBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(WebProjectBuilder.class);

  private Settings settings;

  public WebProjectBuilder(ProjectReactor reactor, Settings settings) {
    super(reactor);
    this.settings = settings;
  }

  @Override
  protected void build(ProjectReactor reactor) {
    if (settings.hasKey(WebConstants.SOURCE_DIRECTORY_PROP_KEY)) {
      String oldSourceDirParam = settings.getString(WebConstants.SOURCE_DIRECTORY_PROP_KEY);

      ProjectDefinition root = reactor.getRoot();
      if (StringUtils.isNotBlank(oldSourceDirParam)) {
        LOG.warn("/!\\ You are using the old 'sonar.web.sourceDirectory' property that is deprecated since version 1.2 of the Web plugin. " +
          "Please use the standard way to declare source directories according to the runner you are using (Maven, Ant or Simple Runner).");
        root.setSourceDirs(new File(root.getBaseDir(), oldSourceDirParam));
      }
    }
  }
}
