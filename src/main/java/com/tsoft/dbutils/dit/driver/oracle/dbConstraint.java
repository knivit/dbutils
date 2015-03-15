package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObject;
import java.sql.*;
import com.tsoft.dbutils.lib.db.*;

class dbConstraint extends dbObject {
    public static final int CHECK_CONSTRAINT = 1;       // "C" (CHECK, NOT NULL)
    public static final int PRIMARYKEY_CONSTRAINT = 2;  // "P" (PRIMARY KEY)
    public static final int FOREIGNKEY_CONSTRAINT = 3;  // "R" (FOREIGN KEY)
    public static final int UNIQUE_CONSTRAINT = 4;      // "U" (UNIQUE)
    public static final int CHECKOPTION_CONSTRAINT = 5; // "V" (CHECK OPTION)
    public static final int READONLY_CONSTRAINT = 5;    // "O" (READ ONLY)

    private String tableName;
    private int constraintType;                   // CHECK_CONSTRAINT .. READONLY_CONSTRAINT
    private String columnName;
    private String searchCondition;               // (like "ID" IS NOT NULL)
    private String refTableName;                  //
    private String refColumnName;                 //
    private String deleteRule;                    // foreign key: (cascade delete, no action, set null)
    private String status;                        // "ENABLED" or "DISABLED"
    private String deferrable;                    // "DEFERRABLE" or "NOT DEFERRABLE"
    private String deferred;                      // "IMMEDIATE" or "DEFERRED"
    private boolean generated;
    private boolean skipNotNull;

    public dbConstraint(dbConstraintList list, String aTableName, String aObjectName) throws SQLException {
        super(list, aObjectName);
        tableName = aTableName;
        getDatabase().readDataSet("SELECT COLUMN_NAME FROM ALL_CONS_COLUMNS WHERE OWNER = ? AND CONSTRAINT_NAME = ?",
                new Object[]{getDatabase().getSchema(), name.toUpperCase()},
                new DataSetInterface() {
                    @Override
                    public boolean onReadRecord(ResultSet rs) throws SQLException {
                        if (columnName == null) {
                            columnName = rs.getString("COLUMN_NAME");
                        } else {
                            columnName += ", " + rs.getString("COLUMN_NAME");
                        }
                        return true;
                    }
                });
    }

    @Override
    public String getCategory() {
        return "constraint";
    }

    public void setConstraintType(String aConstraintType) throws SQLException {
        if (aConstraintType.equals("C")) {
            constraintType = CHECK_CONSTRAINT;
        } else if (aConstraintType.equals("P")) {
            constraintType = PRIMARYKEY_CONSTRAINT;
        } else if (aConstraintType.equals("R")) {
            constraintType = FOREIGNKEY_CONSTRAINT;
        } else if (aConstraintType.equals("U")) {
            constraintType = UNIQUE_CONSTRAINT;
        } else if (aConstraintType.equals("V")) {
            constraintType = CHECKOPTION_CONSTRAINT;
        } else if (aConstraintType.equals("O")) {
            constraintType = READONLY_CONSTRAINT;
        } else {
            throw new SQLException("Unknown (constraint) '" + aConstraintType + "'");
        }
    }

    public void setSearchCondition(String aSearchCondition) {
        searchCondition = aSearchCondition;
    }

    public void setDeleteRule(String aDeleteRule) {
        deleteRule = aDeleteRule;
    }

    public void setStatus(String aStatus) {
        status = aStatus;
    }

    public void setDeferrable(String aDeferrable) {
        deferrable = aDeferrable;
    }

    public void setDeferred(String aDeferred) {
        deferred = aDeferred;
    }

    public void setGenerated(boolean aGenerated) {
        generated = aGenerated;
    }

    public void setRConstraintName(String aRConstraintName) throws SQLException {
        if (aRConstraintName != null) {
            getDatabase().readDataSet("SELECT TABLE_NAME, COLUMN_NAME FROM ALL_CONS_COLUMNS WHERE OWNER = ? AND CONSTRAINT_NAME = ?",
                    new Object[]{getDatabase().getSchema(), aRConstraintName},
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

    public boolean getGenerated() {
        return generated;
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

    private String getScript(boolean showColumnNameInRefConstraint, boolean hideGenerated) {
        String s = "";
        if (constraintType == PRIMARYKEY_CONSTRAINT) {
            s += " PRIMARY KEY";
        }
        if (constraintType == UNIQUE_CONSTRAINT) {
            s += " UNIQUE";
        }
        if ((constraintType == CHECK_CONSTRAINT) && (searchCondition != null)) {
            skipNotNull = false;
            if ((searchCondition.endsWith("NOT NULL")) && hideGenerated) {
                skipNotNull = true;
            } else {
                s += " CHECK (" + searchCondition + ")";
            }
        }
        if (constraintType == FOREIGNKEY_CONSTRAINT) {
            if (showColumnNameInRefConstraint) {
                s += " FOREIGN KEY (" + columnName + ")";
            }
            s += " REFERENCES " + refTableName + "(" + refColumnName + ")";
            if (deleteRule.equals("CASCADE")) {
                s += " ON DELETE CASCADE";
            }
            if (deleteRule.equals("SET NULL")) {
                s += " ON DELETE SET NULL";
            }
            if (deleteRule.equals("NO ACTION"));
        }

        if (deferrable.equals("DEFERRABLE")) {
            if (deferred.equals("IMMEDIATE")) {
                s += " DEFERRABLE INITIALLY IMMEDIATE";
            } else {
                s += " DEFERRABLE INITIALLY DEFERRED";
            }
        }

        return s;
    }

    public String getXML() {
        StringBuilder s = new StringBuilder();
        s.append("<constraint name=\"").append(name).append("\">\n");
        s.append("<type name=\"").append(getConstraintTypeName()).append("\"");
        s.append(" columns=\"").append(columnName).append("\"");
        if (refTableName != null) {
            s.append(" refTable=\"").append(refTableName).append("\"");
        }
        if (refColumnName != null) {
            s.append(" refColumn=\"").append(refColumnName).append("\"");
        }
        if (searchCondition != null) {
            s.append(" condition=\"").append(toXML(searchCondition)).append("\"");
        }
        s.append("/>\n");
        if (deleteRule != null) {
            s.append("<deleterule>").append(deleteRule).append("</deleterule>\n");
        }
        s.append("<deferrable>").append(deferrable).append("</deferrable>\n");
        s.append("<deferred>").append(deferred).append("</deferred>\n");
        s.append("<enabled>").append(status.equalsIgnoreCase("ENABLED")).append("</enabled>\n");
        s.append("<script><![CDATA[ALTER TABLE ").append(tableName).append(" ADD CONSTRAINT ").append(name);
        s.append(getScript(true, false)).append("]]></script>\n");
        s.append("</constraint>\n");
        return s.toString();
    }

    public String getSQL(boolean showColumnNameInRefConstraint) {
        StringBuilder s = new StringBuilder();
        if (!generated) {
            s.append(" CONSTRAINT ").append(name);
        }

        s.append(getScript(showColumnNameInRefConstraint, !generated));
        if (status.equals("ENABLED")) {
            return s.toString();
        } else {
            return "/* " + s.toString() + " */";
        }
    }
}
