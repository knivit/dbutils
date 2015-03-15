package com.tsoft.dbutils.dmt.driver.dit;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import org.w3c.dom.*;

import com.tsoft.dbutils.lib.db.*;
import com.tsoft.dbutils.dmt.driver.*;
import com.tsoft.dbutils.lib.metadb.*;

public class DITDriver extends UnicastRemoteObject implements DMTDriver, DMTObject {
    private String DITFileName;
    private Database database;
    private MDDatabase metabase;

    public DITDriver() throws RemoteException {
    }

    @Override
    public void load(Element root) throws Exception {
        NodeList node = root.getChildNodes();
        for (int i = 0; i < node.getLength(); i++) {
            Node child = node.item(i);
            if (child instanceof Element) {
                Element element = (Element) child;
                if (element.getTagName().equals("DIT")) {
                    DITFileName = element.getAttribute("file");
                    if (DITFileName.length() == 0) {
                        throw new Exception("'DIT' tag must have the 'file' attribute");
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return getClass().getName() + "." + DITFileName;
    }

    @Override
    public DMTObject execute(Database aDatabase) throws Exception {
        database = aDatabase;
        metabase = new MDDatabase();
        if (metabase.open(DITFileName)) {
            return this;
        }
        return null;
    }
}
