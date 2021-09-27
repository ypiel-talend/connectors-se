#! /bin/bash

# git config hack when pushing to bypass :
# "fatal: could not read Username for 'https://github.com': No such device or address" error.
# This appeared after 2fa auth activation on github.
git config --global credential.username ${GITHUB_LOGIN}
git config --global credential.helper '!echo password=${GITHUB_TOKEN}; echo'
git config --global credential.name "jenkins-build"
env | sort

pre_release_version=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout)
RELEASE_VERSION=$(echo ${pre_release_version} | cut -d- -f1)
