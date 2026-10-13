package lms;

<<<<<<< HEAD
=======
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author ultro
 */
>>>>>>> b323dc30037ccdf9c2133f5e0b0c6e2675f3a553
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
<<<<<<< HEAD
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
=======
import javax.swing.JFrame;
import javax.swing.JLabel;
>>>>>>> b323dc30037ccdf9c2133f5e0b0c6e2675f3a553
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
<<<<<<< HEAD
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class FrmDataGuru extends JFrame {

    private static final String PASSWORD_AWAL = "guru123";

    /** Item combo mapel: menyimpan id + nama. */
    private static class Item {
        final int id;
        final String label;
        Item(int id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    private JPanel bgPanel;
    private RoundedPanel panelForm, panelTabel;
    private JLabel lblFormTitle;
    private JTextField txtNip, txtNama, txtHp, txtEmail, txtCari;
    private JComboBox<String> cmbJk;
    private JComboBox<Item> cmbMapel;
    private JTable tabelGuru;
    private DefaultTableModel tableModel;
    private RoundedButton btnSimpan, btnBatal, btnUbah, btnHapus, btnKembali;

    private int idEdit = 0; // 0 = mode tambah, >0 = mode edit (id_guru)

    public FrmDataGuru() {
        initUI();
        muatMapel();
        muatTabel();
    }

    // =====================================================================
    //  UI
    // =====================================================================
    private void initUI() {
        setTitle("LMS UPTD SMPN 33 Depok - Data Guru");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setPreferredSize(new java.awt.Dimension(1000, 650));
        pack();
        setLocationRelativeTo(null);

=======
import javax.swing.table.DefaultTableModel;

public class FrmDataGuru extends javax.swing.JFrame {

    // Panel Background Gradasi Utama
    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- SISI KIRI: FORM INPUT ---
    private RoundedPanel panelForm;
    private JLabel lblFormTitle, lblNip, lblNama, lblMapel;
    private JTextField txtNip, txtNama, txtMapel;
    private RoundedButton btnSimpan, btnReset;

    // --- SISI KANAN: TABEL DATA ---
    private RoundedPanel panelTabel;
    private JLabel lblTabelTitle;
    private JTable tabelGuru;
    private JScrollPane scrollPaneTabel;
    private RoundedButton btnUbah, btnHapus;
    private DefaultTableModel tableModel;
    /**
     * Creates new form FrmDataGuru
     */
    public FrmDataGuru() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        // --- WINDOW SETTINGS ---
        setTitle("LMS UPTD SMPN 33 Depok - Data Guru");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Menutup halaman ini saja, tidak mematikan program utama
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(null);

        // ==========================================
        // 1. PANEL BACKGROUND UTAMA (Gradasi Gelap LMS)
        // ==========================================
>>>>>>> b323dc30037ccdf9c2133f5e0b0c6e2675f3a553
        bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
<<<<<<< HEAD
                g2d.setPaint(new GradientPaint(0, 0, new Color(26, 26, 46), 0, getHeight(), new Color(15, 52, 96)));
=======
                GradientPaint gp = new GradientPaint(0, 0, new Color(26, 26, 46), 0, getHeight(), new Color(15, 52, 96));
                g2d.setPaint(gp);
>>>>>>> b323dc30037ccdf9c2133f5e0b0c6e2675f3a553
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setBounds(0, 0, 1000, 650);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

<<<<<<< HEAD
        JLabel lblHeader = new JLabel("PENGELOLAAN DATA GURU");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setBounds(40, 25, 400, 30);
        bgPanel.add(lblHeader);

        btnKembali = new RoundedButton("KEMBALI", 10, new Color(108, 117, 125), new Color(198, 40, 40));
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnKembali.setBounds(840, 25, 110, 35);
        btnKembali.addActionListener(e -> {
            new FrmMenuAdmin().setVisible(true);
            dispose();
        });
        bgPanel.add(btnKembali);

        // ---------------- FORM (kiri) ----------------
        panelForm = new RoundedPanel(20, Color.WHITE, true);
        panelForm.setBounds(40, 80, 350, 520);
        panelForm.setLayout(null);
        bgPanel.add(panelForm);

        lblFormTitle = new JLabel("Form Input Guru");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(26, 26, 46));
        lblFormTitle.setBounds(25, 18, 300, 25);
        panelForm.add(lblFormTitle);

        txtNip = buatField("NIP / Kode Guru *", 55);
        txtNama = buatField("Nama Lengkap *", 117);

        buatLabel("Jenis Kelamin *", 179);
        cmbJk = new JComboBox<>(new String[]{"L - Laki-laki", "P - Perempuan"});
        cmbJk.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbJk.setBackground(Color.WHITE);
        cmbJk.setBounds(25, 199, 300, 30);
        panelForm.add(cmbJk);

        buatLabel("Mata Pelajaran", 241);
        cmbMapel = new JComboBox<>();
        cmbMapel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbMapel.setBackground(Color.WHITE);
        cmbMapel.setBounds(25, 261, 300, 30);
        panelForm.add(cmbMapel);

        txtHp = buatField("No. HP", 303);
        txtEmail = buatField("Email", 365);

        btnSimpan = new RoundedButton("SIMPAN", 10, new Color(46, 204, 113), new Color(39, 174, 96));
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimpan.setBounds(25, 440, 145, 42);
        btnSimpan.addActionListener(e -> aksiSimpan());
        panelForm.add(btnSimpan);

        btnBatal = new RoundedButton("BATAL / RESET", 10, new Color(149, 165, 166), new Color(127, 140, 141));
        btnBatal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBatal.setBounds(180, 440, 145, 42);
        btnBatal.addActionListener(e -> resetForm());
        panelForm.add(btnBatal);

        // ---------------- TABEL (kanan) ----------------
        panelTabel = new RoundedPanel(20, Color.WHITE, true);
        panelTabel.setBounds(410, 80, 540, 520);
        panelTabel.setLayout(null);
        bgPanel.add(panelTabel);

        JLabel lblTabel = new JLabel("Daftar Guru Terdaftar");
        lblTabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTabel.setForeground(new Color(26, 26, 46));
        lblTabel.setBounds(25, 18, 220, 25);
        panelTabel.add(lblTabel);

        JLabel lblCari = new JLabel("Cari:");
        lblCari.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCari.setForeground(new Color(15, 52, 96));
        lblCari.setBounds(295, 20, 40, 22);
        panelTabel.add(lblCari);

        txtCari = new JTextField();
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCari.setBounds(335, 17, 180, 28);
        txtCari.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        txtCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { muatTabel(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { muatTabel(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { muatTabel(); }
        });
        panelTabel.add(txtCari);

        tableModel = new DefaultTableModel(
            new String[]{"ID", "No", "NIP", "Nama Guru", "JK", "Mata Pelajaran", "No HP"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelGuru = new JTable(tableModel);
        tabelGuru.setRowHeight(24);
        tabelGuru.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelGuru.removeColumn(tabelGuru.getColumnModel().getColumn(0)); // sembunyikan kolom ID
        tabelGuru.getColumnModel().getColumn(0).setPreferredWidth(35);
        tabelGuru.getColumnModel().getColumn(1).setPreferredWidth(95);
        tabelGuru.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabelGuru.getColumnModel().getColumn(3).setPreferredWidth(35);
        tabelGuru.getColumnModel().getColumn(4).setPreferredWidth(115);
        tabelGuru.getColumnModel().getColumn(5).setPreferredWidth(90);

        JScrollPane sp = new JScrollPane(tabelGuru);
        sp.setBounds(25, 60, 490, 380);
        panelTabel.add(sp);

        btnUbah = new RoundedButton("UBAH DATA", 10, new Color(241, 196, 15), new Color(230, 126, 34));
        btnUbah.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUbah.setBounds(260, 458, 120, 38);
        btnUbah.addActionListener(e -> aksiUbah());
        panelTabel.add(btnUbah);

        btnHapus = new RoundedButton("HAPUS", 10, new Color(231, 76, 60), new Color(192, 41, 43));
        btnHapus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHapus.setBounds(395, 458, 120, 38);
=======
        // Header Title
        lblHeaderTitle = new JLabel("PENGELOLAAN DATA GURU");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(40, 25, 400, 30);
        bgPanel.add(lblHeaderTitle);

        // Tombol Kembali ke Menu Utama
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
        lblFormTitle = new JLabel("Form Input Guru");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(26, 26, 46));
        lblFormTitle.setBounds(25, 20, 200, 25);
        panelForm.add(lblFormTitle);

        // Input NIP
        lblNip = new JLabel("NIP / Kode Guru");
        lblNip.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNip.setForeground(new Color(15, 52, 96));
        lblNip.setBounds(25, 70, 300, 20);
        panelForm.add(lblNip);

        txtNip = new JTextField();
        txtNip.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNip.setBounds(25, 95, 300, 35);
        txtNip.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtNip);

        // Input Nama
        lblNama = new JLabel("Nama Lengkap");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNama.setForeground(new Color(15, 52, 96));
        lblNama.setBounds(25, 150, 300, 20);
        panelForm.add(lblNama);

        txtNama = new JTextField();
        txtNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNama.setBounds(25, 175, 300, 35);
        txtNama.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtNama);

        // Input Mata Pelajaran
        lblMapel = new JLabel("Mata Pelajaran");
        lblMapel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMapel.setForeground(new Color(15, 52, 96));
        lblMapel.setBounds(25, 230, 300, 20);
        panelForm.add(lblMapel);

        txtMapel = new JTextField();
        txtMapel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMapel.setBounds(25, 255, 300, 35);
        txtMapel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtMapel);

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
        lblTabelTitle = new JLabel("Daftar Guru Terdaftar");
        lblTabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTabelTitle.setForeground(new Color(26, 26, 46));
        lblTabelTitle.setBounds(25, 20, 250, 25);
        panelTabel.add(lblTabelTitle);

        // Inisialisasi JTable & Model Kolom
        String[] kolom = {"No", "NIP / Kode", "Nama Guru", "Mata Pelajaran"};
        tableModel = new DefaultTableModel(kolom, 0);
        tabelGuru = new JTable(tableModel);
        
        // Memasukkan contoh data awal ke dalam tabel
        tableModel.addRow(new Object[]{"1", "1987021301", "Budi Susanto, M.Pd.", "Matematika"});
        tableModel.addRow(new Object[]{"2", "1991052402", "Siti Aminah, S.Pd.", "Bahasa Indonesia"});
        tableModel.addRow(new Object[]{"3", "1984110303", "Ahmad Fauzi, S.Si.", "Fisika"});

        scrollPaneTabel = new JScrollPane(tabelGuru);
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
>>>>>>> b323dc30037ccdf9c2133f5e0b0c6e2675f3a553
        btnHapus.addActionListener(e -> aksiHapus());
        panelTabel.add(btnHapus);
    }

<<<<<<< HEAD
    private void buatLabel(String teks, int y) {
        JLabel l = new JLabel(teks);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(15, 52, 96));
        l.setBounds(25, y, 300, 18);
        panelForm.add(l);
    }

    private JTextField buatField(String label, int y) {
        buatLabel(label, y);
        JTextField t = new JTextField();
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setBounds(25, y + 20, 300, 30);
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        panelForm.add(t);
        return t;
    }

    // =====================================================================
    //  LOAD DATA
    // =====================================================================
    private void muatMapel() {
        cmbMapel.removeAllItems();
        cmbMapel.addItem(new Item(0, "- Belum ditentukan -"));
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) return;
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id_mapel, nama_mapel FROM tb_mapel ORDER BY nama_mapel")) {
                while (rs.next()) {
                    cmbMapel.addItem(new Item(rs.getInt(1), rs.getString(2)));
                }
            }
        } catch (SQLException ex) {
            tampilError(ex);
        }
    }

    private void muatTabel() {
        tableModel.setRowCount(0);
        String kata = "%" + txtCari.getText().trim() + "%";
        String sql = "SELECT g.id_guru, g.nip, g.nama_guru, g.jenis_kelamin, "
                   + "COALESCE(m.nama_mapel,'-') AS mapel, COALESCE(g.no_hp,'-') AS no_hp "
                   + "FROM tb_guru g LEFT JOIN tb_mapel m ON g.id_mapel = m.id_mapel "
                   + "WHERE g.nama_guru LIKE ? OR g.nip LIKE ? ORDER BY g.nama_guru";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { pesanKoneksiGagal(); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, kata);
                ps.setString(2, kata);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        tableModel.addRow(new Object[]{
                            rs.getInt("id_guru"), no++, rs.getString("nip"),
                            rs.getString("nama_guru"), rs.getString("jenis_kelamin"),
                            rs.getString("mapel"), rs.getString("no_hp")});
                    }
                }
            }
        } catch (SQLException ex) {
            tampilError(ex);
        }
    }

    private int idTerpilih() {
        int r = tabelGuru.getSelectedRow();
        if (r == -1) return 0;
        return (Integer) tableModel.getValueAt(tabelGuru.convertRowIndexToModel(r), 0);
    }

    // =====================================================================
    //  AKSI
    // =====================================================================
    private void resetForm() {
        txtNip.setText("");
        txtNama.setText("");
        txtHp.setText("");
        txtEmail.setText("");
        cmbJk.setSelectedIndex(0);
        cmbMapel.setSelectedIndex(0);
        idEdit = 0;
        lblFormTitle.setText("Form Input Guru");
        btnSimpan.setText("SIMPAN");
        tabelGuru.clearSelection();
        txtNip.requestFocus();
    }

    private void aksiUbah() {
        int id = idTerpilih();
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu guru yang ingin diubah di tabel.");
            return;
        }
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { pesanKoneksiGagal(); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT nip, nama_guru, jenis_kelamin, no_hp, email, id_mapel FROM tb_guru WHERE id_guru=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return;
                    txtNip.setText(rs.getString("nip"));
                    txtNama.setText(rs.getString("nama_guru"));
                    cmbJk.setSelectedIndex(rs.getString("jenis_kelamin").equals("L") ? 0 : 1);
                    txtHp.setText(rs.getString("no_hp") == null ? "" : rs.getString("no_hp"));
                    txtEmail.setText(rs.getString("email") == null ? "" : rs.getString("email"));
                    int idMapel = rs.getInt("id_mapel"); // 0 jika NULL
                    cmbMapel.setSelectedIndex(0);
                    for (int i = 0; i < cmbMapel.getItemCount(); i++) {
                        if (cmbMapel.getItemAt(i).id == idMapel) {
                            cmbMapel.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
            idEdit = id;
            lblFormTitle.setText("Edit Data Guru");
            btnSimpan.setText("UPDATE");
        } catch (SQLException ex) {
            tampilError(ex);
        }
    }

    private String validasi() {
        String nip = txtNip.getText().trim();
        String nama = txtNama.getText().trim();
        String hp = txtHp.getText().trim();
        String email = txtEmail.getText().trim();

        if (nip.isEmpty()) return "NIP / Kode Guru wajib diisi.";
        if (nip.length() > 30) return "NIP maksimal 30 karakter.";
        if (nip.contains(" ")) return "NIP tidak boleh mengandung spasi.";
        if (nama.isEmpty()) return "Nama lengkap wajib diisi.";
        if (!hp.isEmpty() && !hp.matches("\\d{8,15}")) return "No. HP harus berupa angka 8-15 digit.";
        if (!email.isEmpty() && !email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) return "Format email tidak valid.";
        return null;
    }

    private void aksiSimpan() {
        String err = validasi();
        if (err != null) {
            JOptionPane.showMessageDialog(this, err, "Data belum lengkap", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nip = txtNip.getText().trim();
        String nama = txtNama.getText().trim();
        String jk = cmbJk.getSelectedItem().toString().substring(0, 1);
        String hp = txtHp.getText().trim();
        String email = txtEmail.getText().trim();
        int idMapel = ((Item) cmbMapel.getSelectedItem()).id;

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { pesanKoneksiGagal(); return; }
            con.setAutoCommit(false);
            try {
                if (idEdit == 0) {
                    // ---- TAMBAH: buat akun login + data guru ----
                    int idUser;
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO tb_user (username, password, role, nama_lengkap, status) "
                          + "VALUES (?, ?, 'guru', ?, 'aktif')", Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, nip);
                        ps.setString(2, PASSWORD_AWAL);
                        ps.setString(3, nama);
                        ps.executeUpdate();
                        try (ResultSet k = ps.getGeneratedKeys()) {
                            k.next();
                            idUser = k.getInt(1);
                        }
                    }
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO tb_guru (nip, nama_guru, jenis_kelamin, no_hp, email, id_mapel, id_user) "
                          + "VALUES (?,?,?,?,?,?,?)")) {
                        isiParameter(ps, nip, nama, jk, hp, email, idMapel);
                        ps.setInt(7, idUser);
                        ps.executeUpdate();
                    }
                    con.commit();
                    JOptionPane.showMessageDialog(this,
                        "Guru berhasil ditambahkan.\n\nAkun login otomatis dibuat:\n"
                      + "Username : " + nip + "\nPassword : " + PASSWORD_AWAL);
                } else {
                    // ---- UBAH ----
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE tb_guru SET nip=?, nama_guru=?, jenis_kelamin=?, no_hp=?, email=?, id_mapel=? "
                          + "WHERE id_guru=?")) {
                        isiParameter(ps, nip, nama, jk, hp, email, idMapel);
                        ps.setInt(7, idEdit);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE tb_user u JOIN tb_guru g ON u.id_user = g.id_user "
                          + "SET u.nama_lengkap=? WHERE g.id_guru=?")) {
                        ps.setString(1, nama);
                        ps.setInt(2, idEdit);
                        ps.executeUpdate();
                    }
                    con.commit();
                    JOptionPane.showMessageDialog(this, "Data guru berhasil diperbarui.");
                }
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
            resetForm();
            muatTabel();
        } catch (SQLIntegrityConstraintViolationException dup) {
            JOptionPane.showMessageDialog(this,
                "NIP / username \"" + nip + "\" sudah terdaftar. Gunakan NIP lain.",
                "Data ganda", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            tampilError(ex);
        }
    }

    /** Mengisi parameter 1-6 yang sama untuk INSERT dan UPDATE. */
    private void isiParameter(PreparedStatement ps, String nip, String nama, String jk,
                              String hp, String email, int idMapel) throws SQLException {
        ps.setString(1, nip);
        ps.setString(2, nama);
        ps.setString(3, jk);
        ps.setString(4, hp.isEmpty() ? null : hp);
        ps.setString(5, email.isEmpty() ? null : email);
        if (idMapel == 0) ps.setNull(6, Types.INTEGER); else ps.setInt(6, idMapel);
    }

    private void aksiHapus() {
        int id = idTerpilih();
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu guru yang ingin dihapus di tabel.");
            return;
        }
        int r = tabelGuru.convertRowIndexToModel(tabelGuru.getSelectedRow());
        String nama = tableModel.getValueAt(r, 3).toString();

        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Hapus guru \"" + nama + "\"?\n\n"
          + "Akun login-nya juga akan dihapus, begitu pula jadwal, materi, tugas,\n"
          + "kuis, dan nilai yang dibuat guru ini. Wali kelas akan dikosongkan.\n"
          + "Tindakan ini tidak bisa dibatalkan.",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { pesanKoneksiGagal(); return; }
            con.setAutoCommit(false);
            try {
                int idUser = 0;
                try (PreparedStatement ps = con.prepareStatement("SELECT id_user FROM tb_guru WHERE id_guru=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) idUser = rs.getInt(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM tb_guru WHERE id_guru=?")) {
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
            JOptionPane.showMessageDialog(this, "Guru berhasil dihapus.");
        } catch (SQLException ex) {
            tampilError(ex);
        }
    }

    // =====================================================================
    //  PESAN
    // =====================================================================
    private void pesanKoneksiGagal() {
        JOptionPane.showMessageDialog(this,
            "Tidak bisa konek ke database. Pastikan MySQL di XAMPP sudah running!",
            "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
    }

    private void tampilError(SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan database:\n" + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmDataGuru().setVisible(true));
    }
}
=======
    // --- FUNGSI AKSI TOMBOL (LOGIKA) ---
    
    private void aksiSimpan() {
        // Logika simpan data nanti dimasukkan di sini (koneksi database)
        String nip = txtNip.getText();
        String nama = txtNama.getText();
        String mapel = txtMapel.getText();
        
        if (!nip.isEmpty() && !nama.isEmpty() && !mapel.isEmpty()) {
            int noBaru = tableModel.getRowCount() + 1;
            tableModel.addRow(new Object[]{String.valueOf(noBaru), nip, nama, mapel});
            aksiReset();
        }
    }

    private void aksiReset() {
        txtNip.setText("");
        txtNama.setText("");
        txtMapel.setText("");
    }

    private void aksiUbah() {
        int barisTerpilih = tabelGuru.getSelectedRow();
        if (barisTerpilih != -1) {
            // Isi form dengan data dari baris tabel yang dipilih untuk siap diedit
            txtNip.setText(tableModel.getValueAt(barisTerpilih, 1).toString());
            txtNama.setText(tableModel.getValueAt(barisTerpilih, 2).toString());
            txtMapel.setText(tableModel.getValueAt(barisTerpilih, 3).toString());
        }
    }

    private void aksiHapus() {
        int barisTerpilih = tabelGuru.getSelectedRow();
        if (barisTerpilih != -1) {
            tableModel.removeRow(barisTerpilih);
            // Susun ulang nomor urut pada tabel
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(String.valueOf(i + 1), i, 0);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
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
            new FrmDataGuru().setVisible(true);
        });
    }
}
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */

        /* Create and display the form */
        

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
>>>>>>> b323dc30037ccdf9c2133f5e0b0c6e2675f3a553
