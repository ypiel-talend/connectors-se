#! /bin/bash
# git config hack when pushing to bypass :
# "fatal: could not read Username for 'https://github.com': No such device or address" error.
# This appeared after 2fa auth activation on github.
git config --global credential.username ${GITHUB_LOGIN}
git config --global credential.helper '!echo password=${GITHUB_TOKEN}; echo'
git config --global credential.name "jenkins-build"

if [[ ${BRANCH_NAME} == 'master' ]]; then
    NEW_MAINTENANCE="maintenance/${RELEASE_VERSION%.*}"
    LAST_COMMIT_SHA=$(git log --format="%H" origin/${NEW_MAINTENANCE}...master | head -n -1 | tail -n 1)
    NEW_RELEASE_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout | cut -d- -f1)
else
  # pushed to related origin maintenance branch
  MAJOR=$(echo ${RELEASE_VERSION} | cut -d. -f1)
  MINOR=$(echo ${RELEASE_VERSION} | cut -d. -f2)
  PATCH=$(($(echo ${RELEASE_VERSION} | cut -d. -f3) - 1))
  PREVIOUS_RELEASE_VERSION=${MAJOR}.${MINOR}.${PATCH}
  NEW_RELEASE_VERSION=$RELEASE_VERSION
  LAST_COMMIT_SHA=$(git log --format="%H" release/${PREVIOUS_RELEASE_VERSION}...release/${NEW_RELEASE_VERSION} | head -n -1 | tail -n 1)
fi

IS_DRAFT=true

cd ..
git clone https://github.com/Talend/connectivity-tools.git
cd connectivity-tools
git checkout mbasiuk/TDI-46073_release_note_app
cd release-notes
mvn clean install #package
cd target
JAR_NAME=$(find . -maxdepth 1 -name "*.jar" | cut -d/ -f2)
java -jar $JAR_NAME $NEW_RELEASE_VERSION $REPOSITORY $LAST_COMMIT_SHA
