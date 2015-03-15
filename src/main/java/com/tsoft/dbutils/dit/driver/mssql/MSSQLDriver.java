package com.tsoft.dbutils.dit.driver.mssql;

import java.text.*;
import java.util.Date;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.db.*;
import com.tsoft.dbutils.dit.driver.*;

/**
 * 'jdbc:microsoft:sqlserver://ASU:1433;DatabaseName=V_2004'
 */
public class MSSQLDriver implements DITDriver {
    private dbTableList tableList = new dbTableList();
    private dbTriggerList triggerList = new dbTriggerList();
    private dbViewList viewList = new dbViewList();
    private dbFunctionList functionList = new dbFunctionList();
    private dbProcedureList procedureList = new dbProcedureList();
    private dbTypeList typeList = new dbTypeList();
    private dbDBLinkList dbLinkList = new dbDBLinkList();
    private Database database;

    @Override
    public String getDriverName() {
        return "MSSQL 2000";
    }

    @Override
    public String getServerName() {
        String s = database.getURL();
        int n = s.indexOf("//");
        if (n == -1) {
            return "Unknown";
        }
        s = s.substring(n + 2);
        n = s.indexOf(":");
        if (n == -1) {
            return s;
        }
        return s.substring(0, n);
    }

    @Override
    public String getDatabaseName() {
        String s = database.getURL();
        int n = s.lastIndexOf("=");
        return s.substring(n + 1);
    }

    @Override
    public String getUserName() {
        return database.getUserName();
    }

    @Override
    public void setDatabase(Database aDatabase) {
        database = aDatabase;
    }

    @Override
    public void open() {
        tableList.open(database);
        triggerList.open(database);
        viewList.open(database);
        functionList.open(database);
        procedureList.open(database);
        typeList.open(database);
        dbLinkList.open(database);
    }

    @Override
    public void setUsed(DITDriverObject object) {
        tableList.setUsed(object);
        triggerList.setUsed(object);
        viewList.setUsed(object);
        functionList.setUsed(object);
        procedureList.setUsed(object);
        typeList.setUsed(object);
        dbLinkList.setUsed(object);
    }

    private void addHTMLBegin(StringBuilder buffer) {
        buffer.append("<HTML>\n");
        buffer.append("<HEAD>\n");
        buffer.append("<TITLE>Database ").append(getServerName()).append(".").append(getDatabaseName()).append(".").append(getUserName()).append("</TITLE>\n");
        buffer.append("<META content=\"text/html; charset=windows-1251\" http-equiv=Content-Type>\n");
        buffer.append("</HEAD>\n");
        buffer.append("<BODY bgColor=#ffffff class=Main>\n");
        buffer.append("<strong>Database ").append(getServerName()).append(".").append(getDatabaseName()).append(".").append(getUserName()).append("</strong><br>\n");
        buffer.append("Database Type ").append(database.getTypeDBName()).append("<br>\n");
        buffer.append("Document Build Date ").append(DateFormat.getDateInstance(DateFormat.SHORT).format(new Date())).append("<br>\n");
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
        s.append((typeList.usedSize() == 0 ? "" : "<a href=\"#types\">Types (" + typeList.usedSize() + ")</a><br>\n"));
        s.append((tableList.usedSize() == 0 ? "" : "<a href=\"#tables\">Tables (" + tableList.usedSize() + ")</a><br>\n"));
        s.append((triggerList.usedSize() == 0 ? "" : "<a href=\"#triggers\">Triggers (" + triggerList.usedSize() + ")</a><br>\n"));
        s.append((viewList.usedSize() == 0 ? "" : "<a href=\"#views\">Views (" + viewList.usedSize() + ")</a><br>\n"));
        s.append((functionList.usedSize() == 0 ? "" : "<a href=\"#functions\">Functions (" + functionList.usedSize() + ")</a><br>\n"));
        s.append((procedureList.usedSize() == 0 ? "" : "<a href=\"#procedures\">Procedures (" + procedureList.usedSize() + ")</a><br>\n"));
        s.append((dbLinkList.usedSize() == 0 ? "" : "<a href=\"#databaseLinks\">Database Links (" + dbLinkList.usedSize() + ")</a><br>\n"));
        s.append((tableList.usedSize() == 0 ? "" : "<a href=\"#summary\">Summary</a><br>\n"));
        s.append("<hr>\n");
        s.append(typeList.getHTML());
        s.append(dbLinkList.getHTML());
        s.append(tableList.getHTML());
        s.append(viewList.getHTML());
        s.append(functionList.getHTML());
        s.append(procedureList.getHTML());
        s.append(triggerList.getHTML());
        addHTMLEnd(s);
        return s.toString();
    }

