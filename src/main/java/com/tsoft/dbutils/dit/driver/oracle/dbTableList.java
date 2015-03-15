package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.util.*;
import java.sql.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

class dbTableList extends dbObjectList<dbTable> implements DataSetInterface {
    private dbFieldMap fieldMap;

    public dbTableList() {
        fieldMap = new dbFieldMap(this);
    }

    public void read(Database aDatabase) {
        database = aDatabase;

        database.readDataSet("SELECT OBJECT_NAME, CREATED FROM ALL_OBJECTS " +
                "WHERE SUBOBJECT_NAME IS NULL AND STATUS = 'VALID' AND OWNER = ? AND OBJECT_TYPE = ? " +
                "ORDER BY OBJECT_NAME",
                new Object[]{database.getSchema(), "TABLE"}, this);

        sort();
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbTable t;
        t = new dbTable(this, rs.getString("OBJECT_NAME"));
        t.setCreated(rs.getString("CREATED"));
        add(t);
        return true;
    }

    public dbFieldMap getFieldMap() {
        return fieldMap;
    }

    public int getLOBCount() {
        int n = 0;
        for (int i = 0; i < usedSize(); i++) {
            dbTable t = usedGet(i);
            n = n + t.getLOBList().size();
        }
        return n;
    }

    public String getHTML() {
        if (usedSize() == 0) {
            return "";
        }

        StringBuilder buf = new StringBuilder("<a name=\"tables\"><strong>Table List:</strong></a><br>\n");
        buf.append("<hr>\n");

        buf.append("<table>");
        for (int i = 0; i < usedSize(); i++) {
            dbTable t = usedGet(i);
            buf.append("<tr>");
            buf.append("<td><a href=\"#table").append(t.name).append("\">").append(t.name).append("</a></td>");
            buf.append("<td>").append((t.getIndexList().size() > 0 ? " (<a href=\"#tableindexes" + i + "\">None</a>)" : "")).append("</td>");
            buf.append("<td>").append((t.getComment() == null ? "" : t.getComment())).append("</td>");
            buf.append("</tr>\n");
        }
        buf.append("</table><hr>\n");

        for (int i = 0; i < usedSize(); i++) {
            dbTable t = usedGet(i);
            buf.append("<a name=\"table").append(t.name).append("\">").append(StringUtils.toHTML(t.getInfo())).append("</a>\n");
            buf.append("<table width=\"100%\" bgcolor=\"#b5e7ff\"<tr><td><font face=\"Courier New\" size=\"2\">");
            buf.append(StringUtils.toHTML(t.getSQL()));
            buf.append("</font></td></tr></table>\n");

            if (t.getIndexList().size() > 0) {
                buf.append("<a name=\"tableindexes").append(i).append("\"></a>\n");
                buf.append("<table width=\"100%\" bgcolor=\"#b5deee\"<tr><td><font face=\"Courier New\" size=\"2\">");
                for (int j = 0; j < t.getIndexList().size(); j++) {
                    dbIndex index = t.getIndexList().get(j);
                    buf.append(StringUtils.toHTML(index.getSQL()));
                }
                buf.append("</font></td></tr></table>\n");
            }
        }
        buf.append("<hr>\n");
        return buf.toString();
    }

    public String getXML() {
        String s = "<tables>\n";
        for (int i = 0; i < usedSize(); i++) {
            dbTable t = usedGet(i);
            if (!t.getNested()) {
                s += t.getXML();
            }
        }
        s += "</tables>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < usedSize(); i++) {
            dbTable t = usedGet(i);
            if (!t.getNested()) {
                s = s + t.getSQL() + "\n";
                s = s + t.getIndexList().getSQL();
            }
        }

        int found = 0;
        for (int i = 0; i < usedSize(); i++) {
            dbTable t = usedGet(i);
            dbConstraintList cl = t.getConstraintList();
            for (int j = 0; j < cl.size(); j++) {
                dbConstraint c = (dbConstraint) cl.get(j);
                if ((c.getConstraintType() == dbConstraint.FOREIGNKEY_CONSTRAINT) && !c.getGenerated()) {
                    s = s + "ALTER TABLE " + t.name + " ADD" + c.getSQL(true) + ";\n";
                    found++;
                }
            }
        }
        if (found != 0) {
            s = s + "\n";
        }
        return s;
    }
}
