#!/bin/bash
#
# Generates a markdown report of ruling differences for PR comments.
# Compares ruling files between base branch and current state,
# showing code snippets from the actual source files.
#

set -e

EXPECTED_DIR="its/ruling/src/test/resources/expected/project"
SOURCES_BASE="its/sources"
SOURCES_REPO="https://github.com/SonarCommunity/web-test-sources"
MAX_SNIPPETS=10

# Base branch to compare against (default to master)
BASE_BRANCH="${BASE_BRANCH:-origin/master}"

# Get the commit SHA of the sources submodule for stable URLs
SOURCES_SHA=$(cd "$SOURCES_BASE" 2>/dev/null && git rev-parse HEAD 2>/dev/null || echo "master")

# Check if expected directory exists
if [ ! -d "$EXPECTED_DIR" ]; then
  exit 0
fi

# Get list of changed ruling files compared to base branch
CHANGED_FILES=$(git diff --name-only "$BASE_BRANCH" -- "$EXPECTED_DIR" 2>/dev/null || true)

if [ -z "$CHANGED_FILES" ]; then
  exit 0
fi

# Function to list the issues of a ruling file as sorted "file<TAB>line" entries.
# Supports SARIF files (LITS >= 0.13) and the legacy {"file": [lines]} JSON format.
# Issues without a region (file-level issues) are reported on line 0, as in the legacy format.
list_issues() {
  jq -r 'if type == "object" and has("runs") then
           .runs[].results[].locations[0].physicalLocation
           | "\(.artifactLocation.uri)\t\(.region.startLine // 0)"
         else
           to_entries[] | .key as $key | .value[] | "\($key)\t\(.)"
         end' 2>/dev/null | LC_ALL=C sort
}

# Function to show code snippet around a line
show_snippet() {
  local source_file="$1"
  local line_num="$2"
  local context=5

  if [ ! -f "$source_file" ]; then
    echo "    (file not found)"
    return
  fi

  local start=$((line_num - context))
  [ "$start" -lt 1 ] && start=1
  local end=$((line_num + context))

  local current_line=$start
  while IFS= read -r line || [ -n "$line" ]; do
    if [ "$current_line" -eq "$line_num" ]; then
      printf "> %4d | %s\n" "$current_line" "$line"
    else
      printf "  %4d | %s\n" "$current_line" "$line"
    fi
    current_line=$((current_line + 1))
  done < <(sed -n "${start},${end}p" "$source_file")
}

# Function to resolve source file path from project key
resolve_source_path() {
  local file_key="$1"
  local relative_path="${file_key#project:}"
  echo "$SOURCES_BASE/$relative_path"
}

# Function to generate GitHub URL for a file at a specific line
get_github_url() {
  local file_key="$1"
  local line_num="$2"
  local relative_path="${file_key#project:}"
  echo "${SOURCES_REPO}/blob/${SOURCES_SHA}/${relative_path}#L${line_num}"
}

# Function to get file content from base branch
# Tries the exact path first, then falls back to known legacy locations
# to handle file moves (e.g. expected/Foo.json -> expected/project/Foo.json)
get_base_content() {
  local file_path="$1"
  local content
  content=$(git show "${BASE_BRANCH}:${file_path}" 2>/dev/null) && { echo "$content"; return; }

  # Fallback: try legacy flat expected/ directory (before project/ subdirectory was introduced)
  local filename
  filename=$(basename "$file_path")
  content=$(git show "${BASE_BRANCH}:its/ruling/src/test/resources/expected/${filename}" 2>/dev/null) && { echo "$content"; return; }

  # Fallback: try the legacy JSON sibling of a SARIF file (before LITS 0.13)
  if [[ "$file_path" == *.sarif ]]; then
    content=$(git show "${BASE_BRANCH}:${file_path%.sarif}.json" 2>/dev/null) && { echo "$content"; return; }
  fi

  echo "{}"
}

report_started=false

