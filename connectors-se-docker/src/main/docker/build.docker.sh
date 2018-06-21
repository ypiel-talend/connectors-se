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

#
# we assume we are in target/classes to run that script
#

IMAGE="talend/${project.parent.artifactId}:$(echo "${project.version}" | sed 's/SNAPSHOT/dev/')"
TALEND_REGISTRY="${TALEND_REGISTRY:-registry.datapwn.com}"

if [ -n "$DOCKER_LOGIN" ]; then docker "$TALEND_REGISTRY" -u "$DOCKER_LOGIN" -p "$DOCKER_PASSWORD"; fi

# create the registration file for components (used by the server)
grep '^      <artifactId>' ../../pom.xml | sed 's#.*<artifactId>\(.*\)</artifactId>#\1=org.talend.components:\1:${project.version}#' | sort -u > component-registry.properties

# ensure .car is in the docker build context
cp ../*.car .

# drop already existing snapshot image if any
if [[ "${project.version}" = *"SNAPSHOT" ]]; then
  docker rmi "$IMAGE" "$TALEND_REGISTRY/$IMAGE" || :
fi

# build and push current image
docker build --tag "$IMAGE" . && \
docker tag "$IMAGE" "$TALEND_REGISTRY/$IMAGE" && \
docker push "$TALEND_REGISTRY/$IMAGE"
