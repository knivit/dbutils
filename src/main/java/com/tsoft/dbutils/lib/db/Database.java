package com.tsoft.dbutils.lib.db;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import com.tsoft.dbutils.lib.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    private static final Logger logger = Logger.getLogger(Database.class.getName());

    public static int LOGNONE = 0;
    public static int LOGONLOG = 1;
    public static int LOGTOFILE = 2;

    public static final int MSSQL = 1;                 // MS SQL Server 2000
    public static final int ORACLE = 2;                // ORACLE 9i
    private FileOutputStream log;
    private int counter;
    private ResultSet singleDataSet;
    private PreparedStatement singlePreparedStatement;
    private int logMode = LOGNONE;
    private DatabaseInterface databaseEvents;
    private Connection connection;
    private String driver;
    private String URL;
    private String userName;
    private String password;
    private String schema;
    private boolean connectionFailed;

    /**
     * @return MSSQL..ORACLE (int)
     */
    public int getTypeDB() {
        if (driver.indexOf("oracle") != -1) {
            return ORACLE;
        }
        return MSSQL;
    }

    public String getTypeDBName() {
        if (getTypeDB() == ORACLE) {
            return "Oracle";
        }
        return "MS SQL";
    }

    public DatabaseMetaData getMetaData() {
        try {
            return connection.getMetaData();
        } catch (SQLException e) {
            return null;
        }
    }

    // --------------+----------+----------+------------+
    // java.sql.Types!          !  MS SQL  !   ORACLE   !
    // --------------+----------+----------+------------+
    // BIT           !NUMBER    !BIT       !NUMBER      !
    // TINYINT       !NUMBER    !TINYINT   !NUMBER      !
    // SMALLINT      !NUMBER    !SMALLINT  !NUMBER      !
    // INTEGER       !NUMBER    !INT       !NUMBER      !
    // BIGINT        !NUMBER    !BIGINT    !NUMBER      !
    // FLOAT         !NUMBER    !FLOAT     !NUMBER      !
    // REAL          !NUMBER    !REAL      !NUMBER      !
    // DOUBLE        !NUMBER    !FLOAT     !NUMBER      !
    // NUMERIC       !NUMBER    !NUMERIC   !NUMBER      !
    // DECIMAL       !NUMBER    !DECIMAL   !DECIMAL     !
    // BOOLEAN       !NUMBER    !BIT       !NUMBER      !
    // CHAR          !CHAR      !CHAR      !CHAR        !
    // VARCHAR       !CHAR      !VARCHAR   !VARCHAR2    !
    // BINARY        !CHAR      !BINARY    !VARCHAR2    !
    // VARBINARY     !CHAR      !VARBINARY !VARCHAR2    !
    // DATE          !DATE      !DATETIME  !DATE        !
    // TIME          !DATE      !DATETIME  !DATE        !
    // TIMESTAMP     !DATE      !DATETIME  !TIMESTAMP   !
    // LONGVARCHAR   !BLOB      !TEXT      !CLOB        !
    // LONGVARBINARY !BLOB      !IMAGE     !BLOB        !
    // JAVA_OBJECT   !BLOB      !?         !JAVA_OBJECT !
    // STRUCT        !BLOB      !?         !STRUCT      !
    // ARRAY         !BLOB      !?         !ARRAY       !
    // BLOB          !BLOB      !IMAGE     !BLOB        !
    // CLOB          !BLOB      !TEXT      !CLOB        !
    // REF           !BLOB      !?         !REF         !
    // --------------+----------+----------+------------+
    public String getColumnBaseType(String columnType) {
        int n = columnType.indexOf('(');
        if (n != -1) {
            columnType = columnType.substring(0, n - 1);
        }

        switch (getTypeDB()) {
            case MSSQL: {
                if (StringUtils.strContains(columnType, new String[]{"BIT", "TINYINT", "SMALLINT", "INT", "BIGINT", "FLOAT", "REAL", "NUMERIC", "DECIMAL"})) {
                    return "NUMBER";
                }
                if (StringUtils.strContains(columnType, new String[]{"CHAR", "VARCHAR", "BINARY", "VARBINARY", "TIMESTAMP"})) {
                    return "CHAR";
                }
                if (StringUtils.strContains(columnType, new String[]{"DATETIME"})) {
                    return "DATE";
                }
            }
            case ORACLE: {
                if (StringUtils.strContains(columnType, new String[]{"NUMBER", "NUMERIC", "DECIMAL"})) {
                    return "NUMBER";
                }
                if (StringUtils.strContains(columnType, new String[]{"CHAR", "VARCHAR", "VARCHAR2"})) {
                    return "CHAR";
                }
                if (StringUtils.strContains(columnType, new String[]{"DATE", "TIMESTAMP"})) {
                    return "DATE";
                }
            }
        }
        return "BLOB";
    }

    public String columnTypeToTypeName(int sourceDBType, int columnType, int size, int precision, int scale, String defaultColumnTypeName) {
        switch (getTypeDB()) {
            case MSSQL: {
                switch (columnType) {
                    case java.sql.Types.BIT:
                        return "BIT";
                    case java.sql.Types.TINYINT:
                        return "TINYINT";
                    case java.sql.Types.SMALLINT:
                        return "SMALLINT";
                    case java.sql.Types.INTEGER:
                        return "INT";
                    case java.sql.Types.BIGINT:
                        return "BIGINT";
                    case java.sql.Types.FLOAT:
                        return "FLOAT";
                    case java.sql.Types.REAL:
                        return "REAL";
                    case java.sql.Types.DOUBLE:
                        return "FLOAT";
                    case java.sql.Types.NUMERIC:
                        return "NUMERIC(" + precision + ", " + scale + ")";
                    case java.sql.Types.DECIMAL:
                        return "DECIMAL(" + precision + ", " + scale + ")";
                    case java.sql.Types.CHAR:
                        return "CHAR(" + size + ")";
                    case java.sql.Types.VARCHAR:
                        return "VARCHAR(" + size + ")";
                    case java.sql.Types.LONGVARCHAR:
                        return "VARCHAR(" + size + ")";
                    case java.sql.Types.DATE:
                        return "DATETIME";
                    case java.sql.Types.TIME:
                        return "DATETIME";
                    case java.sql.Types.TIMESTAMP:
                        return "TIMESTAMP";
                    case java.sql.Types.BINARY:
                        return "BINARY(" + size + ")";
                    case java.sql.Types.VARBINARY:
                        return "VARBINARY(" + size + ")";
                    case java.sql.Types.LONGVARBINARY:
                        return "IMAGE";
                    case java.sql.Types.JAVA_OBJECT:
                        return defaultColumnTypeName;
                    case java.sql.Types.STRUCT:
                        return defaultColumnTypeName;
                    case java.sql.Types.ARRAY:
                        return defaultColumnTypeName;
                    case java.sql.Types.BLOB:
                        return "IMAGE";
                    case java.sql.Types.CLOB:
                        return "TEXT";
                    case java.sql.Types.REF:
                        return defaultColumnTypeName;
                    case java.sql.Types.BOOLEAN:
                        return "BIT";
                }
            }
            case ORACLE: {
                switch (columnType) {
                    case java.sql.Types.BIT:
                        return "NUMBER(1)";
                    case java.sql.Types.TINYINT:
                        return "NUMBER(3)";
                    case java.sql.Types.SMALLINT:
                        return "NUMBER(5)";
                    case java.sql.Types.INTEGER:
                        return "NUMBER";
                    case java.sql.Types.BIGINT:
                        return "NUMBER";
                    case java.sql.Types.FLOAT:
                        return "NUMBER";
                    case java.sql.Types.REAL:
                        return "NUMBER";
                    case java.sql.Types.DOUBLE:
                        return "NUMBER";
                    case java.sql.Types.NUMERIC:
                        return (precision == 0 ? "NUMBER" : "NUMERIC(" + precision + ", " + scale + ")");
                    case java.sql.Types.DECIMAL:
                        return "DECIMAL(" + precision + ", " + scale + ")";
                    case java.sql.Types.CHAR:
                        return "CHAR(" + size + ")";
                    case java.sql.Types.VARCHAR:
                        return "VARCHAR2(" + size + ")";
                    case java.sql.Types.LONGVARCHAR:
                        return "CLOB";
                    case java.sql.Types.DATE:
                        return "DATE";
                    case java.sql.Types.TIME:
                        return "DATE";
                    case java.sql.Types.TIMESTAMP:
                        return "TIMESTAMP";
                    case java.sql.Types.BINARY:
                        return "VARCHAR2(" + size + ")";
                    case java.sql.Types.VARBINARY:
                        return "VARCHAR2(" + size + ")";
                    case java.sql.Types.LONGVARBINARY:
                        return "BLOB";
                    case java.sql.Types.JAVA_OBJECT:
                        return "JAVA_OBJECT";
                    case java.sql.Types.STRUCT:
                        return "STRUCT";
                    case java.sql.Types.ARRAY:
                        return "ARRAY";
                    case java.sql.Types.BLOB:
                        return "BLOB";
                    case java.sql.Types.CLOB:
                        return "CLOB";
                    case java.sql.Types.REF:
                        return "REF";
                    case java.sql.Types.BOOLEAN:
                        return "NUMBER(1)";
                }
            }
        }
        return defaultColumnTypeName;
    }

    private String getTime() {
        java.util.Date t = new GregorianCalendar().getTime();
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
        return df.format(t);
    }

    private void write(String text) {
        if (logMode == LOGONLOG) {
            databaseEvents.onLog(text);
        } else if (logMode == LOGTOFILE) {
            try {
                text = text + "\n";
                log.write(text.getBytes());
            } catch (IOException e) {
                logger.severe("Can't write to log file, log is stopped");
                logMode = LOGNONE;
            }
        }
    }

    private Connection getConnection() throws Exception {
        if (connection != null) {
            return connection;
        }
        if (connectionFailed) {
            throw new Exception();
        }

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(URL, userName, password);
            write(getTime() + " connect to database '" + URL + "'");
            return connection;
        } catch (ClassNotFoundException e) {
            write("Driver '" + driver + "' not found");
            connectionFailed = true;
            e.printStackTrace();
        } catch (SQLException e) {
            write("Can't connect to database (wrong username or password)");
            connectionFailed = true;
            e.printStackTrace();
        }
        throw new Exception();
    }

    public Database(String aDriver, String aURL, String aSchema, String aUserName, String aPassword) {
        URL = aURL;
        driver = aDriver;
        schema = aSchema;
        userName = aUserName;
        password = aPassword;
    }

    public void setOnLogEvent(DatabaseInterface aDatabaseEvents) {
        databaseEvents = aDatabaseEvents;
        logMode = LOGONLOG;
    }

    public void setLogFileName(String pathName, String fileName) {
        File path = new File(pathName);
        try {
            path.mkdirs();
            log = new FileOutputStream(pathName + "/" + fileName);
            logMode = LOGTOFILE;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't create database log file '" + fileName, e);
        }
    }

    public void close() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
            write(getTime() + " disconnect, " + counter + " query(s)");
        } catch (SQLException e) {
            write("Can't disconnect database");
            e.printStackTrace();
        }
    }

    public String getSchema() {
        return schema;
    }

    public String getUserName() {
        return userName;
    }

    public String getURL() {
        return URL;
    }

    public boolean providerEquals(Database database) {
        return driver.equals(database.driver);
    }

    private void writeErrorMessage(SQLException e, String query, Object[] paramValueList, DataSetInterface di) {
        String msg = getTime() + " error exception '" + e.getMessage().trim() + "'";
        if (di != null) {
            msg = msg + " at '" + di.getClass() + "'";
        }
        write(msg);
        write("  " + query);
        if (paramValueList != null) {
            String params = "";
            for (int i = 0; i < paramValueList.length; i++) {
                if (i > 0) {
                    params = params + ", ";
                }
                params = params + (i + 1) + "='" + paramValueList[i].toString() + "'";
                query = StringUtils.replaceAll(query, "?", paramValueList[i].toString());
            }
            write("  " + params);
            write("  " + query);
        }
        write("");
        e.printStackTrace();
    }

    public void readDataSet(String query, Object[] paramValueList, DataSetInterface di, int fetchSize) {
        counter++;
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            try {
                if (paramValueList != null) {
                    for (int i = 0; i < paramValueList.length; i++) {
                        ps.setString(i + 1, paramValueList[i].toString());
                    }
                }

                if (fetchSize != -1) {
                    ps.setFetchSize(fetchSize);
                }

                ResultSet rs = ps.executeQuery();

                if (rs != null) {
                    try {
                        while (rs.next()) {
                            if (!di.onReadRecord(rs)) {
                                break;
                            }
                        }
                    } finally {
                        rs.close();
                    }
                }
            } finally {
                ps.close();
            }
        } catch (SQLException e) {
            writeErrorMessage(e, query, paramValueList, di);
        } catch (Exception e) {
            return;
        }
    }

    public void readDataSet(String query, Object[] paramValueList, DataSetInterface di) {
        readDataSet(query, paramValueList, di, -1);
    }

    public boolean readRecord(String query, Object[] paramValueList, ArrayList record) {
        record.clear();

        counter++;
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            try {
                if (paramValueList != null) {
                    for (int i = 0; i < paramValueList.length; i++) {
                        ps.setString(i + 1, paramValueList[i].toString());
                    }
                }

                ps.setFetchSize(1);

                ResultSet rs = null;
                try {
                    rs = ps.executeQuery();
                } catch (SQLException e) {
                    ps.close();
                    return false;
                }

                if (rs != null) {
                    try {
                        if (rs.next()) {
                            ResultSetMetaData metaData = rs.getMetaData();
                            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                                record.add(rs.getObject(i));
                            }
                        }
                    } finally {
                        rs.close();
                    }
                }
            } finally {
                ps.close();
            }
        } catch (SQLException e) {
            writeErrorMessage(e, query, paramValueList, null);
        } catch (Exception e) {
        }

        return record.size() > 0;
    }

    public Object readFieldValue(String query, Object[] paramValueList) {
        counter++;
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            try {
                if (paramValueList != null) {
                    for (int i = 0; i < paramValueList.length; i++) {
                        ps.setString(i + 1, paramValueList[i].toString());
                    }
                }

                ps.setFetchSize(1);

                ResultSet rs = null;
                try {
                    rs = ps.executeQuery();
                } catch (SQLException e) {
                    ps.close();
                    return null;
                }

                if (rs != null) {
                    try {
                        if (rs.next()) {
                            return rs.getObject(1);
                        }
                    } finally {
                        rs.close();
                    }
                }
            } finally {
                ps.close();
            }
        } catch (SQLException e) {
            writeErrorMessage(e, query, paramValueList, null);
        } catch (Exception e) {
        }

        return null;
    }

    public ResultSet openDataSet(String query, Object[] paramValueList) throws SQLException {
        if (singleDataSet != null) {
            try {
                singleDataSet.close();
            } catch (SQLException e) {
                writeErrorMessage(e, query, paramValueList, null);
            }
            try {
                singlePreparedStatement.close();
            } catch (SQLException e) {
                writeErrorMessage(e, query, paramValueList, null);
            }
        }

        counter++;
        try {
            singlePreparedStatement = getConnection().prepareStatement(query);
            if (paramValueList != null) {
                for (int i = 0; i < paramValueList.length; i++) {
                    singlePreparedStatement.setString(i + 1, (String) paramValueList[i]);
                }
            }
            singleDataSet = singlePreparedStatement.executeQuery();
            return singleDataSet;
        } catch (SQLException e) {
            writeErrorMessage(e, query, paramValueList, null);
            throw e;
        } catch (Exception e) {
            throw new SQLException("Can't connect to database");
        }
    }

    public boolean executeCommand(String code) throws SQLException {
        counter++;
        try {
            Statement stat = getConnection().createStatement();
            return stat.execute(code);
        } catch (SQLException e) {
            writeErrorMessage(e, code, null, null);
            throw e;
        } catch (Exception e) {
            throw new SQLException("Can't connect to database");
        }
    }

    public boolean executeBatch(ArrayList commandList) throws SQLException {
        for (int i = 0; i < commandList.size(); i++) {
            executeCommand((String) commandList.get(i));
        }
        return true;
    }

    public PreparedStatement getPreparedStatement(String code) throws SQLException {
        try {
            return getConnection().prepareStatement(code);
        } catch (SQLException e) {
            writeErrorMessage(e, code, null, null);
            throw e;
        } catch (Exception e) {
            throw new SQLException("Can't connect to database");
        }
    }

    public boolean isTableExists(String tableName) throws SQLException {
        try {
            DatabaseMetaData metaData = getConnection().getMetaData();
            ResultSet rs = metaData.getTables(null, userName, tableName.toUpperCase(), null);
            try {
                return rs.next();
            } finally {
                rs.close();
            }
        } catch (SQLException e) {
            writeErrorMessage(e, "Table '" + tableName + "' not found", null, null);
            throw e;
        } catch (Exception e) {
            throw new SQLException("Can't connect to database");
        }
    }
}
