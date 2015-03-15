package com.tsoft.dbutils.dtt;

import java.io.*;
import java.sql.*;
import org.w3c.dom.*;
import com.tsoft.dbutils.lib.db.*;

public class DTTConnection {
    private String name;
    private String driver;                  // "oracle.jdbc.OracleDriver"
    private String url;                     // "jdbc:oracle:thin:@ASU:1521:ORADB"
    private String userName;
    private String password;
    private String wrongMessage;
    private boolean used;
    private Connection connection;
    private Database database;

    public DTTConnection(Element root) {
        name = root.getAttribute("name");
        if (name.length() == 0) {
            wrongMessage = "Element 'connection' has wrong 'name'";
            return;
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
                } else if (element.getTagName().equals("username")) {
                    userName = text;
                } else if (element.getTagName().equals("password")) {
                    password = text;
                } else {
                    wrongMessage = "Unknown element'" + element.getTagName() + "' for 'connection'";
                    break;
                }
            }
        }
    }

    public String getWrongMessage() {
        return wrongMessage;
    }

    public String getName() {
        return name;
    }

    public void setUsed() {
        used = true;
    }

    public boolean isUsed() {
        return used;
    }

    public Database getDatabase() {
        if (database == null) {
            database = new Database(driver, url, userName, userName, password);
            database.setLogFileName(new File("").getAbsolutePath(), name + ".log");
        }
        return database;
    }

    public void close() {
        if (database != null) {
            database.close();
        }
    }
}
