#!/bin/bash
#
# Generates a markdown report of ruling differences for PR comments.
# Compares ruling files between base branch and current state,
# showing code snippets from the actual source files.
#

set -e

EXPECTED_DIR="its/ruling/src/test/resources/expected"
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

# Function to extract line numbers from JSON for a specific file
get_lines_for_file() {
  local json_file="$1"
  local file_key="$2"
  jq -r --arg key "$file_key" '.[$key] // [] | .[]' "$json_file" 2>/dev/null | sort -n
}

# Function to get all file keys from a JSON file
get_file_keys() {
  local json_file="$1"
  jq -r 'keys[]' "$json_file" 2>/dev/null
}

# Function to show code snippet around a line
show_snippet() {
  local source_file="$1"
  local line_num="$2"
  local context=2

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
get_base_content() {
  local file_path="$1"
  git show "${BASE_BRANCH}:${file_path}" 2>/dev/null || echo "{}"
}

# Start report
echo "## Ruling Report"
echo ""
echo "The following ruling changes are in this PR:"
echo ""

# Process each changed file
for file_path in $CHANGED_FILES; do
  rule_name=$(basename "$file_path" .json)

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
  echo "$base_content" > "$base_tmp"
  echo "$current_content" > "$current_tmp"

  echo "### Rule: \`$rule_name\`"
  echo ""

  # Get all unique file keys from both versions
  all_keys=$(cat <(jq -r 'keys[]' "$base_tmp" 2>/dev/null) <(jq -r 'keys[]' "$current_tmp" 2>/dev/null) | sort -u)

  removed_count=0
  added_count=0
  removed_snippets=""
  added_snippets=""

  for file_key in $all_keys; do
    base_lines=$(get_lines_for_file "$base_tmp" "$file_key")
    current_lines=$(get_lines_for_file "$current_tmp" "$file_key")

    # Find removed lines (in base but not in current)
    removed=$(comm -23 <(echo "$base_lines" | grep -v '^$' | sort -n) <(echo "$current_lines" | grep -v '^$' | sort -n) 2>/dev/null || true)

    # Find added lines (in current but not in base)
    added=$(comm -13 <(echo "$base_lines" | grep -v '^$' | sort -n) <(echo "$current_lines" | grep -v '^$' | sort -n) 2>/dev/null || true)

    source_path=$(resolve_source_path "$file_key")
    display_path="${file_key#project:}"

    for line_num in $removed; do
      removed_count=$((removed_count + 1))
      if [ "$removed_count" -le "$MAX_SNIPPETS" ]; then
        github_url=$(get_github_url "$file_key" "$line_num")
        removed_snippets+="[**${display_path}:${line_num}**](${github_url})"$'\n'
        removed_snippets+="\`\`\`html"$'\n'
        removed_snippets+="$(show_snippet "$source_path" "$line_num")"$'\n'
        removed_snippets+="\`\`\`"$'\n\n'
      fi
    done

    for line_num in $added; do
      added_count=$((added_count + 1))
      if [ "$added_count" -le "$MAX_SNIPPETS" ]; then
        github_url=$(get_github_url "$file_key" "$line_num")
        added_snippets+="[**${display_path}:${line_num}**](${github_url})"$'\n'
        added_snippets+="\`\`\`html"$'\n'
        added_snippets+="$(show_snippet "$source_path" "$line_num")"$'\n'
        added_snippets+="\`\`\`"$'\n\n'
      fi
    done
  done

  # Clean up temp files
  rm -f "$base_tmp" "$current_tmp"

  # Output removed issues section
  if [ "$removed_count" -gt 0 ]; then
    echo "<details>"
    echo "<summary>🔽 Code no longer flagged ($removed_count issues)</summary>"
    echo ""
    echo "$removed_snippets"
    if [ "$removed_count" -gt "$MAX_SNIPPETS" ]; then
      echo "_...and $((removed_count - MAX_SNIPPETS)) more (see ruling JSON files for full list)_"
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
      echo "_...and $((added_count - MAX_SNIPPETS)) more (see ruling JSON files for full list)_"
      echo ""
    fi
    echo "</details>"
    echo ""
  fi

  # If no line changes detected but file changed, note it
  if [ "$removed_count" -eq 0 ] && [ "$added_count" -eq 0 ]; then
    echo "_File changed but no line-level differences detected (possibly formatting only)_"
    echo ""
  fi
done
