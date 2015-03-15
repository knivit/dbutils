package com.tsoft.dbcompare.compare.hash;

import com.tsoft.dbcompare.compare.ddl.TableDdl;
import com.tsoft.dbcompare.connection.DatabaseConnection;
import com.tsoft.dbcompare.logger.DestinationLogger;
import com.tsoft.dbcompare.logger.SourceLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.logging.Level;

@Service
public class TableColumnsHashComparator {
    @Autowired
    private SourceLogger sourceLogger;

    @Autowired
    private DestinationLogger destinationLogger;

    public boolean compare(DatabaseConnection sourceConnection, DatabaseConnection destinationConnection, TableDdl tableDdl, String whereClause) throws SQLException {
        TableHash sourceTableHash = sourceConnection.getTableColumnsHash(tableDdl, whereClause);
        TableHash destinationTableHash = destinationConnection.getTableColumnsHash(tableDdl, whereClause);

        if (sourceTableHash.getColumnList().isEmpty()) {
            sourceLogger.log(Level.WARNING, "Table '" + tableDdl.getName() + "' has not hashable columns");
            return true;
        }

        int sourceSize = sourceTableHash.getColumnList().size();
        int destinationSize = destinationTableHash.getColumnList().size();
        if (sourceSize  != destinationSize) {
            sourceLogger.log(Level.SEVERE, "Table '" + tableDdl.getName() + "' has " + sourceSize + " a hashable column but the destination has " + destinationSize);
            return false;
        }

        boolean isEqual = true;
        for (int i = 0; i < sourceTableHash.getColumnList().size(); i ++) {
            ColumnHash sourceColumnHash = sourceTableHash.getColumnList().get(i);
            ColumnHash destinationColumnHash = destinationTableHash.getColumnList().get(i);

            Object value1 = sourceColumnHash.getValue();
            Object value2 = destinationColumnHash.getValue();
            boolean isValueEqual = false;
            if (value1 == null) {
                if (value2 == null) {
                    isValueEqual = true;
                }
            } else {
                if (value1.getClass().equals(BigDecimal.class)) {
                    BigDecimal decimalValue1 = (BigDecimal)value1;
                    BigDecimal decimalValue2 = (BigDecimal)value2;
                    isValueEqual = decimalValue1.compareTo(decimalValue2) == 0;
                } else {
                    isValueEqual = value1.equals(value2);
                }
            }

            if (!isValueEqual) {
                isEqual = false;
                destinationLogger.log(Level.SEVERE, "Source column has value " + sourceColumnHash.toLogString() + " but destination column has got " + destinationColumnHash.toLogString());
            }
        }
        return isEqual;
    }
}
