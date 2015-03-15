package com.tsoft.dbcompare.config;

import com.tsoft.dbcompare.connection.DatabaseType;

import javax.sql.DataSource;

public class ConnectionConfig {
    private DataSource dataSource;

    private DatabaseType databaseType;

    private String schemaName;

    public ConnectionConfig(DataSource dataSource, DatabaseType databaseType) {
        this.dataSource = dataSource;
        this.databaseType = databaseType;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}
