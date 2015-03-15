package com.tsoft.dbcompare;

import com.tsoft.dbcompare.config.CompareApplication;

import java.io.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        if (args.length == 0) {
            System.out.println("Usage <config file name>\n");
            System.out.println("Example:");

            InputStream exampleConfigStream = Main.class.getResourceAsStream("ConfigExample.xml");
            BufferedReader in = new BufferedReader(new InputStreamReader(exampleConfigStream));
            try {
                while (in.ready()) {
                    String text = in.readLine();
                    System.out.println(text);
                }
            } finally {
                in.close();
            }
            System.exit(1);
        }

        CompareApplication app = new CompareApplication();
        app.loadConfigs(args);

        app.startComparation();
    }
}
