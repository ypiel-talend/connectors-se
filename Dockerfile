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

ARG BUILD_VERSION

RUN date

ENV OUTPUT /opt/talend/maven
RUN mkdir -p $OUTPUT
WORKDIR $OUTPUT

ADD component-registry.properties component-registry.properties
ADD connectors-se-docker/target/connectors-se-docker-$BUILD_VERSION.car repository.car

RUN set -ex && \
    unzip repository.car 'MAVEN-INF/*' -d . && \
    mv MAVEN-INF/repository repository && \
    rm -Rf repository.car MAVEN-INF

FROM alpine:3.7

MAINTAINER contact@talend.com

ENV LC_ALL en_US.UTF-8

ENV OUTPUT /opt/talend/maven
RUN mkdir -p $OUTPUT
WORKDIR $OUTPUT

COPY --from=0 /opt/talend/maven/component-registry.properties component-registry.properties
COPY --from=0 /opt/talend/maven/repository repository

CMD [ "/bin/sh" ]
