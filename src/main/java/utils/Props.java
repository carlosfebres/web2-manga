package utils;

import java.io.*;
import java.util.Properties;

public class Props {
    public static Props props = null;

    public Properties properties;

    public Props() {
        properties = new Properties();
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
            properties.load(is);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String getProperty(String s) {
        if (Props.props == null) {
            Props.props = new Props();
        }
        return Props.props.properties.getProperty(s);
    }
}
