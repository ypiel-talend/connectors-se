#!/usr/bin/env bash

set -xe

# Releases the components
# $1: the Jenkinsfile's params.Action
# $2: the version being released (semver, extracted from pom)
# $@: the extra parameters to be used in the maven commands
main() (
  jenkinsAction="${1?Missing Jenkins action}"
  releaseVersion="${2?Missing release version}"
  shift; shift
  extraBuildParams=("$@")

  setMavenVersion "${releaseVersion}"
  setMavenProperty 'common.version' "${releaseVersion}"

  mvn deploy \
    --errors \
    --batch-mode \
    --threads '1C' \
    --activate-profiles "${jenkinsAction}" \
    "${extraBuildParams[@]}"

  git add --update
  git commit --message "[jenkins-release] Release ${releaseVersion}"

  majorVersion="$(cut --delimiter '.' --fields 1 <<< "${releaseVersion}")"
  minorVersion="$(cut --delimiter '.' --fields 2 <<< "${releaseVersion}")"
  patchVersion="$(cut --delimiter '.' --fields 3 <<< "${releaseVersion}")"
  postReleaseVersion="${majorVersion}.${minorVersion}.$((patchVersion + 1))-SNAPSHOT"

  tag="release/${releaseVersion}"
  git tag "${tag}"

  setMavenVersion "${postReleaseVersion}"
  setMavenProperty 'common.version' "${postReleaseVersion}"

  git add --update
  git commit --message "[jenkins-release] Prepare for next development iteration ${postReleaseVersion}"

  git push origin "${tag}"
  git push origin "$(git rev-parse --abbrev-ref HEAD)"
)

setMavenVersion() (
  version="$1"
  mvn 'versions:set' \
    --batch-mode \
    --define "newVersion=${version}"
)

setMavenProperty() (
  propertyName="$1"
  propertyValue="$2"
  mvn 'versions:set-property' \
    --batch-mode \
    --define "property=${propertyName}" \
    --define "newVersion=${propertyValue}"
)

main "$@"
