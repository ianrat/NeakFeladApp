/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package neakfeladapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author tarna
 */
public class DatabaseConnection {
    
    private static final String URL = "jdbc:oracle:thin:@10.120.26.21:1521:kvik";
    private static final String DB_USER = "kvikmod";
    private static final String DB_PASSWORD = "kiscelli18";
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC driver nem található!");
        }
    }    
}
