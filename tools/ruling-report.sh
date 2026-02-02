#!/bin/bash
#
# Generates a markdown report of ruling differences for PR comments.
# Compares expected vs actual ruling results and outputs formatted markdown
# with code snippets showing the actual source code around issues.
#

set -e

EXPECTED_DIR="its/ruling/src/test/resources/expected"
ACTUAL_DIR="its/ruling/target/actual"
SOURCES_BASE="its/sources"
SOURCES_REPO="https://github.com/SonarCommunity/web-test-sources"
MAX_SNIPPETS=10

# Get the commit SHA of the sources submodule for stable URLs
SOURCES_SHA=$(cd "$SOURCES_BASE" 2>/dev/null && git rev-parse HEAD 2>/dev/null || echo "master")

# Check if actual directory exists
if [ ! -d "$ACTUAL_DIR" ]; then
  exit 0
fi

# Get list of differing files
DIFF_OUTPUT=$(diff -rq "$EXPECTED_DIR" "$ACTUAL_DIR" 2>/dev/null || true)

if [ -z "$DIFF_OUTPUT" ]; then
  exit 0
fi

# Function to extract line numbers from JSON for a specific file
# Usage: get_lines_for_file <json_file> <file_key>
get_lines_for_file() {
  local json_file="$1"
  local file_key="$2"
  # Use jq to extract the array of line numbers for the file key
  jq -r --arg key "$file_key" '.[$key] // [] | .[]' "$json_file" 2>/dev/null | sort -n
}

# Function to get all file keys from a JSON file
get_file_keys() {
  local json_file="$1"
  jq -r 'keys[]' "$json_file" 2>/dev/null
}

# Function to show code snippet around a line
# Usage: show_snippet <source_file> <line_number>
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

  # Read lines and format with line numbers
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
# Format: "project:ProjectName/path/to/file.ext"
resolve_source_path() {
  local file_key="$1"
  # Remove "project:" prefix and construct path
  local relative_path="${file_key#project:}"
  echo "$SOURCES_BASE/$relative_path"
}

# Function to generate GitHub URL for a file at a specific line
# Usage: get_github_url <file_key> <line_number>
get_github_url() {
  local file_key="$1"
  local line_num="$2"
  local relative_path="${file_key#project:}"
  echo "${SOURCES_REPO}/blob/${SOURCES_SHA}/${relative_path}#L${line_num}"
}

# Start report
echo "## Ruling Report"
echo ""
echo "The following ruling differences were detected:"
echo ""

# Process files that differ
echo "$DIFF_OUTPUT" | grep "differ" | while read -r line; do
  expected_file=$(echo "$line" | awk '{print $2}')
  actual_file=$(echo "$line" | awk '{print $4}')
  rule_name=$(basename "$expected_file" .json)

  echo "### Rule: \`$rule_name\`"
  echo ""

  # Get all unique file keys from both files
  all_keys=$(cat <(get_file_keys "$expected_file") <(get_file_keys "$actual_file") 2>/dev/null | sort -u)

  removed_count=0
  added_count=0
  removed_snippets=""
  added_snippets=""

  for file_key in $all_keys; do
    expected_lines=$(get_lines_for_file "$expected_file" "$file_key")
    actual_lines=$(get_lines_for_file "$actual_file" "$file_key")

    # Find removed lines (in expected but not in actual)
    removed=$(comm -23 <(echo "$expected_lines" | grep -v '^$' | sort -n) <(echo "$actual_lines" | grep -v '^$' | sort -n) 2>/dev/null || true)

    # Find added lines (in actual but not in expected)
    added=$(comm -13 <(echo "$expected_lines" | grep -v '^$' | sort -n) <(echo "$actual_lines" | grep -v '^$' | sort -n) 2>/dev/null || true)

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
done

# Process files only in expected (rule completely removed)
echo "$DIFF_OUTPUT" | grep "Only in $EXPECTED_DIR" | while read -r line; do
  file=$(echo "$line" | awk -F': ' '{print $2}')
  rule_name="${file%.json}"

  echo "### Rule: \`$rule_name\`"
  echo ""
  echo "⚠️ This rule's expected file was removed entirely."
  echo ""
done

# Process files only in actual (new rule)
echo "$DIFF_OUTPUT" | grep "Only in $ACTUAL_DIR" | while read -r line; do
  file=$(echo "$line" | awk -F': ' '{print $2}')
  rule_name="${file%.json}"
  full_path="$ACTUAL_DIR/$file"

  echo "### Rule: \`$rule_name\`"
  echo ""

  if [ -f "$full_path" ]; then
    # Count total issues
    total_issues=0
    snippet_count=0
    snippets=""

    for file_key in $(get_file_keys "$full_path"); do
      source_path=$(resolve_source_path "$file_key")
      display_path="${file_key#project:}"

      for line_num in $(get_lines_for_file "$full_path" "$file_key"); do
        total_issues=$((total_issues + 1))
        if [ "$snippet_count" -lt "$MAX_SNIPPETS" ]; then
          snippet_count=$((snippet_count + 1))
          github_url=$(get_github_url "$file_key" "$line_num")
          snippets+="[**${display_path}:${line_num}**](${github_url})"$'\n'
          snippets+="\`\`\`html"$'\n'
          snippets+="$(show_snippet "$source_path" "$line_num")"$'\n'
          snippets+="\`\`\`"$'\n\n'
        fi
      done
    done

    echo "<details>"
    echo "<summary>🆕 New rule with $total_issues issues</summary>"
    echo ""
    echo "$snippets"
    if [ "$total_issues" -gt "$MAX_SNIPPETS" ]; then
      echo "_...and $((total_issues - MAX_SNIPPETS)) more (see ruling JSON files for full list)_"
      echo ""
    fi
    echo "</details>"
    echo ""
  fi
done
