package com.tsoft.dbcompare.connection;

import com.tsoft.dbcompare.config.ConnectionConfig;
import com.tsoft.dbcompare.logger.ResultsLogger;

public class DatabaseConnectionFactory {
    private DatabaseConnectionFactory() { }

    public static DatabaseConnection getConnectionByDatabaseType(ConnectionConfig connectionConfig, ResultsLogger logger) {
        switch (connectionConfig.getDatabaseType()) {
            case ORACLE: return new OracleConnection(connectionConfig, logger);

            case POSTGRES: return new PostgresConnection(connectionConfig, logger);
        }

        throw new IllegalArgumentException("DatabaseType is unknown");
    }
}
