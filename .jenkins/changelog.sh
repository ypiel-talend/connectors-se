#! /bin/bash

echo "Fetching all tags."
#Too many unnecessary logged info
git fetch --tags -q
echo "Release version ${RELEASE_VERSION}"
echo "Getting last commit sha."
if [[ ${BRANCH_NAME} -ne 'master' ]]; then
    RELEASE_VERSION=1.25.0
    LAST_COMMIT_SHA=$(git log --format="%H" origin/maintenance/${RELEASE_VERSION%.*}...master | head -n -1 | tail -n 1)
else
  # pushed to related origin maintenance branch
  MAJOR=$(echo ${RELEASE_VERSION} | cut -d. -f1)
  MINOR=$(echo ${RELEASE_VERSION} | cut -d. -f2)
  PATCH=$(($(echo ${RELEASE_VERSION} | cut -d. -f3) - 1))
  PREVIOUS_RELEASE_VERSION=${MAJOR}.${MINOR}.${PATCH}
  LAST_COMMIT_SHA=$(git log --format="%H" release/${PREVIOUS_RELEASE_VERSION}...release/${RELEASE_VERSION} | head -n -1 | tail -n 1)
fi

echo "Previous release version ${PREVIOUS_RELEASE_VERSION}"
echo "Last commit sha - ${LAST_COMMIT_SHA}"
echo "Draft - ${DRAFT}"

cd .. && \
git clone https://github.com/Talend/connectivity-tools.git && \
cd connectivity-tools && \
git checkout mbasiuk/TDI-46073_release_note_app && \
cd release-notes && \
mvn clean package && \
cd target
JAR_NAME=$(find . -maxdepth 1 -name "*.jar" | cut -d/ -f2)
java -jar ${JAR_NAME} ${LAST_COMMIT_SHA}
