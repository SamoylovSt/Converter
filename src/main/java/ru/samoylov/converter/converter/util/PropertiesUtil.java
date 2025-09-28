package ru.samoylov.converter.converter.util;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private PropertiesUtil(){}

    private static void loadProperties() {
        try (var input = PropertiesUtil.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find database.properties");
                return;
            }
            PROPERTIES.load(input);
            System.out.println("property is loaded");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

}
