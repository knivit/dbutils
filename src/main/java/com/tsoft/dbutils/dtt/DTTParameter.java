package com.tsoft.dbutils.dtt;

import java.sql.*;
import org.w3c.dom.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

public class DTTParameter implements DataSetInterface {
    private String name;
    private String connectionName;
    private String query;
    private DTTConnection connection;
    private String value;

    public DTTParameter(Element element) {
        name = element.getAttribute("name");
        connectionName = element.getAttribute("connection");
        query = element.getAttribute("value");
    }

    public boolean resolveConnection(DTTConnectionList connectionList) {
        connection = connectionList.find(connectionName);
        return connection != null;
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        value = rs.getString(1);
        return false;
    }

    private String getValue() {
        if (value == null) {
            Database database = connection.getDatabase();
            database.readDataSet(query, null, this, 1);
        }
        return value;
    }

    public String replaceValue(String str, DTTParameterList paramList) {
        return StringUtils.replaceAll(str, "@" + name, getValue());
    }
}
