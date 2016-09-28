#!/usr/bin/env bash

set -e
set -o pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

declare -A RULE_CTX=(
  [dir]="$DIR/src/main/resources/org/sonar/l10n/web/rules/Web"
  [lang]="web"
)
RULE_CTX[profile]="${RULE_CTX[dir]}/Sonar_way_profile.json"

declare -A RULE_API=(
  [groupId]="com.sonarsource.rule-api"
  [artifactId]="rule-api"
  [version]="1.16-SNAPSHOT"
)
RULE_API[artifact]="${RULE_API[groupId]}:${RULE_API[artifactId]}:${RULE_API[version]}"
RULE_API[jar_dir]="${DIR}/target/dependency"
RULE_API[jar]="${RULE_API[jar_dir]}/${RULE_API[artifactId]}-${RULE_API[version]}.jar"

function show_usage {
  echo "Usage:"
  echo "  ./rule-api.sh generate -rule S1234 # Generate html and json description for one rule"
  echo "  ./rule-api.sh update               # Update html and json description files"
  echo "  ./rule-api.sh diff                 # Generates a diff report into target/reports/outdated"
}

function print_and_exec {
  printf "%q " "$@"
  printf "\n"
  "$@"
}

function rule_api {
  local RULE_CMD="$1"
  shift
  if [[ ! -e ${RULE_API[jar]} ]]; then
    mkdir -p "${RULE_API[jar_dir]}"
    print_and_exec mvn --quiet org.apache.maven.plugins:maven-dependency-plugin:2.10:copy -Dartifact="${RULE_API[artifact]}" -DoutputDirectory="${RULE_API[jar_dir]}"
  fi
  # when updating, first delete the profile before execution to ensure a clean generation and get rid of deleted rule keys.
  if [[ ${RULE_CMD} == 'update' ]] && [[ -e "${RULE_CTX[profile]}" ]]; then
    print_and_exec rm "${RULE_CTX[profile]}"
  fi
  print_and_exec java -Djava.awt.headless=true -jar "${RULE_API[jar]}" "${RULE_CMD}" -preserve-filenames -no-language-in-filenames -language "${RULE_CTX[lang]}" -directory "${RULE_CTX[dir]}" "$@"
}

if [[ -z $1 ]]; then
  show_usage
else
  rule_api "$@"
fi
