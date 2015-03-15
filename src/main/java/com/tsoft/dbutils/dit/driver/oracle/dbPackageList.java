package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbPackageList extends dbObjectList<dbPackage> implements DataSetInterface {
    public void read(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT OBJECT_NAME, CREATED FROM ALL_OBJECTS " +
                "WHERE SUBOBJECT_NAME IS NULL AND STATUS = 'VALID' AND OWNER = ? AND OBJECT_TYPE = ?",
                new Object[]{database.getSchema(), "PACKAGE"}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbPackage p = new dbPackage(this, rs.getString("OBJECT_NAME"));
        p.setCreated(rs.getString("CREATED"));
        add(p);
        return true;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder("<a name=\"packages\"><strong>Package List:</strong></a><br>\n");
        buf.append("<hr>\n");

        for (int i = 0; i < usedSize(); i++) {
            dbPackage p = usedGet(i);
            buf.append("<a href=\"#package").append(p.name).append("\">").append(p.name).append("</a><br>\n");
        }
        buf.append("<hr>\n");

        for (int i = 0; i < usedSize(); i++) {
            dbPackage p = usedGet(i);
            buf.append("<a name=\"package").append(p.name).append("\">").append(StringUtils.toHTML(p.getInfo())).append("</a>\n");
            buf.append("<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">");
            buf.append(StringUtils.toHTML(p.getSQL()));
            buf.append("</font></td></tr></table>\n");
        }
        buf.append("<hr>\n");
        return buf.toString();
    }

    public String getXML() {
        StringBuilder buf = new StringBuilder("<packages>\n");
        for (int i = 0; i < usedSize(); i++) {
            dbPackage p = usedGet(i);
            buf.append(p.getXML());
        }
        buf.append("</packages>\n");
        return buf.toString();
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbPackage p = (dbPackage) usedGet(i);
            s += p.getSQL() + "\n";
        }
        return s;
    }
}
