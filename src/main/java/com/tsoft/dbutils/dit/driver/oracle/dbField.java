package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObject;
import java.sql.*;
import com.tsoft.dbutils.lib.db.*;

class dbField extends dbObject {
    private dbObject dataSet;                   // table or view
    private String dataType;
    private int dataLength;
    private int dataPrecision;
    private int dataScale;
    private String defaultValue;
    private boolean nullable;
    private boolean updatable;
    private boolean insertable;
    private boolean deletable;

    public dbField(dbFieldList list, dbObject aDataSet, String aObjectName) throws SQLException {
        super(list, aObjectName);
        dataSet = aDataSet;

        getDatabase().readDataSet("SELECT COMMENTS FROM ALL_COL_COMMENTS WHERE OWNER = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                new Object[]{getDatabase().getSchema(), dataSet.name.toUpperCase(), name.toUpperCase()},
                new DataSetInterface() {
                    @Override
                    public boolean onReadRecord(ResultSet rs) throws SQLException {
                        comment = rs.getString("COMMENTS");
                        if (comment != null) {
                            comment = comment.replaceAll("\n", " ");
                        }
                        return false;
                    }
                });

        getDatabase().readDataSet("SELECT UPDATABLE, INSERTABLE, DELETABLE FROM USER_UPDATABLE_COLUMNS WHERE TABLE_NAME = ? AND COLUMN_NAME = ?",
                new Object[]{dataSet.name.toUpperCase(), name.toUpperCase()},
                new DataSetInterface() {
                    @Override
                    public boolean onReadRecord(ResultSet rs) throws SQLException {
                        updatable = rs.getString("UPDATABLE").equals("YES");
                        insertable = rs.getString("INSERTABLE").equals("YES");
                        deletable = rs.getString("DELETABLE").equals("YES");
                        return false;
                    }
                });
    }

    @Override
    public String getCategory() {
        return "field";
    }

    public void setDataType(String aDataType) {
        dataType = aDataType;
    }

    public void setDataDefault(String aDefaultValue) {
        if (aDefaultValue != null) {
            defaultValue = aDefaultValue.trim();
        }
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

    public String getInsUpdDeletable() {
        String s = "";
        s = s + (updatable ? "+" : "-");
        s = s + (insertable ? "+" : "-");
        s = s + (deletable ? "+" : "-");
        return s;
    }

    public String getDataSize() {
        if (dataType.indexOf("CHAR") != -1 || dataType.equals("TIMESTAMP")) {
            return "(" + dataLength + ")";
        }
        if (dataType.equals("NUMBER") && (dataPrecision > 0)) {
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
        if (defaultValue != null) {
            s += "<default>" + toXML(defaultValue) + "</default>\n";
        }
        if (comment != null) {
            s += "<comment>" + toXML(comment) + "</comment>\n";
        }
        s = s + "</field>\n";
        return s;
    }

    public String getSQL(String appendix, boolean skipNotNull) {
        String s = name + " " + getDataType() + getDataSize();
        if (!nullable && skipNotNull) {
            s = s + " NOT NULL";
        }
        if (defaultValue != null) {
            s = s + " DEFAULT " + defaultValue;
        }
        s = s + appendix;
        if (comment != null) {
            s = s + " -- " + comment;
        }
        return s;
    }

    public boolean isLOBDataType() {
        return (getDataType().indexOf("LOB") != -1) || (getDataType().indexOf("LONG") != -1);
    }
}
