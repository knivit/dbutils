package com.tsoft.dbutils.dit;

import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import com.tsoft.dbutils.lib.*;

/**
 * Database Information Tools
 */
public class DIT {
    private DITConnectionList connectionList = new DITConnectionList();

    private boolean load(String fileName) throws Exception {
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
                        connectionList.add(new DITConnection(element));
                    } else {
                        System.out.println("Unknown element '" + element.getTagName() + "', 'connection' tag ");
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
        return (connectionList.size() > 0);
    }

    private String execute() {
        for (int i = 0; i < connectionList.size(); i++) {
            DITConnection conn = (DITConnection) connectionList.get(i);
            conn.execute();
        }
        return "Ok";
    }

    public static void main(String[] args) throws Exception {
        String fileName = new File("").getAbsolutePath() + "/dit.xml";
        if (args.length == 1) {
            fileName = args[0];
        }

        File file = new File(fileName);
        if (args.length > 1 || !file.exists()) {
            System.exit(0);
        }

        System.out.println("Started at: " + StringUtils.getNow());

        DIT dit = new DIT();
        if (dit.load(fileName)) {
            System.out.println(dit.execute());
        }

        System.out.println("Stopped at: " + StringUtils.getNow());
    }
}
