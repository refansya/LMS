package lms;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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

public class FrmDataSiswa extends JFrame {

    private static final String PASSWORD_AWAL = "siswa123";

    private JLabel lblFormTitle;
    private JTextField txtNis, txtNama, txtTempat, txtTanggal, txtHp, txtCari;
    private JComboBox<String> cmbJk;
    private JComboBox<UI.Item> cmbKelas;
    private DefaultTableModel model;
    private JTable tabel;
    private RoundedButton btnSimpan;

    private int idEdit = 0; // 0 = tambah, >0 = edit (id_siswa)

    public FrmDataSiswa() {
        initUI();
        muatKelas();
        muatTabel();
    }

    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Data Siswa", "PENGELOLAAN DATA SISWA");

        // ---------- FORM ----------
        RoundedPanel panelForm = UI.panelForm(bg);
        lblFormTitle = UI.judulForm(panelForm, "Form Input Siswa");

        txtNis = UI.field(panelForm, "NIS / Nomor Induk Siswa *", 52);
        txtNama = UI.field(panelForm, "Nama Lengkap *", 108);

        cmbJk = new JComboBox<>(new String[]{"L - Laki-laki", "P - Perempuan"});
        UI.combo(panelForm, "Jenis Kelamin *", 164, cmbJk);

        cmbKelas = new JComboBox<>();
        UI.combo(panelForm, "Kelas", 220, cmbKelas);

        txtTempat = UI.field(panelForm, "Tempat Lahir", 276);
        txtTanggal = UI.field(panelForm, "Tanggal Lahir (format: 2012-01-31)", 332);
        txtHp = UI.field(panelForm, "No. HP", 388);

        btnSimpan = UI.tombol(panelForm, "SIMPAN", UI.HIJAU, UI.HIJAU_H, 25, 458, 145, 42, this::aksiSimpan);
        UI.tombol(panelForm, "BATAL / RESET", UI.ABU, UI.ABU_H, 180, 458, 145, 42, this::resetForm);

        // ---------- TABEL ----------
        RoundedPanel panelTabel = UI.panelTabel(bg, "Daftar Siswa Aktif");
        model = UI.modelTabel("ID", "No", "NIS", "Nama Siswa", "JK", "Kelas", "No HP");
        tabel = UI.tabel(model);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(35);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(80);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(160);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(35);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(70);
        tabel.getColumnModel().getColumn(5).setPreferredWidth(100);
        UI.tambahTabel(panelTabel, tabel);
        txtCari = UI.kolomCari(panelTabel, this::muatTabel);

