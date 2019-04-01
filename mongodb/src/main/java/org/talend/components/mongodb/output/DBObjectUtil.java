package org.talend.components.mongodb.output;

public class DBObjectUtil {

    private org.bson.Document object = null;

    // Put value to embedded document
    // If have no embedded document, put the value to root document
    public void put(OutputMapping mapping, String curentName, Object value) {
        String parentNode = mapping == null ? null : mapping.getParentNodePath();
        boolean removeNullField = mapping == null ? false : mapping.isRemoveNullField();
        if (removeNullField && value == null) {
            return;
        }
        if (parentNode == null || "".equals(parentNode)) {
            object.put(curentName, value);
        } else {
            String objNames[] = parentNode.split("\\.");
            org.bson.Document lastNode = getParentNode(parentNode, objNames.length - 1);
            lastNode.put(curentName, value);
            org.bson.Document parenttNode = null;
            for (int i = objNames.length - 1; i >= 0; i--) {
                parenttNode = getParentNode(parentNode, i - 1);
                parenttNode.put(objNames[i], lastNode);
                lastNode = clone(parenttNode);
            }
            object = lastNode;
        }
    }

    private org.bson.Document clone(org.bson.Document source) {
        org.bson.Document to = new org.bson.Document();
        for (java.util.Map.Entry<String, Object> cur : source.entrySet()) {
            to.append(cur.getKey(), cur.getValue());
        }
        return to;
    }

    // Get node(embedded document) by path configuration
    public org.bson.Document getParentNode(String parentNode, int index) {
        org.bson.Document document = object;
        if (parentNode == null || "".equals(parentNode)) {
            return object;
        } else {
            String objNames[] = parentNode.split("\\.");
            for (int i = 0; i <= index; i++) {
                document = (org.bson.Document) document.get(objNames[i]);
                if (document == null) {
                    document = new org.bson.Document();
                    return document;
                }
                if (i == index) {
                    break;
                }
            }
            return document;
        }
    }

    public void putkeyNode(OutputMapping mapping, String currentName, Object value) {
        String parentNode = mapping == null ? null : mapping.getParentNodePath();
        if (mapping == null || parentNode == null || "".equals(parentNode) || ".".equals(parentNode)) {
            put(mapping, currentName, value);
        } else {
            put(new OutputMapping(currentName, "", mapping.isRemoveNullField()), parentNode + "." + currentName, value);
        }
    }

    public org.bson.Document getObject() {
        return this.object;
    }

    public void setObject(org.bson.Document object) {
        this.object = object;
    }

}
