---
name: regenerate-rule-metadata
description: Regenerate sonar-html rule descriptions, metadata and Sonar way membership when adding or changing a rule specification or syncing merged RSPEC changes.
---

# Regenerate HTML rule metadata

1. Read [Rule metadata and Sonar way](../../../docs/systems/rule-metadata.md). Find the rule's HTML specification in `SonarSource/rspec`; make spec, metadata and default profile changes there. Use the working RSPEC branch name for preview generation.
2. Obtain the `rule-api` JAR using the [upstream instructions](https://github.com/SonarSource/sonar-rule-api#usage). Use authorized GitHub credentials for RSPEC. Run from the sonar-html root, which contains `sonarpedia.json`. Set `RULE_API_JAR` to the absolute JAR path, `RSPEC_BRANCH` to the working branch, and `RULE_KEY` to the exact value of `@Rule(key = ...)`, following the [root constraints](../../../AGENTS.md#constraints).
3. Generate the preview from that branch:

   ```bash
   java -jar "$RULE_API_JAR" generate -branch "$RSPEC_BRANCH" -rule "$RULE_KEY"
   ```

4. After the RSPEC change merges, refresh from the default branch:

   ```bash
   java -jar "$RULE_API_JAR" update
   ```

   For a new rule not yet present in the checkout, first run `java -jar "$RULE_API_JAR" generate -rule "$RULE_KEY"` against the merged spec; `update` only refreshes existing rules.

5. Inspect `git diff` and `git status` for unexpected new files, renamed legacy resources or unintended profile changes. Run the appropriate tests from [Commands](../../../AGENTS.md#commands). Stop and report missing access or unexpected generated changes rather than editing output manually.
