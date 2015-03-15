package com.tsoft.dbutils.dit.driver.mssql;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbTriggerList extends dbObjectList<dbTrigger> implements DataSetInterface {
    public void open(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT ID, NAME, CRDATE FROM SYSOBJECTS WHERE XTYPE IN (?) ORDER BY NAME", new Object[]{"TR"}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbTrigger t = new dbTrigger(this, rs.getInt("ID"), rs.getString("NAME"));
        t.setCreated(rs.getString("CRDATE"));
        add(t);
        return true;
    }

    public String getXML() {
        String s = "<triggers>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbTrigger t = (dbTrigger) usedGet(i);
            s += t.getXML();
        }
        s += "</triggers>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbTrigger t = (dbTrigger) usedGet(i);
            s += t.getSQL() + "\n";
        }
        return s;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        String s = "<a name=\"triggers\"><strong>Trigger List:</strong></a><br>\n";
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbTrigger t = usedGet(i);
            s += "<a href=\"#trigger" + t.name + "\">" + t.name + "</a><br>\n";
        }
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbTrigger t = usedGet(i);
            s += "<a name=\"trigger" + t.name + "\">" + StringUtils.toHTML(t.getInfo()) + "</a>\n";
            s += "<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">";
            s += StringUtils.toHTML(t.getSQL());
            s += "</font></td></tr></table>\n";
        }
        s += "<hr>\n";
        return s;
    }
}