# Process each changed file
for file_path in $CHANGED_FILES; do
  # A legacy JSON file replaced by a SARIF file is compared as part of the SARIF file
  if [[ "$file_path" == *.json ]] && [[ ! -f "$file_path" ]] && [[ -f "${file_path%.json}.sarif" ]]; then
    continue
  fi

  rule_name=$(basename "$file_path")
  rule_name="${rule_name%.json}"
  rule_name="${rule_name%.sarif}"

  # Get current content
  if [ -f "$file_path" ]; then
    current_content=$(cat "$file_path")
  else
    current_content="{}"
  fi

  # Get base content
  base_content=$(get_base_content "$file_path")

  # Create temp files for comparison
  base_tmp=$(mktemp)
  current_tmp=$(mktemp)
  echo "$base_content" | list_issues > "$base_tmp"
  echo "$current_content" | list_issues > "$current_tmp"

  # Find removed issues (in base but not in current) and added issues (in current but not in base)
  removed=$(LC_ALL=C comm -23 "$base_tmp" "$current_tmp" | sort -t$'\t' -k1,1 -k2,2n)
  added=$(LC_ALL=C comm -13 "$base_tmp" "$current_tmp" | sort -t$'\t' -k1,1 -k2,2n)

  # Clean up temp files
  rm -f "$base_tmp" "$current_tmp"

  removed_count=0
  added_count=0
  removed_snippets=""
  added_snippets=""

  while IFS=$'\t' read -r file_key line_num; do
    [[ -z "$file_key" ]] && continue
    removed_count=$((removed_count + 1))
    if [[ "$removed_count" -le "$MAX_SNIPPETS" ]]; then
      github_url=$(get_github_url "$file_key" "$line_num")
      removed_snippets+="[**${file_key#project:}:${line_num}**](${github_url})"$'\n'
      removed_snippets+="\`\`\`html"$'\n'
      removed_snippets+="$(show_snippet "$(resolve_source_path "$file_key")" "$line_num")"$'\n'
      removed_snippets+="\`\`\`"$'\n\n'
    fi
  done <<< "$removed"

  while IFS=$'\t' read -r file_key line_num; do
    [[ -z "$file_key" ]] && continue
    added_count=$((added_count + 1))
    if [[ "$added_count" -le "$MAX_SNIPPETS" ]]; then
      github_url=$(get_github_url "$file_key" "$line_num")
      added_snippets+="[**${file_key#project:}:${line_num}**](${github_url})"$'\n'
      added_snippets+="\`\`\`html"$'\n'
      added_snippets+="$(show_snippet "$(resolve_source_path "$file_key")" "$line_num")"$'\n'
      added_snippets+="\`\`\`"$'\n\n'
    fi
  done <<< "$added"

  # Skip rules without issue-level differences (e.g. formatting or JSON to SARIF migration only)
  if [[ "$removed_count" -eq 0 && "$added_count" -eq 0 ]]; then
    continue
  fi

  # Start report on the first rule with differences
  if [[ "$report_started" = false ]]; then
    echo "## Ruling Report"
    echo ""
    echo "The following ruling changes are in this PR:"
    echo ""
    report_started=true
  fi

  echo "### Rule: \`$rule_name\`"
  echo ""

  # Output removed issues section
  if [ "$removed_count" -gt 0 ]; then
    echo "<details>"
    echo "<summary>🔽 Code no longer flagged ($removed_count issues)</summary>"
    echo ""
    echo "$removed_snippets"
    if [ "$removed_count" -gt "$MAX_SNIPPETS" ]; then
      echo "_...and $((removed_count - MAX_SNIPPETS)) more (see ruling files for full list)_"
      echo ""
    fi
    echo "</details>"
    echo ""
  fi

  # Output added issues section
  if [ "$added_count" -gt 0 ]; then
    echo "<details>"
    echo "<summary>🔼 New issues flagged ($added_count issues)</summary>"
    echo ""
    echo "$added_snippets"
    if [ "$added_count" -gt "$MAX_SNIPPETS" ]; then
      echo "_...and $((added_count - MAX_SNIPPETS)) more (see ruling files for full list)_"
      echo ""
    fi
    echo "</details>"
    echo ""
  fi
done
