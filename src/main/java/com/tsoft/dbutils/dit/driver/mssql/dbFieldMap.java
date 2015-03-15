package com.tsoft.dbutils.dit.driver.mssql;

import java.util.*;

class dbFieldMap {
    private ArrayList[] map;
    private dbTableList tableList;
    private LinkedList rows = new LinkedList();

    public dbFieldMap(dbTableList aTableList) {
        tableList = aTableList;
    }

    private String getFieldFilter(dbField f) {
        return f.name + ", " + f.getDataType();
    }

    public void build() {
        for (int i = 0; i < tableList.usedSize(); i++) {
            dbTable t = (dbTable) tableList.usedGet(i);

            for (int j = 0; j < t.getFieldList().size(); j++) {
                dbField f = (dbField) t.getFieldList().get(j);
                String fc = getFieldFilter(f);
                boolean add = true;
                for (int k = 0; k < rows.size() - 1; k++) {
                    String s = (String) rows.get(k);
                    if (s.equals(fc)) {
                        add = false;
                        break;
                    }
                    if (s.compareTo(fc) > 0) {
                        rows.add(k, fc);
                        add = false;
                        break;
                    }
                }

                if (add) {
                    rows.addLast(fc);
                }
            }
        }

        map = new ArrayList[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            map[i] = new ArrayList();
        }

        for (int i = 0; i < tableList.usedSize(); i++) {
            dbTable t = (dbTable) tableList.usedGet(i);
            for (int j = 0; j < t.getFieldList().size(); j++) {
                dbField f = (dbField) t.getFieldList().get(j);
                String fc = getFieldFilter(f);
                for (int k = 0; k < rows.size(); k++) {
                    String s = (String) rows.get(k);
                    if (s.equals(fc)) {
                        map[k].add(t.name);
                        break;
                    }
                }
            }
        }
    }

    public List getRows() {
        return rows;
    }

    public int getMaxColCount() {
        int max = 0;
        ListIterator iter = rows.listIterator();
        while (iter.hasNext()) {
            int idx = iter.nextIndex();
            if (max < map[idx].size()) {
                max = map[idx].size();
            }
            iter.next();
        }
        return max;
    }

    public ArrayList getCols(int row) {
        ArrayList a = new ArrayList();
        a.addAll(map[row]);
        return a;
    }
}
