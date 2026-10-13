package lms;

<<<<<<< HEAD
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
=======
import java.awt.Color;
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
=======
public class FrmKelas extends javax.swing.JFrame {

    // Panel Background Gradasi Utama
    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- SISI KIRI: FORM INPUT KELAS ---
    private RoundedPanel panelForm;
    private JLabel lblFormTitle, lblNamaKelas, lblWaliKelas, lblTingkat;
    private JTextField txtNamaKelas, txtWaliKelas, txtTingkat;
    private RoundedButton btnSimpan, btnReset;

    // --- SISI KANAN: TABEL DATA KELAS ---
    private RoundedPanel panelTabel;
    private JLabel lblTabelTitle;
    private JTable tabelKelas;
    private JScrollPane scrollPaneTabel;
    private RoundedButton btnUbah, btnHapus;
    private DefaultTableModel tableModel;

    public FrmKelas() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        // --- WINDOW SETTINGS ---
        setTitle("LMS UPTD SMPN 33 Depok - Manajemen Kelas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(null);

        // ==========================================
        // 1. PANEL BACKGROUND UTAMA (Gradasi Gelap LMS)
        // ==========================================
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
        lblHeaderTitle = new JLabel("MANAJEMEN DATA KELAS");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(40, 25, 400, 30);
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
        // 2. PANEL FORM INPUT (SISI KIRI)
        // ==========================================
        panelForm = new RoundedPanel(20, Color.WHITE, true);
        panelForm.setBounds(40, 80, 350, 490);
        panelForm.setLayout(null);
        bgPanel.add(panelForm);

        // Judul Form
        lblFormTitle = new JLabel("Form Input Kelas");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(26, 26, 46));
        lblFormTitle.setBounds(25, 20, 200, 25);
        panelForm.add(lblFormTitle);

        // Input Nama Kelas
        lblNamaKelas = new JLabel("Nama Kelas (Contoh: VIII-A)");
        lblNamaKelas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNamaKelas.setForeground(new Color(15, 52, 96));
        lblNamaKelas.setBounds(25, 70, 300, 20);
        panelForm.add(lblNamaKelas);

        txtNamaKelas = new JTextField();
        txtNamaKelas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNamaKelas.setBounds(25, 95, 300, 35);
        txtNamaKelas.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtNamaKelas);

        // Input Wali Kelas
        lblWaliKelas = new JLabel("Wali Kelas");
        lblWaliKelas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblWaliKelas.setForeground(new Color(15, 52, 96));
        lblWaliKelas.setBounds(25, 150, 300, 20);
        panelForm.add(lblWaliKelas);

        txtWaliKelas = new JTextField();
        txtWaliKelas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtWaliKelas.setBounds(25, 175, 300, 35);
        txtWaliKelas.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtWaliKelas);

        // Input Tingkatan
        lblTingkat = new JLabel("Tingkat (7 / 8 / 9)");
        lblTingkat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTingkat.setForeground(new Color(15, 52, 96));
        lblTingkat.setBounds(25, 230, 300, 20);
        panelForm.add(lblTingkat);

        txtTingkat = new JTextField();
        txtTingkat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTingkat.setBounds(25, 255, 300, 35);
        txtTingkat.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtTingkat);

        // Tombol Simpan
        btnSimpan = new RoundedButton("SIMPAN", 10, new Color(46, 204, 113), new Color(39, 174, 96));
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
        // 3. PANEL TABEL DATA (SISI KANAN)
        // ==========================================
        panelTabel = new RoundedPanel(20, Color.WHITE, true);
        panelTabel.setBounds(410, 80, 540, 490);
        panelTabel.setLayout(null);
        bgPanel.add(panelTabel);

        // Judul Tabel
        lblTabelTitle = new JLabel("Daftar Kelas Terdaftar");
        lblTabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTabelTitle.setForeground(new Color(26, 26, 46));
        lblTabelTitle.setBounds(25, 20, 250, 25);
        panelTabel.add(lblTabelTitle);

        // Inisialisasi JTable & Model Kolom
        String[] kolom = {"No", "Nama Kelas", "Wali Kelas", "Tingkat"};
        tableModel = new DefaultTableModel(kolom, 0);
        tabelKelas = new JTable(tableModel);
        
        // Memasukkan data simulasi awal
        tableModel.addRow(new Object[]{"1", "VII-A", "Siti Aminah, S.Pd.", "7"});
        tableModel.addRow(new Object[]{"2", "VIII-B", "Budi Susanto, M.Pd.", "8"});
        tableModel.addRow(new Object[]{"3", "IX-C", "Ahmad Fauzi, S.Si.", "9"});

        scrollPaneTabel = new JScrollPane(tabelKelas);
        scrollPaneTabel.setBounds(25, 65, 490, 330);
        panelTabel.add(scrollPaneTabel);

        // Tombol Ubah
        btnUbah = new RoundedButton("UBAH DATA", 10, new Color(241, 196, 15), new Color(230, 126, 34));
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

    // --- FUNGSI LOGIKA AKSI ---

    private void aksiSimpan() {
        String namaKelas = txtNamaKelas.getText();
        String waliKelas = txtWaliKelas.getText();
        String tingkat = txtTingkat.getText();
        
        if (!namaKelas.isEmpty() && !waliKelas.isEmpty() && !tingkat.isEmpty()) {
            int noBaru = tableModel.getRowCount() + 1;
            tableModel.addRow(new Object[]{String.valueOf(noBaru), namaKelas, waliKelas, tingkat});
            aksiReset();
        }
    }

    private void aksiReset() {
        txtNamaKelas.setText("");
        txtWaliKelas.setText("");
        txtTingkat.setText("");
    }

    private void aksiUbah() {
        int barisTerpilih = tabelKelas.getSelectedRow();
        if (barisTerpilih != -1) {
            txtNamaKelas.setText(tableModel.getValueAt(barisTerpilih, 1).toString());
            txtWaliKelas.setText(tableModel.getValueAt(barisTerpilih, 2).toString());
            txtTingkat.setText(tableModel.getValueAt(barisTerpilih, 3).toString());
>>>>>>> b323dc30037ccdf9c2133f5e0b0c6e2675f3a553
        }
    }

    private void aksiHapus() {
<<<<<<< HEAD
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
=======
        int barisTerpilih = tabelKelas.getSelectedRow();
        if (barisTerpilih != -1) {
            tableModel.removeRow(barisTerpilih);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(String.valueOf(i + 1), i, 0);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>                        

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println("Gagal menerapkan tampilan Nimbus: " + ex.getMessage());
        }

        java.awt.EventQueue.invokeLater(() -> {
            new FrmKelas().setVisible(true);
        });
>>>>>>> b323dc30037ccdf9c2133f5e0b0c6e2675f3a553
    }
}