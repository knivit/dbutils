package com.tsoft.dbutils.lib.metadb;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import com.tsoft.dbutils.lib.*;
import org.xml.sax.helpers.*;

public class MDDatabase extends DefaultHandler {
    private ArrayList<MDDataSet> dataSetList = new ArrayList<MDDataSet>();

    private ArrayList<MDFunction> functionList = new ArrayList<MDFunction>();

    public boolean open(String fileName) {
        File file = new File(fileName);
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            parser.parse(file, this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return false;
    }

    class XMLStack extends Stack {
        public boolean atEnd(String[] strList) {
            if (size() < strList.length) {
                return false;
            }
            for (int i = 0; i < strList.length; i++) {
                if (!((String) get(size() - i - 1)).equalsIgnoreCase(strList[i])) {
                    return false;
                }
            }
            return true;
        }
    }
    private XMLStack XMLPath = new XMLStack();

    public void startElement(String namespaceURI, String lname, String qname, Attributes attrs) throws SAXException {
        XMLPath.push(lname);

        if ((lname.equalsIgnoreCase("table") && XMLPath.atEnd(new String[]{"table", "tables"})) ||
                (lname.equalsIgnoreCase("view") && XMLPath.atEnd(new String[]{"view", "views"}))) {
            MDDataSet dataSet = new MDDataSet();
            if (lname.equalsIgnoreCase("table")) {
                dataSet.setType(MDDataSet.TABLE);
            } else {
                dataSet.setType(MDDataSet.VIEW);
            }
            if (attrs == null || attrs.getValue("name") == null) {
                throw new SAXException("Attribute 'name' in element '" + lname + "' not found");
            }
            dataSet.setName(attrs.getValue("name"));
            dataSetList.add(dataSet);
        }

        if ((lname.equalsIgnoreCase("function") && XMLPath.atEnd(new String[]{"function", "functions"})) ||
                (lname.equalsIgnoreCase("procedure") && XMLPath.atEnd(new String[]{"procedure", "procedures"}))) {
            MDFunction function = new MDFunction();
            if (lname.equalsIgnoreCase("function")) {
                function.setType(MDFunction.FUNCTION);
            } else {
                function.setType(MDFunction.PROCEDURE);
            }
            if (attrs == null || attrs.getValue("name") == null) {
                throw new SAXException("Attribute 'name' in element '" + lname + "' not found");
            }
            function.setName(attrs.getValue("name"));
            functionList.add(function);
        }

        if (XMLPath.atEnd(new String[]{"field", "fields"})) {
            MDDataSet dataSet = (MDDataSet) dataSetList.get(dataSetList.size() - 1);
            MDDataSetField field = new MDDataSetField();
            if (attrs == null || attrs.getValue("name") == null) {
                throw new SAXException("Attribute 'name' in element '" + lname + "' not found");
            }
            field.setName(attrs.getValue("name"));
            dataSet.getFieldList().add(field);
        }

        // PRIMARY KEY, FOREIGN KEY
        if (XMLPath.atEnd(new String[]{"type", "constraint", "constraints", "table"})) {
            MDDataSet dataSet = (MDDataSet) dataSetList.get(dataSetList.size() - 1);
            if (attrs == null || attrs.getValue("name") == null) {
                throw new SAXException("Attribute 'name' in element '" + lname + "' not found");
            }

            String constraintType = attrs.getValue("name");
            if (constraintType.equalsIgnoreCase("primary")) {
                if (attrs.getValue("columns") == null) {
                    throw new SAXException("Attribute 'columns' in element '" + lname + "' not found");
                }

                ArrayList<String> columnNameList = new ArrayList<String>();
                StringUtils.createList(attrs.getValue("columns"), columnNameList);
                for (int i = 0; i < columnNameList.size(); i++) {
                    MDDataSetField field = dataSet.findField(columnNameList.get(i));
                    if (field == null) {
                        throw new SAXException("Field '" + columnNameList.get(i) + "' not found");
                    }
                    field.setPrimary(true);
                }
            }

            if (constraintType.equalsIgnoreCase("foreign")) {
                String columnName = attrs.getValue("columns");
                if (columnName == null) {
                    throw new SAXException("Attribute 'columns' in element '" + lname + "' not found");
                }

                MDDataSetField field = dataSet.findField(columnName);
                if (field == null) {
                    throw new SAXException("Field '" + columnName + "' not found");
                }
                field.setForeign(attrs.getValue("refTable"), attrs.getValue("refColumn"));
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        XMLPath.pop();
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        String value = String.copyValueOf(ch, start, length);

        if (XMLPath.atEnd(new String[]{"comment", "table", "tables"})) {
            MDDataSet dataSet = (MDDataSet) dataSetList.get(dataSetList.size() - 1);
            dataSet.setComment(value);
        }

        if (XMLPath.atEnd(new String[]{"comment", "field", "fields"})) {
            MDDataSet dataSet = (MDDataSet) dataSetList.get(dataSetList.size() - 1);
            int lastIndex = dataSet.getFieldList().size() - 1;
            MDDataSetField field = (MDDataSetField) dataSet.getFieldList().get(lastIndex);
            field.setComment(value);
        }

        if (XMLPath.atEnd(new String[]{"type", "field", "fields"})) {
            MDDataSet dataSet = (MDDataSet) dataSetList.get(dataSetList.size() - 1);
            int lastIndex = dataSet.getFieldList().size() - 1;
            MDDataSetField field = (MDDataSetField) dataSet.getFieldList().get(lastIndex);
            field.setType(value);
        }

        if (XMLPath.atEnd(new String[]{"datatype", "field", "fields"})) {
            MDDataSet dataSet = (MDDataSet) dataSetList.get(dataSetList.size() - 1);
            int lastIndex = dataSet.getFieldList().size() - 1;
            MDDataSetField field = (MDDataSetField) dataSet.getFieldList().get(lastIndex);
            field.setDataType(value);
        }

        if (XMLPath.atEnd(new String[]{"size", "field", "fields"})) {
            MDDataSet dataSet = (MDDataSet) dataSetList.get(dataSetList.size() - 1);
            int lastIndex = dataSet.getFieldList().size() - 1;
            MDDataSetField field = (MDDataSetField) dataSet.getFieldList().get(lastIndex);
            field.setSize(Integer.parseInt(value));
        }

        if (XMLPath.atEnd(new String[]{"scale", "field", "fields"})) {
            MDDataSet dataSet = (MDDataSet) dataSetList.get(dataSetList.size() - 1);
            int lastIndex = dataSet.getFieldList().size() - 1;
            MDDataSetField field = (MDDataSetField) dataSet.getFieldList().get(lastIndex);
            field.setScale(Integer.parseInt(value));
        }

        if (XMLPath.atEnd(new String[]{"nullable", "field", "fields"})) {
            MDDataSet dataSet = (MDDataSet) dataSetList.get(dataSetList.size() - 1);
            int lastIndex = dataSet.getFieldList().size() - 1;
            MDDataSetField field = (MDDataSetField) dataSet.getFieldList().get(lastIndex);
            field.setNullable(value.equalsIgnoreCase("true"));
        }

        if (XMLPath.atEnd(new String[]{"script", "function", "functions"}) ||
                XMLPath.atEnd(new String[]{"script", "procedure", "procedures"})) {
            MDFunction function = (MDFunction) functionList.get(functionList.size() - 1);
            function.addScript(value);
        }
    }

    public ArrayList getDataSetList() {
        return dataSetList;
    }

    public ArrayList getFunctionList() {
        return functionList;
    }
}
