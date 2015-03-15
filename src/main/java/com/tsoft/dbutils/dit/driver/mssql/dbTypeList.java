package com.tsoft.dbutils.dit.driver.mssql;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbTypeList extends dbObjectList<dbType> implements DataSetInterface {
    public void open(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT XTYPE, NAME, ALLOWNULLS, " +
                "(SELECT NAME FROM SYSTYPES WHERE XTYPE = ST.XTYPE AND XUSERTYPE < 256) AS BASETYPE_NAME " +
                "FROM SYSTYPES ST WHERE XUSERTYPE = 256 ORDER BY NAME", null, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbType t = new dbType(this, rs.getInt("XTYPE"), rs.getString("NAME"));
        t.setBaseTypeName(rs.getString("BASETYPE_NAME"));
        t.setAllowNulls(rs.getInt("ALLOWNULLS") == 1);
        add(t);
        return true;
    }

    public String getXML() {
        String s = "<types>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbType t = (dbType) usedGet(i);
            s += t.getXML();
        }
        s += "</types>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbType t = (dbType) usedGet(i);
            s += t.getInfo() + t.getSQL() + "\n";
        }
        return s;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        String s = "<a name=\"types\"><strong>Type List:</strong></a><br>\n";
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbType t = usedGet(i);
            s += "<a href=\"#type" + t.name + "\">" + t.name + "</a><br>\n";
        }
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbType t = usedGet(i);
            s += "<a name=\"type" + t.name + "\">" + StringUtils.toHTML(t.getInfo()) + "</a>\n";
            s += "<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">";
            s += StringUtils.toHTML(t.getSQL());
            s += "</font></td></tr></table>\n";
        }
        s += "<hr>\n";
        return s;
    }
}
