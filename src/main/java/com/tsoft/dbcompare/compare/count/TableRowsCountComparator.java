package com.tsoft.dbcompare.compare.count;

import com.tsoft.dbcompare.connection.DatabaseConnection;
import com.tsoft.dbcompare.logger.DestinationLogger;
import com.tsoft.dbcompare.logger.SourceLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.logging.Level;

@Service
public class TableRowsCountComparator {
    @Autowired
    private SourceLogger sourceLogger;

    @Autowired
    private DestinationLogger destinationLogger;

    public boolean compare(DatabaseConnection sourceConnection, DatabaseConnection destinationConnection, String tableName, String whereClause) throws SQLException {
        long sourceRowsCount = sourceConnection.getTableRowsCount(tableName, whereClause);
        long destinationRowsCount = destinationConnection.getTableRowsCount(tableName, whereClause);

        boolean isEqual = true;
        if (sourceRowsCount != destinationRowsCount) {
            isEqual = false;
            destinationLogger.log(Level.SEVERE, "Table '" + tableName + " has " + destinationRowsCount + " row(s) but must have " + sourceRowsCount + " row(s). Diff=" + (destinationRowsCount - sourceRowsCount));
        }
        return isEqual;
    }
}
