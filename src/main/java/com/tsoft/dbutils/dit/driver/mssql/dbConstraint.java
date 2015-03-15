package com.tsoft.dbutils.dit.driver.mssql;

import java.sql.*;
import java.util.*;
import com.tsoft.dbutils.lib.db.*;

class dbConstraint extends dbObject {
    public static final int CHECK_CONSTRAINT = 1;      // "C" (CHECK)
    public static final int PRIMARYKEY_CONSTRAINT = 2; // "PK" (PRIMARY KEY)
    public static final int FOREIGNKEY_CONSTRAINT = 3; // "F" (FOREIGN KEY)
    public static final int UNIQUE_CONSTRAINT = 4;     // "UQ" (UNIQUE)
    private dbTable table;
    private String columnName;
    private int constraintType;                  // CHECK_CONSTRAINT .. UNIQUE_CONSTRAINT
    private String checkText = "";               // (like "ID" IS NOT NULL)
    private String refTableName;
    private String refColumnName;
    private boolean skipNotNull;

    public dbConstraint(dbConstraintList list, dbTable aTable, int aID, String aObjectName) throws SQLException {
        super(list, aID, aObjectName);
        table = aTable;

        getDatabase().readDataSet("SELECT TEXT FROM SYSCOMMENTS WHERE ID = ? ORDER BY COLID",
                new Object[]{new Integer(aID)},
                new DataSetInterface() {
                    @Override
                    public boolean onReadRecord(ResultSet rs) throws SQLException {
                        checkText += rs.getString("TEXT");
                        return true;
                    }
                });
    }

    @Override
    public String getCategory() {
        return "constraint";
    }

    public void setConstraintType(String aConstraintType) throws SQLException {
        if (aConstraintType.trim().equals("C")) {
            constraintType = CHECK_CONSTRAINT;
        } else if (aConstraintType.trim().equals("PK")) {
            constraintType = PRIMARYKEY_CONSTRAINT;
        } else if (aConstraintType.trim().equals("F")) {
            constraintType = FOREIGNKEY_CONSTRAINT;
        } else if (aConstraintType.trim().equals("UQ")) {
            constraintType = UNIQUE_CONSTRAINT;
        } else {
            throw new SQLException("Unknown (constraint) '" + aConstraintType + "'");
        }

        if (constraintType == FOREIGNKEY_CONSTRAINT) {
            ArrayList record = new ArrayList();
            if (getDatabase().readRecord("SELECT RKEYID, FKEY, RKEY FROM SYSFOREIGNKEYS WHERE CONSTID = ?", new Object[]{new Integer(ID)}, record)) {
                refTableName = (String) getDatabase().readFieldValue("SELECT NAME FROM SYSOBJECTS WHERE ID = ?", new Object[]{record.get(0)});
                columnName = (String) getDatabase().readFieldValue("SELECT NAME FROM SYSCOLUMNS WHERE ID = ? AND COLID = ?", new Object[]{new Integer(table.ID), record.get(1)});
                refColumnName = (String) getDatabase().readFieldValue("SELECT NAME FROM SYSCOLUMNS WHERE ID = ? AND COLID = ?", new Object[]{record.get(0), record.get(2)});
            }
        } else {
            getDatabase().readDataSet("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE WHERE CONSTRAINT_NAME = ?",
                    new Object[]{name},
                    new DataSetInterface() {
                        @Override
                        public boolean onReadRecord(ResultSet rs) throws SQLException {
                            columnName = rs.getString("COLUMN_NAME");
                            return false;
                        }
                    });
        }
    }

    public void setRConstraintName(String aRConstraintName) throws SQLException {
        if (aRConstraintName != null) {
            getDatabase().readDataSet("SELECT TABLE_NAME, COLUMN_NAME FROM USER_CONS_COLUMNS WHERE CONSTRAINT_NAME = ?",
                    new Object[]{aRConstraintName},
                    new DataSetInterface() {
                        @Override
                        public boolean onReadRecord(ResultSet rs) throws SQLException {
                            refTableName = rs.getString("TABLE_NAME");
                            refColumnName = rs.getString("COLUMN_NAME");
                            return false;
                        }
                    });
        }
    }

    public String getColumnName() {
        return columnName;
    }

    public int getConstraintType() {
        return constraintType;
    }

    public boolean getSkipNotNull() {
        return skipNotNull;
    }

    public String getRefTableName() {
        return refTableName;
    }

    private String getConstraintTypeName() {
        if (constraintType == PRIMARYKEY_CONSTRAINT) {
            return "primary";
        }
        if (constraintType == FOREIGNKEY_CONSTRAINT) {
            return "foreign";
        }
        if (constraintType == UNIQUE_CONSTRAINT) {
            return "unique";
        }
        if (constraintType == CHECK_CONSTRAINT) {
            return "check";
        }
        return "unknown";
    }

    private String getScript() {
        if (constraintType == PRIMARYKEY_CONSTRAINT) {
            return " PRIMARY KEY (" + columnName + ")";
        }
        if (constraintType == UNIQUE_CONSTRAINT) {
            return " UNIQUE (" + columnName + ")";
        }
        if ((constraintType == CHECK_CONSTRAINT) && (checkText != null)) {
            return " CHECK (" + checkText + ")\n";
        }
        if (constraintType == FOREIGNKEY_CONSTRAINT) {
            return " FOREIGN KEY (" + columnName + ")\n";
        }
        return "";
    }

    public String getXML() {
        String s = "<constraint name=\"" + name + "\">\n";
        s += "<type name=\"" + getConstraintTypeName() + "\"" +
                " columns=\"" + columnName + "\"" +
                (refTableName == null ? "" : " refTable=\"" + refTableName + "\"") +
                (refColumnName == null ? "" : " refColumn=\"" + refColumnName + "\"") +
                (checkText == null ? "" : " condition=\"" + toXML(checkText) + "\"") +
                "/>\n";
        s += "<script><![CDATA[ALTER TABLE " + table.name + " ADD CONSTRAINT " + name + getScript() + "]]></script>\n";
        s += "</constraint>\n";
        return s;
    }

    public String getSQL() {
        String s = "";
        if (constraintType == PRIMARYKEY_CONSTRAINT) {
            s += " PRIMARY KEY";
        }
        if (constraintType == UNIQUE_CONSTRAINT) {
            s += " UNIQUE";
        }
        if ((constraintType == CHECK_CONSTRAINT) && (checkText != null)) {
            s += " CHECK (" + checkText + ")";
        }
        if (constraintType == FOREIGNKEY_CONSTRAINT) {
            s += " REFERENCES " + refTableName + "(" + refColumnName + ")";
        }
        return s;
    }
}
