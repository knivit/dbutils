package com.tsoft.dbutils.dtt;

import java.util.*;
import java.sql.*;

public class DTTCodeList extends ArrayList {
    public boolean resolveConnections(DTTConnectionList connectionList) {
        for (int i = 0; i < size(); i++) {
            DTTCode code = (DTTCode) get(i);
            if (!code.resolveConnection(connectionList)) {
                return false;
            }
        }
        return true;
    }

    public boolean execute() throws SQLException {
        for (int i = 0; i < size(); i++) {
            DTTCode code = (DTTCode) get(i);
            if (!code.execute()) {
                return false;
            }
        }
        return true;
    }
}
