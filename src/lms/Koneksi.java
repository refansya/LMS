package lms;

import java.sql.*;
 
public class Koneksi {
    static Connection con;
    static String url = "jdbc:mysql://localhost/db_lms_smpn33";
    static String user = "root";
    static String pass = "";
 
    public static Connection getKoneksi() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
        return con;
    }
}
