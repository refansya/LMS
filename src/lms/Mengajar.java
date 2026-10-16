package lms;

import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;

/**
 * Helper untuk penugasan mengajar: satu guru boleh mengajar beberapa mapel di beberapa kelas.
 * Data ada di tb_guru_mengajar (id_guru, id_mapel, id_kelas).
 */
public class Mengajar {

    /** Apakah guru ini sudah punya penugasan mengajar sama sekali. */
    public static boolean adaPenugasan(Connection con, int idGuru) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM tb_guru_mengajar WHERE id_guru = ?")) {
            ps.setInt(1, idGuru);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Kelas yang berkaitan dengan guru: wali kelas, penugasan mengajar, atau sudah punya
     * jadwal/tugas/materi/kuis. Jika tidak ada satu pun, kembalikan semua kelas.
     */
    public static List<UI.Item> kelas(Connection con, int idGuru) throws SQLException {
        List<UI.Item> hasil = new ArrayList<>();
        if (idGuru != 0) {
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT k.id_kelas, k.nama_kelas FROM tb_kelas k "
                  + "WHERE k.id_wali_kelas = ? "
                  + "OR k.id_kelas IN (SELECT id_kelas FROM tb_guru_mengajar WHERE id_guru = ?) "
                  + "OR k.id_kelas IN (SELECT id_kelas FROM tb_jadwal WHERE id_guru = ?) "
                  + "OR k.id_kelas IN (SELECT id_kelas FROM tb_tugas WHERE id_guru = ?) "
                  + "OR k.id_kelas IN (SELECT id_kelas FROM tb_materi WHERE id_guru = ?) "
                  + "OR k.id_kelas IN (SELECT id_kelas FROM tb_kuis WHERE id_guru = ?) "
                  + "ORDER BY k.tingkat, k.nama_kelas")) {
                for (int i = 1; i <= 6; i++) ps.setInt(i, idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) hasil.add(new UI.Item(rs.getInt(1), rs.getString(2)));
                }
            }
        }
        if (hasil.isEmpty()) {
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                     "SELECT id_kelas, nama_kelas FROM tb_kelas ORDER BY tingkat, nama_kelas")) {
                while (rs.next()) hasil.add(new UI.Item(rs.getInt(1), rs.getString(2)));
            }
        }
        return hasil;
    }

    /**
     * Mapel yang diajar guru. Jika idKelas > 0, hanya mapel yang diajar di kelas itu.
     * Jika guru belum punya penugasan sama sekali, kembalikan semua mapel (agar aplikasi tetap jalan).
     */
    public static List<UI.Item> mapel(Connection con, int idGuru, int idKelas) throws SQLException {
        List<UI.Item> hasil = new ArrayList<>();
        if (idGuru != 0 && adaPenugasan(con, idGuru)) {
            String sql = "SELECT DISTINCT m.id_mapel, m.nama_mapel FROM tb_guru_mengajar gm "
                       + "JOIN tb_mapel m ON gm.id_mapel = m.id_mapel WHERE gm.id_guru = ? "
                       + (idKelas > 0 ? "AND gm.id_kelas = ? " : "") + "ORDER BY m.nama_mapel";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idGuru);
                if (idKelas > 0) ps.setInt(2, idKelas);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) hasil.add(new UI.Item(rs.getInt(1), rs.getString(2)));
                }
            }
        } else {
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id_mapel, nama_mapel FROM tb_mapel ORDER BY nama_mapel")) {
                while (rs.next()) hasil.add(new UI.Item(rs.getInt(1), rs.getString(2)));
            }
        }
        return hasil;
    }

    /** true jika guru boleh mengajar mapel itu di kelas itu (atau guru belum punya penugasan sama sekali). */
    public static boolean boleh(Connection con, int idGuru, int idMapel, int idKelas) throws SQLException {
        if (!adaPenugasan(con, idGuru)) return true;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM tb_guru_mengajar WHERE id_guru = ? AND id_mapel = ? AND id_kelas = ?")) {
            ps.setInt(1, idGuru);
            ps.setInt(2, idMapel);
            ps.setInt(3, idKelas);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /** id mapel utama guru (tb_guru.id_mapel), 0 jika belum ditentukan. */
    public static int mapelUtama(Connection con, int idGuru) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT id_mapel FROM tb_guru WHERE id_guru = ?")) {
            ps.setInt(1, idGuru);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0; // getInt = 0 jika NULL
            }
        }
    }

    /**
     * Mengisi combo kelas dan mapel milik guru yang login, dengan mapel yang ikut menyesuaikan
     * kelas yang dipilih (hanya mapel yang memang diajar di kelas itu).
     * Saat mengisi data untuk mode edit, pilih KELAS lebih dulu baru MAPEL.
     *
     * @param placeholderKelas true = tambahkan pilihan "- Pilih kelas -" di urutan pertama
     */
    public static void pasang(Component parent, JComboBox<UI.Item> cmbKelas, JComboBox<UI.Item> cmbMapel,
                              boolean placeholderKelas) {
        cmbKelas.removeAllItems();
        if (placeholderKelas) cmbKelas.addItem(new UI.Item(0, "- Pilih kelas -"));
        int utama = 0;
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) {
                UI.koneksiGagal(parent);
            } else {
                for (UI.Item k : kelas(con, Session.idGuru)) cmbKelas.addItem(k);
                utama = mapelUtama(con, Session.idGuru);
            }
        } catch (SQLException ex) {
            UI.errorDb(parent, ex);
        }
        isiMapel(parent, cmbKelas, cmbMapel, utama);
        cmbKelas.addActionListener(e -> isiMapel(parent, cmbKelas, cmbMapel, -1));
    }

    /** @param pilihId mapel yang dipilih setelah diisi; -1 = pertahankan pilihan sebelumnya jika masih ada. */
    private static void isiMapel(Component parent, JComboBox<UI.Item> cmbKelas, JComboBox<UI.Item> cmbMapel,
                                 int pilihId) {
        UI.Item k = (UI.Item) cmbKelas.getSelectedItem();
        int idKelas = k == null ? 0 : k.id;
        if (pilihId < 0) {
            UI.Item lama = (UI.Item) cmbMapel.getSelectedItem();
            pilihId = lama == null ? 0 : lama.id;
        }
        cmbMapel.removeAllItems();
        cmbMapel.addItem(new UI.Item(0, "- Pilih mata pelajaran -"));
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(parent); return; }
            for (UI.Item m : mapel(con, Session.idGuru, idKelas)) cmbMapel.addItem(m);
        } catch (SQLException ex) {
            UI.errorDb(parent, ex);
        }
        UI.pilihItem(cmbMapel, pilihId);
        if (cmbMapel.getSelectedIndex() == 0 && cmbMapel.getItemCount() == 2) {
            cmbMapel.setSelectedIndex(1); // hanya satu pilihan -> langsung dipilih
        }
    }
}