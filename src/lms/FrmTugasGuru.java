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
import javax.swing.table.DefaultTableModel;

public class FrmTugasGuru extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- PANEL TUGAS (SISI KIRI) ---
    private RoundedPanel panelTugas;
    private JLabel lblTugasTitle;
    private JTable tabelTugas;
    private JScrollPane scrollPaneTugas;
    private DefaultTableModel modelTugas;

    // --- PANEL DETAIL & SISWA (SISI KANAN) ---
    private RoundedPanel panelSiswa;
    private JLabel lblSiswaTitle, lblSiswaDesc;
    private JTable tabelSiswa;
    private JScrollPane scrollPaneSiswa;
    private DefaultTableModel modelSiswa;
    private RoundedButton btnBeriNilai, btnDownload;

    public FrmTugasGuru() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Manajemen Tugas Guru");
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
        lblHeaderTitle = new JLabel("PANEL EVALUASI & TUGAS SISWA");
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
            new FrmMenuGuru().setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnKembali);

        // ==========================================
        // 1. PANEL DAFTAR TUGAS (SISI KIRI)
        // ==========================================
        panelTugas = new RoundedPanel(20, Color.WHITE, true);
        panelTugas.setBounds(40, 80, 420, 490);
        panelTugas.setLayout(null);
        bgPanel.add(panelTugas);

        lblTugasTitle = new JLabel("Daftar Tugas Aktif");
        lblTugasTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTugasTitle.setForeground(new Color(26, 26, 46));
        lblTugasTitle.setBounds(25, 20, 200, 25);
        panelTugas.add(lblTugasTitle);

        String[] kolTugas = {"ID", "Nama Tugas", "Deadline", "Pengumpul"};
        modelTugas = new DefaultTableModel(kolTugas, 0);
        tabelTugas = new JTable(modelTugas);
        modelTugas.addRow(new Object[]{"T01", "Aljabar Linear Sesi 1", "18 Juli 2026", "24/30 Siswa"});
        modelTugas.addRow(new Object[]{"T02", "Persamaan Kuadrat", "25 Juli 2026", "12/30 Siswa"});

        scrollPaneTugas = new JScrollPane(tabelTugas);
        scrollPaneTugas.setBounds(25, 65, 370, 390);
        panelTugas.add(scrollPaneTugas);

        // ==========================================
        // 2. PANEL SISWA YANG MENGUMPULKAN (SISI KANAN)
        // ==========================================
        panelSiswa = new RoundedPanel(20, Color.WHITE, true);
        panelSiswa.setBounds(480, 80, 470, 490);
        panelSiswa.setLayout(null);
        bgPanel.add(panelSiswa);

        lblSiswaTitle = new JLabel("Siswa Yang Sudah Mengumpulkan");
        lblSiswaTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSiswaTitle.setForeground(new Color(26, 26, 46));
        lblSiswaTitle.setBounds(25, 20, 300, 25);
        panelSiswa.add(lblSiswaTitle);

        lblSiswaDesc = new JLabel("Pilih salah satu siswa untuk menilai tugas.");
        lblSiswaDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSiswaDesc.setForeground(Color.GRAY);
        lblSiswaDesc.setBounds(25, 45, 300, 20);
        panelSiswa.add(lblSiswaDesc);

        String[] kolSiswa = {"No", "Nama Siswa", "Tanggal Kirim", "Status"};
        modelSiswa = new DefaultTableModel(kolSiswa, 0);
        tabelSiswa = new JTable(modelSiswa);
        modelSiswa.addRow(new Object[]{"1", "Aditya Pratama", "14 Juli 2026", "Belum Dinilai"});
        modelSiswa.addRow(new Object[]{"2", "Bella Safira", "13 Juli 2026", "Belum Dinilai"});
        modelSiswa.addRow(new Object[]{"3", "Citra Kirana", "12 Juli 2026", "Sudah Dinilai (90)"});

        scrollPaneSiswa = new JScrollPane(tabelSiswa);
        scrollPaneSiswa.setBounds(25, 80, 420, 310);
        panelSiswa.add(scrollPaneSiswa);

        // Tombol Download File Tugas
        btnDownload = new RoundedButton("DOWNLOAD TUGAS", 10, new Color(15, 52, 96), new Color(10, 36, 66));
        btnDownload.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDownload.setForeground(Color.WHITE);
        btnDownload.setBounds(25, 415, 190, 45);
        btnDownload.addActionListener(e -> {
            int selectedRow = tabelSiswa.getSelectedRow();
            if (selectedRow != -1) {
                String namaSiswa = tabelSiswa.getValueAt(selectedRow, 1).toString();
                JOptionPane.showMessageDialog(this, "Mengunduh file tugas milik: " + namaSiswa);
            } else {
                JOptionPane.showMessageDialog(this, "Silakan pilih baris siswa terlebih dahulu!");
            }
        });
        panelSiswa.add(btnDownload);

        // Tombol Beri Nilai
        btnBeriNilai = new RoundedButton("BERI NILAI", 10, new Color(46, 204, 113), new Color(39, 174, 96));
        btnBeriNilai.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBeriNilai.setForeground(Color.WHITE);
        btnBeriNilai.setBounds(255, 415, 190, 45);
        btnBeriNilai.addActionListener(e -> {
            int selectedRow = tabelSiswa.getSelectedRow();
            if (selectedRow != -1) {
                String namaSiswa = tabelSiswa.getValueAt(selectedRow, 1).toString();
                String inputNilai = JOptionPane.showInputDialog(this, "Masukkan Nilai untuk " + namaSiswa + " (0-100):");
                if (inputNilai != null && !inputNilai.isEmpty()) {
                    tabelSiswa.setValueAt("Sudah Dinilai (" + inputNilai + ")", selectedRow, 3);
                    JOptionPane.showMessageDialog(this, "Nilai berhasil disimpan!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Silakan pilih baris siswa terlebih dahulu!");
            }
        });
        panelSiswa.add(btnBeriNilai);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmTugasGuru().setVisible(true));
    }
}