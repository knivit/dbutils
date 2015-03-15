package com.tsoft.dbcompare.connection;

import com.tsoft.dbcompare.compare.ddl.TableDdl;
import com.tsoft.dbcompare.compare.hash.TableHash;
import com.tsoft.dbcompare.config.ConnectionConfig;
import com.tsoft.dbcompare.logger.ResultsLogger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public abstract class DatabaseConnection {
    private ConnectionConfig connectionConfig;
    
    private Connection connection;
    private JdbcTemplate jdbcTemplate;

    private ResultsLogger logger;

    public abstract TableDdl getTableDdl(String tableName) throws SQLException;

    public DatabaseConnection(ConnectionConfig connectionConfig, ResultsLogger logger) {
        this.connectionConfig = connectionConfig;
        this.logger = logger;

        jdbcTemplate = new JdbcTemplate(connectionConfig.getDataSource());
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public synchronized DatabaseMetaData getMetaData() throws SQLException {
        if (connection == null) {
            connection = connectionConfig.getDataSource().getConnection();
        }
        return connection.getMetaData();
    }

    public long getTableRowsCount(String tableName, String whereClause) throws SQLException {
        StringBuilder sql = new StringBuilder("select count(*) from " + tableName);
        if (whereClause != null && whereClause.length() > 0) {
            sql.append(" where ").append(whereClause);
        }

        long rowsCount = getJdbcTemplate().queryForLong(sql.toString());
        return rowsCount;
    }

    public TableHash getTableColumnsHash(TableDdl tableDdl, String whereClause) {
        TableHash tableHash = new TableHash(this);
        tableHash.populateColumns(tableDdl, whereClause);
        return tableHash;
    }

    public DatabaseType getDatabaseType() {
        return connectionConfig.getDatabaseType();
    }

    public String getSchemaName() {
        return connectionConfig.getSchemaName();
    }

    public ResultsLogger getLogger() {
        return logger;
    }
}
