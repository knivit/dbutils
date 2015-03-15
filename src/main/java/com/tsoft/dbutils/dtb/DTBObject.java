package com.tsoft.dbutils.dtb;

import org.w3c.dom.*;

public class DTBObject {
    private String driver;
    private String url;
    private String userName;
    private String password;
    private String value;

    public DTBObject(Element root, String valueTag) throws Exception {
        NodeList node = root.getChildNodes();
        for (int i = 0; i < node.getLength(); i++) {
            Node child = node.item(i);
            if (child instanceof Element) {
                Element element = (Element) child;
                Node textNode = element.getFirstChild();
                String text = textNode.getNodeValue().trim();

                if (element.getTagName().equals("driver")) {
                    driver = text;
                } else if (element.getTagName().equals("url")) {
                    url = text;
                } else if (element.getTagName().equals("username")) {
                    userName = text;
                } else if (element.getTagName().equals("password")) {
                    password = text;
                } else if (element.getTagName().equals(valueTag)) {
                    value = text;
                } else {
                    throw new Exception("Unknown element '" + element.getTagName() + "' '" + root.getTagName() + "' tag");
                }
            }
        }
    }

    public String getValue() {
        return value;
    }

    public String getDriver() {
        return driver;
    }

    public String getURL() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
