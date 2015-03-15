package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObject;
import java.sql.*;
import java.util.*;
import com.tsoft.dbutils.lib.db.*;

class dbTable extends dbObject implements DataSetInterface {
    private dbFieldList fieldList;
    private dbConstraintList constraintList;
    private dbIndexList indexList;
    private dbLOBList lobList;
    private boolean nested;
    private dbTableList masterTableList;
    private dbTableList detailTableList;

    public dbTable(dbTableList list, String aObjectName) throws SQLException {
        super(list, aObjectName);
        getDatabase().readDataSet("SELECT COMMENTS FROM ALL_TAB_COMMENTS WHERE OWNER = ? AND TABLE_NAME = ?",
                new Object[]{getDatabase().getSchema(), name},
                new DataSetInterface() {
                    @Override
                    public boolean onReadRecord(ResultSet rs) throws SQLException {
                        comment = rs.getString("COMMENTS");
                        if (comment != null) {
                            comment = comment.replaceAll("\n", " ");
                        }
                        return false;
                    }
                });

        getDatabase().readDataSet("SELECT NESTED FROM USER_TABLES WHERE TABLE_NAME = ?",
                new Object[]{name},
                new DataSetInterface() {
                    @Override
                    public boolean onReadRecord(ResultSet rs) throws SQLException {
                        nested = rs.getString("NESTED").equals("YES");
                        return false;
                    }
                });
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String getCategory() {
        return "table";
    }

    public dbFieldList getFieldList() {
        if (fieldList == null) {
            fieldList = new dbFieldList();
            fieldList.open(getDatabase(), getSchema(), this);
        }
        return fieldList;
    }

    public dbConstraintList getConstraintList() {
        if (constraintList == null) {
            constraintList = new dbConstraintList();
            constraintList.open(getDatabase(), this);
        }
        return constraintList;
    }

    public dbIndexList getIndexList() {
        if (indexList == null) {
            indexList = new dbIndexList();
            indexList.open(getDatabase(), getSchema(), this);
        }
        return indexList;
    }

    public dbLOBList getLOBList() {
        if (lobList == null) {
            lobList = new dbLOBList();
            lobList.open(getDatabase(), getSchema(), this);
        }
        return lobList;
    }

    public dbTableList getMasterTableList() {
        if (masterTableList == null) {
            masterTableList = new dbTableList();
            dbTableList tableList = (dbTableList)getList();
            for (int i = 0; i < tableList.size(); i++) {
                dbTable table = (dbTable) tableList.get(i);
                for (int j = 0; j < table.getConstraintList().size(); j++) {
                    dbConstraint c = (dbConstraint) table.getConstraintList().get(j);
                    if (c.getRefTableName() != null) {
                        if (this.name.equals(c.getRefTableName())) {
                            masterTableList.add(table);
                        }
                    }
                }
            }
        }
        return masterTableList;
    }

    public dbTableList getDetailTableList() {
        if (detailTableList == null) {
            detailTableList = new dbTableList();
            for (int i = 0; i < getConstraintList().size(); i++) {
                dbConstraint c = (dbConstraint) getConstraintList().get(i);
                if (c.getRefTableName() != null) {
                    dbTableList tl = (dbTableList)getList();
                    dbTable referencesTo = (dbTable) tl.find(c.getRefTableName());
                    if (referencesTo != null) {
                        detailTableList.add(referencesTo);
                    }
                }
            }
        }
        return detailTableList;
    }

    public boolean getNested() {
        return nested;
    }

    public String getXML() {
        String s = "<table name=\"" + name + "\">\n";
        s += "<created>" + created + "</created>\n";
        s += "<nested>" + (nested ? "YES" : "NO") + "</nested>\n";
        if (comment != null) {
            s += "<comment>" + toXML(comment) + "</comment>\n";
        }
        s += getFieldList().getXML();
        s += getConstraintList().getXML();
        s += getIndexList().getXML();
        s += "<script><![CDATA[" + getSQL() + "]]></script>\n";
        s += "</table>\n";
        return s;
    }

    public String getSQL() {
        String s = "CREATE OR REPLACE TABLE " + name + " (\n";

        for (int i = 0; i < getFieldList().size(); i++) {
            String app = "";
            boolean skipNotNull = false;
            dbField f = (dbField) getFieldList().get(i);
            ArrayList cl = getConstraintList().findByFieldName(f.name);

            for (int k = 0; k < cl.size(); k++) {
                dbConstraint c = (dbConstraint) cl.get(k);
                if ((c.getConstraintType() == dbConstraint.FOREIGNKEY_CONSTRAINT) && !c.getGenerated()); else {
                    app = app + c.getSQL(false);
                }
                if (c.getConstraintType() == dbConstraint.CHECK_CONSTRAINT) {
                    skipNotNull = skipNotNull || c.getSkipNotNull();
                }
            }
            if (i != (getFieldList().size() - 1)) {
                app = app + ",";
            }
            s += "  " + f.getSQL(app, skipNotNull);
            s += "\n";
        }
        s += ");\n";
        return s;
    }

    public String getInfo() {
        String s = "/*\n";
        s += " * @table " + name + "\n";
        s += " * @created " + created + "\n";
        s += (nested ? " * @nested YES\n" : "");
        s += " * @fieldCount " + getFieldList().size() + "\n";
        s += (getConstraintList().size() == 0 ? "" : " * @constraintCount " + getConstraintList().size() + "\n");
        s += (getIndexList().size() == 0 ? "" : " * @indexCount " + getIndexList().size() + "\n");
        s += (getLOBList().size() == 0 ? "" : " * @LOBCount " + getLOBList().size() + "\n");

        dbTableList referencesToList = getDetailTableList();
        if (referencesToList.size() > 0) {
            s += " * @referencesCount " + referencesToList.size() + "\n";
            for (int i = 0; i < referencesToList.size(); i++) {
                dbTable t = (dbTable) referencesToList.get(i);
                s += " * @referenceTable " + t.name + "\n";
            }
        }

        if (comment != null) {
            s += " * @comments " + comment + "\n";
        }
        s += " */\n";
        return s;
    }

    public String getQuery() {
        return "SELECT " + getFieldList().getFieldList(false) + " FROM " + name;
    }

    private int INSERT_ROW_COUNT = 16;
    private int recordCount;
    private String insertScript;

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        String fv = "";
        ResultSetMetaData md = rs.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            if (rs.getObject(i) == null) {
                fv = fv + "NULL";
            } else {
                fv = fv + "'" + rs.getObject(i).toString() + "'";
            }
            if (i < md.getColumnCount()) {
                fv = fv + ", ";
            }
        }
        insertScript = insertScript + "INSERT INTO " + name + " (" + getFieldList().getFieldList(false) + ") VALUES (" + fv + ");\n";
        recordCount++;
        return recordCount < INSERT_ROW_COUNT;
    }

    public String getInsertScript() {
        if (nested) {
            return "/* " + name + " is nested table */\n";
        }
        if (getFieldList().size() == 0) {
            return "/* " + name + " has no fileds */\n";
        }
        if (getFieldList().getFieldList(false).length() == 0) {
            return "/* " + name + " containts a lob field only, skipped */\n";
        }

        recordCount = 0;
        insertScript = "";
        getDatabase().readDataSet(getQuery(), null, this, INSERT_ROW_COUNT);
        return insertScript;
    }

    public ResultSet getDataSet() throws SQLException {
        return getDatabase().openDataSet(getQuery(), null);
    }
}
