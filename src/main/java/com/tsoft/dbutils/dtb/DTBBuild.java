package com.tsoft.dbutils.dtb;

import java.io.*;
import org.w3c.dom.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.metadb.*;

public class DTBBuild {
    private DTBObject src;
    private DTBObject dest;

    public DTBBuild(Element root) throws Exception {
        NodeList node = root.getChildNodes();
        for (int i = 0; i < node.getLength(); i++) {
            Node child = node.item(i);
            if (child instanceof Element) {
                Element element = (Element) child;
                if (element.getTagName().equals("source")) {
                    src = new DTBObject(element, "DIT");
                } else if (element.getTagName().equals("dest")) {
                    dest = new DTBObject(element, "DTT");
                } else {
                    throw new Exception("Unknown element '" + element.getTagName() + "' 'build' tag");
                }
            }
        }
    }

    public void execute() throws Exception {
        if (src == null || dest == null) {
            throw new Exception("Can't generate DTT file becouse 'source' or 'dest' element undefined");
        }

        MDDatabase database = new MDDatabase();
        database.open(src.getValue());

        try {
            FileOutputStream out = new FileOutputStream(dest.getValue());
            try {
                out.write(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n").getBytes());
                out.write(("<dtt>\n").getBytes());
                out.write(("  <connection name=\"DB.SRC\">\n").getBytes());
                out.write(("    <driver>" + src.getDriver() + "</driver>\n").getBytes());
                out.write(("    <url>" + src.getURL() + "</url>\n").getBytes());
                out.write(("    <username>" + src.getUserName() + "</username>\n").getBytes());
                out.write(("    <password>" + src.getPassword() + "</password>\n").getBytes());
                out.write(("  </connection>\n").getBytes());
                out.write(("  <connection name=\"DB.DEST\">\n").getBytes());
                out.write(("    <driver>" + dest.getDriver() + "</driver>\n").getBytes());
                out.write(("    <url>" + dest.getURL() + "</url>\n").getBytes());
                out.write(("    <username>" + dest.getUserName() + "</username>\n").getBytes());
                out.write(("    <password>" + dest.getPassword() + "</password>\n").getBytes());
                out.write(("  </connection>\n").getBytes());

                for (int i = 0; i < database.getDataSetList().size(); i++) {
                    MDDataSet dataSet = (MDDataSet) database.getDataSetList().get(i);
                    if (dataSet.getType() == MDDataSet.TABLE) {
                        out.write(("  <transformation name=\"Table " + dataSet.getName() + "\">\n").getBytes());
                        out.write(("    <connection from=\"DB.SRC\" to=\"DB.DEST\"/>\n").getBytes());
                        out.write(("    <dataset from=\"" + dataSet.getName() + "\"" +
                                " to=\"" + dataSet.getName() + "\"" +
                                (dataSet.getComment() == null ? "" : " comment=\"" + StringUtils.toXML(dataSet.getComment()) + "\"") +
                                " create=\"True\"" +
                                "/>\n").getBytes());

                        for (int j = 0; j < dataSet.getFieldList().size(); j++) {
                            MDDataSetField field = (MDDataSetField) dataSet.getFieldList().get(j);
                            out.write(("    <copy from=\"" + field.getName() + "\"" +
                                    " to=\"" + field.getName() + "\"" +
                                    " nullable=\"" + field.getNullable() + "\"" +
                                    (field.getPrimary() ? " primary=\"true\"" : "") +
                                    (field.getComment() == null ? "" : " comment=\"" + StringUtils.toXML(field.getComment()) + "\"") +
                                    "/>\n").getBytes());
                        }
                        out.write(("  </transformation>\n").getBytes());
                    }
                }

                out.write(("</dtt>\n").getBytes());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
