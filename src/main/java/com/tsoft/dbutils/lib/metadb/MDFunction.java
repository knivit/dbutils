package com.tsoft.dbutils.lib.metadb;

import java.util.*;

public class MDFunction {
    public static final int FUNCTION = 1;
    public static final int PROCEDURE = 2;
    private int type;                                  // FUNCTION..PROCEDURE
    private String name;
    private ArrayList<String> commentLineList;
    private boolean isComment;

    public int getType() {
        return type;
    }

    public void setType(int aType) {
        type = aType;
    }

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    public void addScript(String script) {
        int n1 = script.indexOf("/**");
        int n2 = script.indexOf("**/");

        if (n1 != -1) {
            if (commentLineList == null) {
                commentLineList = new ArrayList<String>();
            }
            isComment = true;
        }

        if (n2 != -1 && commentLineList != null) {
            String line = script.substring(n1 == -1 ? 0 : n1 + 3, n2).replace('\n', ' ');
            if (line.trim().length() > 0) {
                commentLineList.add(line);
            }
            isComment = false;
        }

        if (isComment) {
            String line = script.substring(n1 == -1 ? 0 : n1 + 3, script.length()).replace('\n', ' ');
            if (line.trim().length() > 0) {
                commentLineList.add(line);
            }
        }
    }

    public ArrayList<String> getCommentLineList() {
        return commentLineList;
    }

    public boolean isCommented() {
        return commentLineList != null;
    }
}
