package com.tsoft.dbutils.dit.driver;

import com.tsoft.dbutils.lib.db.*;

/**
 * 1) Oracle: tsoft.database.adt.driver.oracle.OracleDriver
 * 2) Microsoft SQL Server: tsoft.database.adt.driver.oracle.MSSQLDriver
 */
public interface DITDriver {
    public String getDriverName();

    public String getServerName();

    public String getDatabaseName();

    public String getUserName();

    public void setDatabase(Database aDatabase);

    public void open();

    public void setUsed(DITDriverObject object);

    public String getHTMLInfo();

    public String getHTMLFields();

    public String getXML();

    public String getCreateScript();

    public String getInsertScript();
}
