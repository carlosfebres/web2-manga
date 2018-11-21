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
        String url = "jdbc:mysql://" + Props.getProperty("DBHost") + ":" + Props.getProperty("DBPort") + "/" + Props.getProperty("DBName");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, Props.getProperty("DBUser"), Props.getProperty("DBPass"));
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error: " + e);
        }
        return conn;
    }
}
