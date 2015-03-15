package com.tsoft.dbutils.dit.driver.mssql;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbTableList extends dbObjectList<dbTable> implements DataSetInterface {
    private dbFieldMap fieldMap;

    public dbTableList() {
        fieldMap = new dbFieldMap(this);
    }

    public void open(Database aDatabase) {
        database = aDatabase;

        database.readDataSet("SELECT ID, NAME, CRDATE FROM SYSOBJECTS WHERE XTYPE IN (?)", new Object[]{"U"}, this);

        sort();
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbTable t;
        t = new dbTable(this, rs.getInt("ID"), rs.getString("NAME"));
        t.setCreated(rs.getString("CRDATE"));
        add(t);
        return true;
    }

    public dbFieldMap getFieldMap() {
        return fieldMap;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        String s = "<a name=\"tables\"><strong>������ ������:</strong></a><br>\n";
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbTable t = (dbTable) usedGet(i);
            s += "<a href=\"#table" + t.name + "\">" + t.name + "</a>";
            if (t.getIndexList().size() > 0) {
                s = s + " (<a href=\"#tableindexes" + i + "\">�������</a>)";
            }
            s += "<br>\n";
        }
        s = s + "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbTable t = usedGet(i);
            s += "<a name=\"table" + t.name + "\">" + StringUtils.toHTML(t.getInfo()) + "</a>\n";
            s += "<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">";
            s += StringUtils.toHTML(t.getSQL());
            s += "</font></td></tr></table>\n";

            if (t.getIndexList().size() > 0) {
                s += "<a name=\"tableindexes" + i + "\"></a>\n";
                s += "<table width=\"100%\" bgcolor=\"#b5deee\"<tr><td><font face=\"Courier New\" size=\"2\">";
                for (int j = 0; j < t.getIndexList().size(); j++) {
                    dbIndex n = t.getIndexList().get(j);
                    s += StringUtils.toHTML(n.getSQL());
                }
                s += "</font></td></tr></table>\n";
            }
        }
        s += "<hr>\n";
        return s;
    }

    public String getXML() {
        String s = "<tables>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbTable t = usedGet(i);
            s += t.getXML();
        }
        s += "</tables>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbTable t = (dbTable) usedGet(i);
            s += t.getSQL() + "\n";
            s += t.getIndexList().getSQL();
        }
        return s;
    }
}
