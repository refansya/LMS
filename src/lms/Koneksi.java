package lms;

<<<<<<< HEAD
import java.sql.Connection;
import java.sql.DriverManager;

public class Koneksi {

    private static final String URL  = "jdbc:mysql://localhost:3306/lms";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getKoneksi() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
            return null;
        }
    }
}
=======
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
>>>>>>> b323dc30037ccdf9c2133f5e0b0c6e2675f3a553
