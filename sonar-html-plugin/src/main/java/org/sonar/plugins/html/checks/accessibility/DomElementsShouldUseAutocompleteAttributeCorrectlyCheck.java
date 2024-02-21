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
package org.sonar.plugins.html.checks.accessibility;

import org.sonar.check.Rule;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.api.accessibility.ControlGroup;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Rule(key = "S6840")
public class DomElementsShouldUseAutocompleteAttributeCorrectlyCheck extends AbstractPageCheck {
  static boolean isADirective(String value) {
    return value.startsWith("<%=") || value.startsWith("<?");
  }

  static class Validator {
    protected List<Predicate<Candidate>> constraints;

    public Validator(List<Predicate<Candidate>> constraints) {
      this.constraints = constraints;
    }

    boolean isValid(TagNode tagNode) {
      String autocomplete = tagNode.getAttribute("autocomplete");

      if (autocomplete == null) {
        return true;
      }

      String[] tokens = isADirective(autocomplete) ? new String[]{autocomplete} : autocomplete.split(" ");

      if (tokens.length != this.size()) {
        return false;
      }

      boolean isValid = true;

      for (int i = 0; i < constraints.size(); i++) {
        Predicate<Candidate> constraint = constraints.get(i);
        String token = tokens[i];
        Candidate candidate = new Candidate(token, tagNode);

        isValid = isValid && constraint.test(candidate);
      }

      return isValid;
    }

    int size() {
      return constraints.size();
    }
  }

  static class Token {
    private final String value;

    private final Predicate<TagNode> controlGroupPredicate;

    Token(
      String value,
      Predicate<TagNode> controlGroup
    ) {
      this.value = value;
      this.controlGroupPredicate = controlGroup;
    }
  }

  static class Candidate {
    public final String value;

    public final TagNode tagNode;

    Candidate(
      String value,
      TagNode tagNode
    ) {
      this.value = value;
      this.tagNode = tagNode;
    }

    public boolean satisfies(Token token) {
      /*
       * Directives are considered as wildcards - i.e. they always match the passed token
       */
      return (isADirective(this.value) || this.value.equalsIgnoreCase(token.value)) && token.controlGroupPredicate.test(this.tagNode);
    }
  }

  static List<Validator> validators = List.of(
    new Validator(List.of(
      value -> value.value.isBlank()
    )),
    new Validator(List.of(
      candidate -> isADirective(candidate.value)
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAOnOffToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isASection,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAutofillFieldNameToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isASection,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAutofillFieldNameToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isASection,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAddressType,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAutofillFieldNameToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isASection,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAddressType,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAutofillFieldNameToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isASection,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAddressType,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumValueToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isASection,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAddressType,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumValueToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isASection,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAddressType,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumTypeToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumValueToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isASection,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAddressType,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumTypeToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumValueToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAddressType,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAutofillFieldNameToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAddressType,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAutofillFieldNameToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAddressType,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumTypeToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAddressType,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumTypeToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAddressType,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumTypeToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumValueToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAddressType,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumTypeToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumValueToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAutofillFieldNameToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAnAutofillFieldNameToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumValueToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumValueToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumTypeToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumValueToken
    )),
    new Validator(List.of(
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumTypeToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAMediumValueToken,
      DomElementsShouldUseAutocompleteAttributeCorrectlyCheck::isAWebAuthnToken
    ))
  );

  static boolean isAOnOffToken(Candidate candidate) {
    return Stream.of(
      new Token("on", ControlGroup::belongsToAutofillExpectationMantleControlGroup),
      new Token("off", ControlGroup::belongsToAutofillExpectationMantleControlGroup)
    ).anyMatch(candidate::satisfies);
  }

  static boolean isAMediumTypeToken(Candidate candidate) {
    return Stream.of(
      "home",
      "work",
      "mobile",
      "fax",
      "pager"
    ).anyMatch(item -> item.equalsIgnoreCase(candidate.value));
  }

  static boolean isAMediumValueToken(Candidate candidate) {
    return Stream.of(
      new Token("tel", ControlGroup::belongsToTelControlGroup),
      new Token("tel-country-code", ControlGroup::belongsToTextControlGroup),
      new Token("tel-national", ControlGroup::belongsToTextControlGroup),
      new Token("tel-area-code", ControlGroup::belongsToTextControlGroup),
      new Token("tel-local", ControlGroup::belongsToTextControlGroup),
      new Token("tel-local-prefix", ControlGroup::belongsToTextControlGroup),
      new Token("tel-local-suffix", ControlGroup::belongsToTextControlGroup),
      new Token("tel-extension", ControlGroup::belongsToTextControlGroup),
      new Token("email", ControlGroup::belongsToUsernameControlGroup),
      new Token("impp", ControlGroup::belongsToUrlControlGroup)
    ).anyMatch(candidate::satisfies);
  }

