package com.tsoft.dbutils.dit.driver.mssql;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbFunctionList extends dbObjectList<dbFunction> implements DataSetInterface {
    public void open(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT ID, NAME, CRDATE FROM SYSOBJECTS WHERE TYPE IN ('FN', 'IF', 'TF') ORDER BY NAME", null, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbFunction f = new dbFunction(this, rs.getInt("ID"), rs.getString("NAME"));
        f.setCreated(rs.getString("CRDATE"));
        add(f);
        return true;
    }

    public String getXML() {
        String s = "<functions>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbFunction f = (dbFunction) usedGet(i);
            s += f.getXML();
        }
        s += "</functions>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbFunction f = (dbFunction) usedGet(i);
            s += f.getSQL() + "\n";
        }
        return s;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        String s = "<a name=\"functions\"><strong>Function List:</strong></a><br>\n";
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbFunction f = usedGet(i);
            s += "<a href=\"#function" + f.name + "\">" + f.name + "</a><br>\n";
        }
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbFunction f = usedGet(i);
            s += "<a name=\"function" + f.name + "\">" + StringUtils.toHTML(f.getInfo()) + "</a>\n";
            s += "<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">";
            s += StringUtils.toHTML(f.getSQL());
            s += "</font></td></tr></table>\n";
        }
        s += "<hr>\n";
        return s;
    }
}
