package com.tsoft.dbcompare.compare.ddl;

public class ColumnDdl {
    private String name;
    private ColumnType columnType;
    private int size;
    private int decimalDigits;
    private boolean isNullable;

    public ColumnDdl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    public String toLogString() {
        return "column {" +
                "name='" + name + '\'' +
                ", columnType=" + columnType +
                ", size=" + size +
                ", decimalDigits=" + decimalDigits +
                ", isNullable=" + isNullable +
                '}';
    }
}
