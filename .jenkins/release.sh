#!/usr/bin/env bash

set -xe

# Releases the components
# $1: the version being released (semver, extracted from pom)
# $@: the extra parameters to be used in the maven commands
main() {
  local releaseVersion="${1?Missing release version}"; shift
  local extraBuildParams=("$@")

  # Prepare release
  mvn release:clean release:prepare \
    --batch-mode \
    --define 'maven.javadoc.skip=true' \
    --define "arguments=-Dmaven.javadoc.skip=true" \
    "${extraBuildParams[@]}"

  # perform release
  mvn release:perform \
    --batch-mode \
    --define 'maven.javadoc.skip=true' \
    --define "arguments=-Dmaven.javadoc.skip=true" \
    "${extraBuildParams[@]}"

  postReleaseVersion="$(
    mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate \
      --quiet \
      --define 'forceStdout' \
      --define 'expression=project.version'
  )"

  # Reset the current branch to the commit just before the release:
  git reset --hard 'HEAD~2'

  # Squash merge to next-iter state:
  git merge --squash 'HEAD@{1}'

  # Commit:
  git add --update
  git commit --message "[jenkins-release] prepare for next development iteration ${postReleaseVersion}"

  # push release bump to origin
  if [[ "${BRANCH_NAME}" == 'master' ]]; then
      # master is out of date, creating a maintenance branch
      # TODO: 👇 shellcheck doesn't like that
      masterVersion=${releaseVersion%.*}
      git checkout -b         "maintenance/${masterVersion}"
      git push --force origin "maintenance/${masterVersion}"

      # bump master
      major="$(awk -F '.' '{print $1}' <<< "${masterVersion}")"
      minor="$(awk -F '.' '{print $2}' <<< "${masterVersion}")"
      newMinor="$(( minor + 1))"
      nextMasterVersion="${major}.${newMinor}.0-SNAPSHOT"

      # apply bump
      mvn versions:set \
        --batch-mode \
        --define "newVersion=${nextMasterVersion}" \
        "${extraBuildParams[@]}"

      setMavenProperty 'common.version' "${nextMasterVersion}"
      setMavenProperty 'locales.version' "[${major}.${newMinor},)"
      setMavenProperty 'connectors-test-bom.version' "${nextMasterVersion}"

      git add --update
      git commit --message "[jenkins-release] prepare for next development iteration ${nextMasterVersion}"

      # master is a protected branch, should create a PR
      git checkout -b                "jenkins/master-next-iteration-${nextMasterVersion}"
      git push --set-upstream origin "jenkins/master-next-iteration-${nextMasterVersion}"
  else
      # pushed to related origin maintenance branch
      git push --force origin "HEAD:${BRANCH_NAME}"
  fi

  #
  # tag / I think no need
  #
  #git checkout     "release/${releaseVersion}"
  #git tag --delete "release/${releaseVersion}"
  #setMavenProperty 'connectors-se.version' "${releaseVersion}"
  
  #git add --update
  #git commit --message "[jenkins-release] setting connectors-se.version to ${releaseVersion}"
  
  #git tag         "release/${releaseVersion}"
  #git push origin "release/${releaseVersion}"
  #printf 'Tagged release\n'
}

setMavenProperty() {
  local propertyName="$1"
  local propertyValue="$2"
  mvn versions:set-property \
    --define "property=${propertyName}" \
    --define "newVersion=${propertyValue}"
}

main "$@"
