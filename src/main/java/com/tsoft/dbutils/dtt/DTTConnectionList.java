package com.tsoft.dbutils.dtt;

import java.util.*;

public class DTTConnectionList extends ArrayList {
    public DTTConnection find(String name) {
        for (int i = 0; i < size(); i++) {
            DTTConnection conn = (DTTConnection) get(i);
            if (conn.getName().equals(name)) {
                conn.setUsed();
                return conn;
            }
        }
        return null;
    }
}
