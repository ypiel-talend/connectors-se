 This dockerfile was prepared to test image with TLS enabled
 
 How to prepare a test image:
 
 1. Run rabbitmq container.
 2. Copy <container id>:/bitnami/ to src/main/docker/demo/bitnami
 3. Copy conf/rabbitmq.config to src/main/docker/demo/bitnami/rabbitmq/conf/
 docker build -t vizotenko/components-integration-test-rabbitmq:1.0.1 .
 docker tag <image id> <registry>/vizotenko/components-integration-test-rabbitmq:1.0.1
 docker push <registry>/vizotenko/components-integration-test-rabbitmq:1.0.1
 