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
FROM alpine:3.7

ARG ARTIFACT_ID=${project.artifactId}
ARG ARTIFACT_VERSION=${project.version}
ARG ARTIFACT_TYPE=car

MAINTAINER contact@talend.com

ENV LC_ALL en_US.UTF-8

ENV OUTPUT /opt/talend/maven
RUN mkdir -p $OUTPUT
WORKDIR $OUTPUT

RUN date

ADD component-registry.properties component-registry.properties
ADD $ARTIFACT_ID-$ARTIFACT_VERSION.$ARTIFACT_TYPE $ARTIFACT_ID.$ARTIFACT_TYPE

# todo: signature when available
RUN set -ex && \
    unzip $ARTIFACT_ID.$ARTIFACT_TYPE 'MAVEN-INF/*' -d . && \
    mv MAVEN-INF/repository repository && \
    rm -Rf $ARTIFACT_ID.$ARTIFACT_TYPE MAVEN-INF

CMD [ "/bin/sh" ]
