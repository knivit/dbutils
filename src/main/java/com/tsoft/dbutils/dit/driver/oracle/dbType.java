package com.tsoft.dbutils.dit.driver.oracle;

import java.sql.*;
import java.util.*;
import com.tsoft.dbutils.lib.db.*;

class dbType extends dbTextObject {
    public dbType(dbTypeList list, String aObjectName) throws SQLException {
        super(list, aObjectName);
    }

    @Override
    public String getCategory() {
        return "type";
    }

    public ArrayList getText() {
        if (getSource() == null) {
            getDatabase().readDataSet("SELECT TEXT FROM ALL_SOURCE WHERE OWNER = ? AND NAME = ? AND TYPE = ? ORDER BY LINE",
                    new Object[]{getDatabase().getSchema(), name, "TYPE"},
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
        String s = "<type name=\"" + name + "\">\n";
        s += "<created>" + created + "</created>\n";
        s += "<script><![CDATA[>" + getSQL() + "]]></script>\n";
        s += "</type>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < getText().size(); i++) {
            if (i == 0) {
                s = s + "CREATE OR REPLACE ";
            }
            s += (String) getText().get(i);
        }
        s = s.trim() + "\n/\n";
        return s;
    }

    public String getInfo() {
        String s = "/*\n";
        s += " * @type " + name + "\n";
        s += " * @created " + created + "\n";
        s += "*/\n";
        return s;
    }
}
