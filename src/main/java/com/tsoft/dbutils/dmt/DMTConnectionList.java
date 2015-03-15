package com.tsoft.dbutils.dmt;

import java.util.ArrayList;

public class DMTConnectionList extends ArrayList {
    public DMTConnection find(String name) {
        for (int i = 0; i < size(); i++) {
            DMTConnection conn = (DMTConnection) get(i);
            if (conn.getName().equalsIgnoreCase(name)) {
                return conn;
            }
        }
        return null;
    }
}
