#FROM registry.datapwn.com/vizotenko/components-integration-test-rabbitmq:1.0.0
FROM bitnami/rabbitmq:3.7.13
USER root
COPY rabbitmq.config /

RUN cd / && ./app-entrypoint.sh nami start rabbitmq
# && sleep 30.0 && sudo ./app-entrypoint.sh nami stop rabbitmq

#USER root
COPY rabbitmq.config /bitnami/rabbitmq/conf/
COPY ca_certificate.pem /bitnami/rabbitmq/certificates/
COPY server_certificate.pem /bitnami/rabbitmq/certificates/
COPY server_key.pem /bitnami/rabbitmq/certificates/

#USER 1001

HEALTHCHECK --interval=5s --retries=20 CMD curl --fail http://localhost:15672/ || exit 1
#HEALTHCHECK --timeout=300s CMD curl --fail http://localhost:15672/ || exit 1