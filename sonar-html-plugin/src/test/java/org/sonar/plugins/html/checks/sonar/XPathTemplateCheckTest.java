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
  void test_xpath_simple_element() {
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
  void test_xpath_no_matches() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "blink";
    check.message = "Blink tag found: {0}";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  void test_empty_xpath_expression() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  void test_xpath_with_attribute() {
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
  void test_xpath_with_attribute_value() {
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
  void test_xpath_multiple_matches() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//script | //img";
    check.message = "Script or image element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(7).withMessage("Script or image element found")
      .next().atLine(11).withMessage("Script or image element found")
      .noMore();
  }

  @Test
  void test_self_closing_tags() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//img";
    check.message = "Image element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // img line 11 + img line 12
    assertThat(sourceCode.getIssues()).hasSize(2);
  }

  @Test
  void test_void_elements() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//br | //hr";
    check.message = "Void element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // br lines 13, 14 + hr lines 15, 16
    assertThat(sourceCode.getIssues()).hasSize(4);
  }

  @Test
  void test_document_structure_elements() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//html | //head | //body";
    check.message = "Document structure element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // html line 2, head line 3, body line 9
    assertThat(sourceCode.getIssues()).hasSize(3);
  }

  @Test
  void test_meta_and_link_tags() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//meta[@charset]";
    check.message = "Meta with charset found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    assertThat(sourceCode.getIssues()).hasSize(1);
  }

  @Test
  void test_text_formatting_elements() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//b | //strong | //i | //em";
    check.message = "Text formatting element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // b(2) + strong(1) + i(2) + em(1) = 6 start elements
    assertThat(sourceCode.getIssues()).hasSize(6);
  }

  @Test
  void test_underline_and_inserted_text() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//u | //ins";
    check.message = "Underline or inserted text found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // u(2) + ins(1) = 3 start elements
    assertThat(sourceCode.getIssues()).hasSize(3);
  }

  @Test
  void test_deprecated_text_formatting() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//del | //s";
    check.message = "Deleted or strikethrough text found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // del(1) + s(1) = 2 start elements
    assertThat(sourceCode.getIssues()).hasSize(2);
  }

  @Test
  void test_code_and_preformatted_elements() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//code | //pre";
    check.message = "Code or preformatted element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // code(1) + pre(1) = 2 start elements
    assertThat(sourceCode.getIssues()).hasSize(2);
  }

  @Test
  void test_superscript_and_subscript() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//sub | //sup";
    check.message = "Subscript or superscript found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // sub(1) + sup(1) = 2 start elements
    assertThat(sourceCode.getIssues()).hasSize(2);
  }

  @Test
  void test_nested_text_formatting() {
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
  void test_text_with_specific_attributes() {
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
  void test_links_within_text() {
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
  void test_all_formatting_elements_count() {
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
  void test_text_node_support() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//p[contains(text(), 'Text')]";
    check.message = "Paragraph with 'Text' found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // 9 paragraphs (lines 26-34) start with "Text with"
    assertThat(sourceCode.getIssues()).hasSize(9);
  }

  @Test
  void test_text_node_normalize_space() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//p[normalize-space()]";
    check.message = "Paragraph with non-empty text";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // All 11 paragraphs (lines 26-35, 40) have non-empty text
    assertThat(sourceCode.getIssues()).hasSize(11);
  }

  @Test
  void test_stale_state_not_carried_over() {
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
    assertThat(sourceCode2.getIssues()).isEmpty();
  }

  @Test
  void test_nodeset_evaluation_with_multiple_matches() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//img | //div";
    check.message = "Image or div found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    // img line 7 + div line 8
    assertThat(sourceCode.getIssues()).hasSize(2);
  }

  @Test
  void test_boolean_evaluation_true() {
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
  void test_boolean_evaluation_false() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "count(//img) > 100";
    check.message = "File has more than 100 images";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    // Should not create any issues when boolean is false
    assertThat(sourceCode.getIssues()).isEmpty();
  }

  @Test
  void test_invalid_xpath_expression() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//img[invalid syntax";
    check.message = "Invalid XPath";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    assertThat(sourceCode.getIssues()).isEmpty();
  }

  @Test
  void test_xpath_with_no_matches() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//nonexistent";
    check.message = "Nonexistent element found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    // Should produce no issues when XPath matches nothing
    assertThat(sourceCode.getIssues()).isEmpty();
  }

  @Test
  void test_complex_xpath_with_predicates() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//div[@class='content' and count(.//*) > 0]";
    check.message = "Content div with children found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/TestXPath.html"),
      check);
    // div.content (line 8) has child elements
    assertThat(sourceCode.getIssues()).hasSize(1);
  }

  @Test
  void test_xpath_with_text_predicate() {
    XPathTemplateCheck check = new XPathTemplateCheck();
    check.expression = "//div[text()]";
    check.message = "Div with text content found";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/XPathTemplateCheck/HtmlSpecifics.html"),
      check);
    // 1 div with non-blank direct text content
    assertThat(sourceCode.getIssues()).hasSize(1);
  }
}
