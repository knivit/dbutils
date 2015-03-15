package com.tsoft.dbutils.dit.driver.mssql;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import java.util.ArrayList;
import com.tsoft.dbutils.lib.db.*;

class dbConstraintList extends dbObjectList<dbConstraint> implements DataSetInterface {
    private dbTable table;

    public void open(Database aDatabase, dbTable aTable) {
        table = aTable;
        database = aDatabase;
        aDatabase.readDataSet("SELECT ID, NAME, XTYPE FROM SYSOBJECTS WHERE PARENT_OBJ = ? AND XTYPE IN ('C', 'PK', 'F', 'UQ')",
                new Object[] {new Integer(table.ID)}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbConstraint c = new dbConstraint(this, table, rs.getInt("ID"), rs.getString("NAME"));
        c.setConstraintType(rs.getString("XTYPE"));
        add(c);
        return true;
    }

    public ArrayList findByFieldName(String fieldName) {
        ArrayList ret = new ArrayList();
        for (int i = 0; i < size(); i++) {
            dbConstraint c = (dbConstraint) get(i);
            if (c.getColumnName() == null) {
                continue;
            }
            if (c.getColumnName().equals(fieldName)) {
                ret.add(c);
            }
        }
        return ret;
    }

    public String getXML() {
        String s = "<constraints>\n";
        for (int i = 0; i < size(); i++) {
            dbConstraint c = (dbConstraint) get(i);
            s += c.getXML();
        }
        s += "</constraints>\n";
        return s;
    }
}
