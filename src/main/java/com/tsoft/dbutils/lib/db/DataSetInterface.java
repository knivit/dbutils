package com.tsoft.dbutils.lib.db;

import java.sql.*;

public interface DataSetInterface {
    public boolean onReadRecord(ResultSet rs) throws SQLException;
}
