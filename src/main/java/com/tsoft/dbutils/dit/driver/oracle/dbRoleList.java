package com.tsoft.dbutils.dit.driver.oracle;

import com.tsoft.dbutils.dit.driver.dbObjectList;
import java.sql.*;
import com.tsoft.dbutils.lib.db.*;

class dbRoleList extends dbObjectList<dbRole> implements DataSetInterface {
    public void read(Database aDatabase) {
        database = aDatabase;
        database.readDataSet("SELECT USERNAME, GRANTED_ROLE FROM USER_ROLE_PRIVS", null, this);
    }

    @Override
    public boolean onReadRecord(ResultSet rs) throws SQLException {
        dbRole r = new dbRole(this, rs.getString("GRANTED_ROLE"));
        r.setUserName(rs.getString("USERNAME"));
        add(r);
        return true;
    }

    public String getXML() {
        String s = "<roles>\n";
        for (int i = 0; i < size(); i++) {
            dbRole r = get(i);
            s += r.getXML();
        }
        s += "</roles>\n";
        return s;
    }
}
