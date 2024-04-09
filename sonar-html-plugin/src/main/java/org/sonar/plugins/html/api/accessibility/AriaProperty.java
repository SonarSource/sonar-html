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

package org.sonar.plugins.html.api.accessibility;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum AriaProperty {
  ACTIVEDESCENDANT("aria-activedescendant"),
  ATOMIC("aria-atomic"),
  AUTOCOMPLETE("aria-autocomplete"),
  BRAILLELABEL("aria-braillelabel"),
  BRAILLEROLEDESCRIPTION("aria-brailleroledescription"),
  BUSY("aria-busy"),
  CHECKED("aria-checked"),
  COLCOUNT("aria-colcount"),
  COLINDEX("aria-colindex"),
  COLSPAN("aria-colspan"),
  CONTROLS("aria-controls"),
  CURRENT("aria-current"),
  DESCRIBEDBY("aria-describedby"),
  DESCRIPTION("aria-description"),
  DETAILS("aria-details"),
  DISABLED("aria-disabled"),
  DROPEFFECT("aria-dropeffect"),
  ERRORMESSAGE("aria-errormessage"),
  EXPANDED("aria-expanded"),
  FLOWTO("aria-flowto"),
  GRABBED("aria-grabbed"),
  HASPOPUP("aria-haspopup"),
  HIDDEN("aria-hidden"),
  INVALID("aria-invalid"),
  KEYSHORTCUTS("aria-keyshortcuts"),
  LABEL("aria-label"),
  LABELLEDBY("aria-labelledby"),
  LEVEL("aria-level"),
  LIVE("aria-live"),
  MODAL("aria-modal"),
  MULTILINE("aria-multiline"),
  MULTISELECTABLE("aria-multiselectable"),
  ORIENTATION("aria-orientation"),
  OWNS("aria-owns"),
  PLACEHOLDER("aria-placeholder"),
  POSINSET("aria-posinset"),
  PRESSED("aria-pressed"),
  READONLY("aria-readonly"),
  RELEVANT("aria-relevant"),
  REQUIRED("aria-required"),
  ROLEDESCRIPTION("aria-roledescription"),
  ROWCOUNT("aria-rowcount"),
  ROWINDEX("aria-rowindex"),
  ROWSPAN("aria-rowspan"),
  SELECTED("aria-selected"),
  SETSIZE("aria-setsize"),
  SORT("aria-sort"),
  VALUEMAX("aria-valuemax"),
  VALUEMIN("aria-valuemin"),
  VALUENOW("aria-valuenow"),
  VALUETEXT("aria-valuetext");

  private final String value;

  AriaProperty(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

  private static final Map<String, AriaProperty> stringMap = Arrays.stream(values())
    .collect(Collectors.toMap(Enum::toString, Function.identity()));

  public static AriaProperty of(String value) {
    return stringMap.get(value);
  }
}
