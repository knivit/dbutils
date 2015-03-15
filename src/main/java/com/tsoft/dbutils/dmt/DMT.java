package com.tsoft.dbutils.dmt;

import java.io.*;

import java.rmi.Naming;

import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

/**
 * Data Mining Tool
 * <?xml version="1.0" encoding="windows-1251" ?>
 * <dmt>
 *   <connection name="name">
 *     <driver>jdbc driver class</driver>
 *     <url>url to the database</url>
 *     <username>database login</username>
 *     <password>login password</password>
 *   </connection>
 *   <model name="Data" connection="V_2004" DMTDriver="tsoft.database.dmt.driver.dit.DITDriver">
 *     <DIT file="c:\project\DatabaseUtils\DIT\MSSQL 2000\ASU\V_2004\sa\object.xml"/>
 *   </model>
 *   <model name="Vyl" connection="V_2004" DMTDriver="tsoft.database.dmt.driver.cube.CubeDriver">
 *     <cube name="TotalRecordCount" query="select Zak, VidVert, count(*) as cnt from Vyl group by Zak, VidVert">
 *       <dim axis="Y" field="Zak" query="select Name from Zak where Kod=@Zak"/>
 *       <dim axis="Y" field="VidVert" query="select Name from VidVert where Kod=@VidVert"/>
 *       <dim axis="X" field="cnt"/>
 *     </cube>
 *     <cube name="QTimeMinValue" query="select Zak, min(QTime) as QTime from Vyl group by Zak">
 *       <dim axis="Y" field="Zak" query="select Name from Zak where Kod=@Zak"/>
 *       <dim axis="X" field="QTime"/>
 *     </cube>
 *   </model>
 * </dmt>
 * 
 * @version 1.00 2005-12-02
 * @author TSoft
 */
public class DMT {
    private DMTConnectionList connectionList = new DMTConnectionList();
    private ArrayList modelList = new ArrayList();
    private boolean stop;

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
                        connectionList.add(new DMTConnection(element));
                    } else if (element.getTagName().equals("model")) {
                        modelList.add(new DMTModel(element));
                    } else {
                        throw new Exception("Unknown element '" + element.getTagName() + "' the 'dmt' tag");
                    }
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("I/O Error\n" + e.getMessage());
        } catch (ParserConfigurationException e) {
            System.out.println("A XML-Parser serios configuration error\n" + e.getMessage());
        } catch (SAXException e) {
            System.out.println("A general SAX error\n" + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void execute() {
        System.out.println("Starting DataMining Server");
        for (int i = 0; i < modelList.size(); i++) {
            try {
                DMTModel model = (DMTModel) modelList.get(i);
                DMTConnection conn = connectionList.find(model.getConnectionName());
                if (conn != null) {
                    System.out.println("Model '" + model.getName() + "'");
                    model.setConnection(conn);
                    model.execute();
                } else {
                    throw new Exception("The model '" + model.getName() + "' of connection '" + model.getConnectionName() + "' not found !");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Server initialized\n");

        System.out.println("Wait for Command (list, stop) and RMI-connections");
        while (!stop) {
            try {
                String cmd = "";

                if (cmd.equalsIgnoreCase("list")) {
                    String[] bindings = Naming.list("");
                    for (int i = 0; i < bindings.length; i++) {
                        System.out.println(bindings[i]);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Server shutdown\n");
    }

    public static void main(String[] args) {
        String fileName = "dmt.xml";
        if (args.length == 1) {
            fileName = args[0];
        }
        System.out.println("Data Mining Tool\n(c) 2000 - 2006 TSoft\n");
        if (args.length > 1) {
            System.out.println("Usage: DMT [<input file name>]\nDefault File is '" + fileName + "'");
        } else {
            DMT dmt = new DMT();
            if (dmt.load(fileName)) {
                dmt.execute();
            }
        }
        System.out.println("Process exited");
    }
}
