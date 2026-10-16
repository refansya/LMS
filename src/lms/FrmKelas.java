package lms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Types;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FrmKelas extends JFrame {

    private static final String TAHUN_DEFAULT = "2026/2027";

    private JLabel lblFormTitle;
    private JTextField txtNama, txtTahun, txtCari;
    private JComboBox<String> cmbTingkat;
    private JComboBox<UI.Item> cmbWali;
    private DefaultTableModel model;
    private JTable tabel;
    private RoundedButton btnSimpan;

    private int idEdit = 0; // 0 = tambah, >0 = edit (id_kelas)

    public FrmKelas() {
        initUI();
        muatWali();
        muatTabel();
    }

    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Manajemen Kelas", "MANAJEMEN DATA KELAS");

        // ---------- FORM ----------
        RoundedPanel panelForm = UI.panelForm(bg);
        lblFormTitle = UI.judulForm(panelForm, "Form Input Kelas");
        txtNama = UI.field(panelForm, "Nama Kelas * (contoh: VIII A)", 52);

        cmbTingkat = new JComboBox<>(new String[]{"7", "8", "9"});
        UI.combo(panelForm, "Tingkat *", 108, cmbTingkat);

        txtTahun = UI.field(panelForm, "Tahun Ajaran * (contoh: 2026/2027)", 164);
        txtTahun.setText(TAHUN_DEFAULT);

        cmbWali = new JComboBox<>();
        UI.combo(panelForm, "Wali Kelas (satu guru hanya satu kelas)", 220, cmbWali);

        // daftar wali ikut diperbarui saat tahun ajaran diubah (status "sudah wali" dihitung per tahun ajaran)
        txtTahun.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) { muatWali(); }
        });

        JLabel catatan = new JLabel("<html>Guru yang sudah menjadi wali kelas lain pada tahun ajaran yang sama "
            + "ditandai \"(sudah wali ...)\" dan tidak bisa dipilih lagi.</html>");
        catatan.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 11));
        catatan.setForeground(java.awt.Color.GRAY);
        catatan.setBounds(25, 270, 300, 40);
        panelForm.add(catatan);

        btnSimpan = UI.tombol(panelForm, "SIMPAN", UI.HIJAU, UI.HIJAU_H, 25, 330, 145, 42, this::aksiSimpan);
        UI.tombol(panelForm, "BATAL / RESET", UI.ABU, UI.ABU_H, 180, 330, 145, 42, this::resetForm);

        // ---------- TABEL ----------
        RoundedPanel panelTabel = UI.panelTabel(bg, "Daftar Kelas");
        model = UI.modelTabel("ID", "No", "Nama Kelas", "Tingkat", "Tahun Ajaran", "Wali Kelas", "Siswa");
        tabel = UI.tabel(model);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(35);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(80);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(55);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(150);
        tabel.getColumnModel().getColumn(5).setPreferredWidth(45);
        UI.tambahTabel(panelTabel, tabel);
        txtCari = UI.kolomCari(panelTabel, this::muatTabel);

        UI.tombol(panelTabel, "UBAH DATA", UI.KUNING, UI.KUNING_H, 260, 458, 120, 38, this::aksiUbah);
        UI.tombol(panelTabel, "HAPUS", UI.MERAH, UI.MERAH_H, 395, 458, 120, 38, this::aksiHapus);
    }

    // ------------------------------------------------------------------
    /**
     * Mengisi combo wali kelas. Guru yang sudah menjadi wali kelas LAIN pada tahun ajaran yang sedang
     * diisi di form diberi keterangan "(sudah wali ...)". Kelas yang sedang diedit tidak dihitung.
     */
    private void muatWali() {
        UI.Item lama = (UI.Item) cmbWali.getSelectedItem();
        int pilih = lama == null ? 0 : lama.id;
        String tahun = txtTahun.getText().trim();

        cmbWali.removeAllItems();
        cmbWali.addItem(new UI.Item(0, "- Belum ditentukan -"));
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) return;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT g.id_guru, g.nama_guru, "
                  + "(SELECT k.nama_kelas FROM tb_kelas k WHERE k.id_wali_kelas = g.id_guru "
                  + " AND k.tahun_ajaran = ? AND k.id_kelas <> ? LIMIT 1) AS dipakai "
                  + "FROM tb_guru g ORDER BY g.nama_guru")) {
                ps.setString(1, tahun);
                ps.setInt(2, idEdit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String dipakai = rs.getString("dipakai");
                        cmbWali.addItem(new UI.Item(rs.getInt("id_guru"), rs.getString("nama_guru")
                            + (dipakai == null ? "" : "  (sudah wali " + dipakai + ")")));
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
        UI.pilihItem(cmbWali, pilih);
    }

    /** Nama kelas lain (pada tahun ajaran yang sama) yang sudah diwalikan guru ini, atau null jika belum. */
    private String waliTerpakai(Connection con, int idWali, String tahun, int kecualiId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT nama_kelas FROM tb_kelas WHERE id_wali_kelas = ? AND tahun_ajaran = ? "
              + "AND id_kelas <> ? LIMIT 1")) {
            ps.setInt(1, idWali);
            ps.setString(2, tahun);
            ps.setInt(3, kecualiId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    private void muatTabel() {
        model.setRowCount(0);
        String kata = "%" + txtCari.getText().trim() + "%";
        String sql = "SELECT k.id_kelas, k.nama_kelas, k.tingkat, COALESCE(k.tahun_ajaran,'-') AS tahun, "
                   + "COALESCE(g.nama_guru,'-') AS wali, "
                   + "(SELECT COUNT(*) FROM tb_siswa s WHERE s.id_kelas = k.id_kelas) AS jml "
                   + "FROM tb_kelas k LEFT JOIN tb_guru g ON k.id_wali_kelas = g.id_guru "
                   + "WHERE k.nama_kelas LIKE ? ORDER BY k.tingkat, k.nama_kelas";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, kata);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        model.addRow(new Object[]{rs.getInt("id_kelas"), no++, rs.getString("nama_kelas"),
                            rs.getString("tingkat"), rs.getString("tahun"), rs.getString("wali"), rs.getInt("jml")});
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private void resetForm() {
        txtNama.setText("");
        txtTahun.setText(TAHUN_DEFAULT);
        cmbTingkat.setSelectedIndex(0);
        idEdit = 0;
        muatWali();
        cmbWali.setSelectedIndex(0);
        lblFormTitle.setText("Form Input Kelas");
        btnSimpan.setText("SIMPAN");
        tabel.clearSelection();
        txtNama.requestFocus();
    }

    private void aksiUbah() {
        int id = UI.idTerpilih(tabel, model);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu kelas yang ingin diubah di tabel.");
            return;
        }
        int idWali = 0;
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT nama_kelas, tingkat, tahun_ajaran, id_wali_kelas FROM tb_kelas WHERE id_kelas=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return;
                    txtNama.setText(rs.getString("nama_kelas"));
                    cmbTingkat.setSelectedItem(rs.getString("tingkat"));
                    txtTahun.setText(rs.getString("tahun_ajaran") == null ? "" : rs.getString("tahun_ajaran"));
                    idWali = rs.getInt("id_wali_kelas"); // 0 jika NULL
                }
            }
            idEdit = id;
            muatWali();                 // setelah idEdit diisi, kelas ini tidak dihitung sebagai "sudah wali"
            UI.pilihItem(cmbWali, idWali);
            lblFormTitle.setText("Edit Data Kelas");
            btnSimpan.setText("UPDATE");
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private String validasi() {
        String nama = txtNama.getText().trim();
        String tahun = txtTahun.getText().trim();
        if (nama.isEmpty()) return "Nama kelas wajib diisi.";
        if (nama.length() > 20) return "Nama kelas maksimal 20 karakter.";
        if (!tahun.matches("\\d{4}/\\d{4}")) return "Tahun ajaran harus berformat 2026/2027.";
        return null;
    }

    private boolean sudahAda(Connection con, String nama, String tahun, int kecualiId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM tb_kelas WHERE nama_kelas=? AND tahun_ajaran=? AND id_kelas<>?")) {
            ps.setString(1, nama);
            ps.setString(2, tahun);
            ps.setInt(3, kecualiId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private void aksiSimpan() {
        String err = validasi();
        if (err != null) { UI.peringatan(this, err); return; }

        String nama = txtNama.getText().trim();
        String tingkat = cmbTingkat.getSelectedItem().toString();
        String tahun = txtTahun.getText().trim();
        UI.Item wali = (UI.Item) cmbWali.getSelectedItem();
        int idWali = wali == null ? 0 : wali.id;

        String sql = (idEdit == 0)
            ? "INSERT INTO tb_kelas (nama_kelas, tingkat, tahun_ajaran, id_wali_kelas) VALUES (?,?,?,?)"
            : "UPDATE tb_kelas SET nama_kelas=?, tingkat=?, tahun_ajaran=?, id_wali_kelas=? WHERE id_kelas=?";

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }

            if (sudahAda(con, nama, tahun, idEdit)) {
                UI.peringatan(this, "Kelas \"" + nama + "\" untuk tahun ajaran " + tahun + " sudah ada.");
                return;
            }
            if (idWali != 0) {
                String kelasLain = waliTerpakai(con, idWali, tahun, idEdit);
                if (kelasLain != null) {
                    String namaGuru = wali.label.replaceAll("\\s+\\(sudah wali.*\\)$", "");
                    UI.peringatan(this, namaGuru + " sudah menjadi wali kelas " + kelasLain
                        + " pada tahun ajaran " + tahun + ".\nSatu guru hanya boleh menjadi wali satu kelas.\n"
                        + "Pilih guru lain atau kosongkan dulu wali di kelas " + kelasLain + ".");
                    return;
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nama);
                ps.setString(2, tingkat);
                ps.setString(3, tahun);
                if (idWali == 0) ps.setNull(4, Types.INTEGER); else ps.setInt(4, idWali);
                if (idEdit != 0) ps.setInt(5, idEdit);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, idEdit == 0
                ? "Kelas berhasil ditambahkan." : "Data kelas berhasil diperbarui.");
            resetForm();
            muatTabel();
        } catch (SQLIntegrityConstraintViolationException dup) {
            UI.peringatan(this, "Data tidak bisa disimpan: nama kelas atau wali kelas ini sudah dipakai "
                + "pada tahun ajaran " + tahun + ".");
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private void aksiHapus() {
        int id = UI.idTerpilih(tabel, model);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu kelas yang ingin dihapus di tabel.");
            return;
        }
        String nama = UI.teksTerpilih(tabel, model, 2);
        String jml = UI.teksTerpilih(tabel, model, 6);
        if (!UI.konfirmasiHapus(this,
                "Hapus kelas \"" + nama + "\"?\n\n"
              + "Siswa di kelas ini (" + jml + " siswa) TIDAK dihapus, hanya kelasnya dikosongkan.\n"
              + "Jadwal, penugasan mengajar, materi, tugas, dan kuis milik kelas ini ikut terhapus.\n"
              + "Tindakan ini tidak bisa dibatalkan.")) return;

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM tb_kelas WHERE id_kelas=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            if (idEdit == id) resetForm();
            muatTabel();
            JOptionPane.showMessageDialog(this, "Kelas berhasil dihapus.");
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmKelas().setVisible(true));
    }
}