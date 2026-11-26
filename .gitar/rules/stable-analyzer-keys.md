---
title: Preserve analyzer keys
description: Flag changes to existing public analyzer keys that have no compatibility migration.
when: A pull request changes or removes a registered rule key, rule repository key, language key or plugin key.
actions: Identify the old identity and require evidence that existing installations, issues and profiles retain compatibility.
---

# Preserve analyzer keys

Use the public-key constraint in `AGENTS.md`. Check existing `@Rule` keys, repository and language identifiers, and the packaged plugin key. Ignore new keys and changes to display names that leave the identifiers unchanged. If an existing public key changes, request the migration and evidence that it handles existing consumers; report the concrete affected key.
