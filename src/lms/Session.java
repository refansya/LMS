package lms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Menyimpan data user yang sedang login.
 * Diisi di FrmLogin, dibaca oleh form lain.
 */
public class Session {

    public static int idUser = 0;
    public static String username = "";
    public static String role = "";
    public static String namaLengkap = "";

    // khusus guru
    public static int idGuru = 0;
    public static String nip = "";

    // khusus siswa
    public static int idSiswa = 0;
    public static int idKelas = 0;
    public static String nis = "";

    public static void clear() {
        idUser = 0;
        username = "";
        role = "";
        namaLengkap = "";
        idGuru = 0;
        nip = "";
        idSiswa = 0;
        idKelas = 0;
        nis = "";
    }

    /** Isi session setelah login berhasil. */
    public static void muat(Connection con, int idUser, String username,
                            String role, String namaLengkap) throws SQLException {
        clear();
        Session.idUser = idUser;
        Session.username = username;
        Session.role = role;
        Session.namaLengkap = namaLengkap;

        if (role.equals("guru")) {
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id_guru, nip FROM tb_guru WHERE id_user=?")) {
                ps.setInt(1, idUser);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idGuru = rs.getInt("id_guru");
                        nip = rs.getString("nip");
                    }
                }
            }
        } else if (role.equals("siswa")) {
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id_siswa, nis, id_kelas FROM tb_siswa WHERE id_user=?")) {
                ps.setInt(1, idUser);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idSiswa = rs.getInt("id_siswa");
                        nis = rs.getString("nis");
                        idKelas = rs.getInt("id_kelas");
                    }
                }
            }
        }
    }

    /** Huruf inisial untuk avatar, contoh "Andi Pratama" -> "AP". */
    public static String inisial() {
        if (namaLengkap == null || namaLengkap.trim().isEmpty()) return "?";
        String[] kata = namaLengkap.trim().split("\\s+");
        String hasil = kata[0].substring(0, 1);
        if (kata.length > 1) hasil += kata[1].substring(0, 1);
        return hasil.toUpperCase();
    }
}