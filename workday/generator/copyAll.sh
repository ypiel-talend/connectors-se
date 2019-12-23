cp target/test-classes/all/Messages.properties ~/project/workday/connectors-se/workday/src/main/resources/org/talend/components/workday/dataset/service/input/
rm ~/project/workday/connectors-se/workday/src/main/java/org/talend/components/workday/dataset/service/input/*.java
cp target/test-classes/all/*.java ~/project/workday/connectors-se/workday/src/main/java/org/talend/components/workday/dataset/service/input/

