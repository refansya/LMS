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

public class FrmLaporan extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- PANEL UTAMA LAPORAN ---
    private RoundedPanel panelLaporan;
    private JLabel lblLaporanTitle, lblStatistik;
    private JTable tabelLaporan;
    private JScrollPane scrollPaneLaporan;
    private DefaultTableModel tableModel;

    // Tombol Export kustom
    private RoundedButton btnCetakPDF, btnExportExcel;

    public FrmLaporan() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Laporan Nilai & Kehadiran");
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
        lblHeaderTitle = new JLabel("LAPORAN AKADEMIK & KEHADIRAN");
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
        // WADAH GRAFIK/TABEL LAPORAN (RoundedPanel)
        // ==========================================
        panelLaporan = new RoundedPanel(20, Color.WHITE, true);
        panelLaporan.setBounds(40, 80, 910, 490);
        panelLaporan.setLayout(null);
        bgPanel.add(panelLaporan);

        lblLaporanTitle = new JLabel("Laporan Ringkasan Akademik Kelas VII - IX");
        lblLaporanTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLaporanTitle.setForeground(new Color(26, 26, 46));
        lblLaporanTitle.setBounds(30, 20, 400, 25);
        panelLaporan.add(lblLaporanTitle);

        lblStatistik = new JLabel("Rata-rata Kehadiran Sekolah: 96.5%   |   Rata-rata KKM Siswa Lulus: 88.2%");
        lblStatistik.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatistik.setForeground(new Color(15, 52, 96));
        lblStatistik.setBounds(30, 50, 500, 20);
        panelLaporan.add(lblStatistik);

        // Tabel Data Laporan
        String[] kolom = {"No", "Nama Kelas", "Wali Kelas", "Hadir (%)", "Sakit/Izin (%)", "Alpha (%)", "Rata-rata Nilai"};
        tableModel = new DefaultTableModel(kolom, 0);
        tabelLaporan = new JTable(tableModel);

        // Dummy Data Laporan
        tableModel.addRow(new Object[]{"1", "VII-A", "Siti Aminah, S.Pd.", "97.2%", "2.1%", "0.7%", "85.4"});
        tableModel.addRow(new Object[]{"2", "VIII-B", "Budi Susanto, M.Pd.", "95.8%", "3.0%", "1.2%", "82.1"});
        tableModel.addRow(new Object[]{"3", "IX-C", "Ahmad Fauzi, S.Si.", "96.5%", "2.5%", "1.0%", "89.0"});

        scrollPaneLaporan = new JScrollPane(tabelLaporan);
        scrollPaneLaporan.setBounds(30, 90, 850, 290);
        panelLaporan.add(scrollPaneLaporan);

        // --- TOMBOL AKSI EKSPOR ---
        // Cetak PDF
        btnCetakPDF = new RoundedButton("CETAK PDF", 10, new Color(231, 76, 60), new Color(192, 41, 43));
        btnCetakPDF.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCetakPDF.setForeground(Color.WHITE);
        btnCetakPDF.setBounds(30, 410, 180, 45);
        btnCetakPDF.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Berhasil! Laporan Akademik diekspor ke PDF.");
        });
        panelLaporan.add(btnCetakPDF);

        // Export Excel
        btnExportExcel = new RoundedButton("EXPORT EXCEL", 10, new Color(46, 204, 113), new Color(39, 174, 96));
        btnExportExcel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExportExcel.setForeground(Color.WHITE);
        btnExportExcel.setBounds(230, 410, 180, 45);
        btnExportExcel.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Berhasil! Data Laporan berhasil diekspor ke Microsoft Excel (.xlsx).");
        });
        panelLaporan.add(btnExportExcel);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmLaporan().setVisible(true));
    }
}