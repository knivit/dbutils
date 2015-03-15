package com.tsoft.dbutils.dit.driver.mssql;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbViewList extends dbObjectList implements DataSetInterface {
    public void open(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT ID, NAME, CRDATE FROM SYSOBJECTS WHERE XTYPE IN (?) ORDER BY NAME",
                new Object[]{"V"}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbView v = new dbView(this, rs.getInt("ID"), rs.getString("NAME"));
        v.setCreated(rs.getString("CRDATE"));
        add(v);
        return true;
    }

    public String getXML() {
        String s = "<views>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbView v = (dbView) usedGet(i);
            s += v.getXML();
        }
        s += "</views>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbView v = (dbView) usedGet(i);
            s += v.getSQL() + "\n";
        }
        return s;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        String s = "<a name=\"views\"><strong>View List:</strong></a><br>\n";
        s = s + "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbView v = (dbView) usedGet(i);
            s = s + "<a href=\"#view" + v.name + "\">" + v.name + "</a><br>\n";
        }
        s = s + "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbView v = (dbView) usedGet(i);
            s = s + "<a name=\"view" + v.name + "\">" + StringUtils.toHTML(v.getInfo()) + "</a>\n";
            s = s + "<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">";
            s = s + StringUtils.toHTML(v.getSQL());
            s = s + "</font></td></tr></table>\n";
        }
        s = s + "<hr>\n";
        return s;
    }
}
