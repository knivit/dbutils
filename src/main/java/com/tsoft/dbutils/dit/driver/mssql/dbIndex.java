package com.tsoft.dbutils.dit.driver.mssql;

import java.sql.*;

class dbIndex extends dbObject {
    private String tableName;
    private String columnNameList;
    private boolean primaryKey;
    private boolean clustered;
    private boolean unique;

    public dbIndex(dbIndexList list, String aTableName, int aID, String aObjectName) throws SQLException {
        super(list, aID, aObjectName);
        tableName = aTableName;
    }

    @Override
    public String getCategory() {
        return "index";
    }

    public void setColumnNameList(String aColumnNameList) {
        columnNameList = aColumnNameList;
    }

    public void setClustered(boolean aClustered) {
        clustered = aClustered;
    }

    public void setUnique(boolean aUnique) {
        unique = aUnique;
    }

    public void setPrimaryKey(boolean aPrimaryKey) {
        primaryKey = aPrimaryKey;
    }

    public String getXML() {
        String s = "<index name=\"" + name + "\">\n";
        s += "<table>" + tableName + "</table>\n";
        s += "<columns>" + columnNameList + "</columns>\n";
        s += "<created>" + created + "</created>\n";
        s += "<script><![CDATA[>" + getSQL() + "]]></script>\n";
        s += "</index>\n";
        return s;
    }

    public String getSQL() {
        String s = "CREATE INDEX ";
        if (unique) {
            s += "UNIQUE ";
        }
        if (clustered) {
            s += "CLUSTERED ";
        }
        s += name + " ON " + tableName + "(" + columnNameList + ")";
        s = s + "\nGO\n";
        return s;
    }

    public String getInfo() {
        String s = "/*\n";
        s += " * @name " + name + "\n";
        s += " * @onTable " + tableName + "\n";
        s += " * @created " + created + "\n";
        s += "*/\n";
        return s;
    }
} 
