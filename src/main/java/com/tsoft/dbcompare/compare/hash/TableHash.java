package com.tsoft.dbcompare.compare.hash;

import com.tsoft.dbcompare.compare.ddl.ColumnDdl;
import com.tsoft.dbcompare.compare.ddl.ColumnType;
import com.tsoft.dbcompare.compare.ddl.TableDdl;
import com.tsoft.dbcompare.connection.DatabaseConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class TableHash {
    private DatabaseConnection databaseConnection;
    private ArrayList<ColumnHash> columnList = new ArrayList<ColumnHash>();

    public TableHash(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void populateColumns(TableDdl tableDdl, String whereClause) {
        buildHashColumnList(tableDdl);
        if (columnList.isEmpty()) {
            return;
        }

        calculateHashes(tableDdl.getName(), whereClause);
    }

    public ArrayList<ColumnHash> getColumnList() {
        return columnList;
    }

    private void calculateHashes(String tableName, String whereClause) {
        String sql = buildSql(tableName, whereClause);
        databaseConnection.getLogger().log(Level.FINE, sql);

        JdbcTemplate jdbcTemplate = databaseConnection.getJdbcTemplate();
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
        Assert.isTrue(resultList.size() == 1);

        Map<String, Object> resultRow = resultList.get(0);
        if (resultRow.keySet().size() != columnList.size()) {
            databaseConnection.getLogger().log(Level.SEVERE, "Source columns: " + columnList.toString());
            databaseConnection.getLogger().log(Level.SEVERE, "Destination columns: " + resultRow.keySet().toString());
            Assert.isTrue(resultRow.keySet().size() == columnList.size());
        }

        for (ColumnHash columnHash : columnList) {
            Assert.isTrue(resultRow.containsKey(columnHash.getQueryColumnName()));
            Object value = resultRow.get(columnHash.getQueryColumnName());
            columnHash.setValue(value);
        }
    }

    private String buildSql(String tableName, String whereClause) {
        StringBuilder buf = new StringBuilder("select ");
        boolean isCommaNeeded = false;
        for (ColumnHash columnHash : columnList) {
            if (isCommaNeeded) buf.append(", ");
            buf.append(columnHash.getColumnName());
            buf.append(" as ").append(columnHash.getQueryColumnName());
            isCommaNeeded = true;
        }

        buf.append(" from ").append(tableName);

        if (whereClause != null && !whereClause.isEmpty()) {
            buf.append(" where ").append(whereClause);
        }
        return buf.toString();
    }

    private void buildHashColumnList(TableDdl tableDdl) {
        int columnIndex = 0;
        for (ColumnDdl columnDdl : tableDdl.getColumnList()) {
            if (columnDdl.getColumnType() == ColumnType.NUMBER) {
                ColumnHash columnHashMax = new ColumnHash(columnDdl.getName(), HashType.MAX, columnIndex);
                columnList.add(columnHashMax);

                ColumnHash columnHashMin = new ColumnHash(columnDdl.getName(), HashType.MIN, columnIndex);
                columnList.add(columnHashMin);

                ColumnHash columnHashSum = new ColumnHash(columnDdl.getName(), HashType.SUM, columnIndex);
                columnList.add(columnHashSum);
                continue;
            }

            if (columnDdl.getColumnType().isDate()) {
                ColumnHash columnHashMax = new ColumnHash(columnDdl.getName(), HashType.MAX, columnIndex);
                columnList.add(columnHashMax);

                ColumnHash columnHashMin = new ColumnHash(columnDdl.getName(), HashType.MIN, columnIndex);
                columnList.add(columnHashMin);
                continue;
            }
            columnIndex ++;
        }
    }
}
