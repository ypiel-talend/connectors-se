#! /bin/bash

#
#  Copyright (C) 2006-2018 Talend Inc. - www.talend.com
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

work_dir="$BASEDIR/target/docker_server"
mkdir -p "$work_dir"
cd "$work_dir"
    grep '^      <artifactId>' "$BASEDIR/pom.xml" | sed "s#.*<artifactId>\(.*\)</artifactId>#\1=org.talend.components:\1:$CONNECTOR_VERSION#" | sort -u > component-registry.properties
    cp "$BASEDIR/src/main/docker/Dockerfile.server" Dockerfile
    cp -r "$BASEDIR/target/docker-m2" m2

    serverImage="tacokit/component-server:$COMPONENT_SERVER_IMAGE_VERSION"
    echo "Copying server from image $serverImage"
    slurpImageFolder component-kit "$serverImage" /opt/talend/component-kit ./component-kit
    echo "" >> ./component-kit/bin/setenv.sh
    echo 'export MEECROWAVE_OPTS="$MEECROWAVE_OPTS -Dtalend.component.server.component.registry=/opt/talend/connectors-se/component-registry.properties"' >> ./component-kit/bin/setenv.sh
    echo 'export MEECROWAVE_OPTS="$MEECROWAVE_OPTS -Dtalend.component.server.maven.repository=/opt/talend/connectors-se"' >> ./component-kit/bin/setenv.sh
    echo "" >> ./component-kit/bin/setenv.sh

    repoImageApp=$(extractApplicationName "$BASEDIR/src/main/docker/Dockerfile.repository")
    repoImage="$TALEND_REGISTRY/talend/$repoImageApp:$DOCKER_IMAGE_VERSION"
    echo "Copying repository from image $repoImage"
    slurpImageFolder connector-se "$repoImage" /opt/talend/connectors-se ./connectors-se

    buildAndTag
    pushImage $LAST_IMAGE
cd -
