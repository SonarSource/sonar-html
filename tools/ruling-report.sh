#!/bin/bash
#
# Generates a markdown report of ruling differences for PR comments.
# Compares expected vs actual ruling results and outputs formatted markdown.
#

set -e

EXPECTED_DIR="its/ruling/src/test/resources/expected"
ACTUAL_DIR="its/ruling/target/actual"

# Check if actual directory exists
if [ ! -d "$ACTUAL_DIR" ]; then
  exit 0
fi

# Get list of differing files
DIFF_OUTPUT=$(diff -rq "$EXPECTED_DIR" "$ACTUAL_DIR" 2>/dev/null || true)

if [ -z "$DIFF_OUTPUT" ]; then
  exit 0
fi

# Start report
echo "## Ruling Report"
echo ""
echo "The following ruling differences were detected:"
echo ""

# Track if we have any differences
HAS_DIFFERENCES=false

# Process files that differ
echo "$DIFF_OUTPUT" | grep "differ" | while read -r line; do
  HAS_DIFFERENCES=true
  expected_file=$(echo "$line" | awk '{print $2}')
  actual_file=$(echo "$line" | awk '{print $4}')

  # Get the rule name from the file path
  rule_name=$(basename "$expected_file" .json)

  echo "### Rule: \`$rule_name\`"
  echo ""
  echo "<details>"
  echo "<summary>Show differences</summary>"
  echo ""
  echo "\`\`\`diff"
  diff -u "$expected_file" "$actual_file" | head -100 || true
  echo "\`\`\`"
  echo ""
  echo "</details>"
  echo ""
done

# Process files only in expected (removed issues)
echo "$DIFF_OUTPUT" | grep "Only in $EXPECTED_DIR" | while read -r line; do
  HAS_DIFFERENCES=true
  file=$(echo "$line" | awk '{print $4}')

  echo "### Removed: \`$file\`"
  echo ""
  echo "This file exists in expected but not in actual results."
  echo ""
done

# Process files only in actual (new issues)
echo "$DIFF_OUTPUT" | grep "Only in $ACTUAL_DIR" | while read -r line; do
  HAS_DIFFERENCES=true
  file=$(echo "$line" | awk '{print $4}')
  full_path="$ACTUAL_DIR/$file"

  echo "### Added: \`$file\`"
  echo ""
  if [ -f "$full_path" ]; then
    # Count issues in the new file
    issue_count=$(grep -c '"line"' "$full_path" 2>/dev/null || echo "0")
    echo "New file with **$issue_count** issues."
    echo ""
    echo "<details>"
    echo "<summary>Show content</summary>"
    echo ""
    echo "\`\`\`json"
    head -50 "$full_path"
    if [ $(wc -l < "$full_path") -gt 50 ]; then
      echo "... (truncated)"
    fi
    echo "\`\`\`"
    echo ""
    echo "</details>"
  fi
  echo ""
done
