package com.tsoft.dbutils.dit.driver.mssql;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbDBLinkList extends dbObjectList<dbDBLink> implements DataSetInterface {
    public void open(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT SRVID, SRVNAME, PROVIDERNAME, DATASOURCE, SCHEMADATE FROM master..SYSSERVERS WHERE ISREMOTE = 1", null, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbDBLink l = new dbDBLink(this, rs.getInt("SRVID"), rs.getString("SRVNAME"));
        l.setProviderName(rs.getString("PROVIDERNAME"));
        l.setDataSource(rs.getString("DATASOURCE"));
        l.setCreated(rs.getString("SCHEMADATE"));
        add(l);
        return true;
    }

    public String getXML() {
        String s = "<dblinks>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbDBLink l = (dbDBLink) usedGet(i);
            s += l.getXML();
        }
        s += "</dblinks>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbDBLink l = (dbDBLink) usedGet(i);
            s += l.getSQL();
        }
        return s;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        String s = "<a name=\"dblinks\"><strong>Linked Server List:</strong></a><br>\n";
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbDBLink l = usedGet(i);
            s += "<a href=\"#dblink" + l.name + "\">" + l.name + "</a><br>\n";
        }
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbDBLink l = usedGet(i);
            s += "<a name=\"dblink" + l.name + "\">" + StringUtils.toHTML(l.getInfo()) + "</a>\n";
            s += "<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">";
            s += StringUtils.toHTML(l.getSQL());
            s += "</font></td></tr></table>\n";
        }
        s += "<hr>\n";
        return s;
    }
}
