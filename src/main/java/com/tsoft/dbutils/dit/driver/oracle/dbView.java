package com.tsoft.dbutils.dit.driver.oracle;

import java.sql.*;
import java.util.*;
import com.tsoft.dbutils.lib.db.*;

class dbView extends dbTextObject {
    private dbFieldList fieldList;

    public dbView(dbViewList list, String aObjectName) throws SQLException {
        super(list, aObjectName);
    }

    @Override
    public String getCategory() {
        return "view";
    }

    public ArrayList getText() {
        if (getSource() == null) {
            getDatabase().readDataSet("SELECT TEXT FROM ALL_VIEWS WHERE OWNER = ? AND VIEW_NAME = ?",
                    new Object[]{getDatabase().getSchema(), name},
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

    private dbFieldList getFieldList() {
        if (fieldList == null) {
            fieldList = new dbFieldList();
            fieldList.open(getDatabase(), getSchema(), this);
        }
        return fieldList;
    }

    public String getXML() {
        String s = "<view name=\"" + name + "\">\n";
        s += "<created>" + created + "</created>\n";
        s += getFieldList().getXML();
        s += "<script><![CDATA[>" + getSQL() + "]]></script>\n";
        s += "</view>\n";
        return s;
    }

    public String getSQL() {
        StringBuilder s = new StringBuilder(10000);
        for (int i = 0; i < getText().size(); i++) {
            if (i == 0) {
                s.append("CREATE OR REPLACE VIEW ").append(name).append(" AS\n");
            }
            s.append((String) getText().get(i));
        }
        s.append("\n/\n");
        return s.toString();
    }

    public String getInfo() {
        String s = "/*\n";
        s += " * @view " + name + "\n";
        s += " * @created " + created + "\n";

        s += " * UID Field Name\n";
        for (int i = 0; i < getFieldList().size(); i++) {
            dbField f = (dbField) getFieldList().get(i);
            s += " * " + f.getInsUpdDeletable() + " " + f.getSQL("", true) + "\n";
        }
        s += "*/\n";
        return s;
    }
}
