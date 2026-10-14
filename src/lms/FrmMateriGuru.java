package lms;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class FrmMateriGuru extends JFrame {

    private JLabel lblFormTitle;
    private JTextField txtJudul, txtBab, txtFile, txtCari;
    private JTextArea txtDeskripsi;
    private JComboBox<UI.Item> cmbMapel, cmbKelas;
    private DefaultTableModel model;
    private JTable tabel;
    private RoundedButton btnSimpan;

    private int idEdit = 0;        // 0 = upload baru, >0 = edit (id_materi)
    private int idMapelGuru = 0;   // mapel bawaan guru yang login
    private File fileBaru = null;  // file yang baru dipilih (belum disalin)
    private String fileLama = null; // path file yang sedang tersimpan (mode edit)

    public FrmMateriGuru() {
        initUI();
        muatInfoGuru();
        muatPilihan();
        resetForm();
        muatTabel();

        if (Session.idGuru == 0) {
            btnSimpan.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                "Akun ini belum terhubung dengan data guru, jadi belum bisa mengunggah materi.\n"
              + "Minta administrator menambahkan Anda di menu Data Guru.",
                "Data Guru Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Manajemen Materi Guru", "MANAJEMEN MATERI BELAJAR",
            () -> new FrmMenuGuru().setVisible(true));

        // ---------- FORM ----------
        RoundedPanel form = UI.panelForm(bg);
        lblFormTitle = UI.judulForm(form, "Form Upload Materi");

        txtJudul = UI.field(form, "Judul Materi *", 52);
        txtBab = UI.field(form, "BAB / Pertemuan (contoh: BAB I)", 108);

        cmbMapel = new JComboBox<>();
        UI.combo(form, "Mata Pelajaran *", 164, cmbMapel);

        cmbKelas = new JComboBox<>();
        UI.combo(form, "Kelas *", 220, cmbKelas);

        UI.label(form, "File Materi * (maks. 25 MB)", 276);
        txtFile = new JTextField();
        txtFile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFile.setEditable(false);
        txtFile.setBounds(25, 293, 190, 28);
        txtFile.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        form.add(txtFile);
        UI.tombol(form, "PILIH FILE", new Color(15, 52, 96), new Color(10, 36, 66),
            225, 293, 100, 28, this::pilihFile);

        UI.label(form, "Deskripsi", 332);
        txtDeskripsi = new JTextArea();
        txtDeskripsi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        JScrollPane spDesk = new JScrollPane(txtDeskripsi);
        spDesk.setBounds(25, 349, 300, 60);
        spDesk.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        form.add(spDesk);

        btnSimpan = UI.tombol(form, "UPLOAD MATERI", UI.HIJAU, UI.HIJAU_H, 25, 440, 145, 42, this::aksiSimpan);
        UI.tombol(form, "BATAL / RESET", UI.ABU, UI.ABU_H, 180, 440, 145, 42, this::resetForm);

        // ---------- TABEL ----------
        RoundedPanel panelTabel = UI.panelTabel(bg, "Materi Saya");
        model = UI.modelTabel("ID", "No", "Judul Materi", "BAB", "Mapel", "Kelas", "Tanggal");
        tabel = UI.tabel(model);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(55);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(95);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(55);
        tabel.getColumnModel().getColumn(5).setPreferredWidth(80);
        UI.tambahTabel(panelTabel, tabel);
        txtCari = UI.kolomCari(panelTabel, this::muatTabel);

        UI.tombol(panelTabel, "BUKA FILE", new Color(52, 152, 219), new Color(41, 128, 185),
            190, 458, 105, 38, this::aksiBuka);
        UI.tombol(panelTabel, "UBAH", UI.KUNING, UI.KUNING_H, 305, 458, 105, 38, this::aksiUbah);
        UI.tombol(panelTabel, "HAPUS", UI.MERAH, UI.MERAH_H, 420, 458, 105, 38, this::aksiHapus);
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    private void muatInfoGuru() {
        if (Session.idGuru == 0) return;
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) return;
            try (PreparedStatement ps = con.prepareStatement("SELECT id_mapel FROM tb_guru WHERE id_guru=?")) {
                ps.setInt(1, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) idMapelGuru = rs.getInt(1); // 0 jika NULL
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private void muatPilihan() {
        cmbMapel.removeAllItems();
        cmbMapel.addItem(new UI.Item(0, "- Pilih mata pelajaran -"));
        cmbKelas.removeAllItems();
        cmbKelas.addItem(new UI.Item(0, "- Pilih kelas -"));
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (Statement st = con.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT id_mapel, nama_mapel FROM tb_mapel ORDER BY nama_mapel")) {
                    while (rs.next()) cmbMapel.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                }
                try (ResultSet rs = st.executeQuery("SELECT id_kelas, nama_kelas FROM tb_kelas ORDER BY tingkat, nama_kelas")) {
                    while (rs.next()) cmbKelas.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private void muatTabel() {
        model.setRowCount(0);
        if (Session.idGuru == 0) return;
        String kata = "%" + txtCari.getText().trim() + "%";
        String sql = "SELECT m.id_materi, m.judul, COALESCE(m.bab,'-') AS bab, "
                   + "COALESCE(mp.nama_mapel,'-') AS mapel, COALESCE(k.nama_kelas,'-') AS kelas, "
                   + "DATE_FORMAT(m.tanggal_upload,'%d-%m-%Y') AS tgl "
                   + "FROM tb_materi m "
                   + "LEFT JOIN tb_mapel mp ON m.id_mapel = mp.id_mapel "
                   + "LEFT JOIN tb_kelas k ON m.id_kelas = k.id_kelas "
                   + "WHERE m.id_guru = ? AND m.judul LIKE ? ORDER BY m.tanggal_upload DESC";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, Session.idGuru);
                ps.setString(2, kata);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        model.addRow(new Object[]{rs.getInt("id_materi"), no++, rs.getString("judul"),
                            rs.getString("bab"), rs.getString("mapel"), rs.getString("kelas"), rs.getString("tgl")});
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
        txtJudul.setText("");
        txtBab.setText("");
        txtFile.setText("");
        txtDeskripsi.setText("");
        fileBaru = null;
        fileLama = null;
        idEdit = 0;
        UI.pilihItem(cmbMapel, idMapelGuru);
        cmbKelas.setSelectedIndex(0);
        lblFormTitle.setText("Form Upload Materi");
        btnSimpan.setText("UPLOAD MATERI");
        tabel.clearSelection();
        txtJudul.requestFocus();
    }

    private void pilihFile() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Pilih file materi");
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new FileNameExtensionFilter(
            "Dokumen & media (pdf, doc, docx, ppt, pptx, xls, xlsx, txt, jpg, png, mp4, zip)",
            "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "jpg", "jpeg", "png", "mp4", "zip"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();
        if (!Storage.ekstensiDiizinkan(f.getName())) {
            UI.peringatan(this, "Jenis file tidak diizinkan.");
            return;
        }
        if (f.length() > Storage.MAKS_BYTE) {
            UI.peringatan(this, "Ukuran file melebihi 25 MB.");
            return;
        }
        fileBaru = f;
        txtFile.setText(f.getName());
    }

    private void aksiUbah() {
        int id = UI.idTerpilih(tabel, model);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu materi yang ingin diubah di tabel.");
            return;
        }
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT judul, bab, deskripsi, file_materi, id_mapel, id_kelas "
                  + "FROM tb_materi WHERE id_materi=? AND id_guru=?")) {
                ps.setInt(1, id);
                ps.setInt(2, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return;
                    txtJudul.setText(rs.getString("judul"));
                    txtBab.setText(rs.getString("bab") == null ? "" : rs.getString("bab"));
                    txtDeskripsi.setText(rs.getString("deskripsi") == null ? "" : rs.getString("deskripsi"));
                    fileLama = rs.getString("file_materi");
                    fileBaru = null;
                    txtFile.setText(Storage.namaAsli(fileLama));
                    UI.pilihItem(cmbMapel, rs.getInt("id_mapel"));
                    UI.pilihItem(cmbKelas, rs.getInt("id_kelas"));
                }
            }
            idEdit = id;
            lblFormTitle.setText("Edit Materi");
            btnSimpan.setText("UPDATE MATERI");
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private String validasi() {
        if (txtJudul.getText().trim().isEmpty()) return "Judul materi wajib diisi.";
        if (txtJudul.getText().trim().length() > 150) return "Judul maksimal 150 karakter.";
        if (txtBab.getText().trim().length() > 50) return "BAB maksimal 50 karakter.";
        if (((UI.Item) cmbMapel.getSelectedItem()).id == 0) return "Pilih mata pelajaran.";
        if (((UI.Item) cmbKelas.getSelectedItem()).id == 0) return "Pilih kelas.";
        if (fileBaru == null && fileLama == null) return "Pilih file materi terlebih dahulu.";
        if (fileBaru != null && !fileBaru.exists()) return "File yang dipilih sudah tidak ada. Pilih ulang.";
        return null;
    }

    private void aksiSimpan() {
        String err = validasi();
        if (err != null) { UI.peringatan(this, err); return; }

        String judul = txtJudul.getText().trim();
        String bab = txtBab.getText().trim();
        String desk = txtDeskripsi.getText().trim();
        int idMapel = ((UI.Item) cmbMapel.getSelectedItem()).id;
        int idKelas = ((UI.Item) cmbKelas.getSelectedItem()).id;

        // 1) salin file lebih dulu (jika ada file baru)
        String relBaru = null;
        if (fileBaru != null) {
            try {
                relBaru = Storage.simpan(fileBaru, "materi");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan file:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        String relSimpan = (relBaru != null) ? relBaru : fileLama;

        // 2) simpan ke database; jika gagal, file salinan dibuang lagi
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) {
                Storage.hapus(relBaru);
                UI.koneksiGagal(this);
                return;
            }
            if (idEdit == 0) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO tb_materi (judul, bab, deskripsi, file_materi, id_guru, id_mapel, id_kelas) "
                      + "VALUES (?,?,?,?,?,?,?)")) {
                    ps.setString(1, judul);
                    ps.setString(2, bab.isEmpty() ? null : bab);
                    ps.setString(3, desk.isEmpty() ? null : desk);
                    ps.setString(4, relSimpan);
                    ps.setInt(5, Session.idGuru);
                    ps.setInt(6, idMapel);
                    ps.setInt(7, idKelas);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE tb_materi SET judul=?, bab=?, deskripsi=?, file_materi=?, id_mapel=?, id_kelas=? "
                      + "WHERE id_materi=? AND id_guru=?")) {
                    ps.setString(1, judul);
                    ps.setString(2, bab.isEmpty() ? null : bab);
                    ps.setString(3, desk.isEmpty() ? null : desk);
                    ps.setString(4, relSimpan);
                    ps.setInt(5, idMapel);
                    ps.setInt(6, idKelas);
                    ps.setInt(7, idEdit);
                    ps.setInt(8, Session.idGuru);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            Storage.hapus(relBaru);
            UI.errorDb(this, ex);
            return;
        }

        // 3) file lama dibuang jika sudah diganti
        if (relBaru != null && idEdit != 0 && fileLama != null) Storage.hapus(fileLama);

        JOptionPane.showMessageDialog(this, idEdit == 0
            ? "Materi berhasil diunggah!" : "Materi berhasil diperbarui!");
        resetForm();
        muatTabel();
    }

    private void aksiBuka() {
        int id = UI.idTerpilih(tabel, model);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu materi yang ingin dibuka di tabel.");
            return;
        }
        String path = ambilPathFile(id);
        if (path != null) Storage.buka(this, path);
    }

    private String ambilPathFile(int idMateri) {
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return null; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT file_materi FROM tb_materi WHERE id_materi=? AND id_guru=?")) {
                ps.setInt(1, idMateri);
                ps.setInt(2, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getString(1) : null;
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return null;
        }
    }

    private void aksiHapus() {
        int id = UI.idTerpilih(tabel, model);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu materi yang ingin dihapus di tabel.");
            return;
        }
        String judul = UI.teksTerpilih(tabel, model, 2);
        if (!UI.konfirmasiHapus(this,
                "Hapus materi \"" + judul + "\"?\n\n"
              + "Materi dan file-nya akan hilang dan tidak bisa lagi diunduh siswa.\n"
              + "Tindakan ini tidak bisa dibatalkan.")) return;

        String path = ambilPathFile(id);
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM tb_materi WHERE id_materi=? AND id_guru=?")) {
                ps.setInt(1, id);
                ps.setInt(2, Session.idGuru);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }
        Storage.hapus(path);
        if (idEdit == id) resetForm();
        muatTabel();
        JOptionPane.showMessageDialog(this, "Materi berhasil dihapus.");
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmMateriGuru().setVisible(true));
    }
}