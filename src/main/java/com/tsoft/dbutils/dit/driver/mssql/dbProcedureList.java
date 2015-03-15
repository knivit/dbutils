package com.tsoft.dbutils.dit.driver.mssql;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbProcedureList extends dbObjectList<dbProcedure> implements DataSetInterface {
    public void open(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT ID, NAME, CRDATE FROM SYSOBJECTS WHERE TYPE IN (?) ORDER BY NAME", new Object[]{"P"}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbProcedure p = new dbProcedure(this, rs.getInt("ID"), rs.getString("NAME"));
        p.setCreated(rs.getString("CRDATE"));
        add(p);
        return true;
    }

    public String getXML() {
        String s = "<procedures>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbProcedure p = (dbProcedure) usedGet(i);
            s += p.getXML();
        }
        s += "</procedures>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbProcedure p = (dbProcedure) usedGet(i);
            s += p.getSQL() + "\n";
        }
        return s;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        String s = "<a name=\"procedures\"><strong>������ �������� ��������:</strong></a><br>\n";
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbProcedure p = usedGet(i);
            s += "<a href=\"#procedure" + p.name + "\">" + p.name + "</a><br>\n";
        }
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbProcedure p = usedGet(i);
            s += "<a name=\"procedure" + p.name + "\">" + StringUtils.toHTML(p.getInfo()) + "</a>\n";
            s += "<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">";
            s += StringUtils.toHTML(p.getSQL());
            s += "</font></td></tr></table>\n";
        }
        s += "<hr>\n";
        return s;
    }
}
