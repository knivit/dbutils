package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObject;
import java.sql.*;

class dbLOB extends dbObject {
    private String tableName;

    public dbLOB(dbLOBList list, String aTableName, String aObjectName) throws SQLException {
        super(list, aObjectName);
        tableName = aTableName;
    }

    @Override
    public String getCategory() {
        return "LOB";
    }
} 
