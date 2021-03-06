package com.tsoft.dbutils.dit.driver.mssql;

import java.sql.*;
import java.util.*;
import com.tsoft.dbutils.lib.db.*;

class dbView extends dbTextObject {
    private dbFieldList fieldList;

    public dbView(dbViewList list, int aID, String aObjectName) throws SQLException {
        super(list, aID, aObjectName);
    }

    @Override
    public String getCategory() {
        return "view";
    }

    public ArrayList getText() {
        if (getSource() == null) {
            getDatabase().readDataSet("SELECT TEXT FROM SYSCOMMENTS WHERE ID = ?",
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

    private dbFieldList getFieldList() {
        if (fieldList == null) {
            fieldList = new dbFieldList();
            fieldList.open(getDatabase(), this);
        }
        return fieldList;
    }

    public String getSQL() {
        StringBuffer s = new StringBuffer(10000);
        for (int i = 0; i < getText().size(); i++) {
            s.append((String) getText().get(i));
        }
        s.append("\nGO\n");
        return s.toString();
    }

    public String getXML() {
        String s = "<view name=\"" + name + "\">\n";
        s += "<created>" + created + "</created>\n";
        s += getFieldList().getXML();
        s += "<script><![CDATA[>" + getSQL() + "]]></script>\n";
        s += "</view>\n";
        return s;
    }

    public String getInfo() {
        String s = "/*\n";
        s += " * @view " + name + "\n";
        s += " * @created " + created + "\n";
        return s;
    }
}
