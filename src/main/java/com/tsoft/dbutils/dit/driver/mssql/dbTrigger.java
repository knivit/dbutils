package com.tsoft.dbutils.dit.driver.mssql;

import java.sql.*;
import java.util.*;
import com.tsoft.dbutils.lib.db.*;

class dbTrigger extends dbTextObject {
    public dbTrigger(dbTriggerList list, int aID, String aObjectName) throws SQLException {
        super(list, aID, aObjectName);
        getText();
    }

    public ArrayList getText() {
        if (getSource() == null) {
            getDatabase().readDataSet("SELECT TEXT FROM SYSCOMMENTS WHERE ID = ? ORDER BY COLID",
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

    @Override
    public String getCategory() {
        return "trigger";
    }

    public String getSQL() {
        StringBuilder s = new StringBuilder(10000);
        for (int i = 0; i < getText().size(); i++) {
            s.append((String) getText().get(i));
        }
        s.append("\nGO\n");
        return s.toString();
    }

    public String getXML() {
        String s = "<trigger name=\"" + name + "\">\n";
        s += "<created>" + created + "</created>\n";
        s += "<script><![CDATA[>" + getSQL() + "]]></script>\n";
        s += "</trigger>\n";
        return s;
    }

    public String getInfo() {
        String s = "/*\n";
        s += "* @trigger " + name + "\n";
        s += " * @created " + created + "\n";
        s += " */\n";
        return s;
    }
}
