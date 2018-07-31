/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
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

import com.google.common.collect.ImmutableList;
import java.util.List;
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
import org.sonar.plugins.web.checks.header.HeaderCheck;
import org.sonar.plugins.web.checks.header.MultiplePageDirectivesCheck;
import org.sonar.plugins.web.checks.scripting.JspScriptletCheck;
import org.sonar.plugins.web.checks.scripting.LongJavaScriptCheck;
import org.sonar.plugins.web.checks.scripting.NestedJavaScriptCheck;
import org.sonar.plugins.web.checks.scripting.UnifiedExpressionCheck;
import org.sonar.plugins.web.checks.sonar.AbsoluteURICheck;
import org.sonar.plugins.web.checks.sonar.BoldAndItalicTagsCheck;
import org.sonar.plugins.web.checks.sonar.DeprecatedAttributesInHtml5Check;
import org.sonar.plugins.web.checks.sonar.DoctypePresenceCheck;
import org.sonar.plugins.web.checks.sonar.ElementWithGivenIdPresentCheck;
import org.sonar.plugins.web.checks.sonar.FieldsetWithoutLegendCheck;
import org.sonar.plugins.web.checks.sonar.FlashUsesBothObjectAndEmbedCheck;
import org.sonar.plugins.web.checks.sonar.FrameWithoutTitleCheck;
import org.sonar.plugins.web.checks.sonar.ImgWithoutAltCheck;
import org.sonar.plugins.web.checks.sonar.ImgWithoutWidthOrHeightCheck;
import org.sonar.plugins.web.checks.sonar.InputWithoutLabelCheck;
import org.sonar.plugins.web.checks.sonar.ItemTagNotWithinContainerTagCheck;
import org.sonar.plugins.web.checks.sonar.LinkToImageCheck;
import org.sonar.plugins.web.checks.sonar.LinkToNothingCheck;
import org.sonar.plugins.web.checks.sonar.LinksIdenticalTextsDifferentTargetsCheck;
import org.sonar.plugins.web.checks.sonar.MetaRefreshCheck;
import org.sonar.plugins.web.checks.sonar.MouseEventWithoutKeyboardEquivalentCheck;
import org.sonar.plugins.web.checks.sonar.NonConsecutiveHeadingCheck;
import org.sonar.plugins.web.checks.sonar.PageWithoutFaviconCheck;
import org.sonar.plugins.web.checks.sonar.PageWithoutTitleCheck;
import org.sonar.plugins.web.checks.sonar.ServerSideImageMapsCheck;
import org.sonar.plugins.web.checks.sonar.TableHeaderHasIdOrScopeCheck;
import org.sonar.plugins.web.checks.sonar.TableWithoutCaptionCheck;
import org.sonar.plugins.web.checks.sonar.UnsupportedTagsInHtml5Check;
import org.sonar.plugins.web.checks.sonar.WmodeIsWindowCheck;
import org.sonar.plugins.web.checks.structure.ChildElementIllegalCheck;
import org.sonar.plugins.web.checks.structure.ChildElementRequiredCheck;
import org.sonar.plugins.web.checks.structure.IllegalElementCheck;
import org.sonar.plugins.web.checks.structure.ParentElementIllegalCheck;
import org.sonar.plugins.web.checks.structure.ParentElementRequiredCheck;
import org.sonar.plugins.web.checks.style.InlineStyleCheck;
import org.sonar.plugins.web.checks.whitespace.IllegalTabCheck;
import org.sonar.plugins.web.checks.whitespace.WhiteSpaceAroundCheck;

public final class CheckClasses {

  private static final List<Class> CLASSES = ImmutableList.of(
    AbsoluteURICheck.class,
    AvoidHtmlCommentCheck.class,
    ChildElementRequiredCheck.class,
    ComplexityCheck.class,
    DeprecatedAttributesInHtml5Check.class,
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
    NestedJavaScriptCheck.class,
    MaxLineLengthCheck.class,
    ParentElementIllegalCheck.class,
    ParentElementRequiredCheck.class,
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
    UnsupportedTagsInHtml5Check.class,
    NonConsecutiveHeadingCheck.class,
    MetaRefreshCheck.class,
    LinkToImageCheck.class,
    LinkToNothingCheck.class,
    ServerSideImageMapsCheck.class,
    FrameWithoutTitleCheck.class,
    BoldAndItalicTagsCheck.class,
    MouseEventWithoutKeyboardEquivalentCheck.class,
    PageWithoutTitleCheck.class,
    ItemTagNotWithinContainerTagCheck.class,
    FieldsetWithoutLegendCheck.class,
    WmodeIsWindowCheck.class,
    TableWithoutCaptionCheck.class,
    LinksIdenticalTextsDifferentTargetsCheck.class,
    FlashUsesBothObjectAndEmbedCheck.class,
    DoctypePresenceCheck.class,
    TableHeaderHasIdOrScopeCheck.class,
    InputWithoutLabelCheck.class,
    ImgWithoutWidthOrHeightCheck.class,
    PageWithoutFaviconCheck.class,
    ElementWithGivenIdPresentCheck.class
  );

  private CheckClasses() {
  }

  /**
   * Gets the list of XML checks.
   */
  @SuppressWarnings("rawtypes")
  public static List<Class> getCheckClasses() {
    return CLASSES;
  }

}
