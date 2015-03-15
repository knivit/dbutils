package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import com.tsoft.dbutils.dit.driver.dbObject;
import java.sql.*;
import com.tsoft.dbutils.lib.db.*;

class dbFieldList extends dbObjectList<dbField> implements DataSetInterface {
    private dbObject dataSet;                 // dbTable ��� dbView

    public void open(Database aDatabase, String aSchemaName, dbObject aDataSet) {
        dataSet = aDataSet;
        database = aDatabase;
        database.readDataSet("SELECT COLUMN_NAME, DATA_TYPE, DATA_LENGTH, DATA_PRECISION, DATA_SCALE, DATA_DEFAULT, NULLABLE " +
                "FROM ALL_TAB_COLUMNS WHERE OWNER = ? AND TABLE_NAME = ? ORDER BY COLUMN_ID",
                new Object[]{database.getSchema(), dataSet.name.toUpperCase()}, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        String defaultValue = rs.getString("DATA_DEFAULT");
        dbField f = new dbField(this, dataSet, rs.getString("COLUMN_NAME"));
        f.setDataType(rs.getString("DATA_TYPE"));
        f.setDataLength(rs.getInt("DATA_LENGTH"));
        f.setNullable(rs.getString("NULLABLE").equals("Y"));
        f.setDataPrecision(rs.getInt("DATA_PRECISION"));
        f.setDataScale(rs.getInt("DATA_SCALE"));
        f.setDataDefault(defaultValue);
        add(f);
        return true;
    }

    public String getXML() {
        String s = "<fields>\n";
        for (int i = 0; i < size(); i++) {
            dbField f = get(i);
            s += f.getXML();
        }
        s += "</fields>\n";
        return s;
    }

    public String getFieldList(boolean includeLOB) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            dbField f = get(i);
            if (!includeLOB && f.isLOBDataType()); else {
                if (s.length() > 0) {
                    s.append(", ");
                }
                s.append("\"").append(f.name).append("\"");
            }
        }
        return s.toString();
    }
}
