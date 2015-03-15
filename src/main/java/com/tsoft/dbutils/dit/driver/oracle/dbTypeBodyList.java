package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbTypeBodyList extends dbObjectList<dbTypeBody> implements DataSetInterface {
    public void read(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT OBJECT_NAME, CREATED FROM ALL_OBJECTS " +
                "WHERE SUBOBJECT_NAME IS NULL AND STATUS = 'VALID' AND OWNER = ? AND OBJECT_TYPE = ?",
                new Object[]{database.getSchema(), "TYPE BODY"}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbTypeBody tb = new dbTypeBody(this, rs.getString("OBJECT_NAME"));
        tb.setCreated(rs.getString("CREATED"));
        add(tb);
        return true;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        String s = "<a name=\"typebodies\"><strong>Type Body List:</strong></a><br>\n";
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbTypeBody tb = usedGet(i);
            s += "<a href=\"#typebody" + tb.name + "\">" + tb.name + "</a><br>\n";
        }
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbTypeBody tb = usedGet(i);
            s += "<a name=\"typebody" + tb.name + "\">" + StringUtils.toHTML(tb.getInfo()) + "</a>\n";
            s += "<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">";
            s += StringUtils.toHTML(tb.getSQL());
            s += "</font></td></tr></table>\n";
        }
        s += "<hr>\n";
        return s;
    }

    public String getXML() {
        String s = "<typebodies>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbTypeBody tb = usedGet(i);
            s += tb.getXML();
        }
        s += "</typebodies>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbTypeBody tb = usedGet(i);
            s += tb.getSQL() + "\n";
        }
        return s;
    }
}
