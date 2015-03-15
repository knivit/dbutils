package com.tsoft.dbutils.dit.driver.oracle;

import java.sql.*;
import java.util.*;
import com.tsoft.dbutils.lib.db.*;

class dbFunction extends dbTextObject {
    public dbFunction(dbFunctionList list, String aObjectName) throws SQLException {
        super(list, aObjectName);
    }

    @Override
    public String getCategory() {
        return "function";
    }

    public ArrayList getText() {
        if (getSource() == null) {
            getDatabase().readDataSet("SELECT TEXT FROM ALL_SOURCE WHERE OWNER = ? AND NAME = ? AND TYPE = ? ORDER BY LINE",
                    new Object[]{getDatabase().getSchema(), name, "FUNCTION"},
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
        StringBuilder s = new StringBuilder(10000);
        for (int i = 0; i < getText().size(); i++) {
            if (i == 0) {
                s.append("CREATE OR REPLACE ");
            }
            s.append((String) getText().get(i));
        }
        s.append("\n/\n");
        return s.toString();
    }

    public String getInfo() {
        String s = "/*\n";
        s += " * @function " + name + "\n";
        s += " * @created " + created + "\n";
        s += "*/\n";
        return s;
    }
}
