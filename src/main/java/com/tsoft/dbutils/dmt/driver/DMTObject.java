package com.tsoft.dbutils.dmt.driver;

import java.rmi.*;

/**
 * Mining Model Object
 */
public interface DMTObject extends Remote {
    public String getName() throws RemoteException;
}
