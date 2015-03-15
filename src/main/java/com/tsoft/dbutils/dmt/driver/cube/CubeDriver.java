package com.tsoft.dbutils.dmt.driver.cube;

import java.rmi.RemoteException;

import java.rmi.server.UnicastRemoteObject;

import org.w3c.dom.*;
import com.tsoft.dbutils.lib.db.*;
import com.tsoft.dbutils.dmt.driver.*;

public class CubeDriver extends UnicastRemoteObject implements DMTDriver, DMTObject {
    public CubeDriver() throws RemoteException {
    }

    @Override
    public void load(Element root) throws Exception {
        NodeList node = root.getChildNodes();
        for (int i = 0; i < node.getLength(); i++) {
            Node child = node.item(i);
            if (child instanceof Element) {
                Element element = (Element) child;
                if (element.getTagName().equals("")) {
                }
            }
        }
    }

    @Override
    public String getName() {
        return "Cube";
    }

    @Override
    public DMTObject execute(Database aDatabase) throws Exception {
        return null;
    }
}
