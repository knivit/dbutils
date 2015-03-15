package com.tsoft.dbutils.dit.driver.mssql;

import java.sql.*;

class dbType extends dbObject {
    private String baseTypeName;
    private boolean allowNulls;

    public dbType(dbTypeList list, int aID, String aObjectName) throws SQLException {
        super(list, aID, aObjectName);
    }

    @Override
    public String getCategory() {
        return "type";
    }

    public String getXML() {
        String s = "<type name=\"" + name + "\">\n";
        s = s + "<script><![CDATA[>" + getSQL() + "]]></script>\n";
        s = s + "</type>\n";
        return s;
    }

    public String getSQL() {
        String s = "EXEC SP_ADDTYPE " + name + ", ";
        s += "'" + baseTypeName + "', ";
        s += (allowNulls ? "'NULL'" : "'NOT NULL'");
        s += "\nGO\n";
        return s;
    }

    public String getInfo() {
        String s = "/*\n";
        s += " * @type " + name + "\n";
        s += "*/\n";
        return s;
    }

    void setBaseTypeName(String aBaseTypeName) {
        baseTypeName = aBaseTypeName;
    }

    void setAllowNulls(boolean aAllowNulls) {
        allowNulls = aAllowNulls;
    }
}
