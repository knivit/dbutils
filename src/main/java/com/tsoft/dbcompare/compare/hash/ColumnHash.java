package com.tsoft.dbcompare.compare.hash;

import com.tsoft.dbcompare.compare.ddl.ColumnType;

public class ColumnHash {
    private String columnName;
    private HashType hashType;
    private ColumnType columnType;

    private String queryColumnName;

    private Object value;

    public ColumnHash(String columnName, HashType hashType, int columnIndex) {
        this.columnName = hashType.name()  + '(' + columnName + ')';
        this.hashType = hashType;

        queryColumnName = columnName + '_' + hashType.name();
        if (queryColumnName.length() > 30) {
            queryColumnName = "F" + Integer.toString(columnIndex) + "_" + Integer.toString(hashType.ordinal()) + "_" + queryColumnName;
            queryColumnName = queryColumnName.substring(0, 30);
        }
    }

    public String getColumnName() {
        return columnName;
    }

    public HashType getHashType() {
        return hashType;
    }

    public String getQueryColumnName() {
        return queryColumnName;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String toLogString() {
        StringBuilder buf = new StringBuilder(queryColumnName);
        if (value != null) {
            buf.append("{value=").append(value.toString()).append('}');
        }
        return buf.toString();
    }
    
    public String toString() {
        return toLogString();
    }
}