    @Override
    public String getHTMLFields() {
        StringBuilder s = new StringBuilder(1000000);
        addHTMLBegin(s);
        if (tableList.usedSize() > 0) {
            tableList.getFieldMap().build();
            s.append("<a name=\"summary\"><strong>Summary</strong></a><br>\n");

            s.append("S01. Fields By Tables List. ").append(tableList.size()).append(" table(s), ").append(tableList.getFieldMap().getRows().size()).append(" field(s)<br>");
            s.append("<font size=\"2\"><table bgcolor=\"#bcd7ff\" border=\"1\">");
            for (int y = 0; y < tableList.getFieldMap().getRows().size() - 1; y++) {
                s.append("<tr>");
                s.append("<td>").append(tableList.getFieldMap().getRows().get(y)).append("</td>\n");
                for (int x = 0; x < tableList.getFieldMap().getMaxColCount(); x++) {
                    s.append("<td>");
                    if (x < tableList.getFieldMap().getCols(y).size()) {
                        String tableName = (String) tableList.getFieldMap().getCols(y).get(x);
                        s.append("<a href=\"info.htm#table").append(tableName).append("\">").append(StringUtils.toHTML(tableName)).append("</a>");
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

    @Override
    public String getXML() {
        StringBuilder s = new StringBuilder(1000000);
        s.append("<?xml version=\"1.0\" encoding=\"windows-1251\" ?>\n");
        s.append("<dit>\n");
        s.append("<type>").append(database.getTypeDBName()).append("</type>\n");
        s.append("<server>").append(getServerName()).append("</server>\n");
        s.append("<database>").append(getDatabaseName()).append("</database>\n");
        s.append("<user>").append(getUserName()).append("</user>\n");
        s.append("<created>").append(DateFormat.getDateInstance(DateFormat.SHORT).format(new Date())).append("</created>\n");
        s.append(dbLinkList.getXML());
        s.append(typeList.getXML());
        s.append(tableList.getXML());
        s.append(viewList.getXML());
        s.append(functionList.getXML());
        s.append(procedureList.getXML());
        s.append(triggerList.getXML());
        s.append("</dit>\n");
        return s.toString();
    }

    @Override
    public String getCreateScript() {
        StringBuilder s = new StringBuilder(1000000);
        s.append("/**\n");
        s.append(" * @server ").append(getServerName()).append("\n");
        s.append(" * @database ").append(getDatabaseName()).append("\n");
        s.append(" * @user ").append(getUserName()).append("\n");
        s.append(" * @created ").append(DateFormat.getDateInstance(DateFormat.SHORT).format(new Date())).append("\n");
        s.append(" */\n\n");
        s.append(dbLinkList.getSQL());
        s.append(typeList.getSQL());
        s.append(tableList.getSQL());
        s.append(viewList.getSQL());
        s.append(functionList.getSQL());
        s.append(procedureList.getSQL());
        s.append(triggerList.getSQL());
        return s.toString();
    }

    @Override
    public String getInsertScript() {
        StringBuilder s = new StringBuilder(1000000);
        for (int i = 0; i < triggerList.usedSize(); i++) {
            dbTrigger t = (dbTrigger) triggerList.usedGet(i);
            s.append("ALTER TRIGGER ").append(t.name).append(" DISABLE;\n");
        }

        for (int i = 0; i < tableList.usedSize(); i++) {
            dbTable t = (dbTable) tableList.usedGet(i);
            s.append("\n").append(t.getInsertScript());
        }

        // �������� ��������
        for (int i = 0; i < triggerList.usedSize(); i++) {
            dbTrigger t = (dbTrigger) triggerList.usedGet(i);
            s.append("ALTER TRIGGER ").append(t.name).append(" ENABLE;\n");
        }
        return s.toString();
    }
}
