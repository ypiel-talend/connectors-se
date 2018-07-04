#!/usr/bin/env bash

# Build local connectors and then build final component-server by multi-stage, then we only need one final component-server rather than volume way between connectors-se and component-server

mvn install -DskipTests -Dmaven.javadoc.skip=true -Pdocker
__version=$(grep "<version>" pom.xml  | head -n 1 | sed "s/.*>\\(.*\\)<.*/\\1/")
__image=talend/connectors-se:$(echo $__version | sed "s/SNAPSHOT/dev/")
grep "^      <artifactId>" connectors-se-docker/pom.xml \
    | sed "s#.*<artifactId>\\(.*\\)</artifactId>#\\1=org.talend.components:\\1:${__version}#" \
    | grep localio \
    | sort -u > component-registry.properties
echo Installed components:
cat component-registry.properties
docker build --tag $__image --build-arg BUILD_VERSION=${__version} .

# Need to specify two env for build,
# CONNECTORS_SE_TAG is the connectors-se image tag build on local,
# COMPONENT_SERVER_TAG is the component-server image tag can be found from https://hub.docker.com/r/tacokit/component-server/
# for example
# export CONNECTORS_SE_TAG=1.0.0-dev                                                                                                  1 ↵
# export COMPONENT_SERVER_TAG=1.0.1_20180702085026

CONNECTORS_SE_TAG_SUGGEST=$(echo $__version | sed "s/SNAPSHOT/dev/")
COMPONENT_SERVER_TAG=${COMPONENT_SERVER_TAG:-latest}

docker build --tag talend/component-server:1.0.2-sprint26-connectorsIncluded \
	--build-arg CONNECTORS_SE_TAG=${CONNECTORS_SE_TAG:-$CONNECTORS_SE_TAG_SUGGEST} \
	--build-arg COMPONENT_SERVER_TAG=$COMPONENT_SERVER_TAG \
	-f component-server-docker/Dockerfile .
