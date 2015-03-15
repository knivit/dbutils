package com.tsoft.dbutils.dit.driver;

import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;

public abstract class dbObject {
    private dbObjectList list;

    public String name;
    public String comment;
    public String created;
    public boolean used;

    public abstract String getCategory();

    public dbObject(dbObjectList aList, String aObjectName) {
        list = aList;
        name = aObjectName;
    }

    public Database getDatabase() {
        return list.getDatabase();
    }

    public String getSchema() {
        return list.getSchema();
    }

    public dbObjectList getList() {
        return list;
    }

    public void setCreated(String aCreated) {
        if (aCreated == null) {
            created = "<unknown>";
        } else {
            created = aCreated.substring(0, aCreated.length() - 5);
        }
    }

    public String toXML(String str) {
        return StringUtils.toXML(str);
    }
}
