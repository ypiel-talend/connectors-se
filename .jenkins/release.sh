#! /bin/bash

#echo ${BRANCH_NAME:0:3}
#echo ${GITHUB_LOGIN:0:3}
#echo ${GITHUB_TOKEN:0:3}
#
#echo ${NEXUS_USER:0:3}
#echo ${NEXUS_PASSWORD:0:3}
#
#echo ${NETSUITE_INTEGRATION_USER:0:3}
#echo ${NETSUITE_INTEGRATION_PASSWORD:0:3}
#
#echo ${NETSUITE_INTEGRATION_CONSUMER_USER:0:3}
#echo ${NETSUITE_INTEGRATION_CONSUMER_PASSWORD:0:3}
#
#echo ${NETSUITE_INTEGRATION_TOKEN_USER:0:3}
#echo ${NETSUITE_INTEGRATION_TOKEN_PASSWORD:0:3}

cd netsuite
mvn -U -B -s .jenkins/settings.xml clean install -PITs -Dtalend.maven.decrypter.m2.location=${WORKSPACE}/../.jenkins/ -e ${talendOssRepositoryArg}