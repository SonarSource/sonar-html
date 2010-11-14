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

package org.sonar.plugins.web;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.web.CodeColorizerFormat;
import org.sonar.colorizer.RegexpTokenizer;
import org.sonar.colorizer.Tokenizer;
import org.sonar.plugins.web.language.Web;

public class WebCodeColorizerFormat extends CodeColorizerFormat {

  private final List<Tokenizer> tokenizers = new ArrayList<Tokenizer>();

  public WebCodeColorizerFormat() {
    super(Web.KEY);
    tokenizers.add(new RegexpTokenizer("<span class=\"k\">", "</span>", "</?\\p{L}*>?"));
    tokenizers.add(new RegexpTokenizer("<span class=\"k\">", "</span>", ">"));
  }

  @Override
  public List<Tokenizer> getTokenizers() {
    return tokenizers;
  }

}
