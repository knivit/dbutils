package com.tsoft.dbutils.dmt;

import java.rmi.Naming;

import org.w3c.dom.*;
import com.tsoft.dbutils.dmt.driver.*;

public class DMTModel {
    private String name;
    private String connectionName;
    private DMTConnection connection;
    private String driverName;
    private DMTDriver driver;      // 'tsoft.database.dmt.driver.DIT'

    public DMTModel(Element root) throws Exception {
        name = root.getAttribute("name");
        if (name.length() == 0) {
            throw new Exception("'model' tag must have the 'name' attribute");
        }

        driverName = root.getAttribute("DMTDriver");
        if (name.length() == 0) {
            throw new Exception("'model' tag must have the 'DMTDriver' attribute");
        }

        connectionName = root.getAttribute("connection");
        if (name.length() == 0) {
            throw new Exception("'model' tag must have the 'connection' attribute");
        }

        driver = (DMTDriver) Class.forName(driverName).newInstance();
        driver.load(root);
    }

    public String getName() {
        return name;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnection(DMTConnection aConnection) {
        connection = aConnection;
    }

    public void execute() throws Exception {
        if ((driver != null) && (connection != null)) {
            DMTObject model = driver.execute(connection.getDatabase());
            if (model != null) {
                Naming.rebind(model.getName(), model);
            } else {
                throw new Exception("Can't create data mining object");
            }
        }
    }
}
