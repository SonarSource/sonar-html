#!/usr/bin/env bash

set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
BASE_SHA="${BASE_BRANCH:-origin/master}" node .github/actions/ruling_bot/local-report.mjs