  static boolean isASection(Candidate candidate) {
    return candidate.value.toLowerCase().startsWith("section-");
  }

  static boolean isAnAddressType(Candidate candidate) {
    return Stream.of(
      "billing",
      "shipping"
    ).anyMatch(item -> item.equalsIgnoreCase(candidate.value));
  }

  static boolean isAnAutofillFieldNameToken(Candidate candidate) {
    return Stream.of(
      new Token("name", ControlGroup::belongsToTextControlGroup),
      new Token("honorific-prefix", ControlGroup::belongsToTextControlGroup),
      new Token("given-name", ControlGroup::belongsToTextControlGroup),
      new Token("additional-name", ControlGroup::belongsToTextControlGroup),
      new Token("family-name", ControlGroup::belongsToTextControlGroup),
      new Token("honorific-suffix", ControlGroup::belongsToTextControlGroup),
      new Token("nickname", ControlGroup::belongsToTextControlGroup),
      new Token("username", ControlGroup::belongsToUsernameControlGroup),
      new Token("new-password", ControlGroup::belongsToPasswordControlGroup),
      new Token("current-password", ControlGroup::belongsToPasswordControlGroup),
      new Token("one-time-code", ControlGroup::belongsToPasswordControlGroup),
      new Token("organization-title", ControlGroup::belongsToTextControlGroup),
      new Token("organization", ControlGroup::belongsToTextControlGroup),
      new Token("street-address", ControlGroup::belongsToMultilineControlGroup),
      new Token("address-line1", ControlGroup::belongsToTextControlGroup),
      new Token("address-line2", ControlGroup::belongsToTextControlGroup),
      new Token("address-line3", ControlGroup::belongsToTextControlGroup),
      new Token("address-level4", ControlGroup::belongsToTextControlGroup),
      new Token("address-level3", ControlGroup::belongsToTextControlGroup),
      new Token("address-level2", ControlGroup::belongsToTextControlGroup),
      new Token("address-level1", ControlGroup::belongsToTextControlGroup),
      new Token("country", ControlGroup::belongsToTextControlGroup),
      new Token("country-name", ControlGroup::belongsToTextControlGroup),
      new Token("postal-code", ControlGroup::belongsToTextControlGroup),
      new Token("cc-name", ControlGroup::belongsToTextControlGroup),
      new Token("cc-given-name", ControlGroup::belongsToTextControlGroup),
      new Token("cc-additional-name", ControlGroup::belongsToTextControlGroup),
      new Token("cc-family-name", ControlGroup::belongsToTextControlGroup),
      new Token("cc-number", ControlGroup::belongsToTextControlGroup),
      new Token("cc-exp", ControlGroup::belongsToMonthControlGroup),
      new Token("cc-exp-month", ControlGroup::belongsToNumericControlGroup),
      new Token("cc-exp-year", ControlGroup::belongsToMultilineControlGroup),
      new Token("cc-csc", ControlGroup::belongsToTextControlGroup),
      new Token("cc-type", ControlGroup::belongsToTextControlGroup),
      new Token("transaction-currency", ControlGroup::belongsToTextControlGroup),
      new Token("transaction-amount", ControlGroup::belongsToMultilineControlGroup),
      new Token("language", ControlGroup::belongsToTextControlGroup),
      new Token("bday", ControlGroup::belongsToDateControlGroup),
      new Token("bday-day", ControlGroup::belongsToMultilineControlGroup),
      new Token("bday-month", ControlGroup::belongsToMultilineControlGroup),
      new Token("bday-year", ControlGroup::belongsToMultilineControlGroup),
      new Token("sex", ControlGroup::belongsToTextControlGroup),
      new Token("url", ControlGroup::belongsToUrlControlGroup),
      new Token("photo", ControlGroup::belongsToUrlControlGroup)
    ).anyMatch(candidate::satisfies);
  }

  static boolean isAWebAuthnToken(Candidate candidate) {
    return Stream.of(
      new Token("webauthn", node -> node.getNodeName().equalsIgnoreCase(HtmlConstants.NAME_INPUT) || node.getNodeName().equalsIgnoreCase(HtmlConstants.NAME_TEXTAREA))
    ).anyMatch(candidate::satisfies);
  }

  @Override
  public void startElement(TagNode node) {
    boolean isValid = validators.stream().anyMatch(validator -> validator.isValid(node));

    if (!isValid) {
      createViolation(node, "DOM elements should use the \"autocomplete\" attribute correctly");
    }
  }
}
