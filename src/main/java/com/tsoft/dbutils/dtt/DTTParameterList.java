package com.tsoft.dbutils.dtt;

import java.util.*;

public class DTTParameterList extends ArrayList<DTTParameter> {
    public boolean resolveConnections(DTTConnectionList connectionList) {
        for (int i = 0; i < size(); i++) {
            DTTParameter param = get(i);
            if (!param.resolveConnection(connectionList)) {
                return false;
            }
        }
        return true;
    }

    public String replaceParams(String str) {
        for (int i = 0; i < size(); i++) {
            DTTParameter param = get(i);
            str = param.replaceValue(str, this);
        }
        return str;
    }
}
