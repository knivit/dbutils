package com.tsoft.dbutils.dit.driver.mssql;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.db.*;

class dbFieldList extends dbObjectList<dbField> {
    private dbObject dataSet;                  // dbTable or dbView

    public void open(Database aDatabase, dbObject aDataSet) {
        dataSet = aDataSet;
        database = aDatabase;

        if (dataSet instanceof dbTable) {
            database.readDataSet("SELECT SC.COLID, SC.NAME, ST.NAME AS DATA_TYPE, SC.LENGTH, SC.PREC, SC.SCALE, " +
                    "SC.ISNULLABLE, (SC.STATUS & 0x80) AS IDENT " +
                    "FROM SYSCOLUMNS SC JOIN SYSTYPES ST ON SC.XTYPE=ST.XTYPE WHERE SC.ID = ? ORDER BY COLORDER",
                    new Object[]{new Integer(dataSet.ID)}, new dbTableFieldList(this));
        } else {
            database.readDataSet("SELECT TOP 1 * FROM " + dataSet.name, null, new dbViewFieldList(this), 1);
        }
    }

    class dbTableFieldList implements DataSetInterface {
        dbFieldList list;

        public dbTableFieldList(dbFieldList aList) {
            list = aList;
        }

        @Override
        public boolean onReadRecord(ResultSet rs) throws SQLException {
            dbField f = new dbField(list, dataSet, rs.getInt("COLID"), rs.getString("NAME"));
            f.setDataType(rs.getString("DATA_TYPE"));
            f.setDataLength(rs.getInt("LENGTH"));
            f.setNullable(rs.getInt("ISNULLABLE") == 1);
            f.setDataPrecision(rs.getInt("PREC"));
            f.setDataScale(rs.getInt("SCALE"));
            f.setIdentity(rs.getInt("IDENT") != 0);
            add(f);
            return true;
        }
    }

    class dbViewFieldList implements DataSetInterface {
        dbFieldList list;

        public dbViewFieldList(dbFieldList aList) {
            list = aList;
        }

        @Override
        public boolean onReadRecord(ResultSet rs) throws SQLException {
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                dbField f = new dbField(list, dataSet, i, metaData.getColumnName(i));
                f.setDataType(metaData.getColumnTypeName(i));
                f.setDataLength(metaData.getColumnDisplaySize(i));
                f.setNullable(metaData.isNullable(i) == ResultSetMetaData.columnNullable);
                f.setDataPrecision(metaData.getPrecision(i));
                f.setDataScale(metaData.getScale(i));
                f.setIdentity(metaData.isAutoIncrement(i));
                add(f);
            }
            return false;
        }
    }

    public String getXML() {
        String s = "<fields>\n";
        for (int i = 0; i < size(); i++) {
            dbField f = (dbField) get(i);
            s += f.getXML();
        }
        s += "</fields>\n";
        return s;
    }

    public String getFieldList(boolean includeLOB) {
        String s = "";
        for (int i = 0; i < size(); i++) {
            dbField f = (dbField) get(i);
            if (!includeLOB && f.isLOBDataType()); else {
                if (s.length() > 0) {
                    s += ", ";
                }
                s += "\"" + f.name + "\"";
            }
        }
        return s;
    }
}
