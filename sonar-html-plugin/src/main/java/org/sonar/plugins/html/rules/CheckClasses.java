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
import org.sonar.plugins.html.checks.accessibility.AnchorsShouldNotBeUsedAsButtonsCheck;
import org.sonar.plugins.html.checks.accessibility.AriaActiveDescendantHasTabIndexCheck;
import org.sonar.plugins.html.checks.accessibility.AriaProptypesCheck;
import org.sonar.plugins.html.checks.accessibility.AriaRoleCheck;
import org.sonar.plugins.html.checks.accessibility.AriaUnsupportedElementsCheck;
import org.sonar.plugins.html.checks.accessibility.ElementWithRoleShouldHaveRequiredPropertiesCheck;
import org.sonar.plugins.html.checks.accessibility.FocusableInteractiveElementsCheck;
import org.sonar.plugins.html.checks.accessibility.NoAriaHiddenOnFocusableCheck;
import org.sonar.plugins.html.checks.accessibility.PreferTagOverRoleCheck;
import org.sonar.plugins.html.checks.accessibility.RoleSupportsAriaPropertyCheck;
import org.sonar.plugins.html.checks.accessibility.ValidAutocompleteCheck;
import org.sonar.plugins.html.checks.accessibility.ImgRedundantAltCheck;
import org.sonar.plugins.html.checks.accessibility.LabelHasAssociatedControlCheck;
import org.sonar.plugins.html.checks.accessibility.NoInteractiveElementToNoninteractiveRoleCheck;
import org.sonar.plugins.html.checks.accessibility.NoNonInteractiveElementsWithHandlersCheck;
import org.sonar.plugins.html.checks.accessibility.NoNoninteractiveElementToInteractiveRoleCheck;
import org.sonar.plugins.html.checks.accessibility.NoNoninteractiveTabIndexCheck;
import org.sonar.plugins.html.checks.accessibility.NoRedundantRolesCheck;
import org.sonar.plugins.html.checks.accessibility.NoStaticElementInteractionsCheck;
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
    AnchorsShouldNotBeUsedAsButtonsCheck.class,
    AriaActiveDescendantHasTabIndexCheck.class,
    AriaProptypesCheck.class,
    AriaRoleCheck.class,
    AriaUnsupportedElementsCheck.class,
    AvoidCommentedOutCodeCheck.class,
    AvoidHtmlCommentCheck.class,
    BoldAndItalicTagsCheck.class,
    ChildElementIllegalCheck.class,
    ChildElementRequiredCheck.class,
    ComplexityCheck.class,
    DeprecatedAttributesInHtml5Check.class,
    DisabledAutoescapeCheck.class,
    DoctypePresenceCheck.class,
    DoubleQuotesCheck.class,
    DynamicJspIncludeCheck.class,
    ElementWithGivenIdPresentCheck.class,
    ElementWithRoleShouldHaveRequiredPropertiesCheck.class,
    FieldsetWithoutLegendCheck.class,
    FileLengthCheck.class,
    FixmeCommentCheck.class,
    FlashUsesBothObjectAndEmbedCheck.class,
    FocusableInteractiveElementsCheck.class,
    FrameWithoutTitleCheck.class,
    HeaderCheck.class,
    HeadingHasAccessibleContentCheck.class,
    IllegalAttributeCheck.class,
    IllegalElementCheck.class,
    IllegalNamespaceCheck.class,
    IllegalTabCheck.class,
    IllegalTagLibsCheck.class,
    ImgRedundantAltCheck.class,
    ImgWithoutAltCheck.class,
    ImgWithoutWidthOrHeightCheck.class,
    IndistinguishableSimilarElementsCheck.class,
    InlineStyleCheck.class,
    InputWithoutLabelCheck.class,
    InternationalizationCheck.class,
    ItemTagNotWithinContainerTagCheck.class,
    JspScriptletCheck.class,
    LabelHasAssociatedControlCheck.class,
    LangAttributeCheck.class,
    LayoutTableCheck.class,
    LayoutTableWithSemanticMarkupCheck.class,
    LibraryDependencyCheck.class,
    LinksIdenticalTextsDifferentTargetsCheck.class,
    LinkToImageCheck.class,
    LinkToNothingCheck.class,
    LinkWithTargetBlankCheck.class,
    LongJavaScriptCheck.class,
    MaxLineLengthCheck.class,
    MetaRefreshCheck.class,
    MouseEventWithoutKeyboardEquivalentCheck.class,
    MultiplePageDirectivesCheck.class,
    NestedJavaScriptCheck.class,
    NoAccessKeyCheck.class,
    NoAriaHiddenOnFocusableCheck.class,
    NoInteractiveElementToNoninteractiveRoleCheck.class,
    NonConsecutiveHeadingCheck.class,
    NoNonInteractiveElementsWithHandlersCheck.class,
    NoNoninteractiveElementToInteractiveRoleCheck.class,
    NoStaticElementInteractionsCheck.class,
    NoNoninteractiveTabIndexCheck.class,
    NoRedundantRolesCheck.class,
    ObjectWithAlternativeContentCheck.class,
    PageWithoutFaviconCheck.class,
    PageWithoutTitleCheck.class,
    ParentElementIllegalCheck.class,
    ParentElementRequiredCheck.class,
    PreferTagOverRoleCheck.class,
    RequiredAttributeCheck.class,
    ResourceIntegrityCheck.class,
    RoleSupportsAriaPropertyCheck.class,
    ServerSideImageMapsCheck.class,
    TabIndexNoPositiveCheck.class,
    TableHeaderHasIdOrScopeCheck.class,
    TableHeaderReferenceCheck.class,
    TableWithoutCaptionCheck.class,
    TableWithoutHeaderCheck.class,
    TodoCommentCheck.class,
    UnclosedTagCheck.class,
    UnifiedExpressionCheck.class,
    UnsupportedTagsInHtml5Check.class,
    ValidAutocompleteCheck.class,
    VideoTrackCheck.class,
    WhiteSpaceAroundCheck.class,
    WmodeIsWindowCheck.class
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
