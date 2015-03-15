package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbPackageBodyList extends dbObjectList<dbPackageBody> implements DataSetInterface {
    public void read(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT OBJECT_NAME, CREATED FROM ALL_OBJECTS " +
                "WHERE SUBOBJECT_NAME IS NULL AND STATUS = 'VALID' AND OWNER = ? AND OBJECT_TYPE = ?",
                new Object[]{database.getSchema(), "PACKAGE BODY"}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbPackageBody pb = new dbPackageBody(this, rs.getString("OBJECT_NAME"));
        pb.setCreated(rs.getString("CREATED"));
        add(pb);
        return true;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        String s = "<a name=\"packagebodies\"><strong>Package Body List:</strong></a><br>\n";
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbPackageBody pb = usedGet(i);
            s += "<a href=\"#packagebody" + pb.name + "\">" + pb.name + "</a><br>\n";
        }
        s += "<hr>\n";

        for (int i = 0; i < usedSize(); i++) {
            dbPackageBody pb = usedGet(i);
            s += "<a name=\"packagebody" + pb.name + "\">" + StringUtils.toHTML(pb.getInfo()) + "</a>\n";
            s += "<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">";
            s += StringUtils.toHTML(pb.getSQL());
            s += "</font></td></tr></table>\n";
        }
        s += "<hr>\n";
        return s;
    }

    public String getXML() {
        String s = "<packagebodies>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbPackageBody p = usedGet(i);
            s += p.getXML();
        }
        s += "</packagebodies>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbPackageBody pb = usedGet(i);
            s += pb.getSQL() + "\n";
        }
        return s;
    }
}
