package com.tsoft.dbutils.dit.driver.mssql;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import java.util.*;

abstract class dbTextObject extends dbObject {
    private ArrayList source;
    private boolean encrypted;

    public dbTextObject(dbObjectList aList, int aID, String aObjectName) {
        super(aList, aID, aObjectName);
    }

    protected void addSource(ResultSet rs) throws SQLException {
        if (source == null) {
            source = new ArrayList();
        }
        source.add(rs.getString("TEXT"));
    }

    protected ArrayList getSource() {
        return source;
    }
}
