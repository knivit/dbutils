package com.tsoft.dbutils.dtt;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import com.tsoft.dbutils.lib.*;
import org.xml.sax.SAXException;

/**
 * Data Transformation Tool
 */
public class DTT {
    private DTTConnectionList connectionList = new DTTConnectionList();
    private ArrayList transformationList = new ArrayList();

    public boolean load(String fileName) {
        File file = new File(fileName);
        System.out.println("Load file '" + fileName + "'");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            Element root = doc.getDocumentElement();

            NodeList node = root.getChildNodes();
            for (int i = 0; i < node.getLength(); i++) {
                Node child = node.item(i);
                if (child instanceof Element) {
                    Element element = (Element) child;
                    if (element.getTagName().equals("connection")) {
                        connectionList.add(new DTTConnection(element));
                    } else if (element.getTagName().equals("transformation")) {
                        transformationList.add(new DTTTransformation(element));
                    } else {
                        System.out.println("Unknown element '" + element.getTagName() + "' 'dtt' tag");
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return (connectionList.size() > 0) && (transformationList.size() > 0);
    }

    private void checkConnectionList() throws Exception {
        for (int i = 0; i < connectionList.size(); i++) {
            DTTConnection conn = (DTTConnection) connectionList.get(i);
            if (conn.getWrongMessage() != null) {
                throw new Exception("Connection error: " + conn.getWrongMessage());
            }
        }
    }

    private void checkTransformationList() throws Exception {
        for (int i = 0; i < transformationList.size(); i++) {
            DTTTransformation trans = (DTTTransformation) transformationList.get(i);
            if (trans.getWrongMessage() != null) {
                throw new Exception("Transformation error: " + trans.getWrongMessage());
            }

            if (!trans.resolveConnections(connectionList)) {
                throw new Exception("Transformation '" + trans.getName() + "' has not database connect information");
            }
        }
    }

    private void executeTransformationList() throws Exception {
        try {
            for (int i = 0; i < transformationList.size(); i++) {
                DTTTransformation trans = (DTTTransformation) transformationList.get(i);
                System.out.print(trans.getName() + " ... ");
                int recordCount = trans.execute();
                if (recordCount == -1) {
                    throw new Exception("Error during transformation '" + trans.getName() + "', program abort");
                }
                System.out.println(recordCount + " record(s)");
            }
        } finally {
            for (int i = 0; i < connectionList.size(); i++) {
                DTTConnection conn = (DTTConnection) connectionList.get(i);
                conn.close();
            }
        }
    }

    public void execute() {
        try {
            checkConnectionList();
            checkTransformationList();
            executeTransformationList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        String fileName = "dtt.xml";
        if (args.length == 1) {
            fileName = args[0];
        }
        if (args.length > 1) {
            System.out.println("Usage: DTT <dtt file name.xml>");
            System.exit(0);
        }

        System.out.println("Started at: " + StringUtils.getNow());

        DTT dtt = new DTT();
        if (dtt.load(fileName)) {
            dtt.execute();
        }

        System.out.println("Stopped at: " + StringUtils.getNow());
    }
}
