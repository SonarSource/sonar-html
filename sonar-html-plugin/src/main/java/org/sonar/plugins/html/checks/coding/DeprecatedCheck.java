package org.sonar.plugins.html.checks.coding;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.Attribute;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Rule(key = "S1874")
public class DeprecatedCheck extends AbstractPageCheck {

  private static final Set<String> DEPRECATED_TAGS = Set.of(
    "acronym",
    "applet",
    "basefont",
    "big",
    "blink",
    "bgsound",
    "center",
    "dir",
    "font",
    "frame",
    "frameset",
    "isindex",
    "keygen",
    "listing",
    "marquee",
    "multicol",
    "nextid",
    "noframes",
    "plaintext",
    "rb",
    "rtc",
    "spacer",
    "strike",
    "tt",
    "xmp"
  );

  private static final Map<String, Set<String>> DEPRECATED_ATTRIBUTES_BY_TAG = Map.ofEntries(
    Map.entry("align", Set.of("applet", "caption", "col", "colgroup", "hr", "iframe", "img", "table", "tbody", "td", "tfoot", "th", "thead", "tr")),
    Map.entry("bgcolor", Set.of("body", "table", "td", "th")),
    Map.entry("border", Set.of("img", "object", "table")),
    Map.entry("color", Set.of("font")),
    Map.entry("height", Set.of("td", "th", "tr")), // deprecated for layout purposes
    Map.entry("hspace", Set.of("img", "object")),
    Map.entry("name", Set.of("applet", "frame")),
    Map.entry("noshade", Set.of("hr")),
    Map.entry("nowrap", Set.of("td", "th")),
    Map.entry("size", Set.of("hr", "font")),
    Map.entry("valign", Set.of("col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr")),
    Map.entry("vspace", Set.of("img", "object")),
    Map.entry("background", Set.of("body")),
    Map.entry("clear", Set.of("br")),
    Map.entry("compact", Set.of("dl", "menu", "ol", "ul")),
    Map.entry("start", Set.of("ol")),
    Map.entry("text", Set.of("body")),
    Map.entry("link", Set.of("body")),
    Map.entry("alink", Set.of("body")),
    Map.entry("vlink", Set.of("body")),
    Map.entry("frameborder", Set.of("iframe")),
    Map.entry("scrolling", Set.of("iframe")),
    Map.entry("marginwidth", Set.of("iframe")),
    Map.entry("marginheight", Set.of("iframe"))
  );

  @Override
  public void startElement(TagNode element) {
    String tagName = element.getNodeName().toLowerCase(Locale.ENGLISH);

    // Check deprecated tag
    if (DEPRECATED_TAGS.contains(tagName)) {
      createViolation(element, String.format("The <%s> tag is deprecated/obsolete and should not be used.", tagName));
      return;
    }

    // Check deprecated attributes for this tag
    for (Attribute attr : element.getAttributes()) {
      String attrName = attr.getName().toLowerCase(Locale.ENGLISH);
      if (DEPRECATED_ATTRIBUTES_BY_TAG.containsKey(attrName) &&
              DEPRECATED_ATTRIBUTES_BY_TAG.get(attrName).contains(tagName)) {
        createViolation(element,
                String.format("The '%s' attribute on <%s> is deprecated/obsolete and should not be used.",
                        attrName, tagName)
        );
      }
    }
  }
}