package com.tsoft.dbutils.dtt;

import java.sql.*;
import java.util.*;
import org.w3c.dom.*;
import com.tsoft.dbutils.lib.db.*;

public class DTTTransformation implements DataSetInterface {
    private String name;
    private String fromConnectionName;
    private DTTConnection fromConnection;
    private String toConnectionName;
    private DTTConnection toConnection;
    private DTTParameterList paramList = new DTTParameterList();
    private DTTCodeList codeList = new DTTCodeList();
    private DTTCopyList copyList = new DTTCopyList();
    private String fromDataSetName;
    private String toDataSetName;
    private String commentToDataSet;
    private boolean create;
    private boolean append;
    Database fromDatabase;
    Database toDatabase;
    private String wrongMessage;

    public DTTTransformation(Element root) {
        name = root.getAttribute("name");
        if (name.length() == 0) {
            wrongMessage = "Element 'transformation' has wrong 'name'";
            return;
        }

        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (element.getTagName().equals("connection")) {
                    fromConnectionName = element.getAttribute("from");
                    toConnectionName = element.getAttribute("to");
                } else if (element.getTagName().equals("parameter")) {
                    paramList.add(new DTTParameter(element));
                } else if (element.getTagName().equals("code")) {
                    codeList.add(new DTTCode(element));
                } else if (element.getTagName().equals("dataset")) {
                    fromDataSetName = element.getAttribute("from");
                    toDataSetName = element.getAttribute("to");
                    commentToDataSet = element.getAttribute("comment");
                    create = element.getAttribute("create").length() > 0;
                    append = element.getAttribute("append").length() > 0;
                } else if (element.getTagName().equals("copy")) {
                    copyList.add(new DTTCopy(element));
                } else {
                    wrongMessage = "Unknown element '" + element.getTagName() + "' 'connection' tag";
                    break;
                }
            }
        }
    }

    public String getWrongMessage() {
        return wrongMessage;
    }

    public String getName() {
        return name;
    }

    public boolean resolveConnections(DTTConnectionList connectionList) {
        if (!paramList.resolveConnections(connectionList)) {
            return false;
        }

        if (!codeList.resolveConnections(connectionList)) {
            return false;
        }

        fromConnection = connectionList.find(fromConnectionName);
        toConnection = connectionList.find(toConnectionName);

        return (fromConnection != null) && (toConnection != null);
    }

    public int execute() {
        try {
            if (!codeList.execute()) {
                return -1;
            }

            String fromQuery;
            if (fromDataSetName.length() > 7 && fromDataSetName.substring(0, 7).toUpperCase().equals("SELECT ")) {
                fromQuery = fromDataSetName;
            } else {
                if (copyList.size() == 0) {
                    fromQuery = "SELECT * ";
                } else {
                    fromQuery = "SELECT ";
                    for (int i = 0; i < copyList.size(); i++) {
                        DTTCopy from = (DTTCopy) copyList.get(i);
                        if (i > 0) {
                            fromQuery += ", ";
                        }
                        fromQuery += from.getFromName();
                    }
                }
                fromQuery += " FROM " + fromDataSetName;
            }

            fromQuery = paramList.replaceParams(fromQuery);

            fromDatabase = fromConnection.getDatabase();
            toDatabase = toConnection.getDatabase();

            fromDatabase.readDataSet(fromQuery, null, this);
            if (ps != null) {
                ps.close();
            }
            return recordCount;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private PreparedStatement ps;
    private int recordCount;
    ResultSetMetaData metaData;

    public boolean onReadRecord(ResultSet rs) throws SQLException {
        if (ps == null) {
            createTable(rs);
        }

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            ps.setObject(i, rs.getObject(i));
        }

        ps.execute();
        recordCount++;
        return true;
    }

    private void createTable(ResultSet rs) throws SQLException {
        recordCount = 0;
        String createFields = "";
        String insertFields = "";                  // FIELDS-list for INSERT
        String insertValues = "";                  // VALUES-list for INSERT
        String insertData = "";                    // INSERT
        String primaryFields = "";
        ArrayList commentFields = new ArrayList();

        metaData = rs.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (i > 1) {
                createFields += ", ";
                insertFields += ", ";
                insertValues += ", ";
            }

            String columnType = null;
            String columnComment = null;
            String columnName = metaData.getColumnName(i);
            DTTCopy copy = copyList.findByFromName(columnName);
            if (copy != null) {
                if (copy.getToName() != null) {
                    columnName = copy.getToName();
                }
                columnType = copy.getType();
                columnComment = copy.getComment();
            }

            if (columnType == null) {
                columnType = metaData.getColumnTypeName(i);
                int size = metaData.getColumnDisplaySize(i);
                int precision = metaData.getPrecision(i);
                int scale = metaData.getScale(i);

                columnType = toDatabase.columnTypeToTypeName(fromDatabase.getTypeDB(), metaData.getColumnType(i), size, precision, scale, columnType);
            }

            if (columnComment != null) {
                commentFields.add("COMMENT ON COLUMN " + toDataSetName + "." + columnName + " IS '" + columnComment + "'");
            }

            createFields += columnName + " " + columnType;
            if (copy.getPrimary()) {
                primaryFields += (primaryFields.length() == 0 ? columnName : ", " + columnName);
            }
            insertFields += columnName;
            insertValues += "?";
        }
        insertData = "INSERT INTO " + toDataSetName + " (" + insertFields + ") VALUES (" + insertValues + ")";

        if (create) {
            if (toDatabase.isTableExists(toDataSetName)) {
                toDatabase.executeCommand("DROP TABLE " + toDataSetName);
            }

            toDatabase.executeCommand("CREATE TABLE " + toDataSetName + " (" + createFields + ")");

            if (primaryFields.length() > 0) {
                toDatabase.executeCommand("ALTER TABLE " + toDataSetName + " ADD CONSTRAINT PK_" + toDataSetName + " PRIMARY KEY(" + primaryFields + ")");
            }

            if (commentToDataSet != null) {
                toDatabase.executeCommand("COMMENT ON TABLE " + toDataSetName + " IS '" + commentToDataSet + "'");
            }

            toDatabase.executeBatch(commentFields);
        } else if (!append) {
            toDatabase.executeCommand("DELETE FROM " + toDataSetName);
        }

        ps = toDatabase.getPreparedStatement(insertData);
    }
}
