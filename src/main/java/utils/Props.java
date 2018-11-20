package utils;

import java.io.*;
import java.util.Properties;

public class Props {
    Properties props;

    Props() {
        props = new Properties();
        InputStream is = null;

        try {
            File f = new File("C:\\java\\web2\\Manga - Copy\\config.properties");
            is = new FileInputStream(f);
        } catch (Exception e) {
            System.out.println(e);
            is = null;
        }

        try {
            if (is == null) {
                is = getClass().getResourceAsStream("C:\\java\\web2\\Manga - Copy\\config.properties");
            }
            props.load(is);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    String getProperty(String s) {
        return props.getProperty(s);
    }
}
