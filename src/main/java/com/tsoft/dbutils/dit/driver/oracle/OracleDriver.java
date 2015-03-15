package com.tsoft.dbutils.dit.driver.oracle;

import java.text.*;
import java.util.Date;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;
import com.tsoft.dbutils.dit.driver.*;

/**
 * 'jdbc:oracle:thin:@ASU:1521:ORADB'
 */
public class OracleDriver implements DITDriver {
    private dbRoleList roleList = new dbRoleList();
    private dbTableList tableList = new dbTableList();
    private dbTriggerList triggerList = new dbTriggerList();
    private dbViewList viewList = new dbViewList();
    private dbFunctionList functionList = new dbFunctionList();
    private dbProcedureList procedureList = new dbProcedureList();
    private dbPackageList packageList = new dbPackageList();
    private dbPackageBodyList packageBodyList = new dbPackageBodyList();
    private dbTypeList typeList = new dbTypeList();
    private dbTypeBodyList typeBodyList = new dbTypeBodyList();
    private dbSynonymList synonymList = new dbSynonymList();
    private dbSequenceList sequenceList = new dbSequenceList();
    private dbDBLinkList dbLinkList = new dbDBLinkList();
    private Database database;

    @Override
    public String getDriverName() {
        return "Oracle 9i";
    }

    @Override
    public String getServerName() {
        String s = database.getURL();
        int n = s.indexOf("@");
        s = s.substring(n + 1);
        n = s.indexOf(":");
        if (n == -1) {
            return s;
        }
        return s.substring(0, n);
    }

    @Override
    public String getDatabaseName() {
        String s = database.getURL();
        int n = s.lastIndexOf(":");
        return s.substring(n + 1);
    }

    @Override
    public String getUserName() {
        return database.getSchema();
    }

    @Override
    public void setDatabase(Database aDatabase) {
        database = aDatabase;
    }

    @Override
    public void open() {
        roleList.read(database);
        tableList.read(database);
        triggerList.read(database);
        viewList.read(database);
        functionList.read(database);
        procedureList.read(database);
        packageList.read(database);
        packageBodyList.read(database);
        typeList.read(database);
        typeBodyList.read(database);
        synonymList.read(database);
        sequenceList.read(database);
        dbLinkList.read(database);
    }

    @Override
    public void setUsed(DITDriverObject object) {
        roleList.setUsed(object);
        tableList.setUsed(object);
        triggerList.setUsed(object);
        viewList.setUsed(object);
        functionList.setUsed(object);
        procedureList.setUsed(object);
        packageList.setUsed(object);
        packageBodyList.setUsed(object);
        typeList.setUsed(object);
        typeBodyList.setUsed(object);
        synonymList.setUsed(object);
        sequenceList.setUsed(object);
        dbLinkList.setUsed(object);
    }

