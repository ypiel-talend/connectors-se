#!/usr/bin/env bash

set -xe

# Builds the components and deploys them on Nexus, SKIPPING TESTS!
# $1: the Jenkinsfile's params.Action
# $@: the extra parameters to be used in the maven command
main() {
  local jenkinsAction="${1?Missing Jenkins action}"; shift
  local extraBuildParams=("$@")

  mvn clean deploy \
    --errors \
    --batch-mode \
    --threads '1C' \
    --activate-profiles "${jenkinsAction}" \
    "${extraBuildParams[@]}"
}

main "$@"
