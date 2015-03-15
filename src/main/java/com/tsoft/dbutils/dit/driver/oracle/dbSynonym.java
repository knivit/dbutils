package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObject;
import java.sql.*;
import com.tsoft.dbutils.lib.db.*;

class dbSynonym extends dbObject {
    private String tableOwner;
    private String tableName;
    private String dbLink;

    public dbSynonym(dbSynonymList list, String aObjectName) throws SQLException {
        super(list, aObjectName);
        getDatabase().readDataSet("SELECT TABLE_OWNER, TABLE_NAME, DB_LINK FROM ALL_SYNONYMS " +
                "WHERE OWNER = ? AND SYNONYM_NAME = ?",
                new Object[]{getDatabase().getSchema(), name},
                new DataSetInterface() {
                    @Override
                    public boolean onReadRecord(ResultSet rs) throws SQLException {
                        tableOwner = rs.getString("TABLE_OWNER");
                        tableName = rs.getString("TABLE_NAME");
                        dbLink = rs.getString("DB_LINK");
                        return false;
                    }
                });
    }

    @Override
    public String getCategory() {
        return "synonym";
    }

    public String getXML() {
        String s = "<synonym name=\"" + name + "\">\n";
        s = s + "<created>" + created + "</created>\n";
        s = s + "<script><![CDATA[>" + getSQL() + "]]></script>\n";
        s = s + "</synonym>\n";
        return s;
    }

    public String getSQL() {
        String s = "CREATE OR REPLACE SYNONYM " + name + " FOR ";
        if (tableOwner != null) {
            s += tableOwner + ".";
        }
        if (tableName != null) {
            s += tableName;
        }
        if (dbLink != null) {
            s += dbLink;
        }
        s += ";\n";
        return s;
    }

    public String getInfo() {
        String s = "/*\n";
        s += " * @synonym " + name + "\n";
        s += " * @created " + created + "\n";
        s += "*/\n";
        return s;
    }
}
