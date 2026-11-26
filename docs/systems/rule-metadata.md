---
title: Rule metadata and Sonar way
description: Explain how the analyzer ties generated descriptions, profile membership and public rule keys together.
generated:
  by: sonar-tirion
---

# Rule metadata and Sonar way

`sonarpedia.json` configures `rule-api` to generate the HTML descriptions and JSON metadata used by `sonar-html-plugin/src/main/java/org/sonar/plugins/html/rules/HtmlRulesDefinition.java`. RSPEC is authoritative so product descriptions and rule semantics don't diverge from the analyzer's packaged resources.

The metadata loader matches generated resources to the keys declared by the checks registered in `sonar-html-plugin/src/main/java/org/sonar/plugins/html/rules/CheckClasses.java`. `preserve-filenames: true` in `sonarpedia.json` keeps the existing filenames aligned with those public keys. The S-key used to find a spec upstream is not necessarily the packaged rule key.

`sonar-html-plugin/src/main/resources/org/sonar/l10n/web/rules/Web/Sonar_way_profile.json` is generated too: `rule-api generate` and `rule-api update` maintain the profile file when `profiles-path` is unset, as it is here. Change default profile membership in RSPEC, not in this JSON file. `sonar-html-plugin/src/main/java/org/sonar/plugins/html/rules/SonarWayProfile.java` loads the resulting profile.

Use the [regeneration skill](../../.agents/skills/regenerate-rule-metadata/SKILL.md) to generate from an RSPEC branch and refresh after it merges. See [External context](../../AGENTS.md#external-context) for the upstream location.
