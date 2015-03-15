package com.tsoft.dbcompare.config;

import com.tsoft.dbcompare.compare.DatabaseComparator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.util.Assert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompareApplication {
    private List<CompareConfig> configList = new ArrayList<CompareConfig>();

    public void loadConfigs(String... configFileNames) {
        Assert.notNull(configFileNames);

        configList.clear();
        for (String configFileName : configFileNames) {
            ApplicationContext context = new FileSystemXmlApplicationContext(configFileName);
            String[] compareConfigNames = context.getBeanNamesForType(CompareConfig.class);
            for (String compareConfigName : compareConfigNames) {
                CompareConfig compareConfig = (CompareConfig) context.getBean(compareConfigName);
                configList.add(compareConfig);
            }
        }
    }

    public void startComparation() throws SQLException {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/tsoft/dbcompare/SpringContext.xml");
        for (CompareConfig config : configList) {
            DatabaseComparator comparator = (DatabaseComparator) context.getBean("databaseComparator");
            comparator.compare(config);
        }
    }

    public List<CompareConfig> getConfigList() {
        return configList;
    }
}
