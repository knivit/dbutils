package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import java.util.*;
import com.tsoft.dbutils.lib.db.*;

class dbConstraintList extends dbObjectList implements DataSetInterface {
    protected dbTable table;

    public void open(Database aDatabase, dbTable aTable) {
        table = aTable;
        database = aDatabase;
        aDatabase.readDataSet("SELECT CONSTRAINT_NAME, CONSTRAINT_TYPE, SEARCH_CONDITION, R_CONSTRAINT_NAME, DELETE_RULE, " +
                "STATUS, DEFERRABLE, DEFERRED, GENERATED FROM ALL_CONSTRAINTS WHERE OWNER = ? AND TABLE_NAME = ?",
                new Object[]{database.getSchema(), table.name.toUpperCase()}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        String searchCondition = rs.getString("SEARCH_CONDITION");
        dbConstraint c = new dbConstraint(this, table.name, rs.getString("CONSTRAINT_NAME"));
        c.setConstraintType(rs.getString("CONSTRAINT_TYPE"));
        c.setSearchCondition(searchCondition);
        c.setRConstraintName(rs.getString("R_CONSTRAINT_NAME"));
        c.setDeleteRule(rs.getString("DELETE_RULE"));
        c.setStatus(rs.getString("STATUS"));
        c.setDeferrable(rs.getString("DEFERRABLE"));
        c.setDeferred(rs.getString("DEFERRED"));
        c.setGenerated(!rs.getString("GENERATED").equals("USER NAME"));
        add(c);
        return true;
    }

    public ArrayList findByFieldName(String fieldName) {
        ArrayList ret = new ArrayList();
        for (int i = 0; i < size(); i++) {
            dbConstraint c = (dbConstraint) get(i);
            if (c.getColumnName().equals(fieldName)) {
                ret.add(c);
            }
        }
        return ret;
    }

    public String getXML() {
        String s = "<constraints>\n";
        s = s + "<count>" + size() + "</count>\n";
        for (int i = 0; i < size(); i++) {
            dbConstraint c = (dbConstraint) get(i);
            s = s + c.getXML();
        }
        s = s + "</constraints>\n";
        return s;
    }
}