    private void addHTMLBegin(StringBuilder buffer) {
        buffer.append("<HTML>\n");
        buffer.append("<HEAD>\n");
        buffer.append("<TITLE>Database " + getServerName() + "." + getDatabaseName() + "." + getUserName() + "</TITLE>\n");
        buffer.append("<META content=\"text/html; charset=windows-1251\" http-equiv=Content-Type>\n");
        buffer.append("</HEAD>\n");
        buffer.append("<BODY bgColor=#ffffff class=Main>\n");
        buffer.append("<strong>Database " + getServerName() + "." + getDatabaseName() + "." + getUserName() + "</strong><br>\n");
        buffer.append("Database Type " + database.getTypeDBName() + "<br>\n");
        buffer.append("Document Build Date " + DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()) + "<br>\n");
        buffer.append("<hr>\n");
    }

    private void addHTMLEnd(StringBuilder buffer) {
        buffer.append("</BODY>\n");
        buffer.append("</HTML>\n");
    }

    @Override
    public String getHTMLInfo() {
        StringBuilder s = new StringBuilder(1000000);
        addHTMLBegin(s);
        s.append((tableList.usedSize() == 0 ? "" : "<a href=\"#tables\">Tables (" + tableList.usedSize() + ")</a><br>\n"));
        s.append((viewList.usedSize() == 0 ? "" : "<a href=\"#views\">Views (" + viewList.usedSize() + ")</a><br>\n"));
        s.append((sequenceList.usedSize() == 0 ? "" : "<a href=\"#sequences\">Sequences (" + sequenceList.usedSize() + ")</a><br>\n"));
        s.append((typeList.usedSize() == 0 ? "" : "<a href=\"#types\">Types (" + typeList.usedSize() + ")</a><br>\n"));
        s.append((typeBodyList.usedSize() == 0 ? "" : "<a href=\"#typeBodies\">Type Bodies (" + typeBodyList.usedSize() + ")</a><br>\n"));
        s.append((triggerList.usedSize() == 0 ? "" : "<a href=\"#triggers\">Triggers (" + triggerList.usedSize() + ")</a><br>\n"));
        s.append((functionList.usedSize() == 0 ? "" : "<a href=\"#functions\">Functions (" + functionList.usedSize() + ")</a><br>\n"));
        s.append((procedureList.usedSize() == 0 ? "" : "<a href=\"#procedures\">Procedures (" + procedureList.usedSize() + ")</a><br>\n"));
        s.append((packageList.usedSize() == 0 ? "" : "<a href=\"#packages\">Packages (" + packageList.usedSize() + ")</a><br>\n"));
        s.append((packageBodyList.usedSize() == 0 ? "" : "<a href=\"#packageBodies\">Package Bodies (" + packageBodyList.usedSize() + ")</a><br>\n"));
        s.append((synonymList.usedSize() == 0 ? "" : "<a href=\"#synonyms\">Synonyms (" + synonymList.usedSize() + ")</a><br>\n"));
        s.append((dbLinkList.usedSize() == 0 ? "" : "<a href=\"#databaseLinks\">Database Links (" + dbLinkList.usedSize() + ")</a><br>\n"));
        s.append("<hr>\n");
        s.append(tableList.getHTML());
        s.append(viewList.getHTML());
        s.append(sequenceList.getHTML());
        s.append(typeList.getHTML());
        s.append(dbLinkList.getHTML());
        s.append(typeBodyList.getHTML());
        s.append(synonymList.getHTML());
        s.append(functionList.getHTML());
        s.append(procedureList.getHTML());
        s.append(packageList.getHTML());
        s.append(packageBodyList.getHTML());
        s.append(triggerList.getHTML());
        addHTMLEnd(s);
        return s.toString();
    }

    @Override
    public String getHTMLFields() {
        StringBuilder s = new StringBuilder(1000000);
        addHTMLBegin(s);
        if (tableList.size() > 0) {
            tableList.getFieldMap().build();
            s.append("<a name=\"summary\"><strong>Summary</strong></a><br>\n");

            s.append("S01. Fields by Tables List. " + tableList.size() + " table(s), " + tableList.getFieldMap().getRows().size() + " field(s)<br>");
            s.append("<font size=\"2\"><table bgcolor=\"#bcd7ff\" border=\"1\">");
            for (int y = 0; y < tableList.getFieldMap().getRows().size() - 1; y++) {
                s.append("<tr>");
                s.append("<td>" + tableList.getFieldMap().getRows().get(y) + "</td>\n");
                for (int x = 0; x < tableList.getFieldMap().getMaxColCount(); x++) {
                    s.append("<td>");
                    if (x < tableList.getFieldMap().getCols(y).size()) {
                        String tableName = (String) tableList.getFieldMap().getCols(y).get(x);
                        s.append("<a href=\"info.htm#table" + tableName + "\">" + StringUtils.toHTML(tableName) + "</a>");
                    }
                    s.append("</td>\n");
                }
                s.append("</tr>\n");
            }
            s.append("</table></font><br>\n");
        }
        addHTMLEnd(s);
        return s.toString();
    }

    // ��������� XML-�������� ������� �����
    @Override
    public String getXML() {
        StringBuilder s = new StringBuilder(1000000);
        s.append("<?xml version=\"1.0\" encoding=\"windows-1251\" ?>\n");
        s.append("<dit>\n");
        s.append("<type>" + database.getTypeDBName() + "</type>\n");
        s.append("<server>" + getServerName() + "</server>\n");
        s.append("<database>" + getDatabaseName() + "</database>\n");
        s.append("<user>" + getUserName() + "</user>\n");
        s.append("<created>" + DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()) + "</created>\n");
        s.append(roleList.getXML());
        s.append(dbLinkList.getXML());
        s.append(sequenceList.getXML());
        s.append(typeList.getXML());
        s.append(typeBodyList.getXML());
        s.append(synonymList.getXML());
        s.append(tableList.getXML());
        s.append(viewList.getXML());
        s.append(functionList.getXML());
        s.append(procedureList.getXML());
        s.append(packageList.getXML());
        s.append(packageBodyList.getXML());
        s.append(triggerList.getXML());
        s.append("</dit>\n");
        return s.toString();
    }

    @Override
    public String getCreateScript() {
        StringBuilder s = new StringBuilder(1000000);
        s.append("/**\n");
        s.append(" * @server " + getServerName() + "\n");
        s.append(" * @database " + getDatabaseName() + "\n");
        s.append(" * @schema " + getUserName() + "\n");
        s.append(" * @created " + DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()) + "\n");
        s.append(" */\n\n");
        s.append(dbLinkList.getSQL());
        s.append(sequenceList.getSQL());
        s.append(typeList.getSQL());
        s.append(typeBodyList.getSQL());
        s.append(synonymList.getSQL());
        s.append(tableList.getSQL());
        s.append(viewList.getSQL());
        s.append(functionList.getSQL());
        s.append(procedureList.getSQL());
        s.append(packageList.getSQL());
        s.append(packageBodyList.getSQL());
        s.append(triggerList.getSQL());
        return s.toString();
    }

    @Override
    public String getInsertScript() {
        StringBuilder s = new StringBuilder(1000000);

        for (int i = 0; i < triggerList.usedSize(); i++) {
            dbTrigger t = (dbTrigger) triggerList.usedGet(i);
            s.append("ALTER TRIGGER " + t.name + " DISABLE;\n");
        }

        for (int i = 0; i < tableList.usedSize(); i++) {
            dbTable t = (dbTable) tableList.usedGet(i);
            s.append("\n" + t.getInsertScript());
        }

        for (int i = 0; i < triggerList.usedSize(); i++) {
            dbTrigger t = (dbTrigger) triggerList.usedGet(i);
            s.append("ALTER TRIGGER " + t.name + " ENABLE;\n");
        }
        return s.toString();
    }
}
