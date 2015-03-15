package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.db.*;

class dbLOBList extends dbObjectList<dbLOB> implements DataSetInterface {
    private dbTable table;

    public void open(Database aDatabase, String aSchemaName, dbTable aTable) {
        table = aTable;
        database = aDatabase;
        database.readDataSet("SELECT COLUMN_NAME FROM ALL_LOBS WHERE OWNER = ? AND TABLE_NAME = ?",
                new Object[]{database.getSchema(), table.name.toUpperCase()}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbLOB l = new dbLOB(this, table.name, rs.getString("COLUMN_NAME"));
        add(l);
        return true;
    }

    public String getHTML() {
        String s = "";
        for (int i = 0; i < size(); i++) {
            dbLOB l = (dbLOB) get(i);
            s = s + "<a href=\"#table" + table.name + "\">" + table.name + " : " + l.name + "</a><br>\n";
        }
        return s;
    }
}
