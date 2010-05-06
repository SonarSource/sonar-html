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

package org.sonar.plugins.web.lex;

import org.sonar.api.batch.SensorContext;
import org.sonar.plugins.web.language.WebFile;

/**
 * @author Matthijs Galesloot
 */
public abstract class HtmlVisitor {

  private WebFile resource;
  private SensorContext sensorContext;

  public void destroy() {
    
  }
  
  public void endDocument(SensorContext sensorContext, WebFile resource) {

  }
  
  public void endElement(Token token) {
    
  }

  protected WebFile getResource() {
    return resource;
  }

  protected SensorContext getSensorContext() {
    return sensorContext;
  }

  public void init() {
    
  }
  
  public void startDocument(SensorContext sensorContext, WebFile resource) {
    this.sensorContext = sensorContext;
    this.resource = resource;
  }

  public void startElement(Token token) {
    
  }
}
