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
package org.sonar.plugins.html.checks.accessibility;

import org.sonar.check.Rule;
import org.sonar.plugins.html.api.accessibility.ControlGroup;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Rule(key = "S6840")
public class ValidAutocompleteCheck extends AbstractPageCheck {
  /**
   * A very naive and straightforward implementation of a validator capable of testing a candidate against a series
   * of predicates until either one is matched or none is. Should totally be replaced by a validation library would
   * we eventually decide to use one.
   */
  static class Validator {
    protected List<Predicate<Candidate>> predicates;

    public Validator(List<Predicate<Candidate>> predicates) {
      this.predicates = predicates;
    }

    boolean isValid(TagNode tagNode) {
      var autocomplete = tagNode.getAttribute("autocomplete");

      if (autocomplete == null) {
        return true;
      }

      var tokens = isADirective(autocomplete) ? new String[]{autocomplete} : autocomplete.split("\\s+");
      var numberOfPredicates = predicates.size();

      if (tokens.length != numberOfPredicates) {
        return false;
      }

      var isValid = true;

      for (int i = 0; i < numberOfPredicates; i++) {
        var predicate = predicates.get(i);
        var token = tokens[i];
        var candidate = new Candidate(token, tagNode);

        isValid = isValid && predicate.test(candidate);
      }

      return isValid;
    }
  }

  /**
   * A class representing an autocomplete token as defined by the HTML specification - i.e. a value and a control
   * group this value belongs to.
   * See https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#autofilling-form-controls:-the-autocomplete-attribute
   */
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

  /**
   * A class representing an autocomplete value in the context of a tag - e.g. "cc-exp" in the context of an "input" -
   * that can be validated as a whole.
   */
  static class Candidate {
    private final String value;

    private final TagNode tagNode;

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
      candidate -> candidate.value.isBlank()
    )),
    new Validator(List.of(
      candidate -> isADirective(candidate.value)
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isAOnOffToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isASection,
      ValidAutocompleteCheck::isAnAutofillFieldNameToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isASection,
      ValidAutocompleteCheck::isAnAutofillFieldNameToken,
      ValidAutocompleteCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isASection,
      ValidAutocompleteCheck::isAnAddressType,
      ValidAutocompleteCheck::isAnAutofillFieldNameToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isASection,
      ValidAutocompleteCheck::isAnAddressType,
      ValidAutocompleteCheck::isAnAutofillFieldNameToken,
      ValidAutocompleteCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isASection,
      ValidAutocompleteCheck::isAnAddressType,
      ValidAutocompleteCheck::isAMediumValueToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isASection,
      ValidAutocompleteCheck::isAnAddressType,
      ValidAutocompleteCheck::isAMediumValueToken,
      ValidAutocompleteCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isASection,
      ValidAutocompleteCheck::isAnAddressType,
      ValidAutocompleteCheck::isAMediumTypeToken,
      ValidAutocompleteCheck::isAMediumValueToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isASection,
      ValidAutocompleteCheck::isAnAddressType,
      ValidAutocompleteCheck::isAMediumTypeToken,
      ValidAutocompleteCheck::isAMediumValueToken,
      ValidAutocompleteCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isAnAddressType,
      ValidAutocompleteCheck::isAnAutofillFieldNameToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isAnAddressType,
      ValidAutocompleteCheck::isAnAutofillFieldNameToken,
      ValidAutocompleteCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isAnAddressType,
      ValidAutocompleteCheck::isAMediumTypeToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isAnAddressType,
      ValidAutocompleteCheck::isAMediumTypeToken,
      ValidAutocompleteCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isAnAddressType,
      ValidAutocompleteCheck::isAMediumTypeToken,
      ValidAutocompleteCheck::isAMediumValueToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isAnAddressType,
      ValidAutocompleteCheck::isAMediumTypeToken,
      ValidAutocompleteCheck::isAMediumValueToken,
      ValidAutocompleteCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isAnAutofillFieldNameToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isAnAutofillFieldNameToken,
      ValidAutocompleteCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isAMediumValueToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isAMediumValueToken,
      ValidAutocompleteCheck::isAWebAuthnToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isAMediumTypeToken,
      ValidAutocompleteCheck::isAMediumValueToken
    )),
    new Validator(List.of(
      ValidAutocompleteCheck::isAMediumTypeToken,
      ValidAutocompleteCheck::isAMediumValueToken,
      ValidAutocompleteCheck::isAWebAuthnToken
    ))
  );

  static boolean isADirective(String value) {
    return value.startsWith("<%=") || value.startsWith("<?");
  }

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
      new Token("webauthn", node -> node.getNodeName().equalsIgnoreCase("input") || node.getNodeName().equalsIgnoreCase("textarea"))
    ).anyMatch(candidate::satisfies);
  }

  @Override
  public void startElement(TagNode node) {
    var isValid = validators.stream().anyMatch(validator -> validator.isValid(node));

    if (!isValid) {
      createViolation(node, "DOM elements should use the \"autocomplete\" attribute correctly.");
    }
  }
}
