Before test need to run the command in cmd:
docker build -t skhylko/solr:1.0.0 .

generate keystore
"%JAVA_HOME%\bin\keytool" -genkeypair -alias solr-ssl -keyalg RSA -keysize 2048 -dname "CN=test_docker_solr" -keypass secret -validity 3650 -storetype JKS -keystore solr-ssl.keystore.jks -storepass secret -ext san=ip:127.0.0.1,ip:172.20.17.134,ip:172.17.0.1,ip:172.20.17.139,ip:192.168.99.100