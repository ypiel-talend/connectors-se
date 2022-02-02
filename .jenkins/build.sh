#!/usr/bin/env bash

set -xe

# Builds the components with tests, Docker image and spotBugs enabled
# Also generates the Talend components uispec
# $1: the Jenkinsfile's params.Action
# $@: the extra parameters to be used in the maven commands
main() (
  jenkinsAction="${1?Missing Jenkins action}"; shift
  extraBuildParams=("$@")

  # Real task
  # ITs profile is added for jdbc
  mvn verify \
      --errors \
      --batch-mode \
      --threads '1C' \
      --activate-profiles "${jenkinsAction},ITs" \
      "${extraBuildParams[@]}"
)

main "$@"
