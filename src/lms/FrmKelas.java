package lms;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

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
        }
    }

    private void aksiHapus() {
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
    }
}