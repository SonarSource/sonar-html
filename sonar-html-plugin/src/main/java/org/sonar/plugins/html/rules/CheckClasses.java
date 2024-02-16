/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.rules;

import java.util.List;

import org.sonar.plugins.html.checks.accessibility.AnchorsHaveContentCheck;
import org.sonar.plugins.html.checks.accessibility.AriaProptypesCheck;
import org.sonar.plugins.html.checks.accessibility.ImgRedundantAltCheck;
import org.sonar.plugins.html.checks.accessibility.LabelHasAssociatedControlCheck;
import org.sonar.plugins.html.checks.accessibility.TabIndexNoPositiveCheck;
import org.sonar.plugins.html.checks.attributes.IllegalAttributeCheck;
import org.sonar.plugins.html.checks.attributes.NoAccessKeyCheck;
import org.sonar.plugins.html.checks.attributes.RequiredAttributeCheck;
import org.sonar.plugins.html.checks.coding.ComplexityCheck;
import org.sonar.plugins.html.checks.coding.DoubleQuotesCheck;
import org.sonar.plugins.html.checks.coding.FileLengthCheck;
import org.sonar.plugins.html.checks.coding.InternationalizationCheck;
import org.sonar.plugins.html.checks.coding.MaxLineLengthCheck;
import org.sonar.plugins.html.checks.coding.UnclosedTagCheck;
import org.sonar.plugins.html.checks.comments.AvoidCommentedOutCodeCheck;
import org.sonar.plugins.html.checks.comments.AvoidHtmlCommentCheck;
import org.sonar.plugins.html.checks.comments.FixmeCommentCheck;
import org.sonar.plugins.html.checks.comments.TodoCommentCheck;
import org.sonar.plugins.html.checks.dependencies.DynamicJspIncludeCheck;
import org.sonar.plugins.html.checks.dependencies.IllegalNamespaceCheck;
import org.sonar.plugins.html.checks.dependencies.IllegalTagLibsCheck;
import org.sonar.plugins.html.checks.dependencies.LibraryDependencyCheck;
import org.sonar.plugins.html.checks.header.HeaderCheck;
import org.sonar.plugins.html.checks.header.MultiplePageDirectivesCheck;
import org.sonar.plugins.html.checks.security.DisabledAutoescapeCheck;
import org.sonar.plugins.html.checks.scripting.JspScriptletCheck;
import org.sonar.plugins.html.checks.scripting.LongJavaScriptCheck;
import org.sonar.plugins.html.checks.scripting.NestedJavaScriptCheck;
import org.sonar.plugins.html.checks.scripting.UnifiedExpressionCheck;
import org.sonar.plugins.html.checks.security.ResourceIntegrityCheck;
import org.sonar.plugins.html.checks.sonar.AbsoluteURICheck;
import org.sonar.plugins.html.checks.sonar.BoldAndItalicTagsCheck;
import org.sonar.plugins.html.checks.sonar.DeprecatedAttributesInHtml5Check;
import org.sonar.plugins.html.checks.sonar.DoctypePresenceCheck;
import org.sonar.plugins.html.checks.sonar.ElementWithGivenIdPresentCheck;
import org.sonar.plugins.html.checks.sonar.FieldsetWithoutLegendCheck;
import org.sonar.plugins.html.checks.sonar.FlashUsesBothObjectAndEmbedCheck;
import org.sonar.plugins.html.checks.sonar.FrameWithoutTitleCheck;
import org.sonar.plugins.html.checks.accessibility.HeadingHasAccessibleContentCheck;
import org.sonar.plugins.html.checks.sonar.ImgWithoutAltCheck;
import org.sonar.plugins.html.checks.sonar.ImgWithoutWidthOrHeightCheck;
import org.sonar.plugins.html.checks.sonar.IndistinguishableSimilarElementsCheck;
import org.sonar.plugins.html.checks.sonar.InputWithoutLabelCheck;
import org.sonar.plugins.html.checks.sonar.ItemTagNotWithinContainerTagCheck;
import org.sonar.plugins.html.checks.sonar.LangAttributeCheck;
import org.sonar.plugins.html.checks.sonar.LayoutTableCheck;
import org.sonar.plugins.html.checks.sonar.LayoutTableWithSemanticMarkupCheck;
import org.sonar.plugins.html.checks.sonar.LinkToImageCheck;
import org.sonar.plugins.html.checks.sonar.LinkToNothingCheck;
import org.sonar.plugins.html.checks.sonar.LinkWithTargetBlankCheck;
import org.sonar.plugins.html.checks.sonar.LinksIdenticalTextsDifferentTargetsCheck;
import org.sonar.plugins.html.checks.sonar.MetaRefreshCheck;
import org.sonar.plugins.html.checks.sonar.MouseEventWithoutKeyboardEquivalentCheck;
import org.sonar.plugins.html.checks.sonar.NonConsecutiveHeadingCheck;
import org.sonar.plugins.html.checks.sonar.ObjectWithAlternativeContentCheck;
import org.sonar.plugins.html.checks.sonar.PageWithoutFaviconCheck;
import org.sonar.plugins.html.checks.sonar.PageWithoutTitleCheck;
import org.sonar.plugins.html.checks.sonar.ServerSideImageMapsCheck;
import org.sonar.plugins.html.checks.sonar.TableHeaderHasIdOrScopeCheck;
import org.sonar.plugins.html.checks.sonar.TableHeaderReferenceCheck;
import org.sonar.plugins.html.checks.sonar.TableWithoutCaptionCheck;
import org.sonar.plugins.html.checks.sonar.TableWithoutHeaderCheck;
import org.sonar.plugins.html.checks.sonar.UnsupportedTagsInHtml5Check;
import org.sonar.plugins.html.checks.sonar.VideoTrackCheck;
import org.sonar.plugins.html.checks.sonar.WmodeIsWindowCheck;
import org.sonar.plugins.html.checks.structure.ChildElementIllegalCheck;
import org.sonar.plugins.html.checks.structure.ChildElementRequiredCheck;
import org.sonar.plugins.html.checks.structure.IllegalElementCheck;
import org.sonar.plugins.html.checks.structure.ParentElementIllegalCheck;
import org.sonar.plugins.html.checks.structure.ParentElementRequiredCheck;
import org.sonar.plugins.html.checks.style.InlineStyleCheck;
import org.sonar.plugins.html.checks.whitespace.IllegalTabCheck;
import org.sonar.plugins.html.checks.whitespace.WhiteSpaceAroundCheck;

