package com.tsoft.dbcompare.config;

import com.tsoft.dbcompare.config.CompareMode;

public class TableConfig {
    private String name;

    private CompareMode compareMode;

    private String whereClause;

    private boolean forcedCompare;

    public TableConfig(String name, CompareMode compareMode) {
        this.name = name;
        this.compareMode = compareMode;
    }

    public String getName() {
        return name;
    }

    public CompareMode getCompareMode() {
        return compareMode;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public boolean isForcedCompare() {
        return forcedCompare;
    }

    public void setForcedCompare(boolean forcedCompare) {
        this.forcedCompare = forcedCompare;
    }

    @Override
    public String toString() {
        return "TableConfig {" +
                "name='" + name + '\'' +
                ", compareMode=" + compareMode +
                ", whereClause='" + whereClause + '\'' +
                '}';
    }
}
