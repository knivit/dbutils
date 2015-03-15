package com.tsoft.dbcompare.compare.ddl;

import com.tsoft.dbcompare.connection.DatabaseType;

public enum ColumnType {
    NUMBER,
    CHAR,
    VARCHAR,
    TIMESTAMP,
    DATE;

    private static class ColumnTypeNames {
        public ColumnType columnType;
        public String[] names;

        public ColumnTypeNames(ColumnType columnType, String ... names) {
            this.columnType = columnType;
            this.names = names;
        }
    }

    private static ColumnTypeNames[] namesCollection = new ColumnTypeNames[] {
        new ColumnTypeNames(NUMBER, "number", "numeric"),
        new ColumnTypeNames(CHAR, "char", "bpchar"),
        new ColumnTypeNames(VARCHAR, "varchar", "varchar2"),
        new ColumnTypeNames(TIMESTAMP, "timestamp", "TIMESTAMP(6)"),
        new ColumnTypeNames(DATE, "date")
    };

    private static class ColumnTypesByDatabase {
        public DatabaseType databaseType1;
        public ColumnType columnType1;
        public DatabaseType databaseType2;
        public ColumnType columnType2;

        public ColumnTypesByDatabase(DatabaseType databaseType1, ColumnType columnType1, DatabaseType databaseType2, ColumnType columnType2) {
            this.databaseType1 = databaseType1;
            this.columnType1 = columnType1;
            this.databaseType2 = databaseType2;
            this.columnType2 = columnType2;
        }
    }

    private static ColumnTypesByDatabase[] linksCollection = new ColumnTypesByDatabase[] {
        // Oracle --> Postgres
        new ColumnTypesByDatabase(DatabaseType.ORACLE, NUMBER, DatabaseType.POSTGRES, NUMBER),
        new ColumnTypesByDatabase(DatabaseType.ORACLE, CHAR, DatabaseType.POSTGRES, CHAR),
        new ColumnTypesByDatabase(DatabaseType.ORACLE, VARCHAR, DatabaseType.POSTGRES, VARCHAR),
        new ColumnTypesByDatabase(DatabaseType.ORACLE, TIMESTAMP, DatabaseType.POSTGRES, TIMESTAMP),
        new ColumnTypesByDatabase(DatabaseType.ORACLE, DATE, DatabaseType.POSTGRES, TIMESTAMP)
    };

    public static ColumnType convertFromString(String typeName) {
        for (ColumnTypeNames name : namesCollection) {
            for (String val : name.names) {
                if (val.equalsIgnoreCase(typeName)) {
                    return name.columnType;
                }
            }
        }

        throw new IllegalArgumentException(typeName + " is unknown");
    }

    public static boolean isSame(DatabaseType databaseType1, ColumnType columnType1, DatabaseType databaseType2, ColumnType columnType2) {
        for (ColumnTypesByDatabase link : linksCollection) {
            if (link.databaseType1 == databaseType1 && link.columnType1 == columnType1 &&
                    link.databaseType2 == databaseType2 && link.columnType2 == columnType2) {
                return true;
            }
        }
        return false;
    }

    public boolean isDate() {
        return this == TIMESTAMP || this == DATE;
    }
}
