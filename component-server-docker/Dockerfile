ARG CONNECTORS_SE_TAG
ARG COMPONENT_SERVER_TAG

FROM talend/connectors-se:${CONNECTORS_SE_TAG} as componentRepoImage

FROM talend/component-server:${COMPONENT_SERVER_TAG}

COPY --from=componentRepoImage /opt/talend/maven /opt/talend/maven
