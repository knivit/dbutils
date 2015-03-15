package com.tsoft.dbcompare.config;

import java.util.List;

public class CompareConfig {
    private ConnectionConfig sourceConnectionConfig;

    private ConnectionConfig destinationConnectionConfig;

    private List<TableConfig> tableConfigList;

    public ConnectionConfig getSourceConnectionConfig() {
        return sourceConnectionConfig;
    }

    public void setSourceConnectionConfig(ConnectionConfig sourceConnectionConfig) {
        this.sourceConnectionConfig = sourceConnectionConfig;
    }

    public ConnectionConfig getDestinationConnectionConfig() {
        return destinationConnectionConfig;
    }

    public void setDestinationConnectionConfig(ConnectionConfig destinationConnectionConfig) {
        this.destinationConnectionConfig = destinationConnectionConfig;
    }

    public List<TableConfig> getTableConfigList() {
        return tableConfigList;
    }

    public void setTableConfigList(List<TableConfig> tableConfigList) {
        this.tableConfigList = tableConfigList;
    }
}
