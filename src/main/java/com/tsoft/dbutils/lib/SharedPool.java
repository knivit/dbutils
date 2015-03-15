package com.tsoft.dbutils.lib;

import java.util.*;

public class SharedPool {

    private static Properties props = new Properties();

    public static void set(String propName, String propValue) {
        props.setProperty(propName, propValue);
    }
}
