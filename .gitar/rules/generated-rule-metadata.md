---
title: Require provenance for generated rule resources
description: Flag generated rule descriptions, metadata or profile changes without evidence of RSPEC regeneration.
when: A pull request changes files in sonar-html-plugin/src/main/resources/org/sonar/l10n/web/rules/Web/.
actions: Check the diff and PR evidence for an RSPEC branch or a rule-api run; ask for regeneration evidence if it is missing.
---

# Require provenance for generated rule resources

Use `docs/systems/rule-metadata.md` and the linked skill as the source of truth. Ask for the matching RSPEC branch or `rule-api` run against merged RSPEC, not manual edits to the generated output. Check that legacy filenames stay aligned with the registered rule keys and that any generated profile changes are intentional. Report only concrete discrepancies or missing provenance, in a single review comment.
