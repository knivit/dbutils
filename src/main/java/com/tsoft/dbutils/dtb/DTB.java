package com.tsoft.dbutils.dtb;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import com.tsoft.dbutils.lib.*;

public class DTB {
    private ArrayList buildList = new ArrayList();

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
                    if (element.getTagName().equals("build")) {
                        buildList.add(new DTBBuild(element));
                    } else {
                        throw new Exception("Unknown element '" + element.getTagName() + "' for 'dtb'");
                    }
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public String execute() {
        for (int i = 0; i < buildList.size(); i++) {
            DTBBuild build = (DTBBuild) buildList.get(i);
            try {
                build.execute();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return "Ok";
    }

    public static void main(String[] args) {
        String fileName = "dtb.xml";
        if (args.length == 1) {
            fileName = args[0];
        }

        System.out.println("Started at: " + StringUtils.getNow());

        DTB dtb = new DTB();
        if (dtb.load(fileName)) {
            System.out.println(dtb.execute());
        }

        System.out.println("Stopped at: " + StringUtils.getNow());
    }
}
