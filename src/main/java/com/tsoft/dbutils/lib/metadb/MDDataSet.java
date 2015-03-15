package com.tsoft.dbutils.lib.metadb;

import java.util.*;

public class MDDataSet {
    public static final int TABLE = 1;
    public static final int VIEW = 2;
    private int type;                       // TABLE..VIEW
    private String name;
    private String comment;
    private ArrayList fieldList = new ArrayList();

    public void setType(int aType) {
        type = aType;
    }

    public void setName(String aName) {
        name = aName;
    }

    public void setComment(String aComment) {
        comment = aComment;
    }

    public ArrayList getFieldList() {
        return fieldList;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public int getType() {
        return type;
    }

    public MDDataSetField findField(String fieldName) {
        for (int i = 0; i < fieldList.size(); i++) {
            MDDataSetField result = (MDDataSetField) fieldList.get(i);
            if (result.getName().equalsIgnoreCase(fieldName)) {
                return result;
            }
        }
        return null;
    }
}
