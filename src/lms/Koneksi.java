package lms;

import java.sql.Connection;
import java.sql.DriverManager;

public class Koneksi {

    private static final String URL  = "jdbc:mysql://localhost:3306/lms";
    private static final String USER = "root";
    private static final String PASS = "";

    /**
     * Membuat koneksi BARU setiap dipanggil.
     * Pemanggil wajib menutupnya (pakai try-with-resources).
     * Mengembalikan null jika gagal.
     */
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