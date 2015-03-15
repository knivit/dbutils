package com.tsoft.dbutils.dtt;

import org.w3c.dom.*;

public class DTTCopy {
    private String fromName;
    private String toName;
    private String dataType;
    private String comment;
    private String primary;

    public DTTCopy(Element element) {
        fromName = element.getAttribute("from");
        if (fromName != null && fromName.length() == 0) {
            fromName = null;
        }
        toName = element.getAttribute("to");
        if (toName != null && toName.length() == 0) {
            toName = null;
        }
        dataType = element.getAttribute("datatype");
        if (dataType != null && dataType.length() == 0) {
            dataType = null;
        }
        comment = element.getAttribute("comment");
        if (comment != null && comment.length() == 0) {
            comment = null;
        }
        primary = element.getAttribute("primary");
    }

    public boolean getPrimary() {
        return (primary != null && primary.equalsIgnoreCase("true"));
    }

    public String getFromName() {
        return fromName;
    }

    public String getToName() {
        return toName;
    }

    public String getType() {
        return dataType;
    }

    public String getComment() {
        return comment;
    }
}
