package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbSequenceList extends dbObjectList<dbSequence> implements DataSetInterface {
    public void read(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT OBJECT_NAME, CREATED FROM ALL_OBJECTS " +
                "WHERE SUBOBJECT_NAME IS NULL AND STATUS = 'VALID' AND OWNER = ? AND OBJECT_TYPE = ?",
                new Object[]{database.getSchema(), "SEQUENCE"}, this);
        sort();
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbSequence s = new dbSequence(this, rs.getString("OBJECT_NAME"));
        s.setCreated(rs.getString("CREATED"));
        add(s);
        return true;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        String s = "<a name=\"sequences\"><strong>Sequence List:</strong></a><br>\n";
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbSequence e = usedGet(i);
            s += "<a href=\"#sequence" + e.name + "\">" + e.name + "</a><br>\n";
        }
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbSequence e = usedGet(i);
            s += "<a name=\"sequence" + e.name + "\">" + StringUtils.toHTML(e.getInfo()) + "</a>\n";
            s += "<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">";
            s += StringUtils.toHTML(e.getSQL());
            s += "</font></td></tr></table>\n";
        }
        s += "<hr>\n";
        return s;
    }

    public String getXML() {
        String s = "<sequences>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbSequence e = usedGet(i);
            s += e.getXML();
        }
        s += "</sequences>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbSequence e = usedGet(i);
            s += e.getSQL() + "\n";
        }
        return s;
    }
}
