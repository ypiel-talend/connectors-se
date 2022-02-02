#!/usr/bin/env bash

set -xe

# TODO: why is this necessary?
main() (
  printf 'Remove out-dated yum repository and refresh cache\n'
  rm /etc/yum.repos.d/bintray-sbt-rpm.repo
  yum clean all
  yum makecache
)

main "$@"
