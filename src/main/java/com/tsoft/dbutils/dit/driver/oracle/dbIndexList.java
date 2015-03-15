package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.db.*;

class dbIndexList extends dbObjectList<dbIndex> implements DataSetInterface {
    private dbTable table;

    public void open(Database aDatabase, String aSchemaName, dbTable aTable) {
        table = aTable;
        database = aDatabase;
        aDatabase.readDataSet("SELECT OBJECT_NAME, CREATED FROM ALL_OBJECTS O " +
                "WHERE SUBOBJECT_NAME IS NULL AND STATUS = 'VALID' AND OWNER = ? AND OBJECT_TYPE = ? AND " +
                "EXISTS(SELECT 1 FROM USER_INDEXES I WHERE O.OBJECT_NAME = I.INDEX_NAME AND I.TABLE_NAME = ?) AND " +
                "NOT EXISTS(SELECT 1 FROM USER_CONSTRAINTS C WHERE O.OBJECT_NAME = C.CONSTRAINT_NAME)",
                new Object[]{database.getSchema(), "INDEX", table.name}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbIndex n = new dbIndex(this, table.name, rs.getString("OBJECT_NAME"));
        n.setCreated(rs.getString("CREATED"));
        add(n);
        return true;
    }

    public String getXML() {
        String s = "<indexes>\n";
        s = s + "<count>" + size() + "</count>\n";
        for (int i = 0; i < size(); i++) {
            dbIndex n = get(i);
            s = s + n.getXML();
        }
        s = s + "</indexes>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < size(); i++) {
            dbIndex n = get(i);
            s = s + n.getSQL();
        }
        
        if (s.length() != 0) {
            s = s + "\n";
        }
        return s;
    }
}
