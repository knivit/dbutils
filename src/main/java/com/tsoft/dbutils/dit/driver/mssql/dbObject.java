package com.tsoft.dbutils.dit.driver.mssql;

import com.tsoft.dbutils.dit.driver.dbObjectList;

abstract class dbObject extends com.tsoft.dbutils.dit.driver.dbObject {
    public int ID;		 	  // SYSOBJECTS.ID - SYSCOLUMNS.COLID

    public dbObject(dbObjectList aList, int aID, String aObjectName) {
        super(aList, aObjectName);
        ID = aID;
    }
}
