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

public class FrmAbsensiGuru extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- PANEL ABSENSI (RoundedPanel) ---
    private RoundedPanel panelAbsensi;
    private JLabel lblAbsensiTitle, lblAbsensiSub;
    private JTable tabelAbsensi;
    private JScrollPane scrollPaneAbsensi;
    private DefaultTableModel modelAbsensi;
    private RoundedButton btnSimpanPresensi;

    public FrmAbsensiGuru() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Presensi Siswa Guru");
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
        lblHeaderTitle = new JLabel("PRESENSI HARIAN SISWA");
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
        // PANEL UTAMA ABSENSI (RoundedPanel)
        // ==========================================
        panelAbsensi = new RoundedPanel(20, Color.WHITE, true);
        panelAbsensi.setBounds(40, 80, 910, 490);
        panelAbsensi.setLayout(null);
        bgPanel.add(panelAbsensi);

        lblAbsensiTitle = new JLabel("Lembar Presensi Kelas VIII-A");
        lblAbsensiTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAbsensiTitle.setForeground(new Color(26, 26, 46));
        lblAbsensiTitle.setBounds(30, 20, 300, 25);
        panelAbsensi.add(lblAbsensiTitle);

        lblAbsensiSub = new JLabel("Isi kolom status dengan: Hadir / Sakit / Izin / Alpha.");
        lblAbsensiSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAbsensiSub.setForeground(Color.GRAY);
        lblAbsensiSub.setBounds(30, 45, 400, 20);
        panelAbsensi.add(lblAbsensiSub);

        // Tabel Data Presensi
        String[] kolom = {"No", "NISN", "Nama Siswa", "Status Kehadiran", "Keterangan"};
        modelAbsensi = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Kolom Status Kehadiran (3) dan Keterangan (4) yang boleh diedit
                return column == 3 || column == 4;
            }
        };
        tabelAbsensi = new JTable(modelAbsensi);

        // Memasukkan data dummy awal siswa
        modelAbsensi.addRow(new Object[]{"1", "10118021", "Aditya Pratama", "Hadir", "-"});
        modelAbsensi.addRow(new Object[]{"2", "10118022", "Bella Safira", "Hadir", "-"});
        modelAbsensi.addRow(new Object[]{"3", "10118023", "Citra Kirana", "Izin", "Acara Keluarga"});
        modelAbsensi.addRow(new Object[]{"4", "10118024", "Dharmawan", "Hadir", "-"});

        scrollPaneAbsensi = new JScrollPane(tabelAbsensi);
        scrollPaneAbsensi.setBounds(30, 85, 850, 300);
        panelAbsensi.add(scrollPaneAbsensi);

        // Tombol Simpan Presensi
        btnSimpanPresensi = new RoundedButton("SIMPAN PRESENSI", 10, new Color(46, 204, 113), new Color(39, 174, 96));
        btnSimpanPresensi.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimpanPresensi.setForeground(Color.WHITE);
        btnSimpanPresensi.setBounds(30, 415, 200, 45);
        btnSimpanPresensi.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Sukses! Data presensi harian kelas telah berhasil disimpan ke database.");
        });
        panelAbsensi.add(btnSimpanPresensi);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmAbsensiGuru().setVisible(true));
    }
}