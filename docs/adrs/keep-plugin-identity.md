---
title: Keep the installed plugin identity
description: Keep the plugin key stable so existing SonarWeb installations can migrate to SonarHTML.
generated:
  by: sonar-tirion
---

# Keep the installed plugin identity

`sonar-html-plugin/pom.xml` sets `pluginKey` to `web` even though the artifact is named `sonar-html-plugin`.

The installed identity is a compatibility boundary, not a display name: SonarHTML replaces SonarWeb. Using a name derived from the Maven artifact for a new key would make the replacement harder for existing installations. Keep the key independent of the artifact and product naming.

See the [root constraints](../../AGENTS.md#constraints) for review requirements when changing public keys.
