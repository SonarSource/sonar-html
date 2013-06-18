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
package org.sonar.plugins.web.rules;

import org.sonar.plugins.web.checks.attributes.AttributeValidationCheck;
import org.sonar.plugins.web.checks.attributes.IllegalAttributeCheck;
import org.sonar.plugins.web.checks.attributes.RequiredAttributeCheck;
import org.sonar.plugins.web.checks.coding.ComplexityCheck;
import org.sonar.plugins.web.checks.coding.DoubleQuotesCheck;
import org.sonar.plugins.web.checks.coding.FileLengthCheck;
import org.sonar.plugins.web.checks.coding.InternationalizationCheck;
import org.sonar.plugins.web.checks.coding.MaxLineLengthCheck;
import org.sonar.plugins.web.checks.coding.UnclosedTagCheck;
import org.sonar.plugins.web.checks.comments.AvoidCommentedOutCodeCheck;
import org.sonar.plugins.web.checks.comments.AvoidHtmlCommentCheck;
import org.sonar.plugins.web.checks.dependencies.DynamicJspIncludeCheck;
import org.sonar.plugins.web.checks.dependencies.IllegalNamespaceCheck;
import org.sonar.plugins.web.checks.dependencies.IllegalTagLibsCheck;
import org.sonar.plugins.web.checks.dependencies.LibraryDependencyCheck;
import org.sonar.plugins.web.checks.generic.RegularExpressionCheck;
import org.sonar.plugins.web.checks.header.DocTypeCheck;
import org.sonar.plugins.web.checks.header.HeaderCheck;
import org.sonar.plugins.web.checks.header.MultiplePageDirectivesCheck;
import org.sonar.plugins.web.checks.scripting.JspScriptletCheck;
import org.sonar.plugins.web.checks.scripting.LongJavaScriptCheck;
import org.sonar.plugins.web.checks.scripting.OGNLExpressionCheck;
import org.sonar.plugins.web.checks.scripting.UnifiedExpressionCheck;
import org.sonar.plugins.web.checks.sonar.ImgWithoutAltCheck;
import org.sonar.plugins.web.checks.sonar.UnsupportedTagsInHtml5Check;
import org.sonar.plugins.web.checks.structure.ChildElementIllegalCheck;
import org.sonar.plugins.web.checks.structure.ChildElementRequiredCheck;
import org.sonar.plugins.web.checks.structure.IllegalElementCheck;
import org.sonar.plugins.web.checks.structure.ParentElementIllegalCheck;
import org.sonar.plugins.web.checks.structure.ParentElementRequiredCheck;
import org.sonar.plugins.web.checks.structure.RequiredElementCheck;
import org.sonar.plugins.web.checks.style.InlineStyleCheck;
import org.sonar.plugins.web.checks.whitespace.IllegalTabCheck;
import org.sonar.plugins.web.checks.whitespace.WhiteSpaceAroundCheck;

import java.util.Arrays;
import java.util.List;

/**
 * Provides a list of available checks.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
final class CheckClasses {

  @SuppressWarnings("rawtypes")
  private static final Class[] CLASSES = new Class[] {
    AttributeValidationCheck.class,
    AvoidHtmlCommentCheck.class,
    ChildElementRequiredCheck.class,
    ComplexityCheck.class,
    DocTypeCheck.class,
    DoubleQuotesCheck.class,
    DynamicJspIncludeCheck.class,
    FileLengthCheck.class,
    IllegalElementCheck.class,
    IllegalTabCheck.class,
    IllegalTagLibsCheck.class,
    InlineStyleCheck.class,
    InternationalizationCheck.class,
    JspScriptletCheck.class,
    LibraryDependencyCheck.class,
    LongJavaScriptCheck.class,
    MaxLineLengthCheck.class,
    OGNLExpressionCheck.class,
    ParentElementIllegalCheck.class,
    ParentElementRequiredCheck.class,
    RegularExpressionCheck.class,
    RequiredElementCheck.class,
    UnclosedTagCheck.class,
    UnifiedExpressionCheck.class,
    WhiteSpaceAroundCheck.class,
    ChildElementIllegalCheck.class,
    HeaderCheck.class,
    IllegalAttributeCheck.class,
    IllegalNamespaceCheck.class,
    MultiplePageDirectivesCheck.class,
    RequiredAttributeCheck.class,
    AvoidCommentedOutCodeCheck.class,
    ImgWithoutAltCheck.class,
    UnsupportedTagsInHtml5Check.class
  };

  /**
   * Gets the list of XML checks.
   */
  @SuppressWarnings("rawtypes")
  public static List<Class> getCheckClasses() {
    return Arrays.asList(CLASSES);
  }

  private CheckClasses() {

  }
}
