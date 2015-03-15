package com.tsoft.dbutils.dmt;

import org.w3c.dom.*;
import com.tsoft.dbutils.lib.db.*;

/*
 *  <connection name="name">
 *     <driver>jdbc driver class</driver>
 *     <url>url to the database</url>
 *     <username>database login</username>
 *     <password>login password</password>
 *   </connection>
 */
public class DMTConnection {
    private String name;
    private String driver;   // "oracle.jdbc.OracleDriver"
    private String url;      // "jdbc:oracle:thin:@ASU:1521:ORADB"
    private String schema;   // schema
    private String userName;
    private String password; // password
    
    private Database database;

    public DMTConnection(Element root) throws Exception {
        name = root.getAttribute("name");
        if (name.length() == 0) {
            throw new Exception("'connection' tag must have the 'name' attribute");
        }

        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                Node textNode = element.getFirstChild();
                String text = textNode.getNodeValue().trim();

                if (element.getTagName().equals("driver")) {
                    driver = text;
                } else if (element.getTagName().equals("url")) {
                    url = text;
                } else if (element.getTagName().equals("schema")) {
                    schema = text;
                } else if (element.getTagName().equals("username")) {
                    userName = text;
                } else if (element.getTagName().equals("password")) {
                    password = text;
                } else {
                    throw new Exception("Unknown element '" + element.getTagName() + "' in the 'connection' tag");
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public Database getDatabase() {
        if (database == null) {
            database = new Database(driver, url, schema, userName, password);
        }
        return database;
    }

    @Override
    public void finalize() {
        if (database != null) {
            database.close();
        }
    }
}
