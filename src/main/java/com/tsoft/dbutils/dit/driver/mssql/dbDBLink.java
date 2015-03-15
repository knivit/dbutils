package com.tsoft.dbutils.dit.driver.mssql;

import java.sql.*;

/**
 * Linked Server
 */
class dbDBLink extends dbObject {
    private String providerName;   // 'SQLOLEDB', 'MSDAORA'
    private String dataSource;

    public dbDBLink(dbDBLinkList list, int aID, String aObjectName) throws SQLException {
        super(list, aID, aObjectName);
    }

    @Override
    public String getCategory() {
        return "dblink";
    }

    public void setProviderName(String aProviderName) {
        providerName = aProviderName;
    }

    public void setDataSource(String aDataSource) {
        dataSource = aDataSource;
    }

    public String getXML() {
        String s = "<dblink name=\"" + name + "\">\n";
        s += "<created>" + created + "</created>\n";
        s += "<script><![CDATA[>" + getSQL() + "]]></script>\n";
        s += "</dblink>\n";
        return s;
    }

    public String getSQL() {
        return "EXEC SP_ADDLINKEDSERVER @SERVER = '" + name + "', @PROVIDER = '" + providerName + "'\nGO\n";
    }

    public String getInfo() {
        String s = "/*\n";
        s += " * dblink " + name + "\n";
        s += "*/\n";
        return s;
    }
}
