package com.tsoft.dbutils.dtt;

import org.w3c.dom.*;
import java.sql.*;
import com.tsoft.dbutils.lib.db.*;

/**
 * TSQL/PLSQL
 */
public class DTTCode {
    private String code = "";           // TSQL/PLSQL
    private String connectionName;
    private String wrongMessage;
    private DTTConnection connection;

    public DTTCode(Element element) {
        connectionName = element.getAttribute("connection");
        if (connectionName == null) {
            wrongMessage = "Wrong element";
            return;
        }

        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node != null) {
                code += node.getNodeValue();
            }
        }
    }

    public boolean resolveConnection(DTTConnectionList connectionList) {
        connection = connectionList.find(connectionName);
        return connection != null;
    }

    public boolean execute() throws SQLException {
        Database database = connection.getDatabase();
        return database.executeCommand(code);
    }
}
