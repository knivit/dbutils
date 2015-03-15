package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObject;
import java.sql.*;

class dbRole extends dbObject {
    private String userName;

    public dbRole(dbRoleList list, String aObjectName) throws SQLException {
        super(list, aObjectName);
    }

    @Override
    public String getCategory() {
        return "role";
    }

    public void setUserName(String aUserName) {
        userName = aUserName;
    }

    public String getXML() {
        String s = "<role name=\"" + name + "\">\n";
        s = s + "<script><![CDATA[>" + getSQL() + "]]></script>\n";
        s = s + "</role>\n";
        return s;
    }

    public String getSQL() {
        return "GRANT " + name + " TO " + userName + "\n";
    }
}
