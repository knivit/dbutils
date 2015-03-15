package com.tsoft.dbcompare.compare.ddl;

import com.tsoft.dbcompare.connection.DatabaseConnection;
import com.tsoft.dbcompare.logger.ResultsLogger;
import org.springframework.util.Assert;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

public class TableDdl {
    private DatabaseConnection databaseConnection;

    private String name;

    private ArrayList<ColumnDdl> columnList = new ArrayList<ColumnDdl>();
    private ArrayList<PrimaryKeyDdl> keyList = new ArrayList<PrimaryKeyDdl>();

    public TableDdl(DatabaseConnection connection, String name) {
        this.databaseConnection = connection;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private DatabaseMetaData getMetaData() throws SQLException {
        return databaseConnection.getMetaData();
    }

    public String getSchemaName() {
        return databaseConnection.getSchemaName();
    }

    public ResultsLogger getLogger() {
        return databaseConnection.getLogger();
    }

    public ArrayList<ColumnDdl> getColumnList() {
        return columnList;
    }

    public ArrayList<PrimaryKeyDdl> getKeyList() {
        return keyList;
    }

    public boolean isTableExists() {
        return columnList.size() > 0;
    }

    public void populate() throws SQLException {
        ResultSet tablesResultSet = getMetaData().getTables(null, getSchemaName(), getName(), new String[] {"TABLE"});
        try {
            if (tablesResultSet.next()) {
                populateColumns();
                populateKeys();
            }
        } finally {
            tablesResultSet.close();
        }
    }

    private void populateColumns() throws SQLException {
        ResultSet columnsResultSet = getMetaData().getColumns(null, getSchemaName(), getName(), null);
        try {
            while (columnsResultSet.next()) {
                String columnName = columnsResultSet.getString("COLUMN_NAME");
                String columnType = columnsResultSet.getString("TYPE_NAME");
                int columnSize = columnsResultSet.getInt("COLUMN_SIZE");
                int decimalDigits = columnsResultSet.getInt("DECIMAL_DIGITS");
                boolean nullable = columnsResultSet.getInt("NULLABLE") != 0;

                getLogger().log(Level.FINE, "COLUMN_NAME=" + columnName + ", TYPE_NAME=" + columnType + ", COLUMN_SIZE=" + columnSize + ", DECIMAL_DIGITS=" + decimalDigits + ", NULLABLE=" + nullable);

                ColumnDdl columnDdl = new ColumnDdl(columnName);
                columnDdl.setColumnType(ColumnType.convertFromString(columnType));
                columnDdl.setSize(columnSize);
                columnDdl.setDecimalDigits(decimalDigits);
                columnDdl.setNullable(nullable);
                columnList.add(columnDdl);
            }
        } finally {
            columnsResultSet.close();
        }
    }

    public ColumnDdl findColumn(String columnName) {
        Assert.notNull(columnName);

        for (ColumnDdl columnDdl : getColumnList()) {
            if (columnName.equalsIgnoreCase(columnDdl.getName())) {
                return columnDdl;
            }
        }

        return null;
    }

    private void populateKeys() throws SQLException {
        ResultSet keysSet = getMetaData().getPrimaryKeys(null, getSchemaName(), getName());
        try {
            while (keysSet.next()) {
                String columnName = keysSet.getString("COLUMN_NAME");
                int keySeq = keysSet.getInt("KEY_SEQ");
                String pkName = keysSet.getString("PK_NAME");

                getLogger().log(Level.FINE, "COLUMN_NAME=" + columnName + ", KEY_SEQ=" + keySeq + ", PK_NAME=" + pkName);

                PrimaryKeyDdl keyDdl = new PrimaryKeyDdl(columnName);
                keyDdl.setKeySeq(keySeq);
                keyDdl.setPkName(pkName);
                keyList.add(keyDdl);
            }
        } finally {
            keysSet.close();
        }
    }

    public PrimaryKeyDdl findPrimaryKey(String columnName) {
        Assert.notNull(columnName);

        for (PrimaryKeyDdl keyDdl : getKeyList()) {
            if (columnName.equalsIgnoreCase(keyDdl.getColumnName())) {
                return keyDdl;
            }
        }

        return null;
    }
}
