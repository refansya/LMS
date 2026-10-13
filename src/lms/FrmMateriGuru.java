package lms;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FrmMateriGuru extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- FORM UPLOAD MATERI (SISI KIRI) ---
    private RoundedPanel panelForm;
    private JLabel lblFormTitle, lblJudulMateri, lblBab, lblFile;
    private JTextField txtJudulMateri, txtBab, txtFile;
    private RoundedButton btnPilihFile, btnUpload;

    // --- TABEL DAFTAR MATERI (SISI KANAN) ---
    private RoundedPanel panelDaftar;
    private JLabel lblDaftarTitle;
    private JTable tabelMateri;
    private JScrollPane scrollPaneMateri;
    private DefaultTableModel tableModel;
    private RoundedButton btnHapusMateri;

    public FrmMateriGuru() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Manajemen Materi Guru");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(null);

        // Background Gradasi Utama
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
        lblHeaderTitle = new JLabel("MANAJEMEN MATERI BELAJAR");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(40, 25, 450, 30);
        bgPanel.add(lblHeaderTitle);

        // Tombol Kembali ke Portal Guru
        btnKembali = new RoundedButton("KEMBALI", 10, new Color(108, 117, 125), new Color(198, 40, 40));
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setBounds(840, 25, 110, 35);
        btnKembali.addActionListener(e -> {
            new FrmMenuGuru().setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnKembali);

        // ==========================================
        // 1. PANEL FORM UPLOAD MATERI (SISI KIRI)
        // ==========================================
        panelForm = new RoundedPanel(20, Color.WHITE, true);
        panelForm.setBounds(40, 80, 360, 490);
        panelForm.setLayout(null);
        bgPanel.add(panelForm);

        lblFormTitle = new JLabel("Form Upload Materi");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(26, 26, 46));
        lblFormTitle.setBounds(25, 20, 200, 25);
        panelForm.add(lblFormTitle);

        // Judul Materi
        lblJudulMateri = new JLabel("Judul Materi Belajar");
        lblJudulMateri.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblJudulMateri.setForeground(new Color(15, 52, 96));
        lblJudulMateri.setBounds(25, 70, 310, 20);
        panelForm.add(lblJudulMateri);

        txtJudulMateri = new JTextField();
        txtJudulMateri.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtJudulMateri.setBounds(25, 95, 310, 35);
        txtJudulMateri.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtJudulMateri);

        // Bab Pembahasan
        lblBab = new JLabel("BAB Pembahasan");
        lblBab.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBab.setForeground(new Color(15, 52, 96));
        lblBab.setBounds(25, 150, 310, 20);
        panelForm.add(lblBab);

        txtBab = new JTextField();
        txtBab.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBab.setBounds(25, 175, 310, 35);
        txtBab.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtBab);

        // File Unggahan (Simulasi File Chooser)
        lblFile = new JLabel("Pilih Dokumen Materi (.pdf / .docx)");
        lblFile.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFile.setForeground(new Color(15, 52, 96));
        lblFile.setBounds(25, 230, 310, 20);
        panelForm.add(lblFile);

        txtFile = new JTextField();
        txtFile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFile.setEditable(false);
        txtFile.setBounds(25, 255, 190, 35);
        txtFile.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtFile);

        // Tombol Pilih File
        btnPilihFile = new RoundedButton("PILIH FILE", 8, new Color(108, 117, 125), new Color(90, 98, 104));
        btnPilihFile.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnPilihFile.setForeground(Color.WHITE);
        btnPilihFile.setBounds(225, 255, 110, 35);
        btnPilihFile.addActionListener(e -> {
            txtFile.setText("Materi_Aljabar_Kelas8.pdf");
        });
        panelForm.add(btnPilihFile);

        // Tombol Upload Materi Baru
        btnUpload = new RoundedButton("UPLOAD MATERI BARU", 10, new Color(46, 204, 113), new Color(39, 174, 96));
        btnUpload.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpload.setForeground(Color.WHITE);
        btnUpload.setBounds(25, 350, 310, 45);
        btnUpload.addActionListener(e -> {
            String judul = txtJudulMateri.getText();
            String bab = txtBab.getText();
            String file = txtFile.getText();
            
            if(!judul.isEmpty() && !bab.isEmpty() && !file.isEmpty()){
                int noBaru = tableModel.getRowCount() + 1;
                tableModel.addRow(new Object[]{String.valueOf(noBaru), bab, judul, file});
                JOptionPane.showMessageDialog(this, "Materi Belajar Berhasil Diupload!");
                txtJudulMateri.setText("");
                txtBab.setText("");
                txtFile.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Mohon lengkapi semua isian materi!");
            }
        });
        panelForm.add(btnUpload);


        // ==========================================
        // 2. PANEL TABEL DAFTAR MATERI (SISI KANAN)
        // ==========================================
        panelDaftar = new RoundedPanel(20, Color.WHITE, true);
        panelDaftar.setBounds(420, 80, 530, 490);
        panelDaftar.setLayout(null);
        bgPanel.add(panelDaftar);

        lblDaftarTitle = new JLabel("Daftar Materi Terupload");
        lblDaftarTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDaftarTitle.setForeground(new Color(26, 26, 46));
        lblDaftarTitle.setBounds(25, 20, 250, 25);
        panelDaftar.add(lblDaftarTitle);

        // Tabel
        String[] kolom = {"No", "BAB", "Judul Pembahasan", "Nama File"};
        tableModel = new DefaultTableModel(kolom, 0);
        tabelMateri = new JTable(tableModel);

        // Data awal simulasi
        tableModel.addRow(new Object[]{"1", "BAB I", "Pengenalan Sistem Komputer", "Sistem_Komputer.pdf"});
        tableModel.addRow(new Object[]{"2", "BAB II", "Logika Matematika", "Logika_MTK.docx"});

        scrollPaneMateri = new JScrollPane(tabelMateri);
        scrollPaneMateri.setBounds(25, 65, 480, 330);
        panelDaftar.add(scrollPaneMateri);

        // Tombol Hapus Materi
        btnHapusMateri = new RoundedButton("HAPUS MATERI", 10, new Color(231, 76, 60), new Color(192, 41, 43));
        btnHapusMateri.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHapusMateri.setForeground(Color.WHITE);
        btnHapusMateri.setBounds(365, 415, 140, 35);
        btnHapusMateri.addActionListener(e -> {
            int baris = tabelMateri.getSelectedRow();
            if (baris != -1) {
                tableModel.removeRow(baris);
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    tableModel.setValueAt(String.valueOf(i + 1), i, 0);
                }
            }
        });
        panelDaftar.add(btnHapusMateri);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmMateriGuru().setVisible(true));
    }
}