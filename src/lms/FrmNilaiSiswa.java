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
import javax.swing.table.DefaultTableModel;

public class FrmNilaiSiswa extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- PANEL REKAP NILAI (RoundedPanel) ---
    private RoundedPanel panelRekap;
    private JLabel lblRekapTitle, lblRekapSub;
    private JTable tabelNilai;
    private JScrollPane scrollPaneNilai;
    private DefaultTableModel modelNilai;

    public FrmNilaiSiswa() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Rekap Nilai Siswa");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 600);
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
        bgPanel.setBounds(0, 0, 950, 600);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // Header Title
        lblHeaderTitle = new JLabel("REKAPITULASI NILAI AKADEMIK");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(40, 25, 450, 30);
        bgPanel.add(lblHeaderTitle);

        // Tombol Kembali
        btnKembali = new RoundedButton("KEMBALI", 10, new Color(108, 117, 125), new Color(198, 40, 40));
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setBounds(790, 25, 110, 35);
        btnKembali.addActionListener(e -> {
            new FrmMenuSiswa().setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnKembali);

        // ==========================================
        // PANEL TABEL REKAP NILAI (RoundedPanel)
        // ==========================================
        panelRekap = new RoundedPanel(20, Color.WHITE, true);
        panelRekap.setBounds(40, 85, 860, 450);
        panelRekap.setLayout(null);
        bgPanel.add(panelRekap);

        lblRekapTitle = new JLabel("Laporan Nilai Hasil Belajar Semester Ganjil");
        lblRekapTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRekapTitle.setForeground(new Color(26, 26, 46));
        lblRekapTitle.setBounds(30, 25, 400, 25);
        panelRekap.add(lblRekapTitle);

        lblRekapSub = new JLabel("Nilai diperbarui secara berkala oleh guru mata pelajaran masing-masing.");
        lblRekapSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRekapSub.setForeground(Color.GRAY);
        lblRekapSub.setBounds(30, 50, 500, 20);
        panelRekap.add(lblRekapSub);

        // Tabel Nilai
        String[] kolom = {"Mata Pelajaran", "Tugas (30%)", "Kuis (20%)", "UTS (25%)", "UAS (25%)", "Nilai Akhir", "Keterangan"};
        modelNilai = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Nilai siswa tidak boleh diedit oleh siswa sendiri
            }
        };
        tabelNilai = new JTable(modelNilai);

        // Memasukkan data dummy nilai siswa
        modelNilai.addRow(new Object[]{"Matematika", "85", "80", "90", "85", "85.5", "TUNTAS"});
        modelNilai.addRow(new Object[]{"Bahasa Indonesia", "90", "85", "88", "92", "89.1", "TUNTAS"});
        modelNilai.addRow(new Object[]{"Bahasa Inggris", "80", "75", "82", "80", "79.4", "TUNTAS"});
        modelNilai.addRow(new Object[]{"Ilmu Pengetahuan Alam (IPA)", "75", "70", "78", "72", "73.9", "TUNTAS"});
        modelNilai.addRow(new Object[]{"Pendidikan Pancasila (PPKn)", "95", "90", "92", "95", "93.4", "TUNTAS"});

        scrollPaneNilai = new JScrollPane(tabelNilai);
        scrollPaneNilai.setBounds(30, 95, 800, 320);
        panelRekap.add(scrollPaneNilai);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmNilaiSiswa().setVisible(true));
    }
}