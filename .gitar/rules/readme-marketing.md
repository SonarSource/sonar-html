---
title: Protect the Marketing-managed README section
description: Flag changes to the Marketing-managed README block unless coordinated with Marketing.
when: A pull request changes README.md.
actions: Compare the Marketing markers and all content between them against the base; ask for Product Marketing Manager confirmation when changed.
---

# Protect the Marketing-managed README section

Review the block from `<!-- sonar-marketing:start -->` through `<!-- sonar-marketing:end -->`, including the markers. Changes elsewhere in `README.md` do not trigger this rule. Flag changes inside the block, or removed or changed markers, unless the PR documents Product Marketing Manager coordination.
