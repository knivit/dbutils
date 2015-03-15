package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbViewList extends dbObjectList<dbView> implements DataSetInterface {
    public void read(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT OBJECT_NAME, CREATED FROM ALL_OBJECTS " +
                "WHERE SUBOBJECT_NAME IS NULL AND STATUS = 'VALID' AND OWNER = ? AND OBJECT_TYPE = ? " +
                "ORDER BY OBJECT_NAME",
                new Object[]{database.getSchema(), "VIEW"}, this);
        sort();
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbView v = new dbView(this, rs.getString("OBJECT_NAME"));
        v.setCreated(rs.getString("CREATED"));
        add(v);
        return true;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        String s = "<a name=\"views\"><strong>View List:</strong></a><br>\n";
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbView v = usedGet(i);
            s += "<a href=\"#view" + v.name + "\">" + v.name + "</a><br>\n";
        }
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbView v = usedGet(i);
            s += "<a name=\"view" + v.name + "\">" + StringUtils.toHTML(v.getInfo()) + "</a>\n";
            s += "<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">";
            s += StringUtils.toHTML(v.getSQL());
            s += "</font></td></tr></table>\n";
        }
        s += "<hr>\n";
        return s;
    }

    public String getXML() {
        String s = "<views>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbView v = usedGet(i);
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
}