public final class CheckClasses {

  private static final List<Class<?>> CLASSES = List.of(
    AbsoluteURICheck.class,
    AnchorsHaveContentCheck.class,
    AriaProptypesCheck.class,
    AvoidHtmlCommentCheck.class,
    ChildElementRequiredCheck.class,
    ComplexityCheck.class,
    DeprecatedAttributesInHtml5Check.class,
    DoubleQuotesCheck.class,
    DynamicJspIncludeCheck.class,
    FileLengthCheck.class,
    HeadingHasAccessibleContentCheck.class,
    IllegalElementCheck.class,
    IllegalTabCheck.class,
    IllegalTagLibsCheck.class,
    ImgRedundantAltCheck.class,
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
    LinkWithTargetBlankCheck.class,
    ServerSideImageMapsCheck.class,
    FrameWithoutTitleCheck.class,
    BoldAndItalicTagsCheck.class,
    MouseEventWithoutKeyboardEquivalentCheck.class,
    PageWithoutTitleCheck.class,
    VideoTrackCheck.class,
    ItemTagNotWithinContainerTagCheck.class,
    FieldsetWithoutLegendCheck.class,
    WmodeIsWindowCheck.class,
    TableWithoutCaptionCheck.class,
    LinksIdenticalTextsDifferentTargetsCheck.class,
    FlashUsesBothObjectAndEmbedCheck.class,
    DoctypePresenceCheck.class,
    TableHeaderHasIdOrScopeCheck.class,
    InputWithoutLabelCheck.class,
    DisabledAutoescapeCheck.class,
    ImgWithoutWidthOrHeightCheck.class,
    PageWithoutFaviconCheck.class,
    TodoCommentCheck.class,
    FixmeCommentCheck.class,
    ElementWithGivenIdPresentCheck.class,
    LayoutTableCheck.class,
    LayoutTableWithSemanticMarkupCheck.class,
    TableWithoutHeaderCheck.class,
    LangAttributeCheck.class,
    ObjectWithAlternativeContentCheck.class,
    IndistinguishableSimilarElementsCheck.class,
    TableHeaderReferenceCheck.class,
    ResourceIntegrityCheck.class,
    LabelHasAssociatedControlCheck.class,
    NoAccessKeyCheck.class,
    TabIndexNoPositiveCheck.class
  );

  private CheckClasses() {
  }

  /**
   * Gets the list of XML checks.
   */
  public static List<Class<?>> getCheckClasses() {
    return CLASSES;
  }

}
