#! /bin/bash

# git config hack when pushing to bypass :
# "fatal: could not read Username for 'https://github.com': No such device or address" error.
# This appeared after 2fa auth activation on github.
git config --global credential.username ${GITHUB_LOGIN}
git config --global credential.helper '!echo password=${GITHUB_TOKEN}; echo'
git config --global credential.name "jenkins-build"
env | sort

echo "Fetching all tags."
#Too many unnecessary logged info
git fetch --tags -q
echo "Release version ${RELEASE_VERSION}"
echo "Getting last commit sha."
BRANCH_NAME=master
RELEASE_VERSION=1.25.0

if [[ ${BRANCH_NAME} == 'master' ]]; then
    MAINTENANCE_BRANCH=maintenance/${RELEASE_VERSION%.*}
    git fetch origin ${MAINTENANCE_BRANCH}:${MAINTENANCE_BRANCH} -q
    git fetch origin master:master -q
    LAST_COMMIT_SHA=$(git log --format="%H" ${MAINTENANCE_BRANCH}...master | head -n -1 | tail -n 1)
else
  # Maintenance branch
  MAJOR=$(echo ${RELEASE_VERSION} | cut -d. -f1)
  MINOR=$(echo ${RELEASE_VERSION} | cut -d. -f2)
  PATCH=$(($(echo ${RELEASE_VERSION} | cut -d. -f3) - 1))
  PREVIOUS_RELEASE_VERSION=${MAJOR}.${MINOR}.${PATCH}
  echo "Previous release version ${PREVIOUS_RELEASE_VERSION}"
  LAST_COMMIT_SHA=$(git log --format="%H" release/${PREVIOUS_RELEASE_VERSION}...release/${RELEASE_VERSION} | head -n -1 | tail -n 1)
fi

if [[ -z "${LAST_COMMIT_SHA}" ]]; then
    echo "Cannot evaluate last commit SHA. Changelog won't be genarated."
else
    echo "Last commit sha - ${LAST_COMMIT_SHA}"
    echo "Draft - ${DRAFT}"

    # Checkout piece will be removed when the application is merged
    cd .. && \
    git clone https://github.com/Talend/connectivity-tools.git && \
    cd connectivity-tools && \
    git checkout mbasiuk/TDI-46073_release_note_app && \
    cd release-notes && \
    mvn clean package

    java -jar target/$(find target -maxdepth 1 -name "*.jar" | cut -d/ -f2) ${LAST_COMMIT_SHA}
fi
