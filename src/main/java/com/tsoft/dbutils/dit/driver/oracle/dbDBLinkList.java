package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbDBLinkList extends dbObjectList implements DataSetInterface {
    public void read(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT DB_LINK, USERNAME, HOST, CREATED " +
                "FROM ALL_DB_LINKS WHERE OWNER = ?",
                new Object[]{database.getSchema()}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbDBLink l = new dbDBLink(this, rs.getString("DB_LINK"));
        l.setUserName(rs.getString("USERNAME"));
        l.setHost(rs.getString("HOST"));
        l.setCreated(rs.getString("CREATED"));
        add(l);
        return true;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        String s = "<a name=\"dblinks\"><strong>Database Link List:</strong></a><br>\n";
        s = s + "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbDBLink l = (dbDBLink) usedGet(i);
            s = s + "<a href=\"#dblink" + l.name + "\">" + l.name + "</a><br>\n";
        }
        s = s + "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbDBLink l = (dbDBLink) usedGet(i);
            s = s + "<a name=\"dblink" + l.name + "\">" + StringUtils.toHTML(l.getInfo()) + "</a>\n";
            s = s + "<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">";
            s = s + StringUtils.toHTML(l.getSQL());
            s = s + "</font></td></tr></table>\n";
        }
        s = s + "<hr>\n";
        return s;
    }

    public String getXML() {
        String s = "<dblinks>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbDBLink l = (dbDBLink) usedGet(i);
            s = s + l.getXML();
        }
        s = s + "</dblinks>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbDBLink l = (dbDBLink) usedGet(i);
            s = s + l.getSQL();
        }
        return s;
    }
}
