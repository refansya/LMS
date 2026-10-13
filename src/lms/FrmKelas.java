package lms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        UI.combo(panelForm, "Wali Kelas", 220, cmbWali);

        btnSimpan = UI.tombol(panelForm, "SIMPAN", UI.HIJAU, UI.HIJAU_H, 25, 310, 145, 42, this::aksiSimpan);
        UI.tombol(panelForm, "BATAL / RESET", UI.ABU, UI.ABU_H, 180, 310, 145, 42, this::resetForm);

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
    private void muatWali() {
        cmbWali.removeAllItems();
        cmbWali.addItem(new UI.Item(0, "- Belum ditentukan -"));
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) return;
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id_guru, nama_guru FROM tb_guru ORDER BY nama_guru")) {
                while (rs.next()) cmbWali.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
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
        cmbWali.setSelectedIndex(0);
        idEdit = 0;
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
                    UI.pilihItem(cmbWali, rs.getInt("id_wali_kelas")); // 0 jika NULL
                }
            }
            idEdit = id;
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
        int idWali = ((UI.Item) cmbWali.getSelectedItem()).id;

        String sql = (idEdit == 0)
            ? "INSERT INTO tb_kelas (nama_kelas, tingkat, tahun_ajaran, id_wali_kelas) VALUES (?,?,?,?)"
            : "UPDATE tb_kelas SET nama_kelas=?, tingkat=?, tahun_ajaran=?, id_wali_kelas=? WHERE id_kelas=?";

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }

            if (sudahAda(con, nama, tahun, idEdit)) {
                UI.peringatan(this, "Kelas \"" + nama + "\" untuk tahun ajaran " + tahun + " sudah ada.");
                return;
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
              + "Jadwal, materi, tugas, dan kuis milik kelas ini ikut terhapus.\n"
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