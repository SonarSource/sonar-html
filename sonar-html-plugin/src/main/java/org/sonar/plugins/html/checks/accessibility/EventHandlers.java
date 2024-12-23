/*
 * SonarQube HTML
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
package org.sonar.plugins.html.checks.accessibility;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EventHandlers {

  // Reproduced from https://github.com/jsx-eslint/jsx-ast-utils/blob/main/src/eventHandlers.js
  protected static final Map<String, Set<String>> EVENT_HANDLERS_BY_TYPE = new HashMap<>();

  static {
    EVENT_HANDLERS_BY_TYPE.put("clipboard", Set.of(
      "onCopy",
      "onCut",
      "onPaste"
    ));
    EVENT_HANDLERS_BY_TYPE.put("composition", Set.of(
      "onCompositionEnd",
      "onCompositionStart",
      "onCompositionUpdate"
    ));
    EVENT_HANDLERS_BY_TYPE.put("keyboard", Set.of(
      "onKeyDown",
      "onKeyPress",
      "onKeyUp"
    ));
    EVENT_HANDLERS_BY_TYPE.put("focus", Set.of(
      "onFocus",
      "onBlur"
    ));
    EVENT_HANDLERS_BY_TYPE.put("form", Set.of(
      "onChange",
      "onInput",
      "onSubmit"
    ));
    EventHandlers.EVENT_HANDLERS_BY_TYPE.put("mouse", Set.of(
      "onClick",
      "onContextMenu",
      "onDblClick",
      "onDoubleClick",
      "onDrag",
      "onDragEnd",
      "onDragEnter",
      "onDragExit",
      "onDragLeave",
      "onDragOver",
      "onDragStart",
      "onDrop",
      "onMouseDown",
      "onMouseEnter",
      "onMouseLeave",
      "onMouseMove",
      "onMouseOut",
      "onMouseOver",
      "onMouseUp"
    ));
    EventHandlers.EVENT_HANDLERS_BY_TYPE.put("selection", Set.of(
      "onSelect"
    ));
    EventHandlers.EVENT_HANDLERS_BY_TYPE.put("touch", Set.of(
      "onTouchCancel",
      "onTouchEnd",
      "onTouchMove",
      "onTouchStart"
    ));
    EventHandlers.EVENT_HANDLERS_BY_TYPE.put("ui", Set.of(
      "onScroll"
    ));
    EventHandlers.EVENT_HANDLERS_BY_TYPE.put("wheel", Set.of(
      "onWheel"
    ));
    EventHandlers.EVENT_HANDLERS_BY_TYPE.put("media", Set.of(
      "onAbort",
      "onCanPlay",
      "onCanPlayThrough",
      "onDurationChange",
      "onEmptied",
      "onEncrypted",
      "onEnded",
      "onError",
      "onLoadedData",
      "onLoadedMetadata",
      "onLoadStart",
      "onPause",
      "onPlay",
      "onPlaying",
      "onProgress",
      "onRateChange",
      "onSeeked",
      "onSeeking",
      "onStalled",
      "onSuspend",
      "onTimeUpdate",
      "onVolumeChange",
      "onWaiting"
    ));
    EVENT_HANDLERS_BY_TYPE.put("image", Set.of(
      "onLoad",
      "onError"
    ));
    EVENT_HANDLERS_BY_TYPE.put("animation", Set.of(
      "onAnimationStart",
      "onAnimationEnd",
      "onAnimationIteration"
    ));
    EVENT_HANDLERS_BY_TYPE.put("transition", Set.of(
      "onTransitionEnd"
    ));
  }

  private EventHandlers() {
    // Utility class
  }
}
