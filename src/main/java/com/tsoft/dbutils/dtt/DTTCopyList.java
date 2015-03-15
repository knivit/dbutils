package com.tsoft.dbutils.dtt;

import java.util.*;

public class DTTCopyList extends ArrayList<DTTCopy> {
    public DTTCopy findByFromName(String columnName) {
        for (int i = 0; i < size(); i++) {
            DTTCopy copy = get(i);
            if (copy.getFromName().toLowerCase().equals(columnName.toLowerCase())) {
                return copy;
            }
        }
        return null;
    }
}
