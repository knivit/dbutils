package com.tsoft.dbutils.dit.driver.mssql;

import java.sql.*;
import com.tsoft.dbutils.lib.db.*;

class dbField extends dbObject {
    private dbObject dataSet;
    private String dataType;
    private int dataLength;
    private int dataPrecision;
    private int dataScale;
    private String defaultValue;
    private boolean nullable;
    private boolean identity;

    public dbField(dbFieldList list, dbObject aDataSet, int aID, String aObjectName) throws SQLException {
        super(list, aID, aObjectName);
        dataSet = aDataSet;

        getDatabase().readDataSet("SELECT TEXT FROM SYSCOMMENTS WHERE ID = (SELECT CDEFAULT FROM SYSCOLUMNS WHERE Id = ? AND NAME = ?)",
                new Object[]{new Integer(dataSet.ID), name},
                new DataSetInterface() {
                    @Override
                    public boolean onReadRecord(ResultSet rs) throws SQLException {
                        defaultValue = rs.getString("TEXT");
                        return false;
                    }
                });
    }

    @Override
    public String getCategory() {
        return "field";
    }
    
    public void setDataType(String aDataType) {
        dataType = aDataType.toUpperCase();
    }

    public void setIdentity(boolean aIdentity) {
        identity = aIdentity;
    }

    public void setDataLength(int aDataLength) {
        dataLength = aDataLength;
    }

    public void setDataPrecision(int aDataPrecision) {
        dataPrecision = aDataPrecision;
    }

    public void setDataScale(int aDataScale) {
        dataScale = aDataScale;
    }

    public void setNullable(boolean aNullable) {
        nullable = aNullable;
    }

    public dbObject getDataSet() {
        return dataSet;
    }

    private String getDataSize() {
        if (dataType.indexOf("CHAR") != -1 || dataType.equals("BINARY")) {
            return "(" + dataLength + ")";
        }
        if ((dataType.equals("NUMBER") || dataType.equals("DECIMAL")) && (dataPrecision > 0)) {
            if (dataScale > 0) {
                return "(" + dataPrecision + ", " + dataScale + ")";
            } else {
                return "(" + dataPrecision + ")";
            }
        }
        return "";
    }

    public String getDataType() {
        return (dataType == null ? "" : dataType);
    }

    public String getXML() {
        String s = "<field name=\"" + name + "\">\n";
        s += "<type>" + getDatabase().getColumnBaseType(getDataType()) + "</type>\n";
        s += "<size>" + (dataPrecision == 0 ? dataLength : dataPrecision) + "</size>\n";
        s += "<scale>" + dataScale + "</scale>\n";
        s += "<datatype>" + getDataType() + getDataSize() + "</datatype>\n";
        s += "<nullable>" + nullable + "</nullable>\n";
        s += "<identity>" + identity + "</identity>\n";
        if (defaultValue != null) {
            s += "<default>" + defaultValue + "</default>\n";
        }
        s += "</field>\n";
        return s;
    }

    public String getSQL(String appendix) {
        String s = name + " " + getDataType() + getDataSize();
        if (identity) {
            s += " IDENTITY";
        }
        if (!nullable) {
            s += " NOT NULL";
        }
        if (defaultValue != null) {
            s += " DEFAULT " + defaultValue;
        }
        s += appendix;
        return s;
    }

    public boolean isLOBDataType() {
        return (getDataType().indexOf("TEXT") != -1) || (getDataType().equals("IMAGE"));
    }
}
