---
title: Documentation conventions
description: Place, link and verify the public documentation for sonar-html.
generated:
  by: sonar-tirion
sources:
  - resource: https://xtranet-sonarsource.atlassian.net/wiki/spaces/DEV/pages/5739839551
baseline:
  resource: https://xtranet-sonarsource.atlassian.net/wiki/spaces/DEV/pages/5742002202
  checked: "2026-11-26"
  template: template not yet published
---

# Documentation conventions

- Keep the root [AGENTS.md](../AGENTS.md) an index, not a manual. Include tool-managed blocks when counting its 150 lines and 8 KiB. Keep any nested instructions within 4 KiB and give each `AGENTS.md` a sibling `CLAUDE.md` containing only the `@AGENTS.md` import.
- Put current behavior and non-obvious constraints in [Systems](systems/index.md). Put decisions in force, their rationale and rejected alternatives in [Decisions](adrs/index.md). Edit them in place; remove superseded decisions. Keep plans in the tracker and history in Git.
- Keep one topic per file and at most 300 lines per doc. Use a linked Markdown index in every directory, as in [Systems](systems/index.md). Keep procedures used by agents in skills; keep one canonical copy plus thin tool adapters.
- Lead with the code affected by a doc. Use plain backticks around existing paths relative to the repository root (include a trailing slash for directories), and regular file-relative Markdown links for navigation. Add a one-line doc pointer at the key class or configuration when practical.
- Give each doc YAML frontmatter with `title` and a one-sentence `description`. For agent-written docs, set `generated.by` to the tool name. Cite authoritative Jira or Confluence facts with `sources` entries using `resource`; link to external knowledge rather than copying it. Set `stale_after` (ISO date) only for a document with a real expiration.
- Update affected docs in the same PR as the corresponding behavior. Use the [verification guide](systems/verification.md) for the documentation check and owner review.
