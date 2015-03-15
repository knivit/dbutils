package com.tsoft.dbutils.lib.metadb;

public class MDDataSetField {
    private String name;
    private String type;                // tsoft.database.lib.db.Database#getColumnBaseType
    private String dataType;
    private String comment;
    private int size;
    private int scale;
    private boolean nullable;
    private boolean primary;
    private String refTableName;
    private String refColumnName;

    public void setName(String aName) {
        name = aName;
    }

    public void setComment(String aComment) {
        comment = aComment;
    }

    public void setType(String aType) {
        type = aType;
    }

    public void setSize(int aSize) {
        size = aSize;
    }

    public void setScale(int aScale) {
        scale = aScale;
    }

    public void setNullable(boolean aNullable) {
        nullable = aNullable;
    }

    public void setPrimary(boolean aPrimary) {
        primary = aPrimary;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getComment() {
        return comment;
    }

    public boolean getNullable() {
        return nullable;
    }

    public boolean getPrimary() {
        return primary;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataType() {
        return dataType;
    }

    void setForeign(String refTableName, String refColumnName) {
        this.refTableName = refTableName;
        this.refColumnName = refColumnName;
    }

    public String getRefTableName() {
        return refTableName;
    }

    public String getRefColumnName() {
        return refColumnName;
    }
}
