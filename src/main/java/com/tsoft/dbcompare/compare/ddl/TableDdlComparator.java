package com.tsoft.dbcompare.compare.ddl;

import com.tsoft.dbcompare.connection.DatabaseConnection;
import com.tsoft.dbcompare.connection.DatabaseType;
import com.tsoft.dbcompare.logger.DestinationLogger;
import com.tsoft.dbcompare.logger.SourceLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.logging.Level;

@Service
public class TableDdlComparator {
    @Autowired
    private SourceLogger sourceLogger;

    @Autowired
    private DestinationLogger destinationLogger;

    public boolean compare(DatabaseConnection sourceConnection, TableDdl sourceTableDdl, DatabaseConnection destinationConnection, TableDdl destinationTableDdl) throws SQLException {
        boolean isEqual = true;

        isEqual &= checkDestinationTableHasAllSourceFields(sourceConnection.getDatabaseType(), sourceTableDdl, destinationConnection.getDatabaseType(), destinationTableDdl);
        isEqual &= checkDestinationTableHasOnlySourceFields(sourceTableDdl, destinationTableDdl);

        isEqual &= checkDestinationHasAllSourceKeys(sourceTableDdl, destinationTableDdl);
        isEqual &= checkDestinationHasOnlySourceKeys(sourceTableDdl, destinationTableDdl);
        return isEqual;
    }

    private boolean checkDestinationHasAllSourceKeys(TableDdl sourceTableDdl, TableDdl destinationTableDdl) {
        boolean isEqual = true;
        for (PrimaryKeyDdl sourceKeyDdl : sourceTableDdl.getKeyList()) {
            PrimaryKeyDdl destinationKeyDdl = destinationTableDdl.findPrimaryKey(sourceKeyDdl.getColumnName());
            if (destinationKeyDdl == null) {
                isEqual = false;
                destinationLogger.log(Level.SEVERE, "Table '" + destinationTableDdl.getName() + "' doesn't have " + sourceKeyDdl.toLogString());
                continue;
            }

            if (!destinationKeyDdl.equals(sourceKeyDdl)) {
                isEqual = false;
                destinationLogger.log(Level.SEVERE, "Table '" + destinationTableDdl.getName() + "' has " + destinationKeyDdl.toLogString() + " but must be " + sourceKeyDdl.toLogString());
                continue;
            }
        }
        return isEqual;
    }

    private boolean checkDestinationHasOnlySourceKeys(TableDdl sourceTableDdl, TableDdl destinationTableDdl) {
        boolean isEqual = true;
        for (PrimaryKeyDdl destinationKeyDdl : destinationTableDdl.getKeyList()) {
            if (sourceTableDdl.findPrimaryKey(destinationKeyDdl.getColumnName()) == null) {
                isEqual = false;
                destinationLogger.log(Level.SEVERE, "Source table '" + sourceTableDdl.getName() + "' doesn't have " + destinationKeyDdl.toLogString());
            }
        }
        return isEqual;
    }

    private boolean checkDestinationTableHasAllSourceFields(DatabaseType sourceDatabaseType, TableDdl sourceTableDdl, DatabaseType destinationDatabaseType, TableDdl destinationTableDdl) {
        boolean isEqual = true;
        for (ColumnDdl sourceColumnDdl : sourceTableDdl.getColumnList()) {
            ColumnDdl destinationColumnDdl = destinationTableDdl.findColumn(sourceColumnDdl.getName());
            if (destinationColumnDdl == null) {
                isEqual = false;
                destinationLogger.log(Level.SEVERE, "Table '" + destinationTableDdl.getName() + "' doesn't have " + sourceColumnDdl.toLogString());
                continue;
            }

            String columnName = "Column " + destinationTableDdl.getName() + "." + destinationColumnDdl.getName();
            if (!ColumnType.isSame(sourceDatabaseType, sourceColumnDdl.getColumnType(), destinationDatabaseType, destinationColumnDdl.getColumnType())) {
                isEqual = false;
                destinationLogger.log(Level.SEVERE, columnName + " is " + destinationColumnDdl.getColumnType() + " but must be " + sourceColumnDdl.getColumnType());
                continue;
            }

            if (!sourceColumnDdl.getColumnType().isDate()) {
                // compare sizes if only they were defined, because for example,
                // Oracle.NUMBER has size 131089 but Postgres.NUMERIC is 22.
                boolean isDefined = sourceColumnDdl.getSize() < 22 && destinationColumnDdl.getSize() < 22;
                if (isDefined) {
                    if (destinationColumnDdl.getSize() != sourceColumnDdl.getSize()) {
                        isEqual = false;
                        destinationLogger.log(Level.SEVERE, columnName + "'s size is " + destinationColumnDdl.getSize() + " but must be " + sourceColumnDdl.getSize());
                    }

                    if (destinationColumnDdl.getDecimalDigits() != sourceColumnDdl.getDecimalDigits()) {
                        isEqual = false;
                        destinationLogger.log(Level.SEVERE, columnName + " have " + destinationColumnDdl.getDecimalDigits() + " decimal digits but must have " + sourceColumnDdl.getDecimalDigits());
                    }
                }
            }

            if (destinationColumnDdl.isNullable() != sourceColumnDdl.isNullable()) {
                isEqual = false;
                destinationLogger.log(Level.SEVERE, columnName + " is " + (destinationColumnDdl.isNullable() ? "nullable" : "not nullable") + " but must be " + (sourceColumnDdl.isNullable() ? "nullable" : "not nullable"));
            }
        }
        return isEqual;
    }

    private boolean checkDestinationTableHasOnlySourceFields(TableDdl sourceTableDdl, TableDdl destinationTableDdl) {
        boolean isEqual = true;
        for (ColumnDdl columnDdl : destinationTableDdl.getColumnList()) {
            if (sourceTableDdl.findColumn(columnDdl.getName()) == null) {
                isEqual = false;
                destinationLogger.log(Level.SEVERE, "Source table '" + sourceTableDdl.getName() + "' doesn't have " + columnDdl.toLogString());
            }
        }
        return isEqual;
    }
}
