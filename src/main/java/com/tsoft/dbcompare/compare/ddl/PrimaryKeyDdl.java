package com.tsoft.dbcompare.compare.ddl;

public class PrimaryKeyDdl {
    private String columnName;
    private int keySeq;
    private String pkName;

    public PrimaryKeyDdl(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getKeySeq() {
        return keySeq;
    }

    public void setKeySeq(int keySeq) {
        this.keySeq = keySeq;
    }

    public String getPkName() {
        return pkName;
    }

    public void setPkName(String pkName) {
        this.pkName = pkName;
    }

    public String toLogString() {
        return "primary key {" +
                keySeq +
                ", columnName='" + columnName + '\'' +
                ", pkName='" + pkName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimaryKeyDdl that = (PrimaryKeyDdl) o;

        if (keySeq != that.keySeq) return false;
        if (columnName != null ? !columnName.equalsIgnoreCase(that.columnName) : that.columnName != null) return false;
        if (pkName != null ? !pkName.equalsIgnoreCase(that.pkName) : that.pkName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = columnName != null ? columnName.hashCode() : 0;
        result = 31 * result + keySeq;
        result = 31 * result + (pkName != null ? pkName.hashCode() : 0);
        return result;
    }
}
