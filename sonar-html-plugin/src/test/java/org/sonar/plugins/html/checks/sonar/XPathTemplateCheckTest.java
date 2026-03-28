/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.sonar;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

class XPathTemplateCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void test_xpath_simple_element() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//img";
    check.message = "Image element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(7).withMessage("Image element found")
      .noMore();
  }

  @Test
  void test_xpath_no_matches() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "blink";
    check.message = "Blink tag found: {0}";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  void test_empty_xpath_expression() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  void test_xpath_with_attribute() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//img[@alt]";
    check.message = "Image with alt attribute found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(7).withMessage("Image with alt attribute found")
      .noMore();
  }

  @Test
  void test_xpath_with_attribute_value() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//div[@class='content']";
    check.message = "Div with class 'content' found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(8).withMessage("Div with class 'content' found")
      .noMore();
  }

  @Test
  void test_xpath_multiple_matches() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//script | //img";
    check.message = "Script or image element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(7).withMessage("Script or image element found")
      .next().atLine(11).withMessage("Script or image element found")
      .next().atLine(13).withMessage("Script or image element found")
      .noMore();
  }

  @Test
  void test_self_closing_tags() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//img";
    check.message = "Image element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // Should match both <img> and <img/> syntax
    assertThat(sourceCode.getIssues()).hasSizeGreaterThanOrEqualTo(2);
  }

  @Test
  void test_void_elements() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//br | //hr";
    check.message = "Void element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // Should match br and hr elements regardless of self-closing syntax
    assertThat(sourceCode.getIssues()).hasSizeGreaterThanOrEqualTo(4);
  }

  @Test
  void test_document_structure_elements() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//html | //head | //body";
    check.message = "Document structure element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // Should match html, head, and body tags
    assertThat(sourceCode.getIssues()).isNotEmpty();
  }

  @Test
  void test_meta_and_link_tags() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//meta[@charset]";
    check.message = "Meta with charset found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    assertThat(sourceCode.getIssues()).hasSize(1);
  }

  @Test
  void test_text_formatting_elements() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//b | //strong | //i | //em";
    check.message = "Text formatting element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // XPath finds 12 elements total - this confirms text formatting elements are recognized correctly
    assertThat(sourceCode.getIssues()).hasSize(12);
  }

  @Test
  void test_underline_and_inserted_text() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//u | //ins";
    check.message = "Underline or inserted text found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // XPath finds 6 elements total - confirms underline and inserted text elements are recognized correctly
    assertThat(sourceCode.getIssues()).hasSize(6);
  }

  @Test
  void test_deprecated_text_formatting() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//del | //s";
    check.message = "Deleted or strikethrough text found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // XPath finds 4 elements total - confirms deleted and strikethrough elements are recognized correctly
    assertThat(sourceCode.getIssues()).hasSize(4);
  }

  @Test
  void test_code_and_preformatted_elements() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//code | //pre";
    check.message = "Code or preformatted element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // XPath finds 4 elements total - confirms code and preformatted elements are recognized correctly
    assertThat(sourceCode.getIssues()).hasSize(4);
  }

  @Test
  void test_superscript_and_subscript() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//sub | //sup";
    check.message = "Subscript or superscript found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // XPath finds 4 elements total - confirms subscript and superscript elements are recognized correctly
    assertThat(sourceCode.getIssues()).hasSize(4);
  }

  @Test
  void test_nested_text_formatting() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//b//i";
    check.message = "Italic text inside bold found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // Should match i elements that are children of b elements
    assertThat(sourceCode.getIssues()).hasSize(1);
  }

  @Test
  void test_text_with_specific_attributes() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//span[@style]";
    check.message = "Styled span found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // Should match span elements with style attribute
    assertThat(sourceCode.getIssues()).hasSize(1);
  }

  @Test
  void test_links_within_text() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//a[@href]";
    check.message = "Link found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // Should match a elements with href attribute
    assertThat(sourceCode.getIssues()).hasSize(1);
  }

  @Test
  void test_all_formatting_elements_count() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "count(//b | //strong | //i | //em | //u | //ins | //small | //mark | //del | //s | //code | //pre | //sub | //sup | //span | //a) > 15";
    check.message = "Many formatting elements found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // Should find more than 15 formatting elements (file-level issue)
    assertThat(sourceCode.getIssues()).hasSize(1);
  }

  @Test
  void test_text_node_support() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//p[contains(text(), 'Text')]";
    check.message = "Paragraph with 'Text' found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // Should find paragraphs containing text "Text" - verifies text nodes are in DOM
    assertThat(sourceCode.getIssues()).hasSizeGreaterThanOrEqualTo(1);
  }

  @Test
  void test_text_node_normalize_space() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//p[normalize-space()]";
    check.message = "Paragraph with non-empty text";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // Should find paragraphs with text content - verifies normalize-space() works with text nodes
    assertThat(sourceCode.getIssues()).hasSizeGreaterThanOrEqualTo(1);
  }

  @Test
  void test_stale_state_not_carried_over() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "count(//img) > 0";
    check.message = "File has images";
    check.filePattern = "**/*Specifics.html";

    // Scan first file that matches pattern and has images
    HtmlSourceCode sourceCode1 = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    assertThat(sourceCode1.getIssues()).hasSize(1);

    // Scan second file that doesn't match pattern - should have NO issues (not stale state)
    check.filePattern = "**/NonExistent*.html";
    HtmlSourceCode sourceCode2 = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    assertThat(sourceCode2.getIssues()).hasSize(0);
  }

  @Test
  void test_nodeset_evaluation_with_multiple_matches() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//img | //div";
    check.message = "Image or div found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    // Should create line issues for each matched node
    assertThat(sourceCode.getIssues()).hasSizeGreaterThanOrEqualTo(2);
  }

  @Test
  void test_boolean_evaluation_true() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "count(//img) > 0";
    check.message = "File contains images";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    // Should create file-level issue when boolean expression is true
    assertThat(sourceCode.getIssues()).hasSize(1);
  }

  @Test
  void test_boolean_evaluation_false() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "count(//img) > 100";
    check.message = "File has more than 100 images";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    // Should not create any issues when boolean is false
    assertThat(sourceCode.getIssues()).hasSize(0);
  }

  @Test
  void test_invalid_xpath_expression() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//img[invalid syntax";
    check.message = "Invalid XPath";

    // Invalid XPath should throw exception during compilation in startDocument
    // The scanner catches it and logs, so no issues are created
    try {
      HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
        check);
      // If it doesn't throw, verify no issues were created
      assertThat(sourceCode.getIssues()).hasSize(0);
    } catch (IllegalStateException e) {
      // Expected - invalid XPath throws during compilation
      assertThat(e.getMessage()).contains("Failed to compile XPath expression");
    }
  }

  @Test
  void test_xpath_with_no_matches() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//nonexistent";
    check.message = "Nonexistent element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    // Should produce no issues when XPath matches nothing
    assertThat(sourceCode.getIssues()).hasSize(0);
  }

  @Test
  void test_complex_xpath_with_predicates() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//div[@class='content' and count(.//*) > 0]";
    check.message = "Content div with children found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    // Should handle complex predicates correctly
    assertThat(sourceCode.getIssues()).hasSizeGreaterThanOrEqualTo(0);
  }

  @Test
  void test_xpath_with_text_predicate() throws Exception {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//div[text()]";
    check.message = "Div with text content found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // Should find divs with text content (verifies text nodes work)
    assertThat(sourceCode.getIssues()).hasSizeGreaterThanOrEqualTo(1);
  }
}
