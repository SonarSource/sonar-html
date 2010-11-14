/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.web.duplications;

import java.io.File;
import java.util.List;

import net.sourceforge.pmd.cpd.Tokenizer;

import org.sonar.api.batch.CpdMapping;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.language.WebFile;

public class WebCpdMapping implements CpdMapping {

  public Tokenizer getTokenizer() {
    return new WebCpdTokenizer();
  }

  public Resource createResource(File file, List<File> sourceDirs) {
    return WebFile.fromIOFile(file, sourceDirs);
  }

  public Language getLanguage() {
    return Web.INSTANCE;
  }

}
