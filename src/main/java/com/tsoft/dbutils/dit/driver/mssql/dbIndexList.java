package com.tsoft.dbutils.dit.driver.mssql;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.db.*;

class dbIndexList extends dbObjectList<dbIndex> implements DataSetInterface {
    private dbTable table;

    public void open(Database aDatabase, dbTable aTable) {
        table = aTable;
        database = aDatabase;
        aDatabase.readDataSet("EXEC SP_HELPINDEX ?", new Object[]{table.name}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbIndex n = new dbIndex(this, table.name, 0, rs.getString("INDEX_NAME"));
        n.setColumnNameList(rs.getString("INDEX_KEYS"));
        n.setClustered(rs.getString("INDEX_DESCRIPTION").indexOf("nonclustered") == -1);
        n.setPrimaryKey(rs.getString("INDEX_DESCRIPTION").indexOf("primary key") != -1);
        n.setUnique(rs.getString("INDEX_DESCRIPTION").indexOf("unique") != -1);
        add(n);
        return true;
    }

    public String getXML() {
        String s = "<indexes>\n";
        for (int i = 0; i < size(); i++) {
            dbIndex n = (dbIndex) get(i);
            s += n.getXML();
        }
        s += "</indexes>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        for (int i = 0; i < size(); i++) {
            dbIndex n = (dbIndex) get(i);
            s += n.getSQL();
        }
        if (s.length() != 0) {
            s += "\n";
        }
        return s;
    }
}
