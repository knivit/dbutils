package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import com.tsoft.dbutils.dit.driver.dbObject;
import java.util.*;
import java.sql.*;

abstract class dbTextObject extends dbObject {
    protected ArrayList<String> source;

    public dbTextObject(dbObjectList aList, String aObjectName) {
        super(aList, aObjectName);
    }

    protected boolean addSource(ResultSet rs) throws SQLException {
        if (source == null) {
            source = new ArrayList<String>();
        }
        String s = rs.getString("TEXT");
        if (rs.isFirst() && s.indexOf("wrapped") != -1) {
            source.add("-- " + s.substring(0, s.indexOf("wrapped") - 1));
            return false;
        } else {
            source.add(s);
            return true;
        }
    }

    protected ArrayList<String> getSource() {
        return source;
    }
}
