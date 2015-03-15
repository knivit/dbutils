package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObject;
import java.sql.*;

class dbDBLink extends dbObject {
    private String userName;
    private String host;

    public dbDBLink(dbDBLinkList list, String aObjectName) throws SQLException {
        super(list, aObjectName);
    }

    @Override
    public String getCategory() {
        return "dblink";
    }

    public void setUserName(String aUserName) {
        userName = aUserName;
    }

    public void setHost(String aHost) {
        host = aHost;
    }

    public String getXML() {
        String s = "<dblink name=\"" + name + "\">\n";
        s = s + "<created>" + created + "</created>\n";
        s = s + "<script><![CDATA[>" + getSQL() + "]]></script>\n";
        s = s + "</dblink>\n";
        return s;
    }

    public String getSQL() {
        return "CREATE DATABASE LINK " + name + " CONNECT TO " + host + " IDENTIFIED BY " + userName + "\n";
    }

    public String getInfo() {
        String s = "/*\n";
        s = s + " * @dblink " + name + "\n";
        s = s + "*/\n";
        return s;
    }
}
