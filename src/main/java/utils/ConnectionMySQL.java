package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionMySQL {
    private static Connection conn = null;


    public static Connection getConnection() {
        if ( conn != null )
            return conn;

        System.out.println("Creating DB Connection!");
        Props props = new Props();
        String url = "jdbc:mysql://" + props.getProperty("DBHost") + ":" + "3306" + "/" + props.getProperty("DBName");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, props.getProperty("DBUser"), props.getProperty("DBPass"));
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error: " + e);
        }
        return conn;
    }
}
