package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbTriggerList extends dbObjectList<dbTrigger> implements DataSetInterface {
    public void read(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT OBJECT_NAME, CREATED FROM ALL_OBJECTS " +
                "WHERE SUBOBJECT_NAME IS NULL AND STATUS = 'VALID' AND OWNER = ? AND OBJECT_TYPE = ? " +
                "ORDER BY OBJECT_NAME",
                new Object[]{database.getSchema(), "TRIGGER"}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbTrigger t = new dbTrigger(this, rs.getString("OBJECT_NAME"));
        t.setCreated(rs.getString("CREATED"));
        add(t);
        return true;
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

    public String getXML() {
        String s = "<triggers>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbTrigger t = usedGet(i);
            s += t.getXML();
        }
        s += "</triggers>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbTrigger t = usedGet(i);
            s += t.getSQL() + "\n";
        }
        return s;
    }
}
