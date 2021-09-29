#! /bin/bash
# git config hack when pushing to bypass :
# "fatal: could not read Username for 'https://github.com': No such device or address" error.
# This appeared after 2fa auth activation on github.
git config --global credential.username ${GITHUB_LOGIN}
git config --global credential.helper '!echo password=${GITHUB_TOKEN}; echo'
git config --global credential.name "jenkins-build"
echo "Fetching all tags."
#Too many unnecessary logged info
git fetch --tags > /dev/null
echo "Release version for now ${RELEASE_VERSION}"
echo "Getting last commit sha."
if [[ ${BRANCH_NAME} == 'master' ]]; then
    NEW_MAINTENANCE="maintenance/${RELEASE_VERSION%.*}"
    LAST_COMMIT_SHA=$(git log --format="%H" origin/${NEW_MAINTENANCE}...master | head -n -1 | tail -n 1)
else
  # pushed to related origin maintenance branch
  MAJOR=$(echo ${RELEASE_VERSION} | cut -d. -f1)
  MINOR=$(echo ${RELEASE_VERSION} | cut -d. -f2)
  PATCH=$(($(echo ${RELEASE_VERSION} | cut -d. -f3) - 1))
  PREVIOUS_RELEASE_VERSION=${MAJOR}.${MINOR}.${PATCH}
  LAST_COMMIT_SHA=28c9acf4139e37e56818a57260ce31c7e6720ed5
  #Commented to test changes.
  #$(git log --format="%H" release/${PREVIOUS_RELEASE_VERSION}...release/${NEW_RELEASE_VERSION} | head -n -1 | tail -n 1)
fi

echo "Last commit sha - ${LAST_COMMIT_SHA}"
echo "Draft - ${DRAFT}"
echo "New release version - ${RELEASE_VERSION}"

cd .. && \
git clone https://github.com/Talend/connectivity-tools.git && \
cd connectivity-tools && \
git checkout mbasiuk/TDI-46073_release_note_app && \
cd release-notes && \
mvn clean package && \
cd target
JAR_NAME=$(find . -maxdepth 1 -name "*.jar" | cut -d/ -f2)
java -jar ${JAR_NAME} ${LAST_COMMIT_SHA}
