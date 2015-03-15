package com.tsoft.dbutils.dit.driver.mssql;

import java.sql.*;
import java.util.*;
import com.tsoft.dbutils.lib.db.*;

class dbFunction extends dbTextObject {
    public dbFunction(dbFunctionList list, int aID, String aObjectName) throws SQLException {
        super(list, aID, aObjectName);
    }

    @Override
    public String getCategory() {
        return "function";
    }

    public ArrayList getText() {
        if (getSource() == null) {
            getDatabase().readDataSet("SELECT ENCRYPTED, TEXT FROM SYSCOMMENTS WHERE ID = ?",
                    new Object[]{new Integer(ID)},
                    new DataSetInterface() {
                        @Override
                        public boolean onReadRecord(ResultSet rs) throws SQLException {
                            addSource(rs);
                            return true;
                        }
                    });
        }
        return getSource();
    }

    public String getXML() {
        String s = "<function name=\"" + name + "\">\n";
        s += "<created>" + created + "</created>\n";
        s += "<script><![CDATA[>" + getSQL() + "]]></script>\n";
        s += "</function>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < getText().size(); i++) {
            s += (String) getText().get(i);
        }
        s += "\nGO\n";
        return s;
    }

    public String getInfo() {
        String s = "/*\n";
        s += " * @function " + name + "\n";
        s += " * @created " + created + "\n";
        s += "*/\n";
        return s;
    }
}
