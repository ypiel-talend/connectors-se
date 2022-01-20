#!/usr/bin/env bash

set -xe

# Builds the components with tests, Docker image and spotBugs enabled
# Also generates the Talend components uispec
# $1: the Jenkinsfile's params.Action
# $@: the extra parameters to be used in the maven commands
main() {
  local jenkinsAction="${1?Missing Jenkins action}"; shift
  local extraBuildParams=("$@")

  # Real task
  mvn clean install \
      --batch-mode \
      --threads '1C' \
      --activate-profiles "${jenkinsAction}" \
      "${extraBuildParams[@]}"
}

main "$@"
