/*
 * SonarQube HTML Plugin :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA
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

import java.util.Locale;

import javax.annotation.Nullable;

import org.sonar.plugins.html.node.TagNode;

public class FlashHelper {

  private FlashHelper() {
  }

  public static boolean isFlashObject(TagNode node) {
    return hasFlashClassId(node.getAttribute("classid")) ||
      hasFlashType(node.getAttribute("type")) ||
      hasFlashExtension(node.getAttribute("data"));
  }

  private static boolean hasFlashClassId(@Nullable String classId) {
    return classId != null && "CLSID:D27CDB6E-AE6D-11CF-96B8-444553540000".equalsIgnoreCase(classId);
  }

  private static boolean hasFlashType(@Nullable String type) {
    return type != null && type.toUpperCase(Locale.ENGLISH).contains("X-SHOCKWAVE-FLASH");
  }

  private static boolean hasFlashExtension(@Nullable String file) {
    return file != null && file.toUpperCase(Locale.ENGLISH).endsWith(".SWF");
  }

  public static boolean isFlashEmbed(TagNode node) {
    return hasFlashType(node.getAttribute("type")) ||
      hasFlashExtension(node.getAttribute("src"));
  }

}
