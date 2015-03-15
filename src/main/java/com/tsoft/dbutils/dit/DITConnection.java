package com.tsoft.dbutils.dit;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import com.tsoft.dbutils.lib.db.*;
import com.tsoft.dbutils.dit.driver.*;

public class DITConnection {
    private String name;
    private String driver;                  // "oracle.jdbc.OracleDriver"
    private String url;                     // "jdbc:oracle:thin:@ASU:1521:ORADB"
    private String userName;
    private String password;
    private String schema;
    private String DITDriver;
    
    private ArrayList generateList = new ArrayList();

    public DITConnection(Element root) throws Exception {
        name = root.getAttribute("name");
        if (name.length() == 0) {
            throw new Exception("'connection' tag must have the 'name' attribute");
        }

        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                Node textNode = element.getFirstChild();
                String text = textNode.getNodeValue().trim();

                if (element.getTagName().equals("driver")) {
                    driver = text;
                } else if (element.getTagName().equals("url")) {
                    url = text;
                } else if (element.getTagName().equals("username")) {
                    userName = text;
                } else if (element.getTagName().equals("password")) {
                    password = text;
                } else if (element.getTagName().equals("schema")) {
                    schema = text;
                } else if (element.getTagName().equals("DITDriver")) {
                    DITDriver = text;
                } else if (element.getTagName().equals("generate")) {
                    generate(element);
                } else {
                    throw new Exception("Unknown element '" + element.getTagName() + "' the 'connection' tag");
                }
            }
        }
    }

    private void generate(Element root) throws Exception {
        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (element.getTagName().equals("HTML")) {
                    generateList.add(new DITGenerate(element));
                } else if (element.getTagName().equals("XML")) {
                    generateList.add(new DITGenerate(element));
                } else if (element.getTagName().equals("create")) {
                    generateList.add(new DITGenerate(element));
                } else if (element.getTagName().equals("insert")) {
                    generateList.add(new DITGenerate(element));
                } else {
                    throw new Exception("Unknown element '" + element.getTagName() + "' the 'generate' tag");
                }
            }
        }
    }

    public void execute() {
        try {
            Database database = new Database(driver, url, schema, userName, password);
            try {
                DITDriver ditd = (DITDriver) Class.forName(DITDriver).newInstance();
                ditd.setDatabase(database);

                String path = new File("").getAbsolutePath() + "/" + ditd.getUserName();
                database.setLogFileName(path, "database.log");

                ditd.open();

                for (int i = 0; i < generateList.size(); i++) {
                    DITGenerate generate = (DITGenerate) generateList.get(i);
                    generate.setPath(path);
                    generate.execute(ditd);
                }
            } finally {
                database.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
