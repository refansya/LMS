package lms;

import java.awt.Color;
<<<<<<< HEAD
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
=======
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
>>>>>>> b323dc30037ccdf9c2133f5e0b0c6e2675f3a553
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

<<<<<<< HEAD
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
=======
public class FrmMapel extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- SISI KIRI: FORM INPUT MAPEL ---
    private RoundedPanel panelForm;
    private JLabel lblFormTitle, lblKodeMapel, lblNamaMapel, lblKkm;
    private JTextField txtKodeMapel, txtNamaMapel, txtKkm;
    private RoundedButton btnSimpan, btnReset;

    // --- SISI KANAN: TABEL DATA MAPEL ---
    private RoundedPanel panelTabel;
    private JLabel lblTabelTitle;
    private JTable tabelMapel;
    private JScrollPane scrollPaneTabel;
    private RoundedButton btnUbah, btnHapus;
    private DefaultTableModel tableModel;

    public FrmMapel() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Mata Pelajaran");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(null);

        // Background Gradasi
        bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(26, 26, 46), 0, getHeight(), new Color(15, 52, 96));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setBounds(0, 0, 1000, 650);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // Header Title
        lblHeaderTitle = new JLabel("MANAJEMEN MATA PELAJARAN");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(40, 25, 450, 30);
        bgPanel.add(lblHeaderTitle);

        // Tombol Kembali
        btnKembali = new RoundedButton("KEMBALI", 10, new Color(108, 117, 125), new Color(198, 40, 40));
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setBounds(840, 25, 110, 35);
        btnKembali.addActionListener(e -> {
            new FrmMenuAdmin().setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnKembali);

        // ==========================================
        // FORM INPUT (RoundedPanel Kiri)
        // ==========================================
        panelForm = new RoundedPanel(20, Color.WHITE, true);
        panelForm.setBounds(40, 80, 350, 490);
        panelForm.setLayout(null);
        bgPanel.add(panelForm);

        lblFormTitle = new JLabel("Form Input Mapel");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(26, 26, 46));
        lblFormTitle.setBounds(25, 20, 200, 25);
        panelForm.add(lblFormTitle);

        // Kode Mapel
        lblKodeMapel = new JLabel("Kode Mapel (Contoh: MP-001)");
        lblKodeMapel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKodeMapel.setForeground(new Color(15, 52, 96));
        lblKodeMapel.setBounds(25, 70, 300, 20);
        panelForm.add(lblKodeMapel);

        txtKodeMapel = new JTextField();
        txtKodeMapel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtKodeMapel.setBounds(25, 95, 300, 35);
        txtKodeMapel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtKodeMapel);

        // Nama Mapel
        lblNamaMapel = new JLabel("Nama Mata Pelajaran");
        lblNamaMapel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNamaMapel.setForeground(new Color(15, 52, 96));
        lblNamaMapel.setBounds(25, 150, 300, 20);
        panelForm.add(lblNamaMapel);

        txtNamaMapel = new JTextField();
        txtNamaMapel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNamaMapel.setBounds(25, 175, 300, 35);
        txtNamaMapel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtNamaMapel);

        // KKM
        lblKkm = new JLabel("Nilai KKM");
        lblKkm.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKkm.setForeground(new Color(15, 52, 96));
        lblKkm.setBounds(25, 230, 300, 20);
        panelForm.add(lblKkm);

        txtKkm = new JTextField();
        txtKkm.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtKkm.setBounds(25, 255, 300, 35);
        txtKkm.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtKkm);

        // Tombol Simpan
        btnSimpan = new RoundedButton("SIMPAN MAPEL", 10, new Color(46, 204, 113), new Color(39, 174, 96));
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setBounds(25, 340, 300, 40);
        btnSimpan.addActionListener(e -> aksiSimpan());
        panelForm.add(btnSimpan);

        // Tombol Reset
        btnReset = new RoundedButton("RESET", 10, new Color(149, 165, 166), new Color(127, 140, 141));
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReset.setForeground(Color.WHITE);
        btnReset.setBounds(25, 395, 300, 40);
        btnReset.addActionListener(e -> aksiReset());
        panelForm.add(btnReset);

        // ==========================================
        // TABEL DATA (RoundedPanel Kanan)
        // ==========================================
        panelTabel = new RoundedPanel(20, Color.WHITE, true);
        panelTabel.setBounds(410, 80, 540, 490);
        panelTabel.setLayout(null);
        bgPanel.add(panelTabel);

        lblTabelTitle = new JLabel("Daftar Mata Pelajaran");
        lblTabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTabelTitle.setForeground(new Color(26, 26, 46));
        lblTabelTitle.setBounds(25, 20, 250, 25);
        panelTabel.add(lblTabelTitle);

        String[] kolom = {"No", "Kode Mapel", "Nama Mata Pelajaran", "KKM"};
        tableModel = new DefaultTableModel(kolom, 0);
        tabelMapel = new JTable(tableModel);

        // Data awal simulasi
        tableModel.addRow(new Object[]{"1", "MTK-01", "Matematika", "75"});
        tableModel.addRow(new Object[]{"2", "BIN-02", "Bahasa Indonesia", "75"});
        tableModel.addRow(new Object[]{"3", "IPA-03", "Ilmu Pengetahuan Alam", "70"});

        scrollPaneTabel = new JScrollPane(tabelMapel);
        scrollPaneTabel.setBounds(25, 65, 490, 330);
        panelTabel.add(scrollPaneTabel);

        // Tombol Ubah/Edit Mapel
        btnUbah = new RoundedButton("EDIT MAPEL", 10, new Color(241, 196, 15), new Color(230, 126, 34));
        btnUbah.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUbah.setForeground(Color.WHITE);
        btnUbah.setBounds(260, 415, 120, 35);
        btnUbah.addActionListener(e -> aksiUbah());
        panelTabel.add(btnUbah);

        // Tombol Hapus
        btnHapus = new RoundedButton("HAPUS", 10, new Color(231, 76, 60), new Color(192, 41, 43));
        btnHapus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.setBounds(395, 415, 120, 35);
        btnHapus.addActionListener(e -> aksiHapus());
        panelTabel.add(btnHapus);
    }

    private void aksiSimpan() {
        String kode = txtKodeMapel.getText();
        String nama = txtNamaMapel.getText();
        String kkm = txtKkm.getText();
        
        if (!kode.isEmpty() && !nama.isEmpty() && !kkm.isEmpty()) {
            int noBaru = tableModel.getRowCount() + 1;
            tableModel.addRow(new Object[]{String.valueOf(noBaru), kode, nama, kkm});
            aksiReset();
        }
    }

    private void aksiReset() {
        txtKodeMapel.setText("");
        txtNamaMapel.setText("");
        txtKkm.setText("");
    }

    private void aksiUbah() {
        int barisTerpilih = tabelMapel.getSelectedRow();
        if (barisTerpilih != -1) {
            txtKodeMapel.setText(tableModel.getValueAt(barisTerpilih, 1).toString());
            txtNamaMapel.setText(tableModel.getValueAt(barisTerpilih, 2).toString());
            txtKkm.setText(tableModel.getValueAt(barisTerpilih, 3).toString());
>>>>>>> b323dc30037ccdf9c2133f5e0b0c6e2675f3a553
        }
    }

    private void aksiHapus() {
<<<<<<< HEAD
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
=======
        int barisTerpilih = tabelMapel.getSelectedRow();
        if (barisTerpilih != -1) {
            tableModel.removeRow(barisTerpilih);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(String.valueOf(i + 1), i, 0);
            }
        }
    }

    // Bagian main & Generated code bawaan Netbeans
    private void initComponents() {}
    public static void main(String args[]) {
>>>>>>> b323dc30037ccdf9c2133f5e0b0c6e2675f3a553
        java.awt.EventQueue.invokeLater(() -> new FrmMapel().setVisible(true));
    }
}