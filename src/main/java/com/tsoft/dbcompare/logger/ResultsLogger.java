package com.tsoft.dbcompare.logger;

import java.util.logging.Level;

public abstract class ResultsLogger {
    private Level level;

    public void log(Level level, String msg) {
        if (level.intValue() >= getLevel().intValue()) {
            System.out.format("%13s: %4s: %s\n", getClass().getSimpleName().substring(0, 12), level.getName().substring(0, 4), msg);
        }
    }

    public void logln() {
        System.out.println();
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
