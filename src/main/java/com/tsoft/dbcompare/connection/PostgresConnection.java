package com.tsoft.dbcompare.connection;

import com.tsoft.dbcompare.compare.ddl.TableDdl;
import com.tsoft.dbcompare.config.ConnectionConfig;
import com.tsoft.dbcompare.logger.ResultsLogger;

import java.sql.SQLException;

public class PostgresConnection extends DatabaseConnection {
    public PostgresConnection(ConnectionConfig connectionConfig, ResultsLogger logger) {
        super(connectionConfig, logger);
    }

    @Override
    public TableDdl getTableDdl(String tableName) throws SQLException {
        TableDdl tableDdl = new TableDdl(this, tableName.toLowerCase());
        tableDdl.populate();
        return tableDdl;
    }
}
