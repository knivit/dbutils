package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObject;
import java.sql.*;
import java.math.*;
import com.tsoft.dbutils.lib.db.*;

class dbSequence extends dbObject {
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private BigDecimal incrementBy;
    private boolean cycle;
    private BigDecimal lastNumber;
    private int cacheSize;

    public dbSequence(dbSequenceList list, String aObjectName) throws SQLException {
        super(list, aObjectName);
        getDatabase().readDataSet("SELECT MIN_VALUE, MAX_VALUE, INCREMENT_BY, CYCLE_FLAG, LAST_NUMBER, CACHE_SIZE " +
                "FROM ALL_SEQUENCES WHERE SEQUENCE_OWNER = ? AND SEQUENCE_NAME = ?",
                new Object[]{getDatabase().getSchema(), name},
                new DataSetInterface() {
                    @Override
                    public boolean onReadRecord(ResultSet rs) throws SQLException {
                        minValue = rs.getBigDecimal("MIN_VALUE");
                        maxValue = rs.getBigDecimal("MAX_VALUE");
                        incrementBy = rs.getBigDecimal("INCREMENT_BY");
                        cycle = rs.getString("CYCLE_FLAG").equals("Y");
                        lastNumber = rs.getBigDecimal("LAST_NUMBER");
                        cacheSize = rs.getInt("CACHE_SIZE");
                        return false;
                    }
                });
    }

    @Override
    public String getCategory() {
        return "sequence";
    }

    public String getXML() {
        String s = "<sequence name=\"" + name + "\">\n";
        s = s + "<created>" + created + "</created>\n";
        s = s + "<value>" + lastNumber + "</value>\n";
        s = s + "<script><![CDATA[>" + getSQL() + "]]></script>\n";
        s = s + "</sequence>\n";
        return s;
    }

    public String getSQL() {
        String s = "CREATE SEQUENCE " + name;
        s = s + " MINVALUE " + minValue + " MAXVALUE " + maxValue + " START WITH 1 INCREMENT BY " + incrementBy;
        if (cacheSize == 0) {
            s = s + " NOCACHE";
        } else {
            s = s + " CACHE " + cacheSize;
        }
        if (cycle) {
            s = s + " CYCLE";
        }
        s = s + "\n/\n";
        return s;
    }

    public String getInfo() {
        String s = "/*\n";
        s = s + " * @sequence " + name + "\n";
        s = s + " * @created " + created + "\n";
        s = s + " * @value " + lastNumber + "\n";
        s = s + "*/\n";
        return s;
    }
}
