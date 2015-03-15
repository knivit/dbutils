package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbSynonymList extends dbObjectList<dbSynonym> implements DataSetInterface {
    public void read(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT OBJECT_NAME, CREATED FROM ALL_OBJECTS " +
                "WHERE SUBOBJECT_NAME IS NULL AND STATUS = 'VALID' AND OWNER = ? AND OBJECT_TYPE = ?",
                new Object[]{database.getSchema(), "SYNONYM"}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbSynonym s = new dbSynonym(this, rs.getString("OBJECT_NAME"));
        s.setCreated(rs.getString("CREATED"));
        add(s);
        return true;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        String s = "<a name=\"synonyms\"><strong>Synonym List:</strong></a><br>\n";
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbSynonym y = usedGet(i);
            s += "<a href=\"#synonym" + y.name + "\">" + y.name + "</a><br>\n";
        }
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbSynonym y = usedGet(i);
            s += "<a name=\"synonym" + y.name + "\">" + StringUtils.toHTML(y.getInfo()) + "</a>\n";
            s += "<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">";
            s += StringUtils.toHTML(y.getSQL());
            s += "</font></td></tr></table>\n";
        }
        s += "<hr>\n";
        return s;
    }

    public String getXML() {
        String s = "<synonyms>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbSynonym y = usedGet(i);
            s += y.getXML();
        }
        s += "</synonyms>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbSynonym y = usedGet(i);
            s += y.getSQL() + "\n";
        }
        return s;
    }
}