        UI.tombol(panelTabel, "UBAH DATA", UI.KUNING, UI.KUNING_H, 260, 458, 120, 38, this::aksiUbah);
        UI.tombol(panelTabel, "HAPUS", UI.MERAH, UI.MERAH_H, 395, 458, 120, 38, this::aksiHapus);
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    private void muatKelas() {
        cmbKelas.removeAllItems();
        cmbKelas.addItem(new UI.Item(0, "- Belum ada kelas -"));
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) return;
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                     "SELECT id_kelas, nama_kelas FROM tb_kelas ORDER BY tingkat, nama_kelas")) {
                while (rs.next()) cmbKelas.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private void muatTabel() {
        model.setRowCount(0);
        String kata = "%" + txtCari.getText().trim() + "%";
        String sql = "SELECT s.id_siswa, s.nis, s.nama_siswa, s.jenis_kelamin, "
                   + "COALESCE(k.nama_kelas,'-') AS kelas, COALESCE(s.no_hp,'-') AS no_hp "
                   + "FROM tb_siswa s LEFT JOIN tb_kelas k ON s.id_kelas = k.id_kelas "
                   + "WHERE s.nama_siswa LIKE ? OR s.nis LIKE ? ORDER BY k.nama_kelas, s.nama_siswa";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, kata);
                ps.setString(2, kata);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        model.addRow(new Object[]{rs.getInt("id_siswa"), no++, rs.getString("nis"),
                            rs.getString("nama_siswa"), rs.getString("jenis_kelamin"),
                            rs.getString("kelas"), rs.getString("no_hp")});
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    // ------------------------------------------------------------------
    //  AKSI
    // ------------------------------------------------------------------
    private void resetForm() {
        txtNis.setText("");
        txtNama.setText("");
        txtTempat.setText("");
        txtTanggal.setText("");
        txtHp.setText("");
        cmbJk.setSelectedIndex(0);
        cmbKelas.setSelectedIndex(0);
        idEdit = 0;
        lblFormTitle.setText("Form Input Siswa");
        btnSimpan.setText("SIMPAN");
        tabel.clearSelection();
        txtNis.requestFocus();
    }

    private void aksiUbah() {
        int id = UI.idTerpilih(tabel, model);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu siswa yang ingin diubah di tabel.");
            return;
        }
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT nis, nama_siswa, jenis_kelamin, tempat_lahir, tanggal_lahir, no_hp, id_kelas "
                  + "FROM tb_siswa WHERE id_siswa=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return;
                    txtNis.setText(rs.getString("nis"));
                    txtNama.setText(rs.getString("nama_siswa"));
                    cmbJk.setSelectedIndex(rs.getString("jenis_kelamin").equals("L") ? 0 : 1);
                    txtTempat.setText(rs.getString("tempat_lahir") == null ? "" : rs.getString("tempat_lahir"));
                    Date tgl = rs.getDate("tanggal_lahir");
                    txtTanggal.setText(tgl == null ? "" : tgl.toString());
                    txtHp.setText(rs.getString("no_hp") == null ? "" : rs.getString("no_hp"));
                    UI.pilihItem(cmbKelas, rs.getInt("id_kelas")); // 0 jika NULL
                }
            }
            idEdit = id;
            lblFormTitle.setText("Edit Data Siswa");
            btnSimpan.setText("UPDATE");
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private String validasi() {
        String nis = txtNis.getText().trim();
        String nama = txtNama.getText().trim();
        String tempat = txtTempat.getText().trim();
        String tgl = txtTanggal.getText().trim();
        String hp = txtHp.getText().trim();

        if (nis.isEmpty()) return "NIS wajib diisi.";
        if (nis.length() > 20) return "NIS maksimal 20 karakter.";
        if (nis.contains(" ")) return "NIS tidak boleh mengandung spasi.";
        if (nama.isEmpty()) return "Nama lengkap wajib diisi.";
        if (nama.length() > 100) return "Nama maksimal 100 karakter.";
        if (tempat.length() > 50) return "Tempat lahir maksimal 50 karakter.";
        if (!tgl.isEmpty()) {
            if (!tgl.matches("\\d{4}-\\d{2}-\\d{2}")) return "Tanggal lahir harus berformat 2012-01-31.";
            try {
                Date.valueOf(tgl);
            } catch (IllegalArgumentException e) {
                return "Tanggal lahir tidak valid.";
            }
        }
        if (!hp.isEmpty() && !hp.matches("\\d{8,15}")) return "No. HP harus berupa angka 8-15 digit.";
        return null;
    }

    private void isiParameter(PreparedStatement ps, String nis, String nama, String jk, String tempat,
                              String tgl, String hp, int idKelas) throws SQLException {
        ps.setString(1, nis);
        ps.setString(2, nama);
        ps.setString(3, jk);
        ps.setString(4, tempat.isEmpty() ? null : tempat);
        if (tgl.isEmpty()) ps.setNull(5, Types.DATE); else ps.setDate(5, Date.valueOf(tgl));
        ps.setString(6, hp.isEmpty() ? null : hp);
        if (idKelas == 0) ps.setNull(7, Types.INTEGER); else ps.setInt(7, idKelas);
    }

    private void aksiSimpan() {
        String err = validasi();
        if (err != null) { UI.peringatan(this, err); return; }

        String nis = txtNis.getText().trim();
        String nama = txtNama.getText().trim();
        String jk = cmbJk.getSelectedItem().toString().substring(0, 1);
        String tempat = txtTempat.getText().trim();
        String tgl = txtTanggal.getText().trim();
        String hp = txtHp.getText().trim();
        int idKelas = ((UI.Item) cmbKelas.getSelectedItem()).id;

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            con.setAutoCommit(false);
            try {
                if (idEdit == 0) {
                    // ---- TAMBAH: akun login + data siswa dalam satu transaksi ----
                    int idUser;
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO tb_user (username, password, role, nama_lengkap, status) "
                          + "VALUES (?, ?, 'siswa', ?, 'aktif')", Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, nis);
                        ps.setString(2, PASSWORD_AWAL);
                        ps.setString(3, nama);
                        ps.executeUpdate();
                        try (ResultSet k = ps.getGeneratedKeys()) {
                            k.next();
                            idUser = k.getInt(1);
                        }
                    }
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO tb_siswa (nis, nama_siswa, jenis_kelamin, tempat_lahir, tanggal_lahir, "
                          + "no_hp, id_kelas, id_user) VALUES (?,?,?,?,?,?,?,?)")) {
                        isiParameter(ps, nis, nama, jk, tempat, tgl, hp, idKelas);
                        ps.setInt(8, idUser);
                        ps.executeUpdate();
                    }
                    con.commit();
                    JOptionPane.showMessageDialog(this,
                        "Siswa berhasil ditambahkan.\n\nAkun login otomatis dibuat:\n"
                      + "Username : " + nis + "\nPassword : " + PASSWORD_AWAL);
                } else {
                    // ---- UBAH ----
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE tb_siswa SET nis=?, nama_siswa=?, jenis_kelamin=?, tempat_lahir=?, "
                          + "tanggal_lahir=?, no_hp=?, id_kelas=? WHERE id_siswa=?")) {
                        isiParameter(ps, nis, nama, jk, tempat, tgl, hp, idKelas);
                        ps.setInt(8, idEdit);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE tb_user u JOIN tb_siswa s ON u.id_user = s.id_user "
                          + "SET u.nama_lengkap=? WHERE s.id_siswa=?")) {
                        ps.setString(1, nama);
                        ps.setInt(2, idEdit);
                        ps.executeUpdate();
                    }
                    con.commit();
                    JOptionPane.showMessageDialog(this, "Data siswa berhasil diperbarui.");
                }
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
            resetForm();
            muatTabel();
        } catch (SQLIntegrityConstraintViolationException dup) {
            UI.peringatan(this, "NIS / username \"" + nis + "\" sudah terdaftar. Gunakan NIS lain.");
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private void aksiHapus() {
        int id = UI.idTerpilih(tabel, model);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu siswa yang ingin dihapus di tabel.");
            return;
        }
        String nama = UI.teksTerpilih(tabel, model, 3);
        if (!UI.konfirmasiHapus(this,
                "Hapus siswa \"" + nama + "\"?\n\n"
              + "Akun login-nya, nilai, dan pengumpulan tugasnya juga akan dihapus.\n"
              + "Tindakan ini tidak bisa dibatalkan.")) return;

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            con.setAutoCommit(false);
            try {
                int idUser = 0;
                try (PreparedStatement ps = con.prepareStatement("SELECT id_user FROM tb_siswa WHERE id_siswa=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) idUser = rs.getInt(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM tb_siswa WHERE id_siswa=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                if (idUser > 0) {
                    try (PreparedStatement ps = con.prepareStatement("DELETE FROM tb_user WHERE id_user=?")) {
                        ps.setInt(1, idUser);
                        ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
            if (idEdit == id) resetForm();
            muatTabel();
            JOptionPane.showMessageDialog(this, "Siswa berhasil dihapus.");
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmDataSiswa().setVisible(true));
    }
}