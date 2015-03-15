package com.tsoft.dbutils.dit.driver.oracle;

import java.sql.*;
import java.util.*;
import com.tsoft.dbutils.lib.db.*;

class dbTrigger extends dbTextObject {
    public dbTrigger(dbTriggerList list, String aObjectName) throws SQLException {
        super(list, aObjectName);
    }

    @Override
    public String getCategory() {
        return "trigger";
    }

    public ArrayList getText() {
        if (getSource() == null) {
            getDatabase().readDataSet("SELECT TEXT FROM ALL_SOURCE WHERE OWNER = ? AND NAME = ? AND TYPE = ? ORDER BY LINE",
                    new Object[]{getDatabase().getSchema(), name, "TRIGGER"},
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
        String s = "<trigger name=\"" + name + "\">\n";
        s += "<created>" + created + "</created>\n";
        s += "<script><![CDATA[>" + getSQL() + "]]></script>\n";
        s += "</trigger>\n";
        return s;
    }

    public String getSQL() {
        StringBuilder s = new StringBuilder(10000);
        for (int i = 0; i < getText().size(); i++) {
            if (i == 0) {
                s.append("CREATE ");
            }
            s.append((String) getText().get(i));
        }
        s.append("\n/\n");
        return s.toString();
    }

    public String getInfo() {
        String s = "/*\n";
        s += " * @trigger " + name + "\n";
        s += " * @created " + created + "\n";
        s += " */\n";
        return s;
    }
}
