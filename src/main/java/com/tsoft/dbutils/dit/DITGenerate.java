package com.tsoft.dbutils.dit;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.w3c.dom.*;
import com.tsoft.dbutils.dit.driver.*;

public class DITGenerate implements DITDriverObject {
    private static final int HTML = 1;
    private static final int XML = 2;
    private static final int CREATE = 3;
    private static final int INSERT = 4;
    private String path = "";
    private int generateType;                         // HTML .. INSERT

    private ArrayList generateList = new ArrayList();

    public DITGenerate(Element root) throws Exception {
        if (root.getTagName().equals("HTML")) {
            generateType = HTML;
        } else if (root.getTagName().equals("XML")) {
            generateType = XML;
        } else if (root.getTagName().equals("create")) {
            generateType = CREATE;
        } else if (root.getTagName().equals("insert")) {
            generateType = INSERT;
        } else {
            throw new Exception("Unknown element '" + root.getTagName() + "' for 'generate'");
        }

        createGenerateList(root);
    }

    private void createGenerateList(Element root) throws Exception {
        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                int operation;
                Element element = (Element) node;
                if (element.getTagName().equals("include")) {
                    operation = DITGenerateObject.INCLUDE;
                } else if (element.getTagName().equals("exclude")) {
                    operation = DITGenerateObject.EXCLUDE;
                } else {
                    throw new Exception("Unknown element '" + element.getTagName() + "' for '" + root.getTagName() + "'");
                }

                String namePattern = element.getAttribute("name");
                if (namePattern.length() == 0) {
                    throw new Exception("Unknown element '" + element.getTagName() + "' for 'name'");
                }

                String categoryPattern = element.getAttribute("category");
                if (categoryPattern.length() == 0) {
                    throw new Exception("Unknown element '" + element.getTagName() + "' for 'category'");
                }

                generateList.add(new DITGenerateObject(namePattern, categoryPattern, operation));
            }
        }
    }

    public void setPath(String aPath) {
        path = aPath;
    }

    public void execute(DITDriver driver) throws IOException, SQLException {
        driver.setUsed(this);

        switch (generateType) {
            case HTML: {
                System.out.println("Generate HTML info");
                FileOutputStream out = new FileOutputStream(path + "/info.htm");
                out.write(driver.getHTMLInfo().getBytes());
                out.close();

                System.out.println("Generate HTML fields");
                out = new FileOutputStream(path + "/fields.htm");
                out.write(driver.getHTMLFields().getBytes());
                out.close();
                break;
            }
            case XML: {
                System.out.println("Generate XML");
                FileOutputStream out = new FileOutputStream(path + "/object.xml");
                out.write(driver.getXML().getBytes());
                out.close();
                break;
            }
            case CREATE: {
                System.out.println("Generate CREATE object script");
                FileOutputStream out = new FileOutputStream(path + "/create.sql");
                out.write(driver.getCreateScript().getBytes());
                out.close();
                break;
            }
            case INSERT: {
                System.out.println("Generate INSERT data script");
                FileOutputStream out = new FileOutputStream(path + "/insert.sql");
                out.write(driver.getInsertScript().getBytes());
                out.close();
                break;
            }
        }
    }

    @Override
    public boolean skipObject(String name, String category) {
        boolean result = false;
        for (int i = 0; i < generateList.size(); i++) {
            DITGenerateObject o = (DITGenerateObject) generateList.get(i);
            if (o.isCategoryMatches(category) && o.isNameMatches(name)) {
                result = o.isSkip();
            }
        }
        return result;
    }
}
