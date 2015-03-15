package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObject;
import java.sql.*;
import com.tsoft.dbutils.lib.db.*;

class dbIndex extends dbObject {
    private String tableName;
    private String ddl;

    public dbIndex(dbIndexList list, String aTableName, String aObjectName) throws SQLException {
        super(list, aObjectName);
        tableName = aTableName;
        getDatabase().readDataSet("select dbms_metadata.get_ddl('INDEX', ?, ?) txt from dual",
                new Object[]{aObjectName, getDatabase().getSchema()},
                new DataSetInterface() {
                    @Override
                    public boolean onReadRecord(ResultSet rs) throws SQLException {
                        ddl = rs.getString("TXT");
                        return false;
                    }
                });
    }

    @Override
    public String getCategory() {
        return "index";
    }

    public String getXML() {
        String s = "<index name=\"" + name + "\">\n";
        s += "<table>" + tableName + "</table>\n";
        s += "<ddl>" + ddl + "</ddl>\n";
        s += "<created>" + created + "</created>\n";
        s += "</index>\n";
        return s;
    }

    public String getSQL() {
        return ddl + "\n/\n";
    }

    public String getInfo() {
        String s = "/*\n";
        s += " * @index " + name + "\n";
        s += " * @onTable " + tableName + "\n";
        s += " * @created " + created + "\n";
        s += "*/\n";
        return s;
    }
} 
