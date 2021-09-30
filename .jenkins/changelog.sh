#! /bin/bash
echo "Fetching all tags."
#Too many unnecessary logged info
git fetch --tags -q
echo "Release version ${RELEASE_VERSION}"
echo "Getting last commit sha."
RELEASE_VERSION=1.25.0
if [[ ${BRANCH_NAME} != 'master' ]]; then
    MAINTENANCE_BRANCH=maintenance/${RELEASE_VERSION%.*}
    git fetch origin ${MAINTENANCE_BRANCH}:${MAINTENANCE_BRANCH} -q
    git fetch origin master:master -q
    LAST_COMMIT_SHA=$(git log --format="%H" ${MAINTENANCE_BRANCH}...master | head -n -1 | tail -n 1)
else
  # pushed to related origin maintenance branch
  MAJOR=$(echo ${RELEASE_VERSION} | cut -d. -f1)
  MINOR=$(echo ${RELEASE_VERSION} | cut -d. -f2)
  PATCH=$(($(echo ${RELEASE_VERSION} | cut -d. -f3) - 1))
  PREVIOUS_RELEASE_VERSION=${MAJOR}.${MINOR}.${PATCH}
  echo "Previous release version ${PREVIOUS_RELEASE_VERSION}"
  LAST_COMMIT_SHA=$(git log --format="%H" release/${PREVIOUS_RELEASE_VERSION}...release/${RELEASE_VERSION} | head -n -1 | tail -n 1)
fi
if [[ -z "${LAST_COMMIT_SHA}" ]]; then
    echo "Cannot evaluate last commit SHA."
    exit 123
fi
echo "Last commit sha - ${LAST_COMMIT_SHA}"
echo "Draft - ${DRAFT}"

# Checkout piece will be removed when the application is merged
cd .. && \
git clone https://github.com/Talend/connectivity-tools.git && \
cd connectivity-tools && \
git checkout mbasiuk/TDI-46073_release_note_app && \
cd release-notes && \
mvn clean package && \
cd target
JAR_NAME=$(find . -maxdepth 1 -name "*.jar" | cut -d/ -f2)
java -jar ${JAR_NAME} ${LAST_COMMIT_SHA}
