package com.tsoft.dbutils.dit.driver;

import java.util.*;
import com.tsoft.dbutils.lib.db.*;

public abstract class dbObjectList<E extends dbObject> {
    protected Database database;
    protected ArrayList<E> objectList = new ArrayList<E>();

    public void add(E dbo) {
        objectList.add(size(), dbo);
    }

    public void clear() {
        objectList.clear();
    }

    public void addAll(Collection list) {
        objectList.addAll(list);
    }

    public E get(int index) {
        return objectList.get(index);
    }

    public int size() {
        return objectList.size();
    }

    public E usedGet(int index) {
        for (int i = 0; i < size(); i++) {
            if (get(i).used) {
                if (index == 0) {
                    return get(i);
                } else {
                    index--;
                }
            }
        }
        return null;
    }

    public int usedSize() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (get(i).used) {
                count++;
            }
        }
        return count;
    }

    public void setUsed(DITDriverObject object) {
        for (int i = 0; i < size(); i++) {
            E o = get(i);
            o.used = !object.skipObject(o.name, o.getCategory());
        }
    }

    public int indexOf(E dbo) {
        return objectList.indexOf(dbo);
    }

    public E find(String objectName) {
        for (int i = 0; i < size(); i++) {
            if (get(i).name.equals(objectName)) {
                return get(i);
            }
        }

        return null;
    }

    public Database getDatabase() {
        return database;
    }

    public String getSchema() {
        return database.getSchema();
    }

    protected void sort() {
        objectList.sort(new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {
                return o1.name.compareTo(o2.name);
            }
        });
    }
}
