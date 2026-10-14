package lms;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FrmMapel extends JFrame {

    private static final String KKM_DEFAULT = "75";

    private JLabel lblFormTitle;
    private JTextField txtKode, txtNama, txtKkm, txtDeskripsi, txtCari;
    private DefaultTableModel model;
    private JTable tabel;
    private RoundedButton btnSimpan;

    private int idEdit = 0; // 0 = tambah, >0 = edit (id_mapel)

    public FrmMapel() {
        initUI();
        muatTabel();
    }

    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Mata Pelajaran", "MANAJEMEN MATA PELAJARAN");

        // ---------- FORM ----------
        RoundedPanel panelForm = UI.panelForm(bg);
        lblFormTitle = UI.judulForm(panelForm, "Form Input Mapel");
        txtKode = UI.field(panelForm, "Kode Mapel * (maks. 10, contoh: MTK)", 52);
        txtNama = UI.field(panelForm, "Nama Mata Pelajaran *", 108);
        txtKkm = UI.field(panelForm, "Nilai KKM * (0 - 100)", 164);
        txtDeskripsi = UI.field(panelForm, "Deskripsi", 220);
        txtKkm.setText(KKM_DEFAULT);

        btnSimpan = UI.tombol(panelForm, "SIMPAN", UI.HIJAU, UI.HIJAU_H, 25, 310, 145, 42, this::aksiSimpan);
        UI.tombol(panelForm, "BATAL / RESET", UI.ABU, UI.ABU_H, 180, 310, 145, 42, this::resetForm);

        // ---------- TABEL ----------
        RoundedPanel panelTabel = UI.panelTabel(bg, "Daftar Mata Pelajaran");
        model = UI.modelTabel("ID", "No", "Kode", "Nama Mata Pelajaran", "KKM");
        tabel = UI.tabel(model);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(280);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(60);
        UI.tambahTabel(panelTabel, tabel);
        txtCari = UI.kolomCari(panelTabel, this::muatTabel);

        UI.tombol(panelTabel, "EDIT MAPEL", UI.KUNING, UI.KUNING_H, 260, 458, 120, 38, this::aksiUbah);
        UI.tombol(panelTabel, "HAPUS", UI.MERAH, UI.MERAH_H, 395, 458, 120, 38, this::aksiHapus);
    }

    // ------------------------------------------------------------------
    private void muatTabel() {
        model.setRowCount(0);
        String kata = "%" + txtCari.getText().trim() + "%";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id_mapel, kode_mapel, nama_mapel, kkm FROM tb_mapel "
                  + "WHERE nama_mapel LIKE ? OR kode_mapel LIKE ? ORDER BY nama_mapel")) {
                ps.setString(1, kata);
                ps.setString(2, kata);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        model.addRow(new Object[]{rs.getInt(1), no++, rs.getString(2),
                            rs.getString(3), rs.getInt(4)});
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private void resetForm() {
        txtKode.setText("");
        txtNama.setText("");
        txtKkm.setText(KKM_DEFAULT);
        txtDeskripsi.setText("");
        idEdit = 0;
        lblFormTitle.setText("Form Input Mapel");
        btnSimpan.setText("SIMPAN");
        tabel.clearSelection();
        txtKode.requestFocus();
    }

    private void aksiUbah() {
        int id = UI.idTerpilih(tabel, model);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu mata pelajaran yang ingin diubah di tabel.");
            return;
        }
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT kode_mapel, nama_mapel, kkm, deskripsi FROM tb_mapel WHERE id_mapel=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return;
                    txtKode.setText(rs.getString("kode_mapel"));
                    txtNama.setText(rs.getString("nama_mapel"));
                    txtKkm.setText(String.valueOf(rs.getInt("kkm")));
                    txtDeskripsi.setText(rs.getString("deskripsi") == null ? "" : rs.getString("deskripsi"));
                }
            }
            idEdit = id;
            lblFormTitle.setText("Edit Mata Pelajaran");
            btnSimpan.setText("UPDATE");
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private String validasi() {
        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();
        String kkm = txtKkm.getText().trim();

        if (kode.isEmpty()) return "Kode mapel wajib diisi.";
        if (kode.length() > 10) return "Kode mapel maksimal 10 karakter.";
        if (kode.contains(" ")) return "Kode mapel tidak boleh mengandung spasi.";
        if (nama.isEmpty()) return "Nama mata pelajaran wajib diisi.";
        if (nama.length() > 100) return "Nama mata pelajaran maksimal 100 karakter.";
        if (!kkm.matches("\\d{1,3}") || Integer.parseInt(kkm) > 100) return "KKM harus angka 0 - 100.";
        return null;
    }

    private void aksiSimpan() {
        String err = validasi();
        if (err != null) { UI.peringatan(this, err); return; }

        String kode = txtKode.getText().trim().toUpperCase();
        String nama = txtNama.getText().trim();
        int kkm = Integer.parseInt(txtKkm.getText().trim());
        String desk = txtDeskripsi.getText().trim();

        String sql = (idEdit == 0)
            ? "INSERT INTO tb_mapel (kode_mapel, nama_mapel, kkm, deskripsi) VALUES (?,?,?,?)"
            : "UPDATE tb_mapel SET kode_mapel=?, nama_mapel=?, kkm=?, deskripsi=? WHERE id_mapel=?";

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, kode);
                ps.setString(2, nama);
                ps.setInt(3, kkm);
                ps.setString(4, desk.isEmpty() ? null : desk);
                if (idEdit != 0) ps.setInt(5, idEdit);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, idEdit == 0
                ? "Mata pelajaran berhasil ditambahkan." : "Mata pelajaran berhasil diperbarui.");
            resetForm();
            muatTabel();
        } catch (SQLIntegrityConstraintViolationException dup) {
            UI.peringatan(this, "Kode mapel \"" + kode + "\" sudah dipakai. Gunakan kode lain.");
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private void aksiHapus() {
        int id = UI.idTerpilih(tabel, model);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu mata pelajaran yang ingin dihapus di tabel.");
            return;
        }
        String nama = UI.teksTerpilih(tabel, model, 3);
        if (!UI.konfirmasiHapus(this,
                "Hapus mata pelajaran \"" + nama + "\"?\n\n"
              + "Jadwal, materi, tugas, kuis, dan nilai yang terkait mapel ini ikut terhapus.\n"
              + "Guru pengampunya tidak dihapus, hanya mapelnya dikosongkan.\n"
              + "Tindakan ini tidak bisa dibatalkan.")) return;

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM tb_mapel WHERE id_mapel=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            if (idEdit == id) resetForm();
            muatTabel();
            JOptionPane.showMessageDialog(this, "Mata pelajaran berhasil dihapus.");
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmMapel().setVisible(true));
    }
}