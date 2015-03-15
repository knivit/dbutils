package com.tsoft.dbutils.dmt.driver;

import org.w3c.dom.Element;
import com.tsoft.dbutils.lib.db.*;

public interface DMTDriver {
    public void load(Element root) throws Exception;

    public DMTObject execute(Database aDatabase) throws Exception;
}
