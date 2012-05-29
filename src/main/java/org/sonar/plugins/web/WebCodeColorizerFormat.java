/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
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

import org.sonar.api.web.CodeColorizerFormat;
import org.sonar.colorizer.RegexpTokenizer;
import org.sonar.colorizer.Tokenizer;
import org.sonar.plugins.web.api.WebConstants;

import java.util.ArrayList;
import java.util.List;

public class WebCodeColorizerFormat extends CodeColorizerFormat {

  private final List<Tokenizer> tokenizers = new ArrayList<Tokenizer>();

  public WebCodeColorizerFormat() {
    super(WebConstants.LANGUAGE_KEY);
    tokenizers.add(new RegexpTokenizer("<span class=\"k\">", "</span>", "</?\\p{L}*>?"));
    tokenizers.add(new RegexpTokenizer("<span class=\"k\">", "</span>", ">"));
  }

  @Override
  public List<Tokenizer> getTokenizers() {
    return tokenizers;
  }

}
