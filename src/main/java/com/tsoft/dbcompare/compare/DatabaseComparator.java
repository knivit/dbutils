package com.tsoft.dbcompare.compare;

import com.tsoft.dbcompare.compare.count.TableRowsCountComparator;
import com.tsoft.dbcompare.compare.ddl.TableDdl;
import com.tsoft.dbcompare.compare.ddl.TableDdlComparator;
import com.tsoft.dbcompare.compare.hash.TableColumnsHashComparator;
import com.tsoft.dbcompare.config.CompareConfig;
import com.tsoft.dbcompare.config.CompareMode;
import com.tsoft.dbcompare.config.TableConfig;
import com.tsoft.dbcompare.connection.DatabaseConnection;
import com.tsoft.dbcompare.connection.DatabaseConnectionFactory;
import com.tsoft.dbcompare.logger.ApplicationLogger;
import com.tsoft.dbcompare.logger.DestinationLogger;
import com.tsoft.dbcompare.logger.SourceLogger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

@Service
public class DatabaseComparator implements ApplicationContextAware {
    private ApplicationContext context;

    @Autowired
    private SourceLogger sourceLogger;

    @Autowired
    private DestinationLogger destinationLogger;

    @Autowired
    private ApplicationLogger applicationLogger;

    public void compare(CompareConfig config) throws SQLException {
        Assert.notNull(config);

        DatabaseConnection sourceConnection = DatabaseConnectionFactory.getConnectionByDatabaseType(config.getSourceConnectionConfig(), sourceLogger);
        DatabaseConnection destinationConnection = DatabaseConnectionFactory.getConnectionByDatabaseType(config.getDestinationConnectionConfig(), destinationLogger);

        List<TableConfig> tableConfigList = config.getTableConfigList();
        for (TableConfig tableConfig : tableConfigList) {
            applicationLogger.logln();

            applicationLogger.log(Level.FINE, tableConfig.getName() + ": Getting DDL from both connections");
            TableDdl sourceTableDdl = sourceConnection.getTableDdl(tableConfig.getName());
            if (!sourceTableDdl.isTableExists()) {
                sourceLogger.log(Level.SEVERE, "Source table '" + tableConfig.getName() + "' not found");
                continue;
            }

            TableDdl destinationTableDdl = destinationConnection.getTableDdl(tableConfig.getName());
            if (!destinationTableDdl.isTableExists()) {
                destinationLogger.log(Level.SEVERE, "Destination table '" + tableConfig.getName() + "' not found");
                continue;
            }

            boolean isSkipped = false;
            boolean isEqual = compareDdl(sourceConnection, destinationConnection, tableConfig, sourceTableDdl, destinationTableDdl);
            if (!tableConfig.isForcedCompare()) {
                isSkipped = (!isEqual || tableConfig.getCompareMode() == CompareMode.DDL);
            }

            if (isSkipped) {
                applicationLogger.log(Level.INFO, tableConfig.getName() + ": Rows Count comparision skipped");
            } else {
                isEqual = compareRowsCount(sourceConnection, destinationConnection, tableConfig);
                if (!tableConfig.isForcedCompare()) {
                    isSkipped = (!isEqual || tableConfig.getCompareMode() == CompareMode.COUNT);
                }
            }

            if (isSkipped) {
                applicationLogger.log(Level.INFO, tableConfig.getName() + ": Columns Hash comparision skipped");
            } else {
                compareRowsHash(sourceConnection, destinationConnection, tableConfig, sourceTableDdl);
            }
        }
    }

    private void compareRowsHash(DatabaseConnection sourceConnection, DatabaseConnection destinationConnection, TableConfig tableConfig, TableDdl sourceTableDdl) throws SQLException {
        boolean isEqual;
        applicationLogger.log(Level.INFO, tableConfig.getName() + ": Columns Hash comparision started");
        TableColumnsHashComparator tableColumnsHashComparator = (TableColumnsHashComparator) context.getBean("tableColumnsHashComparator");
        isEqual = tableColumnsHashComparator.compare(sourceConnection, destinationConnection, sourceTableDdl, tableConfig.getWhereClause());
        if (isEqual) {
            applicationLogger.log(Level.INFO, tableConfig.getName() + ": Columns Hash comparision passed");
        } else {
            applicationLogger.log(Level.WARNING, tableConfig.getName() + ": Columns Hash comparision failed");
        }
    }

    private boolean compareRowsCount(DatabaseConnection sourceConnection, DatabaseConnection destinationConnection, TableConfig tableConfig) throws SQLException {
        boolean isEqual;
        applicationLogger.log(Level.INFO, tableConfig.getName() + ": Rows Count comparision started");
        TableRowsCountComparator tableRowsCountComparator = (TableRowsCountComparator) context.getBean("tableRowsCountComparator");
        isEqual = tableRowsCountComparator.compare(sourceConnection, destinationConnection, tableConfig.getName(), tableConfig.getWhereClause());
        if (isEqual) {
            applicationLogger.log(Level.INFO, tableConfig.getName() + ": Rows Count comparision passed");
        } else {
            applicationLogger.log(Level.WARNING, tableConfig.getName() + ": Rows Count comparision failed");
        }
        return isEqual;
    }

    private boolean compareDdl(DatabaseConnection sourceConnection, DatabaseConnection destinationConnection, TableConfig tableConfig, TableDdl sourceTableDdl, TableDdl destinationTableDdl) throws SQLException {
        applicationLogger.log(Level.INFO, tableConfig.getName() + ": DDL comparision started");
        TableDdlComparator tableDdlComparator = (TableDdlComparator) context.getBean("tableDdlComparator");
        boolean isEqual = tableDdlComparator.compare(sourceConnection, sourceTableDdl, destinationConnection, destinationTableDdl);
        if (isEqual) {
            applicationLogger.log(Level.INFO, tableConfig.getName() + ": DDL comparision passed");
        } else {
            applicationLogger.log(Level.WARNING, tableConfig.getName() + ": DDL comparision failed");
        }
        return isEqual;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
