#!/usr/bin/env bash

set -xe

# Builds the components with tests, Docker image and spotBugs enabled
# Also generates the Talend components uispec
# $1: the Jenkinsfile's params.Action
# $2: Execute maven verify or deploy whether the branch is maintenance or master.
# $3: Execute sonar anlaysis or not , from jenkinsfile's params.SONAR_ANALYSIS
# $@: the extra parameters to be used in the maven commands
main() (
  jenkinsAction="${1?Missing Jenkins action}"; shift
  isOnMasterOrMaintenanceBranch="${2?Missing is on master or a maintenance branch}"; shift
  sonar="${3?Missing sonar option}"; shift
  extraBuildParams=("$@")

    mavenPhase='verify'
  if [[ "${isOnMasterOrMaintenanceBranch}" == 'true' ]]; then
    mavenPhase='deploy'
  fi

  # Real task
  # ITs profile is added for jdbc
  mvn "${mavenPhase}" \
      --errors \
      --batch-mode \
      --threads '1C' \
      --activate-profiles "${jenkinsAction},ITs" \
      "${extraBuildParams[@]}"

  if [[ "${sonar}" == 'true' ]]; then
    mvn -Dsonar.host.url=https://sonar-eks.datapwn.com -Dsonar.login='$SONAR_LOGIN' \
        -Dsonar.password='$SONAR_PASSWORD' \
        -Dsonar.branch.name=${env.BRANCH_NAME} \
        -Dsonar.coverage.jacoco.xmlReportPaths='${LIST_FILE}' \
        sonar:sonar \
        -PITs,SONAR \
        -s .jenkins/settings.xml \
        -Dtalend.maven.decrypter.m2.location=${env.WORKSPACE}/.jenkins/ \
        "${extraBuildParams[@]}"
  fi
)

main "$@"
